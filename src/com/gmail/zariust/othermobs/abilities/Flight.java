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

public class Flight extends Ability {
	
	public Flight() {
	}

	@Override
	void initLocal() {
		// TODO Auto-generated method stub
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

		// TODO: To be added - flight code ala Pigasus, either via permission or clean room coding 
	}

}

