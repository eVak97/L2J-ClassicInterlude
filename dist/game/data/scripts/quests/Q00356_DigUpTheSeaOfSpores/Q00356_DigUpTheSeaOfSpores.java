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
package quests.Q00356_DigUpTheSeaOfSpores;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00356_DigUpTheSeaOfSpores extends Quest
{
	// Items
	private static final int HERB_SPORE = 5866;
	private static final int CARN_SPORE = 5865;
	// Monsters
	private static final int ROTTING_TREE = 20558;
	private static final int SPORE_ZOMBIE = 20562;
	
	public Q00356_DigUpTheSeaOfSpores()
	{
		super(356);
		registerQuestItems(HERB_SPORE, CARN_SPORE);
		addStartNpc(30717); // Gauen
		addTalkId(30717);
		addKillId(ROTTING_TREE, SPORE_ZOMBIE);
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
			case "30717-06.htm":
			{
				st.startQuest();
				break;
			}
			case "30717-16.htm":
			{
				takeItems(player, HERB_SPORE, -1);
				takeItems(player, CARN_SPORE, -1);
				giveAdena(player, 44000, true);
				st.exitQuest(true, true);
				break;
			}
			case "30717-14.htm":
			{
				takeItems(player, HERB_SPORE, -1);
				takeItems(player, CARN_SPORE, -1);
				addExpAndSp(player, 35000, 2600);
				st.exitQuest(true, true);
				break;
			}
			case "30717-12.htm":
			{
				takeItems(player, HERB_SPORE, -1);
				addExpAndSp(player, 24500, 0);
				break;
			}
			case "30717-13.htm":
			{
				takeItems(player, CARN_SPORE, -1);
				addExpAndSp(player, 0, 1820);
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
				htmltext = (player.getLevel() < 43) ? "30717-01.htm" : "30717-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "30717-07.htm";
				}
				else if (cond == 2)
				{
					if (getQuestItemsCount(player, HERB_SPORE) >= 50)
					{
						htmltext = "30717-08.htm";
					}
					else if (getQuestItemsCount(player, CARN_SPORE) >= 50)
					{
						htmltext = "30717-09.htm";
					}
					else
					{
						htmltext = "30717-07.htm";
					}
				}
				else if (cond == 3)
				{
					htmltext = "30717-10.htm";
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
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		final int cond = st.getCond();
		if (cond < 3)
		{
			switch (npc.getId())
			{
				case ROTTING_TREE:
				{
					if (getRandom(100) < 63)
					{
						giveItems(player, HERB_SPORE, 1);
						if (getQuestItemsCount(player, HERB_SPORE) < 50)
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						else
						{
							st.setCond(cond == 2 ? 3 : 2, true);
						}
					}
					break;
				}
				case SPORE_ZOMBIE:
				{
					if (getRandom(100) < 76)
					{
						giveItems(player, CARN_SPORE, 1);
						if (getQuestItemsCount(player, CARN_SPORE) < 50)
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						else
						{
							st.setCond(cond == 2 ? 3 : 2, true);
						}
					}
					break;
				}
			}
		}
		
		return null;
	}
}
