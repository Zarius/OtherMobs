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

package com.gmail.zariust.othermobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.Event.Priority;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.yaml.snakeyaml.scanner.ScannerException;


import static com.gmail.zariust.othermobs.common.CommonPlugin.*;
import static com.gmail.zariust.othermobs.common.Verbosity.*;

import com.gmail.zariust.othermobs.common.CommonPlugin;
import com.gmail.zariust.othermobs.common.CreatureGroup;
import com.gmail.zariust.othermobs.common.MaterialGroup;
import com.gmail.zariust.othermobs.common.Verbosity;
import com.gmail.zariust.othermobs.data.Data;
import com.gmail.zariust.othermobs.data.SimpleData;
import com.gmail.zariust.othermobs.mobs.Mob;
import com.gmail.zariust.othermobs.mobs.MobConfig;
import com.gmail.zariust.othermobs.options.*;

public class ConfigLoader {

	private OtherMobs parent;
	private static boolean pluginEnabled;
	private static boolean profiling;
	private static Priority priority;
	private static Verbosity verbosity;
	private static Map<UUID, Mob> mobs = new HashMap<UUID, Mob>();
	private static List<MobConfig> mobConf = new ArrayList<MobConfig>();
	private static boolean spoutEnabled = false;

	// Track loaded files so we don't get into an infinite loop
	Set<String> loadedCustomFiles = new HashSet<String>();
	
	// Defaults
	private Map<World, Boolean> defaultWorlds;
	private Map<String, Boolean> defaultRegions;
	private Map<Weather, Boolean> defaultWeather;
	private Map<Biome, Boolean> defaultBiomes;
	private Map<Time, Boolean> defaultTime;
	private Map<String, Boolean> defaultPermissionGroups;
	private Map<String, Boolean> defaultPermissions;
	private Comparative defaultHeight;
	private Comparative defaultAttackRange;
	private Comparative defaultLightLevel;
	private List<Action> defaultAction;
	
	// A place for special events to stash options
	private ConfigurationNode events;

	public ConfigLoader(OtherMobs instance) {
		instance.getDataFolder().mkdirs(); // create the config folder if it doesn't exist
		
		parent = instance;
		
		setPluginEnabled(true);
		
		setVerbosity(NORMAL);
		setPriority(Priority.Lowest);
	}
	
	private void clearDefaults() {
		defaultWorlds = null;
		defaultRegions = null;
		defaultWeather = null;
		defaultBiomes = null;
		defaultTime = null;
		defaultPermissionGroups = null;
		defaultPermissions = null;
		defaultHeight = null;
		defaultAttackRange = null;
		defaultLightLevel = null;
	}

	// load 
	public void load() {
		try {
		//	blocksHash.clear(); // clear here to avoid issues on /obr reloading
			loadedCustomFiles.clear();
			clearDefaults();

			String mainDropsName = loadSettingsFile();
			loadCustomFile(mainDropsName);
		} catch(ScannerException e) {
			Log.logWarning("There was an error in your config file which has forced OtherMobs to abort loading!");
			Log.logWarning("The error was:\n" + e.toString());
			Log.logInfo("You can fix the error and reload with /omr.");
		}
	}
	
	public String loadSettingsFile()
	{		
		String filename = "othermobs-settings.yml";
		
		File global = new File(parent.getDataFolder(), filename);
		Configuration globalConfig = new Configuration(global);
		
		// Make sure config file exists (even for reloads - it's possible this did not create successfully or was deleted before reload) 
		if (!global.exists()) {
			try {
				global.createNewFile();
				Log.logInfo("Created an empty file " + parent.getDataFolder() +"/"+filename+", please edit it!");
				globalConfig.setProperty("verbosity", "normal");
				globalConfig.setProperty("priority", "high");
				globalConfig.setProperty("usepermissions", true);
				globalConfig.save();
			} catch (IOException ex){
				Log.logWarning(parent.getDescription().getName() + ": could not generate "+filename+". Are the file permissions OK?");
			}
		}

		globalConfig.load();
		String configKeys = globalConfig.getKeys().toString();
		
		// Load in the values from the configuration file
		setVerbosity(getConfigVerbosity(globalConfig));
		setPriority(getConfigPriority(globalConfig));
		String mainDropsName = globalConfig.getString("rootconfig", "othermobs-mobs.yml");

		Log.logInfo("Loaded global config ("+global+"), keys found: "+configKeys + " (verbosity="+getVerbosity()+")", Verbosity.HIGH);

		return mainDropsName;
	}

