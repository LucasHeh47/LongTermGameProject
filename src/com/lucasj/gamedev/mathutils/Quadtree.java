package com.lucasj.gamedev.mathutils;

import java.util.ArrayList;
import java.util.List;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.utils.ConcurrentList;

public class Quadtree<T> {

    private int MAX_ENTITIES = 4; // Maximum entities per node before splitting
    private int MAX_LEVELS = 5; // Maximum depth of the quadtree

    private int level; // Current level of the node
    private List<T> entities; // Entities in this node
    private Vector2D boundsTopLeft; // Top-left corner of the boundary
    private Vector2D boundsBottomRight; // Bottom-right corner of the boundary
    private Quadtree<T>[] nodes; // Four child nodes

    public Quadtree(int level, Vector2D boundsTopLeft, Vector2D boundsBottomRight) {
        this.level = level;
        this.entities = new ArrayList<>();
        this.boundsTopLeft = boundsTopLeft;
        this.boundsBottomRight = boundsBottomRight;
        this.nodes = new Quadtree[4];
    }

    public void clear() {
        entities.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }
    
    public void setBounds(Vector2D newBoundsTopLeft, Vector2D newBoundsBottomRight) {
        clear(); // Clear current entities and nodes
        this.boundsTopLeft = newBoundsTopLeft;
        this.boundsBottomRight = newBoundsBottomRight;
    }

    private void split() {
        double subWidth = (boundsBottomRight.getX() - boundsTopLeft.getX()) / 2;
        double subHeight = (boundsBottomRight.getY() - boundsTopLeft.getY()) / 2;
        double x = boundsTopLeft.getX();
        double y = boundsTopLeft.getY();

        nodes[0] = new Quadtree<>(level + 1, new Vector2D(x, y), new Vector2D(x + subWidth, y + subHeight));
        nodes[1] = new Quadtree<>(level + 1, new Vector2D(x + subWidth, y), new Vector2D(x + 2 * subWidth, y + subHeight));
        nodes[2] = new Quadtree<>(level + 1, new Vector2D(x, y + subHeight), new Vector2D(x + subWidth, y + 2 * subHeight));
        nodes[3] = new Quadtree<>(level + 1, new Vector2D(x + subWidth, y + subHeight), new Vector2D(x + 2 * subWidth, y + 2 * subHeight));
    }

    private int getIndex(Entity entity) {
        double midX = boundsTopLeft.getX() + (boundsBottomRight.getX() - boundsTopLeft.getX()) / 2;
        double midY = boundsTopLeft.getY() + (boundsBottomRight.getY() - boundsTopLeft.getY()) / 2;
        Vector2D pos = entity.getPosition();
        boolean topQuadrant = pos.getY() < midY;
        boolean bottomQuadrant = pos.getY() >= midY;

        if (pos.getX() < midX) {
            return topQuadrant ? 0 : 2;
        } else if (pos.getX() >= midX) {
            return topQuadrant ? 1 : 3;
        }
        return -1; // Object cannot be fully contained in one quadrant
    }

    public void insert(T entity) {
        if (nodes[0] != null) {
            int index = getIndex((Entity) entity);
            if (index != -1) {
                nodes[index].insert(entity);
                return;
            }
        }

        entities.add(entity);

        if (entities.size() > MAX_ENTITIES && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < entities.size()) {
                int index = getIndex((Entity) entities.get(i));
                if (index != -1) {
                    nodes[index].insert(entities.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<T> retrieve(List<T> returnEntities, T entity) {
        if (nodes[0] != null) {
            int index = getIndex((Entity) entity);
            if (index != -1) {
                nodes[index].retrieve(returnEntities, entity);
            }
        }

        // Only add entities in this node that are within a specific range (if needed)
        for (T e : entities) {
            if (isNearby((Entity) e, (Entity) entity)) { // Implement `isNearby` for custom range checks
                returnEntities.add(e);
            }
        }

        return returnEntities;
    }

    // Helper method to check if two entities are nearby (implement as needed)
    private boolean isNearby(Entity e1, Entity e2) {
        double range = 100; // Define the nearby range
        return e1.getPosition().distanceTo(e2.getPosition()) <= range;
    }

    // New retrieve method that finds all entities within a rectangular area
    public List<T> retrieve(List<T> returnEntities, Vector2D areaTopLeft, Vector2D areaBottomRight) {
        // Check if this node’s area intersects the search area; skip if not
        if (!intersects(areaTopLeft, areaBottomRight)) {
            return returnEntities;
        }

        // Recursively retrieve entities from child nodes if they exist
        if (nodes[0] != null) {
            for (Quadtree<T> node : nodes) {
                node.retrieve(returnEntities, areaTopLeft, areaBottomRight);
            }
        }

        // Add entities within this node that fall within the specified bounds
        for (T entity : entities) {
            if (entityWithinBounds((Entity) entity, areaTopLeft, areaBottomRight)) {
                returnEntities.add(entity);
            }
        }

        return returnEntities;
    }
    // Helper method to check if an entity is within the specified bounds
    private boolean entityWithinBounds(Entity entity, Vector2D areaTopLeft, Vector2D areaBottomRight) {
        Vector2D pos = entity.getPosition();
        return pos.getX() >= areaTopLeft.getX() && pos.getX() <= areaBottomRight.getX()
                && pos.getY() >= areaTopLeft.getY() && pos.getY() <= areaBottomRight.getY();
    }

    // Helper method to check if this node's bounds intersect the given rectangular area
    private boolean intersects(Vector2D areaTopLeft, Vector2D areaBottomRight) {
        return !(boundsBottomRight.getX() < areaTopLeft.getX() || boundsTopLeft.getX() > areaBottomRight.getX()
                || boundsBottomRight.getY() < areaTopLeft.getY() || boundsTopLeft.getY() > areaBottomRight.getY());
    }

}
