// OtherDrops - a Bukkit plugin
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

package com.gmail.zariust.othermobs.data;

import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.common.CommonEntity;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpawnerData implements Data {
	private CreatureType creature;

	public SpawnerData(BlockState state) {
		if(state instanceof CreatureSpawner)
			creature = ((CreatureSpawner)state).getCreatureType();
	}
	
	public SpawnerData(CreatureType type) {
		creature = type;
	}

	@Override
	public int getData() {
		return CommonEntity.getCreatureId(creature);
	}
	
	@Override
	public void setData(int d) {
		CreatureType c = CommonEntity.getCreatureType(d);
		if(c != null) creature = c;
	}
	
	@Override
	public boolean matches(Data d) {
		if(!(d instanceof SpawnerData)) return false;
		return creature == ((SpawnerData)d).creature;
	}
	
	@Override
	public String get(Enum<?> mat) {
		if(mat == Material.MOB_SPAWNER) 
			return creature.toString();
		return "";
	}

	@Override
	public void setOn(BlockState state) {
		if(!(state instanceof CreatureSpawner)) {
			Log.logWarning("Tried to change a spawner block, but no spawner block was found!");
			return;
		}
		((CreatureSpawner)state).setCreatureType(creature);
	}

	@Override // Spawners aren't entities, so nothing to do here.
	public void setOn(Entity entity, Player witness) {}

	public static Data parse(String state) {
		CreatureType type = CommonEntity.getCreatureType(state);
		if(type != null) return new SpawnerData(type);
		return null;
	}
	
	@Override
	public int hashCode() {
		return creature == null ? 0 : creature.hashCode();
	}
}
