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
package ai.areas.DungeonOfAbyss;

import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.zone.ZoneType;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class DungeonOfAbyssZone extends AbstractNpcAI
{
	private static final ZoneType ABYSS_WEST_ZONE_1 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss");
	private static final ZoneType ABYSS_WEST_ZONE_2 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss 2nd");
	private static final ZoneType ABYSS_EAST_ZONE_3 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss");
	private static final ZoneType ABYSS_EAST_ZONE_4 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss 2nd");
	
	private static final ZoneType ABYSS_WEST_ZONE_BOSS_1 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss Boss Room");
	private static final ZoneType ABYSS_WEST_ZONE_BOSS_2 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss 2nd Boss Room");
	private static final ZoneType ABYSS_EAST_ZONE_BOSS_3 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss Boss Room");
	private static final ZoneType ABYSS_EAST_ZONE_BOSS_4 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss 2nd Boss Room");
	
	private static final int EXIT_TIME = 60 * 60 * 1000; // 60 minute
	private static final int EXIT_TIME_BOSS_ROOM = 30 * 60 * 1000; // 60 minute
	private static final Location EXIT_LOCATION_1 = new Location(-120019, -182575, -6751); // Move to Magrit
	private static final Location EXIT_LOCATION_2 = new Location(-119977, -179753, -6751); // Move to Ingrit
	private static final Location EXIT_LOCATION_3 = new Location(-109554, -180408, -6753); // Move to Iris
	private static final Location EXIT_LOCATION_4 = new Location(-109595, -177560, -6753); // Move to Rosammy
	
	private DungeonOfAbyssZone()
	{
		addEnterZoneId(ABYSS_WEST_ZONE_1.getId(), ABYSS_WEST_ZONE_2.getId(), ABYSS_EAST_ZONE_3.getId(), ABYSS_EAST_ZONE_4.getId(), ABYSS_WEST_ZONE_BOSS_1.getId(), ABYSS_WEST_ZONE_BOSS_2.getId(), ABYSS_EAST_ZONE_BOSS_3.getId(), ABYSS_EAST_ZONE_BOSS_4.getId());
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith("EXIT_PLAYER") && (player != null))
		{
			if (event.contains(ABYSS_WEST_ZONE_1.getName()) && ABYSS_WEST_ZONE_1.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_1);
			}
			else if (event.contains(ABYSS_WEST_ZONE_2.getName()) && ABYSS_WEST_ZONE_2.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_2);
			}
			else if (event.contains(ABYSS_EAST_ZONE_3.getName()) && ABYSS_EAST_ZONE_3.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_3);
			}
			else if (event.contains(ABYSS_EAST_ZONE_4.getName()) && ABYSS_EAST_ZONE_4.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_4);
			}
			else if (event.contains(ABYSS_WEST_ZONE_BOSS_1.getName()) && ABYSS_WEST_ZONE_BOSS_1.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_1);
			}
			else if (event.contains(ABYSS_WEST_ZONE_BOSS_2.getName()) && ABYSS_WEST_ZONE_BOSS_2.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_2);
			}
			else if (event.contains(ABYSS_EAST_ZONE_BOSS_3.getName()) && ABYSS_EAST_ZONE_BOSS_3.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_3);
			}
			else if (event.contains(ABYSS_EAST_ZONE_BOSS_4.getName()) && ABYSS_EAST_ZONE_BOSS_4.getPlayersInside().contains(player))
			{
				player.teleToLocation(EXIT_LOCATION_4);
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onEnterZone(Creature creature, ZoneType zone)
	{
		if (creature.isPlayer())
		{
			final Player player = creature.asPlayer();
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_1.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_2.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_3.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_4.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_BOSS_1.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_BOSS_2.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_BOSS_3.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_BOSS_4.getName() + player.getObjectId(), null, player);
			startQuestTimer("EXIT_PLAYER" + zone.getName() + player.getObjectId(), zone.getName().contains("boss") ? EXIT_TIME_BOSS_ROOM : EXIT_TIME, null, player);
		}
		return super.onEnterZone(creature, zone);
	}
	
	public static void main(String[] args)
	{
		new DungeonOfAbyssZone();
	}
}
