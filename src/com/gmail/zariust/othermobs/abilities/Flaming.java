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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void initLocal() {
		// TODO Auto-generated method stub
		this.mob.getEntity().setFireTicks(20000);  // on fire
		this.mob.getEntity().setNoDamageTicks(400); // immune for 20 seconds
	}

}
