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
package ai.others.Mammons.MerchantOfMammon;

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
public class MerchantOfMammon extends AbstractNpcAI
{
	// NPC
	private static final int MERCHANT = 31113;
	// Locations
	private static final Location[] LOCATIONS =
	{
		new Location(-52172, 78884, -4741, 0), // Devotion
		new Location(-41350, 209876, -5087, 0), // Sacrifice
		new Location(-21657, 77164, -5173, 0), // Patriots
		new Location(45029, 123802, -5413, 0), // Pilgrims
		new Location(83175, 208998, -5439, 0), // Saints
		new Location(111337, 173804, -5439, 0), // Worship
		new Location(118343, 132578, -4831, 0), // Martyrdom
		new Location(172373, -17833, -4901, 0), // Disciple
	};
	// Misc
	private static final int TELEPORT_DELAY = 1800000; // 30 minutes
	private static Npc _lastSpawn;
	
	private MerchantOfMammon()
	{
		addFirstTalkId(MERCHANT);
		onEvent("RESPAWN_MERCHANT", null, null);
		startQuestTimer("RESPAWN_MERCHANT", TELEPORT_DELAY, null, null, true);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		switch (event)
		{
			case "31113.html":
			case "31113-01.html":
			case "31113-02.html":
			{
				htmltext = event;
				break;
			}
			case "RESPAWN_MERCHANT":
			{
				// Check if current period is Seal Validation.
				if (SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION)
				{
					if (_lastSpawn != null)
					{
						_lastSpawn.deleteMe();
					}
					_lastSpawn = addSpawn(MERCHANT, getRandomEntry(LOCATIONS), false, TELEPORT_DELAY);
					if (Config.ANNOUNCE_MAMMON_SPAWN)
					{
						Broadcast.toAllOnlinePlayers("Merchant of Mammon has been spawned near the Town of " + _lastSpawn.getCastle().getName() + ".", false);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new MerchantOfMammon();
	}
}
