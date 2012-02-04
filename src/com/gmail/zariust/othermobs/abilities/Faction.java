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

package com.gmail.zariust.othermobs.abilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.OtherMobs;
import com.gmail.zariust.othermobs.mobs.Mob;


import net.minecraft.server.EnumSkyBlock;
import net.minecraft.server.WorldServer;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;

public class Faction extends Ability {
	private Map<CreatureType, Double> factionMap = new HashMap<CreatureType, Double>();
	private Block glowBlock;
	
	public Faction() {}

	@Override
	void initLocal() {
		// TODO Auto-generated method stub
		factionMap.put(CreatureType.SHEEP, 100.0d);
	}

	@Override
	void updateOnAttack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void updateOnDeath() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void updateOnHurt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void updateOnSpawn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void updateOnTarget() {
		// TODO Auto-generated method stub
		
	}
	@Override
	void updateOnMove() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void run() {
		if (mob.getEntity().isDead()) {
			Bukkit.getServer().getScheduler().cancelTask(taskId);
			Log.high("Entity ("+mob.getEntity().toString()+") dead - task "+taskId+" cancelled.");	
			return;
		}

		//Log.logInfo("Checking faction for "+mob.getEntity().toString());
		
		// TODO: make them pick on the closest?
		Creature creature = mob.getEntity();
		for (Entity entity : creature.getNearbyEntities(40, 40, 40)) {
			if (entity instanceof Creature) {
				Creature closeEnt = (Creature)entity;
			//	Log.logInfo(creature.toString() +" can see "+closeEnt.toString()+", it's target is "+creature.getTarget());
				Integer retargetChance = OtherMobs.rng.nextInt(1000);
				if (creature.getTarget() == null || (retargetChance > 995)) {
					if (OtherMobs.rng.nextInt(100) > 50) {
						Log.high("oldtarget: "+creature.getTarget()+" * "+creature.toString() +" targeting "+closeEnt.toString()+" retarget chance: "+retargetChance);
						creature.setTarget(closeEnt);
					}
				}
			}
		}
	}

}

