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
package custom.FactionSystem;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class FactionSystem extends AbstractNpcAI
{
	// NPCs
	private static final int MANAGER = 500;
	// Other
	private static final String[] TEXTS =
	{
		Config.FACTION_GOOD_TEAM_NAME + " or " + Config.FACTION_EVIL_TEAM_NAME + "?",
		"Select your faction!",
		"The choice is yours!"
	};
	
	private FactionSystem()
	{
		addSpawnId(MANAGER);
		addStartNpc(MANAGER);
		addTalkId(MANAGER);
		addFirstTalkId(MANAGER);
		
		if (Config.FACTION_SYSTEM_ENABLED)
		{
			addSpawn(MANAGER, Config.FACTION_MANAGER_LOCATION, false, 0);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "selectGoodFaction":
			{
				if (Config.FACTION_BALANCE_ONLINE_PLAYERS && (World.getInstance().getAllGoodPlayers().size() >= (World.getInstance().getAllEvilPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
				{
					final String htmltext = null;
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "onlinelimit.html"));
					packet.replace("%name%", player.getName());
					packet.replace("%more%", Config.FACTION_GOOD_TEAM_NAME);
					packet.replace("%less%", Config.FACTION_EVIL_TEAM_NAME);
					player.sendPacket(packet);
					return htmltext;
				}
				if (Config.FACTION_AUTO_NOBLESS)
				{
					player.setNoble(true);
				}
				player.setGood();
				final PlayerAppearance appearance = player.getAppearance();
				appearance.setNameColor(Config.FACTION_GOOD_NAME_COLOR);
				appearance.setTitleColor(Config.FACTION_GOOD_NAME_COLOR);
				player.setTitle(Config.FACTION_GOOD_TEAM_NAME);
				player.sendMessage("You are now fighting for the " + Config.FACTION_GOOD_TEAM_NAME + " faction.");
				player.teleToLocation(Config.FACTION_GOOD_BASE_LOCATION);
				broadcastMessageToFaction(Config.FACTION_GOOD_TEAM_NAME, Config.FACTION_GOOD_TEAM_NAME + " faction grows stronger with the arrival of " + player.getName() + ".");
				World.addFactionPlayerToWorld(player);
				break;
			}
			case "selectEvilFaction":
			{
				if (Config.FACTION_BALANCE_ONLINE_PLAYERS && (World.getInstance().getAllEvilPlayers().size() >= (World.getInstance().getAllGoodPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
				{
					final String htmltext = null;
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "onlinelimit.html"));
					packet.replace("%name%", player.getName());
					packet.replace("%more%", Config.FACTION_EVIL_TEAM_NAME);
					packet.replace("%less%", Config.FACTION_GOOD_TEAM_NAME);
					player.sendPacket(packet);
					return htmltext;
				}
				if (Config.FACTION_AUTO_NOBLESS)
				{
					player.setNoble(true);
				}
				player.setEvil();
				final PlayerAppearance appearance = player.getAppearance();
				appearance.setNameColor(Config.FACTION_EVIL_NAME_COLOR);
				appearance.setTitleColor(Config.FACTION_EVIL_NAME_COLOR);
				player.setTitle(Config.FACTION_EVIL_TEAM_NAME);
				player.sendMessage("You are now fighting for the " + Config.FACTION_EVIL_TEAM_NAME + " faction.");
				player.teleToLocation(Config.FACTION_EVIL_BASE_LOCATION);
				broadcastMessageToFaction(Config.FACTION_EVIL_TEAM_NAME, Config.FACTION_EVIL_TEAM_NAME + " faction grows stronger with the arrival of " + player.getName() + ".");
				World.addFactionPlayerToWorld(player);
				break;
			}
			case "SPEAK":
			{
				if (npc != null)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, getRandomEntry(TEXTS), 1500);
				}
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final String htmltext = null;
		final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
		packet.setHtml(getHtm(player, "manager.html"));
		packet.replace("%name%", player.getName());
		packet.replace("%good%", Config.FACTION_GOOD_TEAM_NAME);
		packet.replace("%evil%", Config.FACTION_EVIL_TEAM_NAME);
		player.sendPacket(packet);
		return htmltext;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == MANAGER)
		{
			startQuestTimer("SPEAK", 10000, npc, null, true);
		}
		return super.onSpawn(npc);
	}
	
	private void broadcastMessageToFaction(String factionName, String message)
	{
		if (factionName.equals(Config.FACTION_GOOD_TEAM_NAME))
		{
			for (Player player : World.getInstance().getAllGoodPlayers())
			{
				player.sendMessage(message);
			}
		}
		else
		{
			for (Player player : World.getInstance().getAllEvilPlayers())
			{
				player.sendMessage(message);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new FactionSystem();
	}
}
