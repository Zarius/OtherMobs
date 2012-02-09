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
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

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
		scheduleTicks = 1;
		scheduleDelay = 3;
		if(rand.nextInt(3) == 0)
			landing = true;
		
		this.TakeOff();
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
		
		this.Fly();

		// TODO: To be added - flight code ala Pigasus, either via permission or clean room coding 
	}

	
	private Boolean landing = false;
	private static Random rand = new Random();

	public Boolean isDead()
	{
		return mob.getEntity().isDead();
	}

	public void Fly()
	{
		Vector v = mob.getEntity().getVelocity();
		Location loc = mob.getEntity().getLocation();

		if(!landing)
		{

			if(rand.nextInt(200) == 0)
				landing = true;
			else
			{
				if(loc.getY() > 120 && v.getY() > 0)
					v.setY(0);
				if(rand.nextInt(20) == 0 && v.getY() < 0)
					v.setY(rand.nextDouble()/3);
				if(loc.getY() - loc.getWorld().getHighestBlockYAt(loc) < (12 * rand.nextDouble() + 5))
					v.setY(rand.nextDouble()/3);
				if(rand.nextInt(5) == 0)
					v.setX(loc.getDirection().getX()/3);
				if(rand.nextInt(5) == 0)
					v.setZ(loc.getDirection().getZ()/3);

				mob.getEntity().setVelocity(v);
				mob.getEntity().setFallDistance(0);
			}
		}
		else if(rand.nextInt(200) == 0)
		{
			landing = false;
			TakeOff();
		}

	}

	public void TakeOff()
	{
		// launch it into the air
		mob.getEntity().setVelocity(new Vector(0,rand.nextDouble()/3,0));
	}

}