	private void loadCustomFile(String filename) {
		// Check for infinite include loops
		if(loadedCustomFiles.contains(filename)) {
			Log.logWarning("Infinite include loop detected at " + filename);
			return;
		} else loadedCustomFiles.add(filename);
		
		Log.normal("Loading file: "+filename);
		
		File yml = new File(parent.getDataFolder(), filename);
		Configuration config = new Configuration(yml);
		
		// Make sure config file exists (even for reloads - it's possible this did not create successfully or was deleted before reload) 
		if (!yml.exists())
		{
			try {
				yml.createNewFile();
				Log.logInfo("Created an empty file " + parent.getDataFolder() +"/"+filename+", please edit it!");
				config.setProperty("othermobs", null);
				config.setProperty("include-files", null);
				config.setProperty("defaults", null);
				config.setProperty("aliases", null);
				config.setProperty("configversion", 3);
				config.save();
			} catch (IOException ex){
				Log.logWarning(parent.getDescription().getName() + ": could not generate "+filename+". Are the file permissions OK?");
			}
			// Nothing to load in this case, so exit now
			return;
		}
		
		config.load();
		
		// Warn if wrong version
		int configVersion = config.getInt("configversion", 3);
		if(configVersion < 3)
			Log.logWarning("config file appears to be in older format; some things may not work");
		else if(configVersion > 3)
			Log.logWarning("config file appears to be in newer format; some things may not work");
		
		loadDefaults(config.getNode("defaults"));
					
		// Load the drops
		List<String> blocks = config.getKeys("othermobs");
		ConfigurationNode node = config.getNode("othermobs");
		if (node != null) {
		    for(String blockName : blocks) {
				List<ConfigurationNode> drops = node.getNodeList(blockName, null);
		    	blockName = blockName.replaceAll("[ -]", "_");
		     //   loadBlockDrops(drops, blockName);
		    }
		}
		
		// Load the include files
		List<String> includeFiles = config.getStringList("include-files", null);
		for(String include : includeFiles) loadCustomFile(include);
	}

	private void loadDefaults(ConfigurationNode defaults) {
		// Load defaults; each of these functions returns null if the value isn't found
		// Check for null - it's possible that the defaults key doesn't exist or is empty
		defaultAction = Collections.singletonList(Action.BREAK);
		if (defaults != null) {
			Log.logInfo("Loading defaults...",HIGH);
			defaultWorlds = parseWorldsFrom(defaults, null);
			defaultRegions = parseRegionsFrom(defaults, null);
			defaultWeather = Weather.parseFrom(defaults, null);
			defaultBiomes = parseBiomesFrom(defaults, null);
			defaultTime = Time.parseFrom(defaults, null);
			defaultPermissionGroups = parseGroupsFrom(defaults, null);
			defaultPermissions = parsePermissionsFrom(defaults, null);
			defaultHeight = Comparative.parseFrom(defaults, "height", null);
			defaultAttackRange = Comparative.parseFrom(defaults, "attackrange", null);
			defaultLightLevel = Comparative.parseFrom(defaults, "lightlevel", null);
			defaultAction = Action.parseFrom(defaults, defaultAction);
		} else Log.logInfo("No defaults set.",HIGHEST);

	}
	
