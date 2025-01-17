/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ai.others.Mammons.BlacksmithOfMammon;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.sevensigns.SevenSigns;
import org.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

/**
 * @author Mobius, Minzee
 */
public class BlacksmithOfMammon extends AbstractNpcAI
{
	// NPC
	private static final int BLACKSMITH = 31126;
	// Locations
	private static final Location[] LOCATIONS =
	{
		new Location(-19360, 13278, -4901, 0), // Dark Omens
		new Location(-53131, -250502, -7909, 0), // Heretic
		new Location(46303, 170091, -4981, 0), // Branded
		new Location(-20485, -251008, -8165, 0), // Apostate
		new Location(12669, -248698, -9581, 0), // Forbidden Path
		new Location(140519, 79464, -5429, 0), // Witch
	};
	// Misc
	private static final int TELEPORT_DELAY = 1800000; // 30 minutes
	private static Npc _lastSpawn;
	
	private BlacksmithOfMammon()
	{
		addFirstTalkId(BLACKSMITH);
		onEvent("RESPAWN_BLACKSMITH", null, null);
		startQuestTimer("RESPAWN_BLACKSMITH", TELEPORT_DELAY, null, null, true);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "31126.html":
			case "31126-01.html":
			case "31126-02.html":
			case "31126-03.html":
			case "31126-04.html":
			{
				htmltext = event;
				break;
			}
			case "RESPAWN_BLACKSMITH":
			{
				// Check if current period is Seal Validation.
				if (SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION)
				{
					if (_lastSpawn != null)
					{
						_lastSpawn.deleteMe();
					}
					_lastSpawn = addSpawn(BLACKSMITH, getRandomEntry(LOCATIONS), false, TELEPORT_DELAY);
					if (Config.ANNOUNCE_MAMMON_SPAWN)
					{
						Broadcast.toAllOnlinePlayers("Blacksmith of Mammon has been spawned near the Town of " + _lastSpawn.getCastle().getName() + ".", false);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new BlacksmithOfMammon();
	}
}
