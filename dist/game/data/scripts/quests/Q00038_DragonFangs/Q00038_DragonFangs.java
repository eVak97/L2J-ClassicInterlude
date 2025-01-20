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
package quests.Q00038_DragonFangs;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00038_DragonFangs extends Quest
{
	// NPCs
	private static final int LUIS = 30386;
	private static final int IRIS = 30034;
	private static final int ROHMER = 30344;
	// Items
	private static final int FEATHER_ORNAMENT = 7173;
	private static final int TOOTH_OF_TOTEM = 7174;
	private static final int TOOTH_OF_DRAGON = 7175;
	private static final int LETTER_OF_IRIS = 7176;
	private static final int LETTER_OF_ROHMER = 7177;
	// Reward { item, adena }
	private static final int[][] REWARD =
	{
		// @formatter:off
		{45, 5200},
		{627, 1500},
		{1123, 3200},
		{605, 3200}
		// @formatter:on
	};
	// Droplist
	private static final Map<Integer, int[]> DROPLIST = new HashMap<>();
	static
	{
		// @formatter:off
		DROPLIST.put(21100, new int[]{1, FEATHER_ORNAMENT, 1000000, 2});
		DROPLIST.put(20357, new int[]{1, FEATHER_ORNAMENT, 1000000, 2});
		DROPLIST.put(21101, new int[]{6, TOOTH_OF_DRAGON, 500000, 1});
		DROPLIST.put(20356, new int[]{6, TOOTH_OF_DRAGON, 500000, 1});
		// @formatter:on
	}
	
	public Q00038_DragonFangs()
	{
		super(38);
		registerQuestItems(FEATHER_ORNAMENT, TOOTH_OF_TOTEM, TOOTH_OF_DRAGON, LETTER_OF_IRIS, LETTER_OF_ROHMER);
		addStartNpc(LUIS);
		addTalkId(LUIS, IRIS, ROHMER);
		addKillId(DROPLIST.keySet());
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
			case "30386-02.htm":
			{
				st.startQuest();
				break;
			}
			case "30386-04.htm":
			{
				st.setCond(3, true);
				takeItems(player, FEATHER_ORNAMENT, 100);
				giveItems(player, TOOTH_OF_TOTEM, 1);
				break;
			}
			case "30034-02a.htm":
			{
				if (hasQuestItems(player, TOOTH_OF_TOTEM))
				{
					htmltext = "30034-02.htm";
					st.setCond(4, true);
					takeItems(player, TOOTH_OF_TOTEM, 1);
					giveItems(player, LETTER_OF_IRIS, 1);
				}
				break;
			}
			case "30344-02a.htm":
			{
				if (hasQuestItems(player, LETTER_OF_IRIS))
				{
					htmltext = "30344-02.htm";
					st.setCond(5, true);
					takeItems(player, LETTER_OF_IRIS, 1);
					giveItems(player, LETTER_OF_ROHMER, 1);
				}
				break;
			}
			case "30034-04a.htm":
			{
				if (hasQuestItems(player, LETTER_OF_ROHMER))
				{
					htmltext = "30034-04.htm";
					st.setCond(6, true);
					takeItems(player, LETTER_OF_ROHMER, 1);
				}
				break;
			}
			case "30034-06a.htm":
			{
				if (getQuestItemsCount(player, TOOTH_OF_DRAGON) >= 50)
				{
					final int position = getRandom(REWARD.length);
					htmltext = "30034-06.htm";
					takeItems(player, TOOTH_OF_DRAGON, 50);
					giveItems(player, REWARD[position][0], 1);
					giveAdena(player, REWARD[position][1], true);
					st.exitQuest(false, true);
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
				htmltext = (player.getLevel() < 19) ? "30386-01a.htm" : "30386-01.htm";
				break;
			
			case State.STARTED:
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case LUIS:
						if (cond == 1)
						{
							htmltext = "30386-02a.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30386-03.htm";
						}
						else if (cond > 2)
						{
							htmltext = "30386-03a.htm";
						}
						break;
					
					case IRIS:
						if (cond == 3)
						{
							htmltext = "30034-01.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30034-02b.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30034-03.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30034-05a.htm";
						}
						else if (cond == 7)
						{
							htmltext = "30034-05.htm";
						}
						break;
					
					case ROHMER:
						if (cond == 4)
						{
							htmltext = "30344-01.htm";
						}
						else if (cond > 4)
						{
							htmltext = "30344-03.htm";
						}
						break;
				}
				break;
			
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
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
		
		final int[] droplist = DROPLIST.get(npc.getId());
		if (st.isCond(droplist[0]) && giveItemRandomly(st.getPlayer(), droplist[1], 1, droplist[2], droplist[3] / 1000000d, true))
		{
			st.setCond(droplist[0] + 1, true);
		}
		
		return null;
	}
}