	/*
	private void loadBlockDrops(List<ConfigurationNode> drops, String blockName) {
		for(ConfigurationNode dropNode : drops) {
			//boolean isGroup = dropNode.getKeys().contains("dropgroup");
			List<Action> actions = Action.parseFrom(dropNode, defaultAction);
			if(actions.isEmpty()) {
				// FIXME: Find a way to say which action was invalid
				Log.logWarning("No recognized action for block " + blockName + "; skipping (known actions: "+Action.getValidActions().toString()+")",NORMAL);
				continue;
			}

		}
	}*/
/*
	private MobConfig loadDrop(ConfigurationNode dropNode, Action action, boolean isGroup) {
		MobConfig mobconf = new MobConfig();
		
//		CustomDrop drop = new SimpleDrop(target, action);
		loadConditions(dropNode, mobconf);
//		if(isGroup) loadDropGroup(dropNode,(GroupDropEvent) drop, target, action);
		loadSimpleDrop(dropNode, mobconf);
		return mobconf;
	}

	private void loadConditions(ConfigurationNode node, MobConfig mobConfig) {
		// Read faces
		mobConfig.setBlockFace(parseFacesFrom(node));
		
		// Now read the stuff that might have a default; if null is returned, use the default
		mobConfig.setWorlds(parseWorldsFrom(node, defaultWorlds));
		mobConfig.setRegions(parseRegionsFrom(node, defaultRegions));
		mobConfig.setWeather(Weather.parseFrom(node, defaultWeather));
		mobConfig.setBiome(parseBiomesFrom(node, defaultBiomes));
		mobConfig.setTime(Time.parseFrom(node, defaultTime));
	//	mobConfig.setGroups(parseGroupsFrom(node, defaultPermissionGroups));
	//	mobConfig.setPermissions(parsePermissionsFrom(node, defaultPermissions));
		mobConfig.setHeight(Comparative.parseFrom(node, "height", defaultHeight));
		mobConfig.setAttackRange(Comparative.parseFrom(node, "attackrange", defaultAttackRange));
		mobConfig.setLightLevel(Comparative.parseFrom(node, "lightlevel", defaultLightLevel));
	//	mobConfig.setFlags(Flag.parseFrom(node));
		
		// Read chance, delay, etc
		mobConfig.setChance(parseChanceFrom(node, "chance"));
	//	Object exclusive = node.getProperty("exclusive");
	//	if(exclusive != null) mobConfig.setExclusiveKey(exclusive.toString());
	//	mobConfig.setDelay(IntRange.parse(node.getString("delay", "0")));
	}

	
	private void loadSimpleDrop(ConfigurationNode node, SimpleDrop drop) {
		// Read drop
		boolean deny = false;
		String dropStr = node.getString("drop", "DEFAULT");
		dropStr = dropStr.replaceAll("[ -]", "_");
		if(dropStr.equals("DENY")) {
			deny = true;
			drop.setDropped(new ItemDrop(Material.AIR));
		} else drop.setDropped(DropType.parseFrom(node));
			
		String quantityStr = node.getString("quantity");
		if(quantityStr == null) drop.setQuantity(1);
		else drop.setQuantity(DoubleRange.parse(quantityStr));
		// Damage
		drop.setAttackerDamage(IntRange.parse(node.getString("damageattacker", "0")));
		drop.setToolDamage(ToolDamage.parseFrom(node));
		// Spread chance
		Object spread = node.getProperty("dropspread");
		if(spread instanceof Boolean) drop.setDropSpread((Boolean) spread);
		else if(spread instanceof Number) drop.setDropSpread(parseChanceFrom(node, "dropspread"));
		else drop.setDropSpread(true);
		// Random location multiplier
		drop.setRandomLocMult(parseLocationFrom(node, "randomise", 0, 0, 0));
		// Commands, messages, sound effects
		drop.setCommands(getMaybeList(node, "command", "commands"));
		drop.setMessages(getMaybeList(node, "message", "messages"));
		drop.setEffects(SoundEffect.parseFrom(node));
	}
*/
	private Map<World, Boolean> parseWorldsFrom(ConfigurationNode node, Map<World, Boolean> def) {
		List<String> worlds = getMaybeList(node, "world", "worlds");
		List<String> worldsExcept = getMaybeList(node, "worldexcept", "worldsexcept");
		if(worlds.isEmpty() && worldsExcept.isEmpty()) return def;
		Map<World, Boolean> result = new HashMap<World,Boolean>();
		result.put(null, containsAll(worlds));
		for(String name : worlds) {
			World world = Bukkit.getServer().getWorld(name);
			if(world == null && name.startsWith("-")) {
				result.put(null, true);
				world = Bukkit.getServer().getWorld(name.substring(1));
				if(world == null) {
					Log.logWarning("Invalid world " + name + "; skipping...");
					continue;
				}
				result.put(world, false);
			} else if (world == null) {
				if (name.equalsIgnoreCase("ALL") || name.equalsIgnoreCase("ANY")) {
					result.put(null, true);
				} else {
					Log.logWarning("Invalid world " + name + "; skipping...");
					continue;
				}
			} else result.put(world, true);
		}
		for(String name : worldsExcept) {
			World world = Bukkit.getServer().getWorld(name);
			if(world == null) {
				Log.logWarning("Invalid world exception " + name + "; skipping...");
				continue;
			}
			result.put(null, true);
			result.put(world, false);
		}
		return result;
	}

