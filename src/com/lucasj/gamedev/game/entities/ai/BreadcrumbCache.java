package com.lucasj.gamedev.game.entities.ai;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreadcrumbCache {
    private Map<Breadcrumb, Map<Breadcrumb, Double>> distances = new HashMap<>();

    // Update method to be called once per frame
    public void updateDistances(List<Breadcrumb> breadcrumbs) {
        distances.clear();
        for (Breadcrumb breadcrumb1 : breadcrumbs) {
            Map<Breadcrumb, Double> innerMap = new HashMap<>();
            for (Breadcrumb breadcrumb2 : breadcrumbs) {
                if (breadcrumb1 != breadcrumb2) {
                    double distance = breadcrumb1.getPosition().distanceTo(breadcrumb2.getPosition());
                    innerMap.put(breadcrumb2, distance);
                }
            }
            distances.put(breadcrumb1, innerMap);
        }
    }

    // Method to retrieve the distance between two breadcrumbs
    public double getDistance(Breadcrumb b1, Breadcrumb b2) {
        return distances.getOrDefault(b1, Collections.emptyMap()).getOrDefault(b2, Double.MAX_VALUE);
    }
}