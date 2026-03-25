package com.example.Dating.specification;

import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.entities.UserProfile;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Hard-filter specification for the recommendation engine.
 *
 * Differences from UserProfileSpecification:
 *  - Also excludes users the current user has already swiped.
 *  - Used exclusively by RecommendationService.
 */
public class RecommendationSpecification {

    public static Specification<UserProfile> filter(
            UUID currentUserId,
            UserProfile currentUser,
            UserPreferenceResponse pref,
            Set<UUID> alreadySwipedIds
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. Exclude self
            predicates.add(cb.notEqual(root.get("userId"), currentUserId));

            // 2. Exclude already-swiped users
            if (!alreadySwipedIds.isEmpty()) {
                predicates.add(root.get("userId").in(alreadySwipedIds).not());
            }

            // 3. Gender preference
            if (StringUtils.hasText(pref.getGenderPreference())
                    && !"ANY".equalsIgnoreCase(pref.getGenderPreference())) {

                String preferredGender = pref.getGenderPreference().toUpperCase().trim();

                predicates.add(cb.equal(
                        cb.upper(root.get("gender")),
                        preferredGender
                ));
            }

            // 4. Age range (birthday-based)
            LocalDate today = LocalDate.now();
            if (pref.getMaxAge() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("birthday"), today.minusYears(pref.getMaxAge())
                ));
            }
            if (pref.getMinAge() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("birthday"), today.minusYears(pref.getMinAge())
                ));
            }

            // 5. Distance (Haversine approximation in SQL)
            if (pref.getMaxDistanceKm() != null
                    && currentUser.getLatitude() != null
                    && currentUser.getLongitude() != null) {
                predicates.add(buildDistancePredicate(
                        currentUser.getLatitude(),
                        currentUser.getLongitude(),
                        pref.getMaxDistanceKm(),
                        root, cb
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Haversine distance in km via JPA criteria expression
    //  distance = 6371 * 2 * asin( sqrt(
    //    sin^2((lat2-lat1)/2) + cos(lat1)*cos(lat2)*sin^2((lon2-lon1)/2)
    //  ))
    // ────────────────────────────────────────────────────────────────────────

    private static Predicate buildDistancePredicate(
            double currentLat, double currentLng,
            int maxDistanceKm,
            Root<UserProfile> root,
            CriteriaBuilder cb
    ) {
        double lat1 = Math.toRadians(currentLat);
        double lng1 = Math.toRadians(currentLng);

        Expression<Double> lat2 = cb.function("RADIANS", Double.class, root.get("latitude"));
        Expression<Double> lng2 = cb.function("RADIANS", Double.class, root.get("longitude"));

        Expression<Double> dLat = cb.diff(lat2, cb.literal(lat1));
        Expression<Double> dLng = cb.diff(lng2, cb.literal(lng1));

        Expression<Double> sinHalfDLat = cb.function("SIN", Double.class,
                cb.quot(dLat, cb.literal(2.0)).as(Double.class));
        Expression<Double> sinHalfDLng = cb.function("SIN", Double.class,
                cb.quot(dLng, cb.literal(2.0)).as(Double.class));

        Expression<Double> a = cb.sum(
                cb.prod(sinHalfDLat, sinHalfDLat),
                cb.prod(
                        cb.prod(
                                cb.function("COS", Double.class, cb.literal(lat1)),
                                cb.function("COS", Double.class, lat2)
                        ),
                        cb.prod(sinHalfDLng, sinHalfDLng)
                )
        );

        Expression<Double> distance = cb.prod(
                cb.literal(6371.0 * 2),
                cb.function("ASIN", Double.class,
                        cb.function("SQRT", Double.class, a))
        );

        return cb.lessThanOrEqualTo(distance, (double) maxDistanceKm);
    }
}