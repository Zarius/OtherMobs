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

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Event;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import com.garbagemule.MobArena.MobArenaHandler;

import com.gmail.zariust.othermobs.listener.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class OtherMobs extends JavaPlugin
{
	public PluginDescriptionFile info = null;
	static String pluginName;
	static String pluginVersion;
	public static OtherMobs plugin;
	static Logger log;
	public ConfigLoader config = null;
	public static Random rng = new Random();

	// Listeners
	private final OdEntityListener entityListener;

	// Plugin Handlers
	public static WorldGuardPlugin worldguardPlugin = null; // for WorldGuard support
	public static MobArenaHandler mobArenaHandler = null; 	// for MobArena

	/**
	 * Setup WorldGuardAPI - hook into the plugin if it's available
	 */
	private void setupWorldGuard() {
		Plugin wg = this.getServer().getPluginManager().getPlugin("WorldGuard");

		if (wg == null) {
			Log.normal("Couldn't load WorldGuard.");
		} else {
			OtherMobs.worldguardPlugin = (WorldGuardPlugin)wg;
			Log.high("Hooked into WorldGuard.");			
		}
	}
	
	private void setupMobArena() {
		Plugin ma = this.getServer().getPluginManager().getPlugin("MobArena");
		if (ma == null) {
			Log.extreme("Couldn't load MobArena."); // mobarena's not essential so no need to worry.
			mobArenaHandler = null;
		} else {
			Log.high("Hooked into MobArena.");
			mobArenaHandler = new MobArenaHandler();
		}		
	}

	public OtherMobs() {
		plugin = this;
		log = Logger.getLogger("Minecraft");
		
		entityListener = new OdEntityListener(this);
	}

	@Override
	public void onEnable() {
		// Store name & version statically for log functions
		pluginName = this.getDescription().getName();
		pluginVersion = this.getDescription().getVersion();
			
		// Setup any plugins needed before loading the config
		setupWorldGuard();
		setupMobArena();

		// Load up the main config files
		config = new ConfigLoader(this);
		config.load();
		
		// Register events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, ConfigLoader.getPriority(), this);
		
		// Register command listener
		this.getCommand("om").setExecutor(new OtherMobsCommand(this));

		// Load up Spout (if available) for custom skins
        if (!getServer().getPluginManager().isPluginEnabled("Spout")) {
        	Log.high("Spout not found - custom skins disabled.");
        	ConfigLoader.setSpoutEnabled(false);
        } else {
        	Log.normal("Hooking into Spout (custom skins enabled).");
        	ConfigLoader.setSpoutEnabled(true);
        }
        		
		Log.low("OtherMobs loaded.");
	}

	// TODO: does this below here or should be moved to another class?
	public boolean hasPermission(Permissible who, String permission) {
		if (who instanceof ConsoleCommandSender) return true;
		boolean perm = who.hasPermission(permission);
		if (!perm) {
			Log.highest("SuperPerms - permission ("+permission+") denied for "+who.toString());
		} else {
			Log.highest("SuperPerms - permission ("+permission+") allowed for "+who.toString());
		}
		return perm;
	}
	
	@Override
	public void onDisable() {
		Log.low(getDescription().getName() + " " + getDescription().getVersion() + " unloaded.");
	}
	
}