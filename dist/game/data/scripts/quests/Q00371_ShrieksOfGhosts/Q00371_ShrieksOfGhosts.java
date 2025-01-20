/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00371_ShrieksOfGhosts;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

public class Q00371_ShrieksOfGhosts extends Quest
{
	private static class DropInfo
	{
		public int _firstChance;
		public int _secondChance;
		
		public DropInfo(int firstChance, int secondChance)
		{
			_firstChance = firstChance;
			_secondChance = secondChance;
		}
		
		public int getFirstChance()
		{
			return _firstChance;
		}
		
		public int getSecondChance()
		{
			return _secondChance;
		}
	}
	
	// NPCs
	private static final int REVA = 30867;
	private static final int PATRIN = 30929;
	// Item
	private static final int URN = 5903;
	private static final int PORCELAIN = 6002;
	// Drop chances
	private static final Map<Integer, DropInfo> MOBS = new HashMap<>();
	static
	{
		MOBS.put(20818, new DropInfo(350, 400)); // hallates_warrior
		MOBS.put(20820, new DropInfo(583, 673)); // hallates_knight
		MOBS.put(20824, new DropInfo(458, 538)); // hallates_commander
	}
	
	public Q00371_ShrieksOfGhosts()
	{
		super(371);
		registerQuestItems(URN, PORCELAIN);
		addStartNpc(REVA);
		addTalkId(REVA, PATRIN);
		addKillId(20818, 20820, 20824);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30867-03.htm":
			{
				st.startQuest();
				break;
			}
			case "30867-07.htm":
			{
				int urns = (int) getQuestItemsCount(player, URN);
				if (urns > 0)
				{
					takeItems(player, URN, urns);
					if (urns >= 100)
					{
						urns += 13;
						htmltext = "30867-08.htm";
					}
					else
					{
						urns += 7;
					}
					giveAdena(player, urns * 1000, true);
				}
				break;
			}
			case "30867-10.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "APPR":
			{
				if (hasQuestItems(player, PORCELAIN))
				{
					final int chance = getRandom(100);
					takeItems(player, PORCELAIN, 1);
					if (chance < 2)
					{
						giveItems(player, 6003, 1);
						htmltext = "30929-03.htm";
					}
					else if (chance < 32)
					{
						giveItems(player, 6004, 1);
						htmltext = "30929-04.htm";
					}
					else if (chance < 62)
					{
						giveItems(player, 6005, 1);
						htmltext = "30929-05.htm";
					}
					else if (chance < 77)
					{
						giveItems(player, 6006, 1);
						htmltext = "30929-06.htm";
					}
					else
					{
						htmltext = "30929-07.htm";
					}
				}
				else
				{
					htmltext = "30929-02.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 59) ? "30867-01.htm" : "30867-02.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case REVA:
					{
						if (hasQuestItems(player, URN))
						{
							htmltext = (hasQuestItems(player, PORCELAIN)) ? "30867-05.htm" : "30867-04.htm";
						}
						else
						{
							htmltext = "30867-06.htm";
						}
						break;
					}
					case PATRIN:
					{
						htmltext = "30929-01.htm";
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs == null) || !Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			return null;
		}
		
		final DropInfo info = MOBS.get(npc.getId());
		final int random = getRandom(1000);
		if (random < info.getFirstChance())
		{
			giveItemRandomly(qs.getPlayer(), npc, URN, 1, 0, 1, true);
		}
		else if (random < info.getSecondChance())
		{
			giveItemRandomly(qs.getPlayer(), npc, PORCELAIN, 1, 0, 1, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
