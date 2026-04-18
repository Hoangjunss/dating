package com.example.Dating.service;

import com.example.Dating.dtos.response.CandidateResponse;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.entities.*;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.repository.*;
import com.example.Dating.specification.RecommendationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final double W_INTEREST     = 0.40;
    private static final double W_ELO          = 0.35;
    private static final double W_DISTANCE     = 0.15;
    private static final double W_ACTIVITY     = 0.10;
    private static final double ELO_K          = 32.0;
    private static final double ELO_MAX        = 3000.0;
    private static final double ELO_DEFAULT    = 1400.0;
    private static final int    DIVERSITY_WINDOW = 3;

    // FIX 2: fallback preference defaults
    private static final String DEFAULT_GENDER   = "ANY";
    private static final int    DEFAULT_MIN_AGE  = 18;
    private static final int    DEFAULT_MAX_AGE  = 99;
    private static final int    DEFAULT_MAX_KM   = 50;

    private final UserProfileRepository userProfileRepository;
    private final UserPreferenceService preferenceService;
    private final UserInterestRepository userInterestRepository;
    private final UserEloScoreRepository userEloScoreRepository;
    private final UserSwipeRepository userSwipeRepository;
    private final UserPresenceRepository userPresenceRepository;
    private final UserPhotoRepository userPhotoRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CandidateResponse> getCandidates(UUID userId, Pageable pageable) {
        log.info("Generating recommendations for userId={}", userId);

        UserProfile me = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + userId));

        //fallback nếu user chưa set preference
        UserPreferenceResponse pref = loadPreferenceSafely(userId);
        log.info("User preference is {}", pref);

        Set<UUID> myInterestIds = loadInterestIds(userId);
        Set<UUID> swipedIds = userSwipeRepository.findFromUserIdSet(userId);

        Specification<UserProfile> spec = RecommendationSpecification.filter(userId, me, pref, swipedIds);
        int poolSize = Math.max(200, pageable.getPageSize() * 20);
        List<UserProfile> pool = userProfileRepository.findAll(spec, PageRequest.of(0, poolSize)).getContent();

        log.info("Pool size is {}", pool.size());
        log.debug("Hard-filter pool size={}", pool.size());
        if (pool.size() < 10) {
            log.warn("Low pool size, using fallback candidates");

            pool = userProfileRepository.findFallbackCandidates(userId,
                    PageRequest.of(0, poolSize)).getContent();
        }

        List<UUID> poolIds = pool.stream()
                .map(p -> p.getUser().getUserId())
                .collect(Collectors.toList());

        Map<UUID, UserEloScore> eloMap = userEloScoreRepository.findAllById(poolIds)
                .stream()
                .collect(Collectors.toMap(UserEloScore::getUserId, e -> e));

        Map<UUID, UserPresence> presenceMap = userPresenceRepository.findAllById(poolIds)
                .stream()
                .collect(Collectors.toMap(UserPresence::getUserId, p -> p));

        Map<UUID, Set<UUID>> candInterests = loadInterestIdsForAll(poolIds);

        //batch load photos cho toàn bộ pool — tránh N+1
        Map<UUID, List<String>> photoMap = loadPhotoUrls(poolIds);

        //batch load interest names để trả về FE
        Map<UUID, List<String>> interestNameMap = loadInterestNames(poolIds);

        List<ScoredCandidate> scored = pool.stream()
                .map(p -> score(p, me, myInterestIds, eloMap, presenceMap, candInterests, pref))
                .sorted(Comparator.comparingDouble(ScoredCandidate::compositeScore).reversed())
                .collect(Collectors.toList());

        List<ScoredCandidate> diverse = applyDiversity(scored, DIVERSITY_WINDOW);

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), diverse.size());
        if (start >= diverse.size()) return Page.empty(pageable);

        List<CandidateResponse> responses = diverse.subList(start, end).stream()
                .map(sc -> toResponse(sc, me, photoMap, interestNameMap))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, diverse.size());
    }

    // ── Elo update ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void processSwipeElo(UUID fromUserId, UUID toUserId, boolean isLike) {

        UserEloScore fromElo = userEloScoreRepository.findById(fromUserId)
                .orElse(defaultElo(fromUserId));

        UserEloScore toElo = userEloScoreRepository.findById(toUserId)
                .orElse(defaultElo(toUserId));

        //save cả hai nếu chưa tồn tại
        if (!userEloScoreRepository.existsById(fromUserId)) userEloScoreRepository.save(fromElo);
        if (!userEloScoreRepository.existsById(toUserId))   userEloScoreRepository.save(toElo);

        double expected = 1.0 / (1.0 + Math.pow(10.0, (fromElo.getScore() - toElo.getScore()) / 400.0));
        double actual   = isLike ? 1.0 : 0.0;
        double newScore = Math.min(ELO_MAX, Math.max(0, toElo.getScore() + ELO_K * (actual - expected)));

        if (isLike) userEloScoreRepository.updateOnLike(toUserId, newScore);
        else        userEloScoreRepository.updateOnSeen(toUserId, newScore);
    }

    // ── Scoring ───────────────────────────────────────────────────────────────

    private ScoredCandidate score(
            UserProfile candidate, UserProfile me, Set<UUID> myInterests,
            Map<UUID, UserEloScore> eloMap, Map<UUID, UserPresence> presenceMap,
            Map<UUID, Set<UUID>> candidateInterestMap,
            UserPreferenceResponse pref  // nhận pref để dùng maxDistanceKm thật
    ) {
        UUID cid = candidate.getUser().getUserId();

        double interestScore = jaccard(myInterests, candidateInterestMap.getOrDefault(cid, Set.of()));

        double elo = eloMap.containsKey(cid) ? eloMap.get(cid).getScore() : ELO_DEFAULT;
        double eloScore = elo / ELO_MAX;

        //dùng pref.getMaxDistanceKm()
        double distanceScore = 0.0;
        if (me.getLatitude() != null && me.getLongitude() != null
                && candidate.getLatitude() != null && candidate.getLongitude() != null) {
            double km    = haversineKm(me.getLatitude(), me.getLongitude(),
                    candidate.getLatitude(), candidate.getLongitude());
            double maxKm = pref.getMaxDistanceKm() != null ? pref.getMaxDistanceKm() : DEFAULT_MAX_KM;
            distanceScore = Math.max(0.0, 1.0 - (km / maxKm));
        }

        double activityScore = 0.1;
        UserPresence presence = presenceMap.get(cid);
        if (presence != null) {
            if (presence.isOnline()) {
                activityScore = 1.0;
            } else if (presence.getLastActiveAt() != null) {
                long hoursAgo = Duration.between(presence.getLastActiveAt(), Instant.now())
                        .toHours();

                activityScore = Math.exp(-hoursAgo / 24.0);
            }
        }

        double composite = W_INTEREST * interestScore + W_ELO * eloScore
                + W_DISTANCE * distanceScore + W_ACTIVITY * activityScore;
        composite += Math.random() * 0.05;
        return new ScoredCandidate(candidate, composite, interestScore, eloScore, distanceScore, activityScore);
    }

    // ── Diversity ─────────────────────────────────────────────────────────────

    private List<ScoredCandidate> applyDiversity(List<ScoredCandidate> sorted, int window) {
        List<ScoredCandidate> result    = new ArrayList<>();
        LinkedList<ScoredCandidate> rem = new LinkedList<>(sorted);

        while (!rem.isEmpty()) {
            if (result.size() >= window) {
                List<ScoredCandidate> tail = result.subList(result.size() - window, result.size());
                String lastCity = tail.get(0).profile().getCity();
                boolean allSame = lastCity != null &&
                        tail.stream().allMatch(c -> lastCity.equals(c.profile().getCity()));
                if (allSame) {
                    Optional<ScoredCandidate> diff = rem.stream()
                            .filter(c -> !lastCity.equals(c.profile().getCity())).findFirst();
                    if (diff.isPresent()) { rem.remove(diff.get()); result.add(diff.get()); continue; }
                }
            }
            result.add(rem.poll());
        }
        return result;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    //fallback preference nếu user chưa set
    private UserPreferenceResponse loadPreferenceSafely(UUID userId) {
        try {
            return preferenceService.get(userId);
        } catch (Exception e) {
            log.warn("No preference found for userId={}, using defaults", userId);
            return UserPreferenceResponse.builder()
                    .genderPreference(DEFAULT_GENDER)
                    .minAge(DEFAULT_MIN_AGE)
                    .maxAge(DEFAULT_MAX_AGE)
                    .maxDistanceKm(DEFAULT_MAX_KM)
                    .build();
        }
    }

    //batch load photos — trả về map userId → List<url> sorted by sortOrder
    private Map<UUID, List<String>> loadPhotoUrls(List<UUID> userIds) {
        return userPhotoRepository.findByUserProfile_User_UserIdIn(userIds).stream()
                .sorted(Comparator.comparingInt(p -> (p.getSortOrder() != null ? p.getSortOrder() : 999)))
                .collect(Collectors.groupingBy(
                        p -> p.getUserProfile().getUser().getUserId(),
                        Collectors.mapping(UserPhoto::getUrl, Collectors.toList())
                ));
    }

    //batch load interest names cho FE hiển thị tag
    private Map<UUID, List<String>> loadInterestNames(List<UUID> userIds) {
        return userInterestRepository.findByUser_UserIdIn(userIds).stream()
                .collect(Collectors.groupingBy(
                        ui -> ui.getUser().getUserId(),
                        Collectors.mapping(ui -> ui.getInterest().getName(), Collectors.toList())
                ));
    }

    private Set<UUID> loadInterestIds(UUID userId) {
        return userInterestRepository.findByUser_UserId(userId).stream()
                .map(ui -> ui.getInterest().getId())
                .collect(Collectors.toSet());
    }

    private Map<UUID, Set<UUID>> loadInterestIdsForAll(List<UUID> userIds) {
        Map<UUID, Set<UUID>> result = new HashMap<>();
        userInterestRepository.findByUser_UserIdIn(userIds).forEach(ui ->
                result.computeIfAbsent(ui.getUser().getUserId(), k -> new HashSet<>())
                        .add(ui.getInterest().getId()));
        return result;
    }

    private double jaccard(Set<UUID> a, Set<UUID> b) {
        if (a.isEmpty() && b.isEmpty()) return 0.0;
        Set<UUID> inter = new HashSet<>(a); inter.retainAll(b);
        Set<UUID> union = new HashSet<>(a); union.addAll(b);
        return union.isEmpty() ? 0.0 : (double) inter.size() / (union.size() +1);
    }

    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1), dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private UserEloScore defaultElo(UUID userId) {
        return UserEloScore.builder()
                .userId(userId)
                .score(ELO_DEFAULT)
                .totalSeen(0L)
                .totalLikes(0L)
                .build();
    }

    //toResponse() giờ nhận photoMap và interestNameMap
    private CandidateResponse toResponse(ScoredCandidate sc, UserProfile me,
                                         Map<UUID, List<String>> photoMap, Map<UUID, List<String>> interestNameMap) {
        UserProfile p = sc.profile();
        int age = p.getBirthday() != null ? Period.between(p.getBirthday(), LocalDate.now()).getYears() : 0;
        Double distKm = null;
        if (me.getLatitude() != null && p.getLatitude() != null) {
            distKm = Math.round(haversineKm(me.getLatitude(), me.getLongitude(),
                    p.getLatitude(), p.getLongitude()) * 10.0) / 10.0;
        }
        return CandidateResponse.builder()
                .userId(p.getUser().getUserId())
                .displayName(p.getDisplayName())
                .gender(p.getGender())
                .age(age)
                .city(p.getCity())
                .distanceKm(distKm)
                .bio(p.getBio())
                .photoUrls(photoMap.getOrDefault(p.getUser().getUserId(), List.of()))
                .interests(interestNameMap.getOrDefault(p.getUser().getUserId(), List.of()))
                .score(sc.compositeScore())
                .interestScore(sc.interestScore())
                .eloScore(sc.eloScore())
                .distanceScore(sc.distanceScore())
                .activityScore(sc.activityScore())
                .build();
    }

    private record ScoredCandidate(UserProfile profile, double compositeScore,
                                   double interestScore, double eloScore, double distanceScore, double activityScore) {}
}