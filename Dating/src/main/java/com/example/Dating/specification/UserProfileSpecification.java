package com.example.Dating.specification;

import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.entities.UserProfile;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserProfileSpecification {

    public static Specification<UserProfile> filter(
            UUID currentUserId,
            UserProfile currentUser,
            UserPreferenceResponse pref
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. Loại bản thân
            predicates.add(excludeSelf(currentUserId, root, cb));

            // 2. Giới tính
            if (StringUtils.hasText(pref.getGenderPreference())
                    && !pref.getGenderPreference().equalsIgnoreCase("ANY")) {
                predicates.add(byGender(pref.getGenderPreference(), root, cb));
            }

            // 3. Độ tuổi
            if (pref.getMinAge() != null || pref.getMaxAge() != null) {
                predicates.add(byAgeRange(pref.getMinAge(), pref.getMaxAge(), root, cb));
            }

            // 4. Khoảng cách
            if (pref.getMaxDistanceKm() != null
                    && currentUser.getLatitude() != null
                    && currentUser.getLongitude() != null) {
                predicates.add(byDistance(
                        currentUser.getLatitude(),
                        currentUser.getLongitude(),
                        pref.getMaxDistanceKm(),
                        root, cb
                ));
            }

            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ════════════════════════════════════════════════════════════

    private static Predicate excludeSelf(
            UUID currentUserId,
            Root<UserProfile> root,
            CriteriaBuilder cb
    ) {
        return cb.notEqual(root.get("userId"), currentUserId);
    }

    private static Predicate byGender(
            String genderPreference,
            Root<UserProfile> root,
            CriteriaBuilder cb
    ) {
        return cb.equal(
                cb.lower(root.get("gender")),
                genderPreference.toLowerCase()
        );
    }

    private static Predicate byAgeRange(
            Integer minAge,
            Integer maxAge,
            Root<UserProfile> root,
            CriteriaBuilder cb
    ) {
        List<Predicate> predicates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        if (maxAge != null) {
            // birthday >= today - maxAge (không quá già)
            predicates.add(cb.greaterThanOrEqualTo(
                    root.get("birthday"), today.minusYears(maxAge)
            ));
        }

        if (minAge != null) {
            // birthday <= today - minAge (không quá trẻ)
            predicates.add(cb.lessThanOrEqualTo(
                    root.get("birthday"), today.minusYears(minAge)
            ));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private static Predicate byDistance(
            Double currentLat,
            Double currentLng,
            Integer maxDistanceKm,
            Root<UserProfile> root,
            CriteriaBuilder cb
    ) {
        Expression<Double> distance = haversine(currentLat, currentLng, root, cb);
        return cb.lessThanOrEqualTo(distance, (double) maxDistanceKm);
    }

    // ════════════════════════════════════════════════════════════
    //  HAVERSINE — tính khoảng cách (km) giữa 2 toạ độ
    // ════════════════════════════════════════════════════════════

    private static Expression<Double> haversine(
            Double currentLat,
            Double currentLng,
            Root<UserProfile> root,
            CriteriaBuilder cb
    ) {
        Expression<Double> lat1 = cb.literal(currentLat);
        Expression<Double> lng1 = cb.literal(currentLng);
        Expression<Double> lat2 = root.get("latitude");
        Expression<Double> lng2 = root.get("longitude");

        // 6371 * acos(
        //   cos(rad(lat1)) * cos(rad(lat2)) * cos(rad(lng2) - rad(lng1))
        //   + sin(rad(lat1)) * sin(rad(lat2))
        // )
        return cb.prod(
                cb.literal(6371.0),
                cb.function("acos", Double.class,
                        cb.sum(
                                cb.prod(
                                        cb.prod(
                                                cb.function("cos", Double.class, cb.function("radians", Double.class, lat1)),
                                                cb.function("cos", Double.class, cb.function("radians", Double.class, lat2))
                                        ),
                                        cb.function("cos", Double.class,
                                                cb.diff(
                                                        cb.function("radians", Double.class, lng2),
                                                        cb.function("radians", Double.class, lng1)
                                                )
                                        )
                                ),
                                cb.prod(
                                        cb.function("sin", Double.class, cb.function("radians", Double.class, lat1)),
                                        cb.function("sin", Double.class, cb.function("radians", Double.class, lat2))
                                )
                        )
                )
        );
    }
}
