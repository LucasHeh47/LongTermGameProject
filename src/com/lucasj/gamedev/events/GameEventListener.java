package com.lucasj.gamedev.events;

public interface GameEventListener<T extends GameEvent> {
    void handleEvent(T event);
}
