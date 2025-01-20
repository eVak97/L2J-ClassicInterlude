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
package quests.Q00381_LetsBecomeARoyalMember;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00381_LetsBecomeARoyalMember extends Quest
{
	// NPCs
	private static final int SORINT = 30232;
	private static final int SANDRA = 30090;
	// Items
	private static final int KAIL_COIN = 5899;
	private static final int COIN_ALBUM = 5900;
	private static final int GOLDEN_CLOVER_COIN = 7569;
	private static final int COIN_COLLECTOR_MEMBERSHIP = 3813;
	// Reward
	private static final int ROYAL_MEMBERSHIP = 5898;
	
	public Q00381_LetsBecomeARoyalMember()
	{
		super(381);
		registerQuestItems(KAIL_COIN, GOLDEN_CLOVER_COIN);
		addStartNpc(SORINT);
		addTalkId(SORINT, SANDRA);
		addKillId(21018, 27316);
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
		
		if (event.equals("30090-02.htm"))
		{
			st.set("aCond", "1"); // Alternative cond used for Sandra.
		}
		else if (event.equals("30232-03.htm"))
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
				htmltext = ((player.getLevel() < 55) || !hasQuestItems(player, COIN_COLLECTOR_MEMBERSHIP)) ? "30232-02.htm" : "30232-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SORINT:
					{
						if (!hasQuestItems(player, KAIL_COIN))
						{
							htmltext = "30232-04.htm";
						}
						else if (!hasQuestItems(player, COIN_ALBUM))
						{
							htmltext = "30232-05.htm";
						}
						else
						{
							htmltext = "30232-06.htm";
							takeItems(player, KAIL_COIN, -1);
							takeItems(player, COIN_ALBUM, -1);
							giveItems(player, ROYAL_MEMBERSHIP, 1);
							st.exitQuest(true, true);
						}
						break;
					}
					case SANDRA:
					{
						if (!hasQuestItems(player, COIN_ALBUM))
						{
							if (st.getInt("aCond") == 0)
							{
								htmltext = "30090-01.htm";
							}
							else
							{
								if (!hasQuestItems(player, GOLDEN_CLOVER_COIN))
								{
									htmltext = "30090-03.htm";
								}
								else
								{
									htmltext = "30090-04.htm";
									takeItems(player, GOLDEN_CLOVER_COIN, -1);
									giveItems(player, COIN_ALBUM, 1);
								}
							}
						}
						else
						{
							htmltext = "30090-05.htm";
						}
						break;
					}
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
		
		if (npc.getId() == 21018)
		{
			if (!hasQuestItems(player, KAIL_COIN) && (getRandom(100) < 5))
			{
				giveItems(player, KAIL_COIN, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		else if (st.getInt("aCond") == 1)
		{
			if (!hasQuestItems(player, GOLDEN_CLOVER_COIN))
			{
				giveItems(player, GOLDEN_CLOVER_COIN, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		return null;
	}
}