	// TODO: refactor parseWorldsFrom, Regions & Biomes as they are all very similar - (beware - fragile, breaks easy)
	private Map<String, Boolean> parseRegionsFrom(ConfigurationNode node, Map<String, Boolean> def) {
		List<String> regions = getMaybeList(node, "region", "regions");
		List<String> regionsExcept = getMaybeList(node, "regionexcept", "regionsexcept");
		if(regions.isEmpty() && regionsExcept.isEmpty()) return def;
		Map<String, Boolean> result = new HashMap<String,Boolean>();
		for(String name : regions) {
			if(name.startsWith("-")) {
				result.put(name, false);  // deliberately including the "-" sign
			} else result.put(name, true);
		}
		for(String name : regionsExcept) {
			result.put(name, false);
		}
		if(result.isEmpty()) return null;
		return result;
	}

	private Map<Biome, Boolean> parseBiomesFrom(ConfigurationNode node, Map<Biome, Boolean> def) {
		List<String> biomes = getMaybeList(node, "biome", "biomes");
		if(biomes.isEmpty()) return def;
		HashMap<Biome, Boolean> result = new HashMap<Biome,Boolean>();
		result.put(null, containsAll(biomes));
		for(String name : biomes) {
			Biome biome = enumValue(Biome.class, name);
			if(biome != null) result.put(biome, true);
			else if(name.startsWith("-")) {
				result.put(null, true);
				biome = enumValue(Biome.class, name.substring(1));
				if(biome == null) {
					Log.logWarning("Invalid biome " + name + "; skipping...");
					continue;
				}
				result.put(biome, false);
			}
		}
		return result;
	}

	private Map<String, Boolean> parseGroupsFrom(ConfigurationNode node, Map<String, Boolean> def) {
		List<String> groups = getMaybeList(node, "permissiongroup", "permissiongroups");
		List<String> groupsExcept = getMaybeList(node, "permissiongroupexcept", "permissiongroupsexcept");
		if(groups.isEmpty() && groupsExcept.isEmpty()) return def;
		Map<String, Boolean> result = new HashMap<String,Boolean>();
		for(String name : groups) {
			if(name.startsWith("-")) {
				result.put(name, false);
			} else result.put(name, true);
		}
		for(String name : groupsExcept) {
			result.put(name, false);
		}
		if(result.isEmpty()) return null;
		return result;
	}

	private Map<String, Boolean> parsePermissionsFrom(ConfigurationNode node, Map<String, Boolean> def) {
		List<String> permissions = getMaybeList(node, "permission", "permissions");
		List<String> permissionsExcept = getMaybeList(node, "permissionexcept", "permissionsexcept");
		if(permissions.isEmpty() && permissionsExcept.isEmpty()) return def;
		Map<String, Boolean> result = new HashMap<String,Boolean>();
		for(String name : permissions) {
			if(name.startsWith("-")) {
				result.put(name, false);
			} else result.put(name, true);
		}
		for(String name : permissionsExcept) {
			result.put(name, false);
		}
		if(result.isEmpty()) return null;
		return result;
	}

