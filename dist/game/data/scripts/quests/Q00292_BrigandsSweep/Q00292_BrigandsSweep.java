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
package quests.Q00292_BrigandsSweep;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00292_BrigandsSweep extends Quest
{
	// NPCs
	private static final int SPIRON = 30532;
	private static final int BALANKI = 30533;
	// Monsters
	private static final int GOBLIN_BRIGAND = 20322;
	private static final int GOBLIN_BRIGAND_LEADER = 20323;
	private static final int GOBLIN_BRIGAND_LIEUTENANT = 20324;
	private static final int GOBLIN_SNOOPER = 20327;
	private static final int GOBLIN_LORD = 20528;
	// Items
	private static final int GOBLIN_NECKLACE = 1483;
	private static final int GOBLIN_PENDANT = 1484;
	private static final int GOBLIN_LORD_PENDANT = 1485;
	private static final int SUSPICIOUS_MEMO = 1486;
	private static final int SUSPICIOUS_CONTRACT = 1487;
	
	public Q00292_BrigandsSweep()
	{
		super(292);
		registerQuestItems(GOBLIN_NECKLACE, GOBLIN_PENDANT, GOBLIN_LORD_PENDANT, SUSPICIOUS_MEMO, SUSPICIOUS_CONTRACT);
		addStartNpc(SPIRON);
		addTalkId(SPIRON, BALANKI);
		addKillId(GOBLIN_BRIGAND, GOBLIN_BRIGAND_LEADER, GOBLIN_BRIGAND_LIEUTENANT, GOBLIN_SNOOPER, GOBLIN_LORD);
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
		
		if (event.equals("30532-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30532-06.htm"))
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
				if (player.getRace() != Race.DWARF)
				{
					htmltext = "30532-00.htm";
				}
				else if (player.getLevel() < 5)
				{
					htmltext = "30532-01.htm";
				}
				else
				{
					htmltext = "30532-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SPIRON:
					{
						final int goblinNecklaces = (int) getQuestItemsCount(player, GOBLIN_NECKLACE);
						final int goblinPendants = (int) getQuestItemsCount(player, GOBLIN_PENDANT);
						final int goblinLordPendants = (int) getQuestItemsCount(player, GOBLIN_LORD_PENDANT);
						final int suspiciousMemos = (int) getQuestItemsCount(player, SUSPICIOUS_MEMO);
						final int countAll = goblinNecklaces + goblinPendants + goblinLordPendants;
						final boolean hasContract = hasQuestItems(player, SUSPICIOUS_CONTRACT);
						if (countAll == 0)
						{
							htmltext = "30532-04.htm";
						}
						else
						{
							if (hasContract)
							{
								htmltext = "30532-10.htm";
							}
							else if (suspiciousMemos > 0)
							{
								if (suspiciousMemos > 1)
								{
									htmltext = "30532-09.htm";
								}
								else
								{
									htmltext = "30532-08.htm";
								}
							}
							else
							{
								htmltext = "30532-05.htm";
							}
							
							takeItems(player, GOBLIN_NECKLACE, -1);
							takeItems(player, GOBLIN_PENDANT, -1);
							takeItems(player, GOBLIN_LORD_PENDANT, -1);
							if (hasContract)
							{
								st.setCond(1);
								takeItems(player, SUSPICIOUS_CONTRACT, -1);
							}
							
							int reward = (12 * goblinNecklaces) + (36 * goblinPendants) + (33 * goblinLordPendants) + ((hasContract) ? 1120 : 0);
							if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && (countAll >= 10))
							{
								reward += 1000;
							}
							
							giveAdena(player, reward, true);
						}
						break;
					}
					case BALANKI:
					{
						if (!hasQuestItems(player, SUSPICIOUS_CONTRACT))
						{
							htmltext = "30533-01.htm";
						}
						else
						{
							htmltext = "30533-02.htm";
							st.setCond(1);
							takeItems(player, SUSPICIOUS_CONTRACT, -1);
							giveAdena(player, 1500, true);
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
		
		final int chance = getRandom(10);
		if (chance > 5)
		{
			switch (npc.getId())
			{
				case GOBLIN_BRIGAND:
				case GOBLIN_SNOOPER:
				case GOBLIN_BRIGAND_LIEUTENANT:
				{
					giveItems(player, GOBLIN_NECKLACE, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
				case GOBLIN_BRIGAND_LEADER:
				{
					giveItems(player, GOBLIN_PENDANT, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
				case GOBLIN_LORD:
				{
					giveItems(player, GOBLIN_LORD_PENDANT, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
			}
		}
		else if ((chance > 4) && st.isCond(1))
		{
			giveItems(player, SUSPICIOUS_MEMO, 1);
			if (getQuestItemsCount(player, SUSPICIOUS_MEMO) < 3)
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				takeItems(player, SUSPICIOUS_MEMO, -1);
				giveItems(player, SUSPICIOUS_CONTRACT, 1);
				st.setCond(2, true);
			}
		}
		
		return null;
	}
}
