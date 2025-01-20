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
package quests.Q00357_WarehouseKeepersAmbition;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00357_WarehouseKeepersAmbition extends Quest
{
	// Monsters
	private static final int FOREST_RUNNER = 20594;
	private static final int FLINE_ELDER = 20595;
	private static final int LIELE_ELDER = 20596;
	private static final int VALLEY_TREANT_ELDER = 20597;
	// Item
	private static final int JADE_CRYSTAL = 5867;
	// Drop chances
	private static final Map<Integer, Double> DROP_DATA = new HashMap<>();
	static
	{
		DROP_DATA.put(FOREST_RUNNER, 0.577); // Forest Runner
		DROP_DATA.put(FLINE_ELDER, 0.6); // Fline Elder
		DROP_DATA.put(LIELE_ELDER, 0.638); // Liele Elder
		DROP_DATA.put(VALLEY_TREANT_ELDER, 0.062); // Valley Treant Elder
	}
	
	public Q00357_WarehouseKeepersAmbition()
	{
		super(357);
		registerQuestItems(JADE_CRYSTAL);
		addStartNpc(30686); // Silva
		addTalkId(30686);
		addKillId(FOREST_RUNNER, FLINE_ELDER, LIELE_ELDER, VALLEY_TREANT_ELDER);
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
			case "30686-2.htm":
			{
				st.startQuest();
				break;
			}
			case "30686-7.htm":
			{
				final int count = (int) getQuestItemsCount(player, JADE_CRYSTAL);
				if (count == 0)
				{
					htmltext = "30686-4.htm";
				}
				else
				{
					int reward = (count * 425) + 3500;
					if (count >= 100)
					{
						reward += 7400;
					}
					
					takeItems(player, JADE_CRYSTAL, -1);
					giveAdena(player, reward, true);
				}
				break;
			}
			case "30686-8.htm":
			{
				st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 47) ? "30686-0a.htm" : "30686-0.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (!hasQuestItems(player, JADE_CRYSTAL)) ? "30686-4.htm" : "30686-6.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if (qs != null)
		{
			giveItemRandomly(qs.getPlayer(), npc, JADE_CRYSTAL, 1, 0, DROP_DATA.get(npc.getId()), true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
