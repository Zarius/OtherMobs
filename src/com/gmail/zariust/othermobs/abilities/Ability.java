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

import com.gmail.zariust.othermobs.OtherMobs;
import com.gmail.zariust.othermobs.mobs.Mob;

public abstract class Ability implements Runnable {
	private double strength;
	private double chance;
	protected Mob mob;
	protected int taskId;

	Ability () {}
	
	// FIXME: trying to init with the class of the object (eg. Glow) rather than this class
//	public Class<E> init (Mob mob, Class<E> ab) {
	//	return new Class<E>();
//	}
	abstract void initLocal(); // for initialisation of the specific ability
	
	abstract void updateOnSpawn(); // called on creatureSpawn();
	abstract void updateOnMove(); // called on each creatureMove();
	abstract void updateOnTarget();
	abstract void updateOnAttack();
	abstract void updateOnHurt(); // called on each creatureMove();
	abstract void updateOnDeath(); // for turning the abilities/effects off
	
	public void init(Mob mob2) {
		// TODO Auto-generated method stub
		this.mob = mob2;
		taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(OtherMobs.plugin, this, 10, 10);
		initLocal();
	}

}
