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

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.gmail.zariust.othermobs.Log;


public class Flaming extends Ability {

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
	void updateOnMove() {
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

	private int count = 0;
	@Override
	public void run() {
		if (mob.getEntity().isDead()) {
			Bukkit.getServer().getScheduler().cancelTask(taskId);
			Log.high("Entity ("+mob.getEntity().toString()+") dead - task "+taskId+" cancelled.");	
			return;
		}

		mob.getEntity().getLocation().getWorld().playEffect(mob.getEntity().getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 16);

		// Idea here is for a random chance of setting a block it's on (or near it) to burst into flames.
		// If the mob is immune to fire then it'll live, if not - well, it'll not live...
		count++;
		Block block = mob.getEntity().getLocation().getBlock();
		Log.normal(block.getType().toString()+ "count"+count);
		if (count > 40) {
			count = 0;
			if (block.getType() == Material.AIR) {
				block.setType(Material.FIRE);
			}
		}
	}

	@Override
	void initLocal() {
		// TODO Auto-generated method stub
		//this.mob.getEntity().setFireTicks(20000);  // on fire
		//this.mob.getEntity().setNoDamageTicks(400); // immune for 20 seconds
		scheduleTicks = 5;
	}

}
