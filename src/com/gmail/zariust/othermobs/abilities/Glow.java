package com.gmail.zariust.othermobs.abilities;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
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

public class Glow extends Ability {
	private Block glowBlock;
	private static Material glowOriginalMaterial;
	
	public Glow() {}
	
	Glow(Mob mob) {
		this.mob = mob;
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(OtherMobs.plugin, this, 40, 40);

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
		// FIXME: this still has bugs - leaves behind some glowstones
		// TODO: attempt to move from glowstones to just a general glow using block light value
		if (mob.getEntity().isDead()) {
			if (glowBlock != null) {
				Location loc = glowBlock.getLocation();
				CraftWorld world = (CraftWorld)glowBlock.getWorld();
				//world.getHandle().a(EnumSkyBlock.BLOCK, (int)loc.getX(), (int)loc.getY()+1, (int)loc.getZ(), 0);
				glowBlock.setType(glowOriginalMaterial);
			}

			Bukkit.getServer().getScheduler().cancelTask(taskId);
			Log.high("Entity ("+mob.getEntity().toString()+") dead - task "+taskId+" cancelled.");	
			return;
		}

		if (glowBlock != null) {
			Location loc = glowBlock.getLocation();
			//CraftWorld world = (CraftWorld)glowBlock.getWorld();
			//world.getHandle().a(EnumSkyBlock.BLOCK, (int)loc.getX(), (int)loc.getY()+1, (int)loc.getZ(), 0);
			glowBlock.setType(glowOriginalMaterial);
		}

		Block block = mob.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN);
		Location loc2 = block.getLocation();
		Log.highest("setting glow at "+loc2.toString());
		//CraftWorld world2 = (CraftWorld)block.getWorld();
		//world2.getHandle().a(EnumSkyBlock.BLOCK, (int)loc2.getX(), (int)loc2.getY()+1, (int)loc2.getZ(), 15);
		glowBlock = block;
		glowOriginalMaterial = block.getType();
		block.setType(Material.GLOWSTONE);	
	}

	@Override
	void initLocal() {
		// TODO Auto-generated method stub
		
	}

}
