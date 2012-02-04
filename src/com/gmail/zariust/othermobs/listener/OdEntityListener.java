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

package com.gmail.zariust.othermobs.listener;

import org.bukkit.block.Biome;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;


import com.gmail.zariust.othermobs.ConfigLoader;
import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.OtherMobs;
import com.gmail.zariust.othermobs.common.Verbosity;
import com.gmail.zariust.othermobs.mobs.Mob;
import com.gmail.zariust.othermobs.mobs.MobConfig;
import com.gmail.zariust.othermobs.options.IntRange;

public class OdEntityListener extends EntityListener
{	
	private OtherMobs parent;
	
	public OdEntityListener(OtherMobs instance)
	{
		parent = instance;
	}
		
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
	
	}

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (!ConfigLoader.isPluginEnabled()) return;
		
		Log.highest("Creature spawned: "+event.getCreatureType() + " in "+event.getLocation().getBlock().getBiome() + " because "+event.getSpawnReason().toString());

		// TODO: Hardcoded spawn replacement with BLAZE in the desert - to be removed when config reading is implemented
		if (event.getSpawnReason() == SpawnReason.NATURAL && event.getLocation().getBlock().getBiome() == Biome.DESERT && event.getLocation().getWorld().getName().equalsIgnoreCase("WORLD")) {
			event.setCancelled(true);
			
			MobConfig config = new MobConfig();
			config.setHealthRange(new IntRange(1,22));
			Mob mob = new Mob(config);
			mob.setEntity(event.getLocation().getWorld().spawnCreature(event.getLocation(), CreatureType.BLAZE));
			event.getLocation().getWorld().spawnCreature(event.getLocation(), CreatureType.BLAZE);
		}
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
	
	}

	
	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
	
	}
}

