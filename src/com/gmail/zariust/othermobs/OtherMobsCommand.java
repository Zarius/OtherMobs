// OtherMobs - a Bukkit plugin
// Copyright (C) 2012 Zarius Tularial, Celtic Minstrel
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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.player.EntitySkinType;

import com.gmail.zariust.othermobs.abilities.Faction;
import com.gmail.zariust.othermobs.abilities.Flaming;
import com.gmail.zariust.othermobs.abilities.Glow;
import com.gmail.zariust.othermobs.common.CommonEntity;
import com.gmail.zariust.othermobs.data.Data;
import com.gmail.zariust.othermobs.mobs.Mob;
import com.gmail.zariust.othermobs.mobs.MobConfig;
import com.gmail.zariust.othermobs.options.IntRange;

public class OtherMobsCommand implements CommandExecutor {
	private static String commandName = "om";
	
	private enum OBCommand {
		ID("id", "i"),
		RELOAD("reload", "r"),
		//SHOW("show", "s"),
		PROFILE("profile", "p"),
		SPAWN("spawn", "s"),
		SPAWNTEST("spawnfire", "w");
		private String cmdName;
		private String cmdShort;

		private OBCommand(String name, String abbr) {
			cmdName = name;
			cmdShort = abbr;
		}
		
		public static OBCommand match(String label, String firstArg) {
			boolean arg = false;
			if(label.equalsIgnoreCase(commandName)) arg = true;
			for(OBCommand cmd : values()) {
				if(arg && firstArg.equalsIgnoreCase(cmd.cmdName)) return cmd;
				else if(label.equalsIgnoreCase(commandName + cmd.cmdShort) || label.equalsIgnoreCase(commandName + cmd.cmdName))
					return cmd;
			}
			return null;
		}

		public String[] trim(String[] args, StringBuffer name) {
			if(args.length == 0) return args;
			if(!args[0].equalsIgnoreCase(cmdName)) return args;
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			if(name != null) name.append(" " + args[0]);
			return newArgs;
		}
	}
	private OtherMobs othermobs;
	
	public OtherMobsCommand(OtherMobs plugin) {
		othermobs = plugin;
	}
	
	private String getName(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) return "CONSOLE";
		else if(sender instanceof Player) return ((Player) sender).getName();
		else return "UNKNOWN";
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		OBCommand cmd = OBCommand.match(label, args.length >= 1 ? args[0] : "");
		if(cmd == null) return false;
		StringBuffer cmdName = new StringBuffer(label);
		args = cmd.trim(args, cmdName);
		switch(cmd) {
		case ID:
			if(othermobs.hasPermission(sender, "othermobs.admin.id")) {
				if (sender instanceof Player) {
					Player player = (Player)sender;
					ItemStack playerItem = player.getItemInHand();
					sender.sendMessage("OtherMobs ID: item in hand is "+playerItem.toString()+" id: "+playerItem.getTypeId()+"@"+playerItem.getDurability());			
				}
			}
			break;
		case RELOAD:
			if(othermobs.hasPermission(sender, "othermobs.admin.reloadconfig")) {
				othermobs.config.load();
				sender.sendMessage("OtherMobs config reloaded.");
				Log.normal("Config reloaded by " + getName(sender) + ".");
			} else sender.sendMessage("You don't have permission to reload the config.");
			break;
		case SPAWNTEST:
			if (sender instanceof Player) {
				Player player = (Player)sender;
				MobConfig mobConfig = new MobConfig();
				CreatureType creatureType;
				if (args.length > 0) {
					creatureType = CreatureType.fromName(args[0]);		
				} else {
					creatureType = CreatureType.SHEEP;
				}
				mobConfig.setCreatureType(creatureType);
				mobConfig.setHealthRange(new IntRange(1,8));
				Mob newMob = OtherMobsAPI.spawnMob(mobConfig, player.getTargetBlock(null, 100).getLocation());
				newMob.getEntity().setFireTicks(20000);  // on fire
				newMob.getEntity().setNoDamageTicks(400); // immune for 20 seconds

			}
			break;
		case SPAWN:
			if (sender instanceof Player) {
				Player player = (Player)sender;
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
					Mob newMob = OtherMobsAPI.spawnMob(mobConfig, player.getTargetBlock(null, 100).getLocation().add(0, 1, 0));
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

		}
		return true;
	}
	
}
