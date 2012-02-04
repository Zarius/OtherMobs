// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
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

package com.gmail.zariust.othermobs.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.CreatureType;
import static org.bukkit.entity.CreatureType.*;

public enum CreatureGroup {
	CREATURE_HOSTILE(CREEPER, GHAST, GIANT, MONSTER, SKELETON, SLIME, SPIDER, ZOMBIE, CAVE_SPIDER, SILVERFISH),
	CREATURE_FRIENDLY(COW, CHICKEN, PIG, SHEEP, SQUID),
	CREATURE_NEUTRAL(PIG_ZOMBIE, WOLF, ENDERMAN),
	CREATURE_ANIMAL(COW, CHICKEN, PIG, SHEEP, WOLF),
	CREATURE_UNDEAD(PIG_ZOMBIE, ZOMBIE, SKELETON, ENDERMAN),
	CREATURE_BUG(SPIDER, CAVE_SPIDER, SILVERFISH),
	CREATURE_WATER(SQUID),
	// FIXME:: (Zarius) Lazy aliases - find a better way that covers both with CREATURE_ and without?  
	CREATURE_LAVASLIME(MAGMA_CUBE),
	CREATURE_MOOSHROOM(MUSHROOM_COW),
	CREATURE_SNOW_MAN(SNOWMAN),
	CREATURE_ENDERDRAGON(ENDER_DRAGON),

	// Add any new ones before this line
	CREATURE_ANY;
	private static Map<String, CreatureGroup> lookup = new HashMap<String, CreatureGroup>();
	private ArrayList<CreatureType> mob;
	
	static {
		for(CreatureType mob : CreatureType.values()) {
			CREATURE_ANY.mob.add(mob);
		}
		for(CreatureGroup group : values())
			lookup.put(group.name(), group);
	}
	
	private void add(List<CreatureType> materials) {
		mob.addAll(materials);
	}
	
	private CreatureGroup(CreatureType... materials) {
		this();
		add(Arrays.asList(materials));
	}
	
	private CreatureGroup(CreatureGroup... merge) {
		this();
		for(CreatureGroup group : merge)
			add(group.mob);
	}
	
	private CreatureGroup(List<CreatureType> materials, CreatureGroup... merge) {
		this(merge);
		add(materials);
	}
	
	private CreatureGroup() {
		mob = new ArrayList<CreatureType>();
	}
	
	@SuppressWarnings("unchecked")
	public List<CreatureType> creatures() {
		return (List<CreatureType>) mob.clone();
	}

	public static CreatureGroup get(String string) {
		return lookup.get(string.toUpperCase());
	}

	public static Set<String> all() {
		return lookup.keySet();
	}

	public static boolean isValid(String string) {
		return lookup.containsKey(string);
	}

	public boolean contains(CreatureType material) {
		return mob.contains(material);
	}
}
