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
package quests.Q00298_LizardmensConspiracy;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00298_LizardmensConspiracy extends Quest
{
	// NPCs
	private static final int PRAGA = 30333;
	private static final int ROHMER = 30344;
	// Items
	private static final int PATROL_REPORT = 7182;
	private static final int WHITE_GEM = 7183;
	private static final int RED_GEM = 7184;
	
	public Q00298_LizardmensConspiracy()
	{
		super(298);
		registerQuestItems(PATROL_REPORT, WHITE_GEM, RED_GEM);
		addStartNpc(PRAGA);
		addTalkId(PRAGA, ROHMER);
		addKillId(20926, 20927, 20922, 20923, 20924);
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
			case "30333-1.htm":
			{
				st.startQuest();
				giveItems(player, PATROL_REPORT, 1);
				break;
			}
			case "30344-1.htm":
			{
				st.setCond(2, true);
				takeItems(player, PATROL_REPORT, 1);
				break;
			}
			case "30344-4.htm":
			{
				if (st.isCond(3))
				{
					htmltext = "30344-3.htm";
					takeItems(player, WHITE_GEM, -1);
					takeItems(player, RED_GEM, -1);
					addExpAndSp(player, 0, 42000);
					st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 25) ? "30333-0b.htm" : "30333-0a.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case PRAGA:
					{
						htmltext = "30333-2.htm";
						break;
					}
					case ROHMER:
					{
						if (st.isCond(1))
						{
							htmltext = (hasQuestItems(player, PATROL_REPORT)) ? "30344-0.htm" : "30344-0a.htm";
						}
						else
						{
							htmltext = "30344-2.htm";
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
		final QuestState qs = getRandomPartyMemberState(player, 2, 3, npc);
		if (qs == null)
		{
			return null;
		}
		final Player partyMember = qs.getPlayer();
		
		final QuestState st = getQuestState(partyMember, false);
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case 20922:
			{
				if (getRandom(100) < 40)
				{
					giveItems(player, WHITE_GEM, 1);
					if (getQuestItemsCount(player, WHITE_GEM) < 50)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else if (getQuestItemsCount(player, RED_GEM) >= 50)
					{
						st.setCond(3, true);
					}
				}
				break;
			}
			case 20923:
			{
				if (getRandom(100) < 45)
				{
					giveItems(player, WHITE_GEM, 1);
					if (getQuestItemsCount(player, WHITE_GEM) < 50)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else if (getQuestItemsCount(player, RED_GEM) >= 50)
					{
						st.setCond(3, true);
					}
				}
				break;
			}
			case 20924:
			{
				if (getRandom(100) < 35)
				{
					giveItems(player, WHITE_GEM, 1);
					if (getQuestItemsCount(player, WHITE_GEM) < 50)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else if (getQuestItemsCount(player, RED_GEM) >= 50)
					{
						st.setCond(3, true);
					}
				}
				break;
			}
			case 20926:
			case 20927:
			{
				if (getRandom(100) < 40)
				{
					giveItems(player, RED_GEM, 1);
					if (getQuestItemsCount(player, RED_GEM) < 50)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else if (getQuestItemsCount(player, WHITE_GEM) >= 50)
					{
						st.setCond(3, true);
					}
				}
				break;
			}
		}
		
		return null;
	}
}
