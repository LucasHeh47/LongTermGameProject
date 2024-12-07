package com.lucasj.gamedev.essentials.ui;

import java.awt.Graphics;
import java.util.function.Consumer;

public class Render {
    private int layer;
    private Consumer<Graphics> renderAction;

    public Render(int layer, Consumer<Graphics> renderAction) {
        this.layer = layer;
        this.renderAction = renderAction;
    }

    public void render(Graphics g) {
        renderAction.accept(g);
    }

    public int getLayer() {
        return layer;
    }
}
