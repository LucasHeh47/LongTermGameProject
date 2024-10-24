package com.lucasj.gamedev.mathutils;

import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.game.entities.Entity;

public class Quadtree {

    private int MAX_ENTITIES = 4; // Maximum entities per node before splitting
    private int MAX_LEVELS = 5; // Maximum depth of the quadtree

    private int level; // Current level of the node
    private List<Entity> entities; // Entities in this node
    private Vector2D boundsTopLeft; // Top-left corner of the boundary
    private Vector2D boundsBottomRight; // Bottom-right corner of the boundary
    private Quadtree[] nodes; // Four child nodes

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
        // Clear current entities and nodes
        clear();
        // Update the bounds
        this.boundsTopLeft = newBoundsTopLeft;
        this.boundsBottomRight = newBoundsBottomRight;
    }

    private void split() {
        double subWidth = (boundsBottomRight.getX() - boundsTopLeft.getX()) / 2;
        double subHeight = (boundsBottomRight.getY() - boundsTopLeft.getY()) / 2;
        double x = boundsTopLeft.getX();
        double y = boundsTopLeft.getY();

        nodes[0] = new Quadtree(level + 1, new Vector2D(x, y), new Vector2D(x + subWidth, y + subHeight));
        nodes[1] = new Quadtree(level + 1, new Vector2D(x + subWidth, y), new Vector2D(x + 2 * subWidth, y + subHeight));
        nodes[2] = new Quadtree(level + 1, new Vector2D(x, y + subHeight), new Vector2D(x + subWidth, y + 2 * subHeight));
        nodes[3] = new Quadtree(level + 1, new Vector2D(x + subWidth, y + subHeight), new Vector2D(x + 2 * subWidth, y + 2 * subHeight));
    }

    private int getIndex(Entity entity) {
        double midX = boundsTopLeft.getX() + (boundsBottomRight.getX() - boundsTopLeft.getX()) / 2;
        double midY = boundsTopLeft.getY() + (boundsBottomRight.getY() - boundsTopLeft.getY()) / 2;
        Vector2D pos = entity.getPosition();
        boolean topQuadrant = pos.getY() < midY;
        boolean bottomQuadrant = pos.getY() >= midY;

        if (pos.getX() < midX) {
            if (topQuadrant) return 0;
            else if (bottomQuadrant) return 2;
        } else if (pos.getX() >= midX) {
            if (topQuadrant) return 1;
            else if (bottomQuadrant) return 3;
        }

        return -1; // Object cannot be fully contained in one quadrant
    }

    public void insert(Entity entity) {
        if (nodes[0] != null) {
            int index = getIndex(entity);

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
                int index = getIndex(entities.get(i));
                if (index != -1) {
                    nodes[index].insert(entities.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<Entity> retrieve(List<Entity> returnEntities, Entity entity) {
        int index = getIndex(entity);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnEntities, entity);
        }

        returnEntities.addAll(entities);
        return returnEntities;
    }
}