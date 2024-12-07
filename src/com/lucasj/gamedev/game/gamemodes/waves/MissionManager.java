package com.lucasj.gamedev.game.gamemodes.waves;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEvent;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEventListener;
import com.lucasj.gamedev.events.entities.EntityDamagedEvent;
import com.lucasj.gamedev.events.entities.EntityDamagedEventListener;
import com.lucasj.gamedev.events.entities.EntityDeathEvent;
import com.lucasj.gamedev.events.entities.EntityDeathEventListener;
import com.lucasj.gamedev.events.weapons.SwapWeaponEvent;
import com.lucasj.gamedev.events.weapons.SwapWeaponEventListener;
import com.lucasj.gamedev.events.weapons.WeaponTierUpgradeEvent;
import com.lucasj.gamedev.events.weapons.WeaponTierUpgradeEventListener;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.gamemodes.waves.missions.Mission;
import com.lucasj.gamedev.game.gamemodes.waves.missions.Missions;
import com.lucasj.gamedev.misc.Debug;

public class MissionManager implements EntityDeathEventListener, CoinCollectedEventListener, EntityDamagedEventListener, WeaponTierUpgradeEventListener, SwapWeaponEventListener {
	
	private Game game;
	private WavesManager waves;
	
	private boolean canStartMission = true;
	
	public MissionManager(Game game, WavesManager waves) {
		this.game = game;
		this.waves = waves;
		
		game.getEventManager().addListener(this, EntityDeathEvent.class);
		game.getEventManager().addListener(this, CoinCollectedEvent.class);
		game.getEventManager().addListener(this, EntityDamagedEvent.class);
		game.getEventManager().addListener(this, WeaponTierUpgradeEvent.class);
		game.getEventManager().addListener(this, SwapWeaponEvent.class);
		
	}
	
	public void update(double deltaTime) {
		if(Mission.activeMission != null) {
			if(!Mission.activeMission.isAccomplished() && Mission.activeMission.getBroadcast().finished) {
				Mission.activeMission = null;
				return;
			}
			if(Mission.activeMission.getBroadcast() != null && Mission.activeMission.getBroadcast().finished) {
				if(Mission.activeMission.getName().equals("Invulnerable") || Mission.activeMission.getName().equals("Frenzy")) {
					Mission.activeMission.reward();
					Mission.activeMission = null;
					return;
				}
			}
			Mission.activeMission.update(deltaTime);
		}
	}
	
	public Render render() {
		if(Mission.activeMission != null) return Mission.activeMission.render();
		return null;
	}
	
	public void startMission() {
		if(Mission.activeMission != null) return;
		
		if(!canStartMission) return;
		
		List<Class<? extends Mission>> missions = new ArrayList<>();
		for (Class<?> nested : Missions.class.getDeclaredClasses()) {
			if (Modifier.isStatic(nested.getModifiers())) {
				if(Mission.class.isAssignableFrom(nested)) {
					missions.add((Class<? extends Mission>) nested);
				}
			}
		}
		if (!missions.isEmpty()) {
		    Random rand = new Random();
		    try {
		        // Get a random mission class and instantiate it
		        Class<? extends Mission> randomMissionClass = missions.get(rand.nextInt(missions.size()));
		        Mission.activeMission = randomMissionClass.getConstructor(Game.class).newInstance(game);
		    } catch (Exception e) {
		        e.printStackTrace(); // Handle reflection-related exceptions
		    }
		}
		this.setCanStartMission(false);
		Debug.log(this, "starting mission");
        Mission.activeMission = new Missions.Terminator(game);
	}

	@Override
	public void onEntityDeath(EntityDeathEvent e) {
		if(Mission.activeMission == null) return;

		if(e.getEntity() instanceof Enemy) {
			if(Mission.activeMission.getName().equals("Slayer")) {
					Missions.Slayer mission = (Missions.Slayer) Mission.activeMission;
					mission.increment();
			}
	
			if(Mission.activeMission.getName().equals("Terminator")) {
				if(e.getProjectile() != null) {
					if(e.getProjectile().getTag().equals("TurretBullet")) {
						Missions.Terminator mission = (Missions.Terminator) Mission.activeMission;
						mission.increment();
					}
				}
			}
		}
		
	}

	@Override
	public void onCoinCollect(CoinCollectedEvent e) {
		if(Mission.activeMission == null) return;

		if(Mission.activeMission.getName().equals("Businessman")) {
			Missions.Businessman mission = (Missions.Businessman) Mission.activeMission;
			mission.increment();
			
		}
	}

	@Override
	public void onEntityDamaged(EntityDamagedEvent e) {
		if(Mission.activeMission == null) return;
		
		if(e.getEntity() instanceof Enemy) {
			if(Mission.activeMission.getName().equals("Frenzy")) {
				((Missions.Frenzy) Mission.activeMission).increment(e.getDamage());
			}
		}
		
		if(e.getEntity() instanceof Player) {
			if(Mission.activeMission.getName().equals("Invulnerable")) {
				((Missions.Invulnerable) Mission.activeMission).fail();
			}
		}
		
	}

	@Override
	public void onWeaponTierUpgrade(WeaponTierUpgradeEvent e) {
		if(Mission.activeMission == null) return;
		if(Mission.activeMission.getName().equals("Frenzy")) {
			((Missions.Frenzy) Mission.activeMission).setDividend();
		}
	}

	@Override
	public void onSwapWeapon(SwapWeaponEvent e) {
		Debug.log(this, "SWAP");
		if(Mission.activeMission == null) return;
		if(Mission.activeMission.getName().equals("Frenzy")) {
			((Missions.Frenzy) Mission.activeMission).setDividend();
		}
	}

	public boolean canStartMission() {
		return canStartMission;
	}

	public void setCanStartMission(boolean canStartMission) {
		this.canStartMission = canStartMission;
	}

}
