// OtherMobs - a Bukkit plugin
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import static org.bukkit.Material.*;

import org.bukkit.TreeSpecies;
import org.bukkit.material.Step;

import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.OtherMobs;
import com.google.common.collect.ImmutableMap;

import static com.gmail.zariust.othermobs.common.CommonPlugin.enumValue;

public final class CommonMaterial {
	// Aliases definitions
    private static final Map<String, String> ALIASES;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("GLASS_PANE", "THIN_GLASS");
        aMap.put("WOODEN_SPADE", "WOOD_SPADE"); 	aMap.put("WOODEN_AXE", "WOOD_AXE");   aMap.put("WOODEN_HOE", "WOOD_HOE");   aMap.put("WOODEN_PICKAXE", "WOOD_PICKAXE"); aMap.put("WOODEN_SWORD", "WOOD_SWORD");
        aMap.put("GOLDEN_SPADE", "GOLD_SPADE"); 	aMap.put("GOLDEN_AXE", "GOLD_AXE");   aMap.put("GOLDEN_HOE", "GOLD_HOE");   aMap.put("GOLDEN_PICKAXE", "GOLD_PICKAXE"); aMap.put("GOLDEN_SWORD", "GOLD_SWORD");
        aMap.put("LEATHER_HELM", "LEATHER_HELMET");	aMap.put("IRON_HELM", "IRON_HELMET"); aMap.put("GOLD_HELM", "GOLD_HELMET"); aMap.put("DIAMOND_HELM", "DIAMOND_HELMET");
        aMap.put("WOODEN_PLATE", "WOOD_PLATE"); 	aMap.put("PLANK", "WOOD"); aMap.put("WOODEN_PLANK", "WOOD"); 
        aMap.put("WOODEN_DOOR_ITEM", "WOOD_DOOR"); 	aMap.put("WOOD_DOOR_ITEM", "WOOD_DOOR");
        aMap.put("WOOD_DOOR", "WOODEN_DOOR");
        aMap.put("STONE_PRESSUREPLATE", "STONE_PLATE"); aMap.put("WOOD_PRESSUREPLATE", "WOOD_PLATE"); aMap.put("WOODEN_PRESSUREPLATE", "WOOD_PLATE");
        aMap.put("HANDS", "AIR"); 					aMap.put("HAND", "AIR"); aMap.put("NOTHING", "AIR");
        aMap.put("TALL_GRASS", "LONG_GRASS");
        aMap.put("DANDELION", "YELLOW_FLOWER"); 	aMap.put("ROSE", "RED_ROSE"); aMap.put("RED_FLOWER", "RED_ROSE");
        aMap.put("MOSS_STONE", "MOSSY_COBBLESTONE");aMap.put("MOSSY_COBBLE", "MOSSY_COBBLESTONE");
        aMap.put("GUNPOWDER", "SULPHUR"); 			aMap.put("SULFUR", "SULPHUR");
        aMap.put("TRAPDOOR", "TRAP_DOOR");
        aMap.put("SLAB", "STEP"); 					aMap.put("DOUBLE_SLAB", "DOUBLE_STEP");
        aMap.put("CRAFTING_TABLE", "WORKBENCH");
        aMap.put("FARMLAND", "SOIL");
        aMap.put("VINES", "VINE");
        aMap.put("STONE_BRICK", "SMOOTH_BRICK");
        aMap.put("DYE", "INK_SACK");
        aMap.put("TRACKS", "RAILS"); 				aMap.put("TRACK", "RAILS"); aMap.put("RAIL", "RAILS");
        aMap.put("ZOMBIE_FLESH", "ROTTEN_FLESH");
        aMap.put("SPAWN_EGG", "MONSTER_EGG");		aMap.put("SPAWNEGG", "MONSTER_EGG");
        aMap.put("GLISTERING_MELON", "SPECKLED_MELON");
        aMap.put("LAPIS", "LAPIS_ORE");        
        ALIASES = Collections.unmodifiableMap(aMap);
    }
	
	public static Material matchMaterial(String mat) {
		// Aliases defined here override those in Material; the only example here is WOODEN_DOOR
		// You can remove it if you prefer not to break the occasional config file.
		// (I doubt many people assign drops to wooden doors, though, and including the BLOCK makes it less confusing.)

		//CommonMaterial material = enumValue(CommonMaterial.class, mat);
		mat = mat.toLowerCase().replaceAll("[ -_]", "");
		
		for (String loopAlias : ALIASES.keySet()) {
			if (mat.equalsIgnoreCase(loopAlias.toLowerCase().replaceAll("[ -_]", ""))) 
				mat = ALIASES.get(loopAlias).toLowerCase().replaceAll("[ -_]", "");
		}

		Material matchedMat = null;
		for (Material loopMat : Material.values()) {
			if (mat.equalsIgnoreCase(loopMat.name().toLowerCase().replaceAll("[ -_]", ""))) matchedMat = loopMat;
		}

		if(matchedMat == null) {
			Material defaultMat = Material.getMaterial(mat);
			if (defaultMat == null) {
				if (!(mat.equalsIgnoreCase("default"))) {
					Log.high("Error: unknown material ("+mat+").");
				}
			}
		}
		return matchedMat;
	}
	
	// Colors
	public static int getWoolColor(DyeColor color) {
		return color.getData();
	}

	public static int getDyeColor(DyeColor color) {
		return 0xF - color.getData();
	}
	
	@SuppressWarnings("incomplete-switch")
	public static Integer parseBlockOrItemData(Material mat, String state) throws IllegalArgumentException {
		if (state.equalsIgnoreCase("this")) return -1;
		switch(mat) {
		case LOG:
		case LEAVES:
		case SAPLING:
			TreeSpecies species = TreeSpecies.valueOf(state);
			if(species != null) return (int) species.getData();
			break;
		case WOOL:
			DyeColor wool = DyeColor.valueOf(state);
			if(wool != null) return getWoolColor(wool);
			break;
		case SMOOTH_BRICK:
			if (state.equalsIgnoreCase("NORMAL")) return 0;
			if (state.equalsIgnoreCase("MOSSY")) return 1;
			if (state.equalsIgnoreCase("CRACKED")) return 2;
			Material brick = Material.valueOf(state);
			if(brick == null) throw new IllegalArgumentException("Unknown material " + state);
			switch(brick) {
			case STONE: return 0;
			case COBBLESTONE: return 2;
			case MOSSY_COBBLESTONE: return 5;
			default:
				throw new IllegalArgumentException("Illegal step material " + state);
			}
		case DOUBLE_STEP:
		case STEP:
			Material step = Material.valueOf(state);
			if(step == null) throw new IllegalArgumentException("Unknown material " + state);
			switch(step) {
			case STONE: return 0;
			case COBBLESTONE: return 3;
			case SANDSTONE: return 1;
			case WOOD: return 2;
			case BRICK: return 4;
			case SMOOTH_BRICK: return 5;
			default:
				throw new IllegalArgumentException("Illegal step material " + state);
			}
		case LONG_GRASS:
			GrassSpecies grass = GrassSpecies.valueOf(state);
			if(grass != null) return (int) grass.getData();
			break;
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public static String getBlockOrItemData(Material mat, int data) {
		switch(mat) {
		case LOG:
		case LEAVES:
		case SAPLING:
			return TreeSpecies.getByData((byte)((0x3) & data)).toString(); // (0x3) & data to remove leaf decay flag
		case WOOL:
			return DyeColor.getByData((byte)data).toString();
		case SMOOTH_BRICK:
			switch(data) {
			case 0: return "NORMAL";
			case 1: return "MOSSY";
			case 2: return "CRACKED";
			}
		case DOUBLE_STEP:
		case STEP:
			Step step = new Step(mat, (byte)data);
			return step.getMaterial().toString();
		case LONG_GRASS:
			return GrassSpecies.getByData((byte)data).toString();
		}
		if(data > 0) return Integer.toString(data);
		return "";
	}
}
