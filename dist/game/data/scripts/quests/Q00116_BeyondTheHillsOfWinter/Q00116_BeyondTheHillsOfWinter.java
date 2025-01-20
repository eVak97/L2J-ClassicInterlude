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
package quests.Q00116_BeyondTheHillsOfWinter;

import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00116_BeyondTheHillsOfWinter extends Quest
{
	// NPCs
	private static final int FILAUR = 30535;
	private static final int OBI = 32052;
	// Items
	private static final int BANDAGE = 1833;
	private static final int ENERGY_STONE = 5589;
	private static final int THIEF_KEY = 1661;
	private static final int GOODS = 8098;
	// Reward
	private static final int SSD = 1463;
	
	public Q00116_BeyondTheHillsOfWinter()
	{
		super(116);
		registerQuestItems(GOODS);
		addStartNpc(FILAUR);
		addTalkId(FILAUR, OBI);
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
			case "30535-02.htm":
			{
				st.startQuest();
				break;
			}
			case "30535-05.htm":
			{
				st.setCond(2, true);
				giveItems(player, GOODS, 1);
				break;
			}
			case "materials":
			{
				htmltext = "32052-02.htm";
				takeItems(player, GOODS, -1);
				rewardItems(player, SSD, 1650);
				st.exitQuest(false, true);
				break;
			}
			case "adena":
			{
				htmltext = "32052-02.htm";
				takeItems(player, GOODS, -1);
				giveAdena(player, 16500, true);
				st.exitQuest(false, true);
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
				htmltext = ((player.getLevel() < 30) || (player.getRace() != Race.DWARF)) ? "30535-00.htm" : "30535-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case FILAUR:
					{
						if (cond == 1)
						{
							if ((getQuestItemsCount(player, BANDAGE) >= 20) && (getQuestItemsCount(player, ENERGY_STONE) >= 5) && (getQuestItemsCount(player, THIEF_KEY) >= 10))
							{
								htmltext = "30535-03.htm";
								takeItems(player, BANDAGE, 20);
								takeItems(player, ENERGY_STONE, 5);
								takeItems(player, THIEF_KEY, 10);
							}
							else
							{
								htmltext = "30535-04.htm";
							}
						}
						else if (cond == 2)
						{
							htmltext = "30535-05.htm";
						}
						break;
					}
					case OBI:
					{
						if (cond == 2)
						{
							htmltext = "32052-00.htm";
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