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
package quests.Q00266_PleasOfPixies;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00266_PleasOfPixies extends Quest
{
	// Items
	private static final int PREDATOR_FANG = 1334;
	// Rewards
	private static final int GLASS_SHARD = 1336;
	private static final int EMERALD = 1337;
	private static final int BLUE_ONYX = 1338;
	private static final int ONYX = 1339;
	
	public Q00266_PleasOfPixies()
	{
		super(266);
		registerQuestItems(PREDATOR_FANG);
		addStartNpc(31852); // Murika
		addTalkId(31852);
		addKillId(20525, 20530, 20534, 20537);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("31852-03.htm"))
		{
			st.startQuest();
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "31852-00.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "31852-01.htm";
				}
				else
				{
					htmltext = "31852-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, PREDATOR_FANG) < 100)
				{
					htmltext = "31852-04.htm";
				}
				else
				{
					htmltext = "31852-05.htm";
					takeItems(player, PREDATOR_FANG, -1);
					
					final int n = getRandom(100);
					if (n < 10)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_JACKPOT);
						rewardItems(player, EMERALD, 1);
					}
					else if (n < 30)
					{
						rewardItems(player, BLUE_ONYX, 1);
					}
					else if (n < 60)
					{
						rewardItems(player, ONYX, 1);
					}
					else
					{
						rewardItems(player, GLASS_SHARD, 1);
					}
					
					st.exitQuest(true, true);
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case 20525:
			{
				giveItems(player, PREDATOR_FANG, getRandom(2, 3));
				if (getQuestItemsCount(player, PREDATOR_FANG) < 100)
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					st.setCond(2, true);
				}
				break;
			}
			case 20530:
			{
				if (getRandom(10) < 8)
				{
					giveItems(player, PREDATOR_FANG, 1);
					if (getQuestItemsCount(player, PREDATOR_FANG) < 100)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						st.setCond(2, true);
					}
				}
				break;
			}
			case 20534:
			{
				if (getRandom(10) < 6)
				{
					giveItems(player, PREDATOR_FANG, getRandom(3) == 0 ? 1 : 2);
					if (getQuestItemsCount(player, PREDATOR_FANG) < 100)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						st.setCond(2, true);
					}
				}
				break;
			}
			case 20537:
			{
				giveItems(player, PREDATOR_FANG, 2);
				if (getQuestItemsCount(player, PREDATOR_FANG) < 100)
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					st.setCond(2, true);
				}
				break;
			}
		}
		
		return null;
	}
}
