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

import static com.gmail.zariust.othermobs.common.Verbosity.*;

import com.gmail.zariust.othermobs.Log;
import com.gmail.zariust.othermobs.common.CommonEntity;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.material.MaterialData;

// Range only allowed for SHEEP, SLIME, and PIG_ZOMBIE
public class CreatureData implements Data, RangeableData {
	private int data;

	public CreatureData(int mobData) {
		data = mobData;
	}

	public CreatureData() {
		this(0);
	}

	@Override
	public int getData() {
		return data;
	}
	
	@Override
	public void setData(int d) {
		data = d;
	}
	
	@Override
	public boolean matches(Data d) {
		if(!(d instanceof CreatureData)) return false;
		return data == d.getData();
	}
	
	@Override
	public String get(Enum<?> creature) {
		if(creature instanceof CreatureType) return get((CreatureType)creature);
		return "";
	}
	
	private String get(CreatureType type) {
		switch(type) {
		case CREEPER:
			if(data > 1) break;
			return data == 1 ? "POWERED" : "UNPOWERED";
		case PIG:
			if(data > 1) break;
			return data == 1 ? "SADDLED" : "UNSADDLED";
		case WOLF:
			if(data > 2) break;
			if(data == 2) return "TAME";
			return data == 1 ? "ANGRY" : "WILD";
		case SLIME:
		case MAGMA_CUBE:
			if(data == 0) return "TINY";
			if(data == 1) return "TINY";
			if(data == 2) return "SMALL";
			if(data == 3) return "BIG";
			if(data == 4) return "HUGE";
			// Fallthrough intentional
		case PIG_ZOMBIE:
			return Integer.toString(data);
		case SHEEP:
			if(data >= 48) break; // Highest valid sheep data: 32 + 15 = 47
			String result = "";
			if(data > 32) {
				result += "SHEARED";
				data -= 32;
			}
			if(data >= 16) break;
			if(data > 0) {
				if(!result.isEmpty()) result += "/";
				result += DyeColor.getByData((byte)data);
			}
			return result;
		case ENDERMAN:
			if(data > 0) {
				int id = data & 0xF, d = data >> 8;
				Material material = Material.getMaterial(id);
				@SuppressWarnings("hiding")
				Data data = new SimpleData(d);
				String dataStr = data.get(material);
				result = material.toString();
				if(!dataStr.isEmpty()) result += "/" + dataStr;
				return result;
			}
			break;
		default:
			if(data > 0) throw new IllegalArgumentException("Invalid data for " + type + ".");
		}
		return "";
	}

	@Override
	public void setOn(Entity mob, Player owner) {
		switch(CommonEntity.getCreatureType(mob)) {
		case CREEPER:
			if(data == 1) ((Creeper)mob).setPowered(true);
			break;
		case PIG:
			if(data == 1) ((Pig)mob).setSaddle(true);
			break;
		case SHEEP:
			if(data >= 32) {
				((Sheep)mob).setSheared(true);
				data -= 32;
			}
			if(data >= 0) ((Sheep)mob).setColor(DyeColor.getByData((byte)data));
			break;
		case SLIME:
			if(data > 0) ((Slime)mob).setSize(data);
			break;
		case WOLF:
			switch(data) {
			case 1:
				((Wolf)mob).setAngry(true);
				break;
			case 2:
				((Wolf)mob).setTamed(true);
				((Wolf)mob).setOwner(owner);
				break;
			}
			break;
		case PIG_ZOMBIE:
			if(data > 0) ((PigZombie)mob).setAnger(data);
			break;
		case ENDERMAN:
			if(data > 0) {
				int id = data & 0xF, d = data >> 8;
				MaterialData md = Material.getMaterial(id).getNewData((byte)d);
				((Enderman)mob).setCarriedMaterial(md);
			}
			break;
		case VILLAGER:
			if (data > 0) {
				//Villager villager = (Villager) mob;
				// TODO: this is just a placeholder until Bukkit allows for setting Villager type
			}
		default:
		}
	}

	@Override // No creature has a block state, so nothing to do here.
	public void setOn(BlockState state) {}

	@SuppressWarnings("incomplete-switch")
	public static Data parse(CreatureType creature, String state) {
		if(state == null || state.isEmpty()) return null;
		String[] split;
		switch(creature) {
		case CREEPER:
			if(state.equalsIgnoreCase("POWERED")) return new CreatureData(1);
			else if(state.equalsIgnoreCase("UNPOWERED")) return new CreatureData(0);
			break;
		case PIG:
			if(state.equalsIgnoreCase("SADDLED")) return new CreatureData(1);
			else if(state.equalsIgnoreCase("UNSADDLED")) return new CreatureData(0);
			break;
		case SHEEP:
			if(state.startsWith("RANGE")) return RangeData.parse(state);
			split = state.split("[/-]",2);
			if(split.length <= 2) {
				String colour = "", wool = "";
				if(split[0].endsWith("SHEARED")) {
					wool = split[0];
					if(split.length == 2) colour = split[1];
				} else if(split.length == 2 && split[1].endsWith("SHEARED")) {
					wool = split[1];
					colour = split[0];
				} else colour = split[0];
				if(!colour.isEmpty() || !wool.isEmpty()) {
					boolean success;
					int data = 0;
					if(!colour.isEmpty()) {
						try {
							data = DyeColor.valueOf(colour).getData() ;
							success = true;
						} catch(IllegalArgumentException e) {
							success = false;
						}
						// Or numbers
						try {
							int clr = Integer.parseInt(colour);
							if(clr < 16) data = clr;
							success = true;
						} catch(NumberFormatException e) {}
					} else success = true;
					if(wool.equalsIgnoreCase("SHEARED")) return new CreatureData(data + 32);
					else if(success || wool.equalsIgnoreCase("UNSHEARED")) return new CreatureData(data);
				}
			}
			break;
		case SLIME:
			if(state.equalsIgnoreCase("TINY")) return new CreatureData(1);
			else if(state.equalsIgnoreCase("SMALL")) return new CreatureData(2);
			else if(state.equalsIgnoreCase("BIG")) return new CreatureData(3);
			else if(state.equalsIgnoreCase("HUGE")) return new CreatureData(4);
			// Fallthrough intentional
		case PIG_ZOMBIE:
			if(state.startsWith("RANGE")) return RangeData.parse(state);
			try {
				int sz = Integer.parseInt(state);
				return new CreatureData(sz);
			} catch(NumberFormatException e) {}
			break;
		case WOLF:
			if(state.equalsIgnoreCase("TAME") || state.equalsIgnoreCase("TAMED"))
				return new CreatureData(2);
			else if(state.equalsIgnoreCase("WILD") || state.equalsIgnoreCase("NEUTRAL"))
				return new CreatureData(0);
			else if(state.equalsIgnoreCase("ANGRY")) return new CreatureData(1);
			break;
		case ENDERMAN:
			split = state.split("/");
			Material material = Material.getMaterial(split[0]);
			Data data = new SimpleData();
			if(split.length > 1)
				data = SimpleData.parse(material, split[1]);
			else data = new SimpleData();
			int md = (data.getData() << 8) | material.getId();
			return new CreatureData(md);
		}
		return new CreatureData();
	}
	
	@Override
	public String toString() {
		// TODO: Should probably make sure this is not used, and always use the get method instead
		Log.logWarning("CreatureData.toString() was called! Is this right?", EXTREME);
		Log.stackTrace();
		return String.valueOf(data);
	}
	
	@Override
	public int hashCode() {
		return data;
	}
}
