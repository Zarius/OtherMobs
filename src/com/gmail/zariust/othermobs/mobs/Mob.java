// OtherMobs - a Bukkit plugin
// Copyright (C) 2012 Zarius Tularial
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.othermobs.mobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.zariust.othermobs.ConfigLoader;
import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.abilities.Ability;
import com.gmail.zariust.othermobs.abilities.Glow;
import com.gmail.zariust.othermobs.common.Verbosity;
import com.gmail.zariust.othermobs.options.Comparative;

public class Mob {
	private Integer uniqueEntityId;
	private Creature entity;
	private MobConfig config;
	
	private Map <Ability, Double> abilities; // map of abilities and chance of success
	private List <String> attacks;
	private Comparative attackRange;
	
	public Mob (MobConfig config) {
		this.config = config;
		abilities = new HashMap<Ability, Double>();
	}
	
	public void addAbility(Ability ability) {
		Log.high("adding ability "+ability.toString()+" to "+entity.toString());
		abilities.put(ability, 100.0d);
		ability.init(this);
	}
	
	public void setEntity(Entity newEntity) {
		if (newEntity instanceof Creature) {
			if (this.entity == null) {
				this.entity = (Creature)newEntity;			
				//			this.abilities.put(new Glow(this), 100.0d);

				setHealth();
				
				Log.logInfo("Entity "+newEntity.toString()+" customised...", Verbosity.HIGHEST);
				ConfigLoader.getMobs().put(newEntity.getUniqueId(), this);
			}
		}
	}
	public Creature getEntity() {
		return entity;
	}
	
	private void setHealth() {
		int rHealth = config.getRandomHealth();
		int maxHealth = this.entity.getMaxHealth();
		if (rHealth > maxHealth) {
			Log.logWarning("Mob.setHealth - "+rHealth+" is over the max health, setting to max ("+maxHealth+").", Verbosity.HIGHEST);
			rHealth = maxHealth;
		}
		if (rHealth < 1) rHealth = 1;
		this.entity.setHealth(rHealth);
	}
	
	public void setMetaData(MobConfig mobConfig) {
		// TODO Auto-generated method stub
		
		
	}
	
}
