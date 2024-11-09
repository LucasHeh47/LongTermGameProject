package com.lucasj.gamedev.game.entities.placeables.data;

import com.lucasj.gamedev.game.entities.Entity;

public class LandmineEnemyDistanceData {
    private Entity entity;
    private int distance;

    public LandmineEnemyDistanceData(Entity entity, int distance) {
        this.entity = entity;
        this.distance = distance;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getDistance() {
        return distance;
    }
}
