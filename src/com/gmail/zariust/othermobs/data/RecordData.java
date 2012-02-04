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

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.OtherMobs;

public class RecordData extends EffectData {
	private Material disc;
	
	public RecordData(Material record) {
		super(64);
		disc = record;
	}

	public RecordData(BlockState state) {
		super(64);
		if(state instanceof Jukebox) {
			disc = ((Jukebox)state).getPlaying();
		}
	}

	@Override
	public int getData() {
		Integer discId = null;
		if (disc == null) {
			// if you don't specify a valid record you just get a random one
			if (OtherMobs.rng.nextInt() > 0.5) {
				discId = Material.GREEN_RECORD.getId();
			} else {
				discId = Material.GOLD_RECORD.getId();
			}			
		} else {
			discId = disc.getId();
		}

		return discId;
	}
	
	@Override
	public void setData(int d) {
		disc = Material.getMaterial(d);
	}
	
	@Override
	public boolean matches(Data d) {
		if(!(d instanceof RecordData)) return false;
		return disc == ((RecordData)d).disc;
	}
	
	@Override
	public String get(Enum<?> mat) {
		String discName = "";
		if (disc != null) discName = disc.toString();
		if(radius != EffectData.DEFAULT_RADIUS && mat == Effect.RECORD_PLAY)
			return discName + (discName.isEmpty() ? "" : "/") + radius;
		if(mat == Material.JUKEBOX || mat == Effect.RECORD_PLAY)
			return discName;
		return "";
	}
	
	@Override
	public void setOn(BlockState state) {
		if(!(state instanceof Jukebox)) {
			Log.logWarning("Tried to change a jukebox, but no jukebox was found!");
			return;
		}
		((Jukebox)state).setPlaying(disc);
	}
	
	@Override // Jukeboxes are not entities, so nothing to do here
	public void setOn(Entity entity, Player witness) {}

	public static RecordData parse(String state) {
		if(state == null || state.isEmpty()) return null;
		Material mat = Material.getMaterial(state);
		if(mat == null || mat.getId() < 2256) {
			return new RecordData((Material)null);
		}
		return new RecordData(mat);
	}
	
	@Override
	public int hashCode() {
		return disc == null ? 0 : disc.hashCode();
	}
}
