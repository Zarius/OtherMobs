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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.gmail.zariust.othermobs.OtherMobs;
import com.gmail.zariust.othermobs.abilities.Ability;
import com.gmail.zariust.othermobs.options.IntRange;
import com.gmail.zariust.othermobs.options.SoundEffect;
import com.gmail.zariust.othermobs.options.Comparative;
import com.gmail.zariust.othermobs.options.Time;
import com.gmail.zariust.othermobs.options.Weather;

public class MobConfig {
	private String name;
	private SpawnReason reason;
	private Map <String, Double> mobsToReplace; // map of creature names to chance values
	private Map<World, Boolean> worlds;
	private Map<String, Boolean> regions;
	private Map<Weather, Boolean> weather;
	private Map<Biome, Boolean> biomes;
	private Map<Time, Boolean> times;
	private Comparative height;
	private Comparative lightLevel;

	private Map <Ability, Double> abilities; // map of abilities and chance of success
	private List <String> attacks;
	private Comparative attackRange;
	private List<String> messages; // TODO: allow messages to be broadcast when creature spawns?
	private IntRange healthRange;

//	private List<SpecialResult> events; // TODO: allow events on spawning/dieing?
	private List<String> commands;
	private Set<SoundEffect> effects;

	private CreatureType creatureType;
	public void setCreatureType(CreatureType creatureType) {
		this.creatureType = creatureType;
	}
	public CreatureType getCreatureType() {
		return creatureType;
	}
	
	public void setHealthRange(IntRange health) {
		this.healthRange = health;
	}
	public IntRange getHealthRange() {
		return healthRange;
	}
	public int getRandomHealth() {
		if (healthRange != null) {
			return healthRange.getRandomIn(OtherMobs.rng);
		} else {
			return 1;
		}
	}

	
}
