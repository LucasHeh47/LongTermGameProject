package com.lucasj.gamedev.essentials.ui;

public enum Layer {

	UI(0),
    Foreground(1),
    Player(2),
    Collectible(3),
    Enemy(5),
    Placeable(10),
    Background(65);

    private final int value;

    Layer(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
