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

package com.gmail.zariust.othermobs.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.gmail.zariust.othermobs.common.Verbosity.*;

import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.OtherMobs;
import com.gmail.zariust.othermobs.ConfigLoader;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.ConfigurationNode;

/**
 * Represents an action that can be taken to lead to a drop.
 */
public final class Action implements Comparable<Action> {
	/**
	 * The basic action; breaking a block, or killing a creature.
	 */
	public final static Action BREAK = new Action("BREAK");
	/**
	 * Left clicking on the target.
	 */
	public final static Action LEFT_CLICK = new Action("LEFT_CLICK");
	/**
	 * Right clicking on the target.
	 */
	public final static Action RIGHT_CLICK = new Action("RIGHT_CLICK");
	/**
	 * The action of natural leaf decay.
	 */
	public final static Action LEAF_DECAY = new Action("LEAF_DECAY");
	// LinkedHashMap because I want to preserve order
	private static Map<String,Action> actions = new LinkedHashMap<String,Action>();
	private static Map<String,Plugin> owners = new HashMap<String,Plugin>();
	private static int nextOrdinal = 0;
	private int ordinal;
	private String name;
	
	static {
		actions.put("BREAK", BREAK);
		actions.put("LEFT_CLICK", LEFT_CLICK);
		actions.put("RIGHT_CLICK", RIGHT_CLICK);
		actions.put("LEAF_DECAY", LEAF_DECAY);
		owners.put("BREAK", OtherMobs.plugin);
		owners.put("LEFT_CLICK", OtherMobs.plugin);
		owners.put("RIGHT_CLICK", OtherMobs.plugin);
		owners.put("LEAF_DECAY", OtherMobs.plugin);
	}
	
	private Action(String tag) {
		name = tag;
		ordinal = nextOrdinal;
		nextOrdinal++;
	}
	
	/**
	 * Convert an interact action into a drop action.
	 * @param action The interact action.
	 * @return The drop action, or null if none applies.
	 */
	public static Action fromInteract(org.bukkit.event.block.Action action) {
		switch(action) {
		case LEFT_CLICK_AIR:
		case LEFT_CLICK_BLOCK:
			return LEFT_CLICK;
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			return RIGHT_CLICK;
		default:
			return null;
		}
	}
	
	/**
	 * Register a new action to your plugin.
	 * @param plugin Your plugin.
	 * @param tag The action tag name. This can be used in the config file or to fetch it again later.
	 */
	public static void register(Plugin plugin, String tag) {
		if(plugin == null || plugin instanceof OtherMobs)
			throw new IllegalArgumentException("Use your own plugin for registering an action!");
		actions.put(tag, new Action(tag));
		owners.put(tag, plugin);
	}
	
	/**
	 * Unregister a previously registered action.
	 * @param plugin The plugin that registered the action (preferably your plugin).
	 * @param tag The action tag name.
	 */
	public static void unregister(Plugin plugin, String tag) {
		Plugin check = owners.get(tag);
		if(!check.getClass().equals(plugin.getClass()))
			throw new IllegalArgumentException("You didn't register that action!");
		owners.remove(tag);
		actions.remove(tag);
	}

	public static List<Action> parseFrom(ConfigurationNode dropNode, List<Action> def) {
		List<String> chosenActions = ConfigLoader.getMaybeList(dropNode, "action", "actions");
		List<Action> result = new ArrayList<Action>();
		for(String action : chosenActions) {
			Action act = actions.get(action.toUpperCase());
			if(act != null) result.add(act);
			else Log.logWarning("Invalid action " + action + " (known actions: "+getValidActions().toString()+")",NORMAL);
		}
		if(result.isEmpty()) {
			if(def == null) {
				def = new ArrayList<Action>();
				def.add(BREAK);
			}
			return def;
		}
		return result;
	}

	@Override
	public int compareTo(Action other) {
		return Integer.valueOf(ordinal).compareTo(other.ordinal);
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Action)) return false;
		return ordinal == ((Action)other).ordinal;
	}

	@Override
	public int hashCode() {
		return ordinal;
	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Return a list of all valid actions.
	 * @return All actions.
	 */
	public static Action[] values() {
		return actions.values().toArray(new Action[0]);
	}
	
	/**
	 * Return a list of all valid action names.
	 * @return All actions.
	 */
	public static Set<String> getValidActions() {
		return actions.keySet();
	}
	
	/**
	 * Get an action by name.
	 * @param key The action tag name.
	 * @return The action, or null if it does not exist.
	 */
	public static Action valueOf(String key) {
		return actions.get(key);
	}
}
