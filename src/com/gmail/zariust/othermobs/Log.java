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

import static com.gmail.zariust.othermobs.common.Verbosity.EXTREME;

import com.gmail.zariust.othermobs.common.Verbosity;

public class Log {

	// LogInfo & Logwarning - display messages with a standard prefix
	public static void logWarning(String msg) {
		OtherMobs.log.warning("["+OtherMobs.pluginName+":"+OtherMobs.pluginVersion+"] "+msg);
	}

	@Deprecated
	public static void logInfo(String msg) {
		OtherMobs.log.info("["+OtherMobs.pluginName+":"+OtherMobs.pluginVersion+"] "+msg);
	}

	public static void low(String msg) {
		if (ConfigLoader.getVerbosity().exceeds(Verbosity.LOW)) logInfo(msg);
	}

	public static void normal(String msg) {
		if (ConfigLoader.getVerbosity().exceeds(Verbosity.NORMAL)) logInfo(msg);
	}

	public static void high(String msg) {
		if (ConfigLoader.getVerbosity().exceeds(Verbosity.HIGH)) logInfo(msg);
	}

	public static void highest(String msg) {
		if (ConfigLoader.getVerbosity().exceeds(Verbosity.HIGHEST)) logInfo(msg);
	}

	public static void extreme(String msg) {
		if (ConfigLoader.getVerbosity().exceeds(Verbosity.EXTREME)) logInfo(msg);
	}
	// LogInfo & LogWarning - if given a level will report the message
	// only for that level & above
	@Deprecated
	public static void logInfo(String msg, Verbosity level) {
		if (ConfigLoader.getVerbosity().exceeds(level)) logInfo(msg);
	}

	@Deprecated
	public static void logWarning(String msg, Verbosity level) {
		if (ConfigLoader.getVerbosity().exceeds(level)) logWarning(msg);
	}

	// TODO: This is only for temporary debug purposes.
	public static void stackTrace() {
		if(ConfigLoader.getVerbosity().exceeds(EXTREME)) Thread.dumpStack();
	}
}
