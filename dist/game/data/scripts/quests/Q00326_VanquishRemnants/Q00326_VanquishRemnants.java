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
package quests.Q00326_VanquishRemnants;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00326_VanquishRemnants extends Quest
{
	// Items
	private static final int RED_CROSS_BADGE = 1359;
	private static final int BLUE_CROSS_BADGE = 1360;
	private static final int BLACK_CROSS_BADGE = 1361;
	// Reward
	private static final int BLACK_LION_MARK = 1369;
	
	public Q00326_VanquishRemnants()
	{
		super(326);
		registerQuestItems(RED_CROSS_BADGE, BLUE_CROSS_BADGE, BLACK_CROSS_BADGE);
		addStartNpc(30435); // Leopold
		addTalkId(30435);
		addKillId(20053, 20437, 20058, 20436, 20061, 20439, 20063, 20066, 20438);
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
		
		if (event.equals("30435-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30435-07.htm"))
		{
			st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 21) ? "30435-01.htm" : "30435-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int redBadges = (int) getQuestItemsCount(player, RED_CROSS_BADGE);
				final int blueBadges = (int) getQuestItemsCount(player, BLUE_CROSS_BADGE);
				final int blackBadges = (int) getQuestItemsCount(player, BLACK_CROSS_BADGE);
				final int badgesSum = redBadges + blueBadges + blackBadges;
				if (badgesSum > 0)
				{
					takeItems(player, RED_CROSS_BADGE, -1);
					takeItems(player, BLUE_CROSS_BADGE, -1);
					takeItems(player, BLACK_CROSS_BADGE, -1);
					giveAdena(player, ((redBadges * 60) + (blueBadges * 65) + (blackBadges * 70) + ((badgesSum >= 10) ? 4320 : 0)), true);
					if (badgesSum >= 100)
					{
						if (!hasQuestItems(player, BLACK_LION_MARK))
						{
							htmltext = "30435-06.htm";
							giveItems(player, BLACK_LION_MARK, 1);
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						else
						{
							htmltext = "30435-09.htm";
						}
					}
					else
					{
						htmltext = "30435-05.htm";
					}
				}
				else
				{
					htmltext = "30435-04.htm";
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
		
		switch (npc.getId())
		{
			case 20053:
			case 20437:
			case 20058:
			{
				if (getRandom(100) < 33)
				{
					giveItems(player, RED_CROSS_BADGE, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case 20436:
			case 20061:
			case 20439:
			case 20063:
			{
				if (getRandom(100) < 16)
				{
					giveItems(player, BLUE_CROSS_BADGE, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
			case 20066:
			case 20438:
			{
				if (getRandom(100) < 12)
				{
					giveItems(player, BLACK_CROSS_BADGE, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				break;
			}
		}
		
		return null;
	}
}
