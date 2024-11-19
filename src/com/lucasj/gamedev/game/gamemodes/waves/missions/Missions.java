package com.lucasj.gamedev.game.gamemodes.waves.missions;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.misc.Debug;

public class Missions {

	public static class Slayer extends Mission {
	
		private int baseAmount = 5;
		
		private int amountKilled = 0;
	
		public Slayer(Game game) {
			super(game, "Slayer", "{LIGHT_GRAY}Kill {RED}{X} {LIGHT_GRAY}Enemies In " + 30 + " Seconds.", "$1500", 30);
		}
		
		public void update(double deltaTime) {
			this.descriptionToDisplay = this.description.replace("{X}", String.valueOf(baseAmount - amountKilled));

			super.update(deltaTime);
		}
	
		public boolean isAccomplished() {
			
			if(amountKilled >= baseAmount) return true;
			
			return false;
		}
	
		@Override
		public void reward() {
			game.getPlayer().addMoney(1500);
		}
		
		public void increment() {
			this.amountKilled += 1;
		}

	}
	
	public static class Businessman extends Mission {
		
		private int baseAmount = 10;
		
		private int amountCollected = 0;
	
		public Businessman(Game game) {
			super(game, "Businessman", "{LIGHT_GRAY}Collect {GOLD}{X} {LIGHT_GRAY}Coins In " + 30 + " Seconds.", "$2500", 30);
		}
		
		public void update(double deltaTime) {
			this.descriptionToDisplay = this.description.replace("{X}", String.valueOf(baseAmount - amountCollected));

			super.update(deltaTime);
		}
	
		public boolean isAccomplished() {
			
			if(amountCollected >= baseAmount) return true;
			
			return false;
		}
	
		@Override
		public void reward() {
			game.getPlayer().addMoney(2500);
		}
		
		public void increment() {
			this.amountCollected += 1;
		}

	}
	
	public static class Invulnerable extends Mission {
		
		private boolean damaged = false;
	
		public Invulnerable(Game game) {
			super(game, "Invulnerable", "{LIGHT_GRAY}Do Not Take Damage for {GOLD}" + 30 + " {LIGHT_GRAY}Seconds.", "$5000", 30);
		}
		
		public void update(double deltaTime) {
			this.descriptionToDisplay = this.description;
			
			super.update(deltaTime);
		}
	
		public boolean isAccomplished() {
			return this.getBroadcast().finished;
		}
	
		@Override
		public void reward() {
			game.getPlayer().addMoney(5000);
		}
		
		public void fail() {
			this.damaged = true;
			Mission.activeMission = null;
		}

	}
}