	private Map<BlockFace, Boolean> parseFacesFrom(ConfigurationNode node) {
		List<String> faces = getMaybeList(node, "face", "faces");
		if(faces.isEmpty()) return null;
		HashMap<BlockFace, Boolean> result = new HashMap<BlockFace,Boolean>();
		result.put(null, containsAll(faces));
		for(String name : faces) {
			BlockFace storm = enumValue(BlockFace.class, name);
			if(storm == null && name.startsWith("-")) {
				result.put(null, true);
				storm = enumValue(BlockFace.class, name.substring(1));
				if(storm == null) {
					Log.logWarning("Invalid block face " + name + "; skipping...");
					continue;
				}
				result.put(storm, false);
			} else result.put(storm, true);
		}
		if(result.isEmpty()) return null;
		return result;
	}
	
	public static double parseChanceFrom(ConfigurationNode node, String key) {
		String chanceString = node.getString(key, null);
		double chance = 100;
		if (chanceString == null) {
			chance = 100;
		} else {
			try {
				chance = Double.parseDouble(chanceString.replaceAll("%$", ""));
			} catch (NumberFormatException ex) {
				chance = 100;
			}
		}
		return chance;
	}
	
	private Location parseLocationFrom(ConfigurationNode node, String type, double d, double defY, double e) {
		String loc = getStringFrom(node, "loc-" + type, type + "loc");
		if(loc == null) return new Location(null,d,defY,e);
		double x = 0, y = 0, z = 0;
		String[] split = loc.split("/");
		if (split.length == 3) {
			try {
				x = Double.parseDouble(split[0]);
				y = Double.parseDouble(split[1]);
				z = Double.parseDouble(split[2]);
			} catch (NumberFormatException ex) {
				x = y = z = 0;
			}
		}
		return new Location(null,x,y,z);
	}

	public static List<String> getMaybeList(ConfigurationNode node, String... keys) {
		if(node == null) return new ArrayList<String>();
		Object prop = null;
		String key = null;
		for (int i = 0; i < keys.length; i++) {
			key = keys[i];
			prop = node.getProperty(key);
			if(prop != null) break;
		}
		List<String> list;
		if(prop == null) return new ArrayList<String>();
		else if(prop instanceof List) list = node.getStringList(key, null);
		else list = Collections.singletonList(prop.toString());
		return list;
	}
	
	public static String getStringFrom(ConfigurationNode node, String... keys) {
		String prop = null;
		for(int i = 0; i < keys.length; i++) {
			prop = node.getString(keys[i]);
			if(prop != null) break;
		}
		return prop;
	}

	public static boolean containsAll(List<String> list) {
		for(String str : list) {
			if(str.equalsIgnoreCase("ALL") || str.equalsIgnoreCase("ANY")) return true;
		}
		return false;
	}

	// TODO: put this in a better location
	public static boolean isCreature(String name, boolean allowCaret) {
		if (name.startsWith("CREATURE_")) return true;
		name = name.split("@")[0];
		CreatureType test = enumValue(CreatureType.class, name.replace("CREATURE_", ""));
		if(test != null) return true;
		if(allowCaret) name = name.replaceFirst("^\\^", "");
		CreatureGroup test2 = CreatureGroup.get(name);
		return test2 != null;
	}

	private static void setPluginEnabled(boolean pluginEnabled) {
		ConfigLoader.pluginEnabled = pluginEnabled;
	}

	public static boolean isPluginEnabled() {
		return pluginEnabled;
	}

	private static void setPriority(Priority pri) {
		ConfigLoader.priority = pri;
	}

	public static Priority getPriority() {
		return priority;
	}

	public static void setMobs(Map<UUID, Mob> mobs) {
		ConfigLoader.mobs = mobs;
	}

	public static Map<UUID, Mob> getMobs() {
		return mobs;
	}

	public static void setProfiling(boolean profiling) {
		ConfigLoader.profiling = profiling;
	}

	public static boolean isProfiling() {
		return profiling;
	}

	public static void setVerbosity(Verbosity verbosity) {
		ConfigLoader.verbosity = verbosity;
	}

	public static Verbosity getVerbosity() {
		return verbosity;
	}

	public static void setSpoutEnabled(boolean spoutEnabled) {
		ConfigLoader.spoutEnabled = spoutEnabled;
	}

	public static boolean isSpoutEnabled() {
		return spoutEnabled;
	}
	
}