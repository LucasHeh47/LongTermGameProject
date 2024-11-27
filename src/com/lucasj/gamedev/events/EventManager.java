package com.lucasj.gamedev.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lucasj.gamedev.events.collectibles.CoinCollectedEvent;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEventListener;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.entities.EntityDamagedEvent;
import com.lucasj.gamedev.events.entities.EntityDamagedEventListener;
import com.lucasj.gamedev.events.entities.EntityDeathEvent;
import com.lucasj.gamedev.events.entities.EntityDeathEventListener;
import com.lucasj.gamedev.events.level.LevelUpEvent;
import com.lucasj.gamedev.events.level.LevelUpEventListener;
import com.lucasj.gamedev.events.player.PlayerAttackEvent;
import com.lucasj.gamedev.events.player.PlayerAttackEventListener;
import com.lucasj.gamedev.events.player.PlayerMoveEvent;
import com.lucasj.gamedev.events.player.PlayerMoveEventListener;
import com.lucasj.gamedev.events.player.PlayerStaminaUseEvent;
import com.lucasj.gamedev.events.player.PlayerStaminaUseEventListener;
import com.lucasj.gamedev.events.waves.WaveEndEvent;
import com.lucasj.gamedev.events.waves.WaveEndEventListener;
import com.lucasj.gamedev.events.weapons.SwapWeaponEvent;
import com.lucasj.gamedev.events.weapons.SwapWeaponEventListener;
import com.lucasj.gamedev.events.weapons.WeaponTierUpgradeEvent;
import com.lucasj.gamedev.events.weapons.WeaponTierUpgradeEventListener;
import com.lucasj.gamedev.misc.Debug;

public class EventManager {
	
    private Map<Class<? extends GameEvent>, List<Object>> eventMap = new HashMap<>();
    
    public void addListener(Object listening, Class<? extends GameEvent> eventToListenFor) {
    	eventMap.computeIfAbsent(eventToListenFor, k -> new ArrayList<>());
        
        eventMap.get(eventToListenFor).add(listening);
        Debug.log("EventManager", "Adding " + listening.getClass().getSimpleName() + " to " + eventToListenFor.getSimpleName());
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
    	} else if (e instanceof EntityDeathEvent) {
    		List<Object> listeners = eventMap.get(EntityDeathEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof EntityDeathEventListener) {
                        ((EntityDeathEventListener) listener).onEntityDeath((EntityDeathEvent) e);
                    }
                });
            }
    	} else if (e instanceof WaveEndEvent) {
    		List<Object> listeners = eventMap.get(WaveEndEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof WaveEndEventListener) {
                        ((WaveEndEventListener) listener).onWaveEnd((WaveEndEvent) e);
                    }
                });
            }
    	} else if (e instanceof EntityDamagedEvent) {
    		List<Object> listeners = eventMap.get(EntityDamagedEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof EntityDamagedEventListener) {
                        ((EntityDamagedEventListener) listener).onEntityDamaged((EntityDamagedEvent) e);
                    }
                });
            }
    	} else if (e instanceof WeaponTierUpgradeEvent) {
    		List<Object> listeners = eventMap.get(WeaponTierUpgradeEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof WeaponTierUpgradeEventListener) {
                        ((WeaponTierUpgradeEventListener) listener).onWeaponTierUpgrade((WeaponTierUpgradeEvent) e);
                    }
                });
            }
    	} else if (e instanceof SwapWeaponEvent) {
    		List<Object> listeners = eventMap.get(WeaponTierUpgradeEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof SwapWeaponEventListener) {
                        ((SwapWeaponEventListener) listener).onSwapWeapon((SwapWeaponEvent) e);
                    }
                });
            }
    	} else if (e instanceof LevelUpEvent) {
    		List<Object> listeners = eventMap.get(LevelUpEvent.class);
            if (listeners != null) {
                listeners.forEach(listener -> {
                    if (listener instanceof LevelUpEventListener) {
                        ((LevelUpEventListener) listener).onLevelUp((LevelUpEvent) e);
                    }
                });
            }
    	}
    }
    
}
