package com.lucasj.gamedev.essentials.ui;

import java.awt.Graphics;
import java.util.function.Consumer;

public class Render {
    private Layer layer;
    private Consumer<Graphics> renderAction;

    public Render(Layer layer, Consumer<Graphics> renderAction) {
        this.layer = layer;
        this.renderAction = renderAction;
    }

    public void render(Graphics g) {
        renderAction.accept(g);
    }

    public Layer getLayer() {
        return layer;
    }

    public int getLayerValue() {
        return layer.getValue();
    }
}
