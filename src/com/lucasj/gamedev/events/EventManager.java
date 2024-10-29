package com.lucasj.gamedev.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lucasj.gamedev.events.collectibles.CoinCollectedEvent;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEventListener;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.player.PlayerAttackEvent;
import com.lucasj.gamedev.events.player.PlayerAttackEventListener;
import com.lucasj.gamedev.events.player.PlayerMoveEvent;
import com.lucasj.gamedev.events.player.PlayerMoveEventListener;
import com.lucasj.gamedev.events.player.PlayerStaminaUseEvent;
import com.lucasj.gamedev.events.player.PlayerStaminaUseEventListener;
import com.lucasj.gamedev.misc.Debug;

public class EventManager {
	
    private Map<Class<? extends GameEvent>, List<Object>> eventMap = new HashMap<>();
    
    public void addListener(Object listening, Class<? extends GameEvent> eventToListenFor) {
    	eventMap.computeIfAbsent(eventToListenFor, k -> new ArrayList<>());
        
        eventMap.get(eventToListenFor).add(listening);
    }
    
    public void removeListener(Object listener, Class<? extends GameEvent> eventListeningFor) {
    	if(eventMap.containsKey(eventListeningFor) && eventMap.get(eventListeningFor).contains(listener)) {
    		eventMap.get(eventListeningFor).remove(listener);
    		Debug.log("EventManager", "Removed " + listener.getClass().getSimpleName() + " from event listener: " + eventListeningFor.getSimpleName());
    	}
    }
    
    public void dispatchEvent(GameEvent e) {
    	if(e instanceof EntityCollisionEvent) {
    		EntityCollisionEvent event = (EntityCollisionEvent) e;
    		event.getCollider().onEntityCollision(event);
            event.getEntity().onEntityCollision(event);
    	} else if(e instanceof PlayerMoveEvent) {
    		List<Object> listeners = eventMap.get(PlayerMoveEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof PlayerMoveEventListener) {
                        ((PlayerMoveEventListener) listener).onPlayerMove((PlayerMoveEvent) e);
                    }
                });
            }
    	} else if (e instanceof CoinCollectedEvent) {
    		List<Object> listeners = eventMap.get(CoinCollectedEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof CoinCollectedEventListener) {
                        ((CoinCollectedEventListener) listener).onCoinCollect((CoinCollectedEvent) e);
                    }
                });
            }
    	} else if (e instanceof PlayerStaminaUseEvent) {
    		List<Object> listeners = eventMap.get(PlayerStaminaUseEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof PlayerStaminaUseEventListener) {
                        ((PlayerStaminaUseEventListener) listener).onPlayerStaminaUse((PlayerStaminaUseEvent) e);
                    }
                });
            }
    	} else if (e instanceof PlayerAttackEvent) {
    		List<Object> listeners = eventMap.get(PlayerAttackEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof PlayerAttackEventListener) {
                        ((PlayerAttackEventListener) listener).onPlayerAttack((PlayerAttackEvent) e);
                    }
                });
            }
    	}
    }
    
}
