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
package quests.Q00005_MinersFavor;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00005_MinersFavor extends Quest
{
	// NPCs
	private static final int BOLTER = 30554;
	private static final int SHARI = 30517;
	private static final int GARITA = 30518;
	private static final int REED = 30520;
	private static final int BRUNON = 30526;
	// Items
	private static final int BOLTERS_LIST = 1547;
	private static final int MINING_BOOTS = 1548;
	private static final int MINERS_PICK = 1549;
	private static final int BOOMBOOM_POWDER = 1550;
	private static final int REDSTONE_BEER = 1551;
	private static final int BOLTERS_SMELLY_SOCKS = 1552;
	// Reward
	private static final int NECKLACE = 906;
	
	public Q00005_MinersFavor()
	{
		super(5);
		registerQuestItems(BOLTERS_LIST, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER, BOLTERS_SMELLY_SOCKS);
		addStartNpc(BOLTER);
		addTalkId(BOLTER, SHARI, GARITA, REED, BRUNON);
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
		
		if (event.equals("30554-03.htm"))
		{
			st.startQuest();
			giveItems(player, BOLTERS_LIST, 1);
			giveItems(player, BOLTERS_SMELLY_SOCKS, 1);
		}
		else if (event.equals("30526-02.htm"))
		{
			takeItems(player, BOLTERS_SMELLY_SOCKS, 1);
			giveItems(player, MINERS_PICK, 1);
			if (hasQuestItems(player, MINING_BOOTS, BOOMBOOM_POWDER, REDSTONE_BEER))
			{
				st.setCond(2, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
				htmltext = (player.getLevel() < 2) ? "30554-01.htm" : "30554-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case BOLTER:
					{
						if (cond == 1)
						{
							htmltext = "30554-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30554-06.htm";
							takeItems(player, BOLTERS_LIST, 1);
							takeItems(player, BOOMBOOM_POWDER, 1);
							takeItems(player, MINERS_PICK, 1);
							takeItems(player, MINING_BOOTS, 1);
							takeItems(player, REDSTONE_BEER, 1);
							giveItems(player, NECKLACE, 1);
							st.exitQuest(false, true);
						}
						break;
					}
					case SHARI:
					{
						if ((cond == 1) && !hasQuestItems(player, BOOMBOOM_POWDER))
						{
							htmltext = "30517-01.htm";
							giveItems(player, BOOMBOOM_POWDER, 1);
							if (hasQuestItems(player, MINING_BOOTS, MINERS_PICK, REDSTONE_BEER))
							{
								st.setCond(2, true);
							}
							else
							{
								playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else
						{
							htmltext = "30517-02.htm";
						}
						break;
					}
					case GARITA:
					{
						if ((cond == 1) && !hasQuestItems(player, MINING_BOOTS))
						{
							htmltext = "30518-01.htm";
							giveItems(player, MINING_BOOTS, 1);
							if (hasQuestItems(player, MINERS_PICK, BOOMBOOM_POWDER, REDSTONE_BEER))
							{
								st.setCond(2, true);
							}
							else
							{
								playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else
						{
							htmltext = "30518-02.htm";
						}
						break;
					}
					case REED:
					{
						if ((cond == 1) && !hasQuestItems(player, REDSTONE_BEER))
						{
							htmltext = "30520-01.htm";
							giveItems(player, REDSTONE_BEER, 1);
							if (hasQuestItems(player, MINING_BOOTS, MINERS_PICK, BOOMBOOM_POWDER))
							{
								st.setCond(2, true);
							}
							else
							{
								playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else
						{
							htmltext = "30520-02.htm";
						}
						break;
					}
					case BRUNON:
					{
						if ((cond == 1) && !hasQuestItems(player, MINERS_PICK))
						{
							htmltext = "30526-01.htm";
						}
						else
						{
							htmltext = "30526-03.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		
		return htmltext;
	}
}