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
package quests.Q00154_SacrificeToTheSea;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00154_SacrificeToTheSea extends Quest
{
	// NPCs
	private static final int ROCKSWELL = 30312;
	private static final int CRISTEL = 30051;
	private static final int ROLFE = 30055;
	// Items
	private static final int FOX_FUR = 1032;
	private static final int FOX_FUR_YARN = 1033;
	private static final int MAIDEN_DOLL = 1034;
	// Reward
	private static final int EARING = 113;
	
	public Q00154_SacrificeToTheSea()
	{
		super(154);
		registerQuestItems(FOX_FUR, FOX_FUR_YARN, MAIDEN_DOLL);
		addStartNpc(ROCKSWELL);
		addTalkId(ROCKSWELL, CRISTEL, ROLFE);
		addKillId(20481, 20544, 20545); // Following Keltirs can be found near Talking Island.
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
		
		if (event.equals("30312-04.htm"))
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
				htmltext = (player.getLevel() < 2) ? "30312-02.htm" : "30312-03.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case ROCKSWELL:
					{
						if (cond == 1)
						{
							htmltext = "30312-05.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30312-08.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30312-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30312-07.htm";
							takeItems(player, MAIDEN_DOLL, -1);
							giveItems(player, EARING, 1);
							addExpAndSp(player, 100, 0);
							st.exitQuest(false, true);
						}
						break;
					}
					case CRISTEL:
					{
						if (cond == 1)
						{
							htmltext = (hasQuestItems(player, FOX_FUR)) ? "30051-01.htm" : "30051-01a.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30051-02.htm";
							st.setCond(3, true);
							takeItems(player, FOX_FUR, -1);
							giveItems(player, FOX_FUR_YARN, 1);
						}
						else if (cond == 3)
						{
							htmltext = "30051-03.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30051-04.htm";
						}
						break;
					}
					case ROLFE:
					{
						if (cond < 3)
						{
							htmltext = "30055-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30055-01.htm";
							st.setCond(4, true);
							takeItems(player, FOX_FUR_YARN, 1);
							giveItems(player, MAIDEN_DOLL, 1);
						}
						else if (cond == 4)
						{
							htmltext = "30055-02.htm";
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
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, FOX_FUR, 1, 10, 0.4, true))
		{
			qs.setCond(2);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
