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

package com.gmail.zariust.othermobs;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;

import com.gmail.zariust.othermobs.abilities.Faction;
import com.gmail.zariust.othermobs.abilities.Flaming;
import com.gmail.zariust.othermobs.abilities.Glow;
import com.gmail.zariust.othermobs.common.CommonEntity;
import com.gmail.zariust.othermobs.common.Verbosity;
import com.gmail.zariust.othermobs.data.Data;
import com.gmail.zariust.othermobs.mobs.Mob;
import com.gmail.zariust.othermobs.mobs.MobConfig;
import com.gmail.zariust.othermobs.options.IntRange;

public class OtherMobsAPI {

	public static void spawnTest(String input, Player player) {
		String[] args = input.split(" ");
		MobConfig mobConfig = new MobConfig();
		CreatureType creatureType = CreatureType.SHEEP;
		Data creatureData = null;
		if (args.length > 0) {
			Log.high("spawning: "+ args[0]);
			String[] firstArg = args[0].split("@");
			String argName = firstArg[0].toLowerCase().replaceAll("[ -_]", "");

			// Aliases
			argName = argName.replace("mooshroom", "mushroomcow");

			// Creature type
			for (CreatureType creature : CreatureType.values())
			{
				if (argName.equalsIgnoreCase(creature.name().toLowerCase().replaceAll("[ -_]", ""))) creatureType = creature;
			}

			// Data
			if (firstArg.length > 1) {
				String dataValue = firstArg[1];
				creatureData = CommonEntity.getCreatureData(creatureType, dataValue);
			}

		}

		// Quantity
		int quantity = 1;
		for (String arg : args) {
			try {
				quantity = Integer.parseInt(arg);
			} catch (NumberFormatException ex) {
				// don't need to do anything here, default quantity is fine.
			}
		}

		for (int i=0;i<quantity;i++) {
			mobConfig.setCreatureType(creatureType);
			mobConfig.setHealthRange(new IntRange(1,8));
			Mob newMob = null;
			if (player == null) {
				Log.logWarning("spawnMob: player is null, no location to spawn available.", Verbosity.LOW);
				return;
			} else {
				newMob = OtherMobsAPI.spawnMob(mobConfig, player.getTargetBlock(null, 100).getLocation().add(0, 1, 0));
			}
				
			if (creatureData != null) {
				Log.normal("Data: "+creatureData.getData());
				creatureData.setOn(newMob.getEntity(), player);
			}

			for (String arg : args) {
				if (arg.length() > 2) {
					Log.highest("arg: "+ arg + "-"+ arg.substring(0,2) +"-"+ arg.substring(2));
					if (arg.substring(0,2).equalsIgnoreCase("a:")) {
						if (arg.substring(2).equalsIgnoreCase("flaming")) {
							Flaming flaming = new Flaming();
							newMob.addAbility(flaming);								
						} else if (arg.substring(2).equalsIgnoreCase("faction")) {
							Faction faction = new Faction();
							newMob.addAbility(faction);								
						} else if (arg.substring(2).equalsIgnoreCase("glow")) {
							Glow glow = new Glow();
							newMob.addAbility(glow);
						}
					} else if (arg.substring(0,2).equalsIgnoreCase("s:") && ConfigLoader.isSpoutEnabled()) {
						if (arg.substring(2).equalsIgnoreCase("ezshirt")) {
							if (creatureType.equals(CreatureType.ZOMBIE)) {
								//Spout.getServer().setEntitySkin(newMob.getEntity(), "http://papercraft.robhack.com/various_finds/Mine/texture_templates/mob/sheep.png", EntitySkinType.DEFAULT);
								Spout.getServer().setEntitySkin(newMob.getEntity(), "http://s3.amazonaws.com/squirt/i4e63f13debf998117424725218911234111341190.png", EntitySkinType.DEFAULT);
							}
						} else if (arg.substring(2).equalsIgnoreCase("storm")) {
							if (creatureType.equals(CreatureType.ZOMBIE)) {
								Spout.getServer().setEntitySkin(newMob.getEntity(), "http://www.minecraftskins.com/uploaded_skins/skin_12012711014059229.png", EntitySkinType.DEFAULT);

							}
						} else if (arg.substring(2).equalsIgnoreCase("patternsheep")) {
							if (creatureType.equals(CreatureType.SHEEP)) {
								Spout.getServer().setEntitySkin(newMob.getEntity(), "http://papercraft.robhack.com/various_finds/Mine/texture_templates/mob/sheep.png", EntitySkinType.DEFAULT);
								//Spout.getServer().setEntitySkin(newMob.getEntity(), "http://s3.amazonaws.com/squirt/i4e63f13debf998117424725218911234111341190.png", EntitySkinType.DEFAULT);
							}
						}
					}
				}
			}
		}
	}
	
	public static Mob spawnMob(MobConfig mobConfig, Location location) { // spawn a custom mob
		if (location == null) {
			Log.logWarning("spawnMob: location is null.", Verbosity.LOW);
			return null;
		}
		if (mobConfig == null) {
			Log.logWarning("spawnMob: mobConfig is null.", Verbosity.LOW);
			return null;
		}
		Mob mob = new Mob(mobConfig);
		LivingEntity livingEntity = location.getWorld().spawnCreature(location, mobConfig.getCreatureType());
		mob.setEntity(livingEntity);
		return (mob);
	}
	
	public static void addAbility() { // add a new ability that could be used in a mob config
		// not sure how to do this - need to add to a list of abilities to be checked?
	}
	
	public static void addAttack() { // add a new attack that could be used in a mob config??
		
	}
	
	public static void addMobConfig(String name, MobConfig mob) { // add a new mob config
		
	}
	
	public static void getMobConfig(String name) { // get an existing mob config, returns MobConfig
		
	}
	
	public static void setMobConfig(String name, MobConfig mob) { // replace an existing config
		
	}
	
	public static Mob getMob(UUID entityUniqueId) { // returns the Mob (metadata) class for this entity - null if none.
		return ConfigLoader.getMobs().get(entityUniqueId);
	}
}
