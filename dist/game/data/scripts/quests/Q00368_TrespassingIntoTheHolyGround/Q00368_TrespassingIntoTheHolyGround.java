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
package quests.Q00368_TrespassingIntoTheHolyGround;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00368_TrespassingIntoTheHolyGround extends Quest
{
	// NPC
	private static final int RESTINA = 30926;
	// Item
	private static final int FANG = 5881;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(20794, 500000);
		CHANCES.put(20795, 770000);
		CHANCES.put(20796, 500000);
		CHANCES.put(20797, 480000);
	}
	
	public Q00368_TrespassingIntoTheHolyGround()
	{
		super(368);
		registerQuestItems(FANG);
		addStartNpc(RESTINA);
		addTalkId(RESTINA);
		addKillId(20794, 20795, 20796, 20797);
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
		
		if (event.equals("30926-02.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30926-05.htm"))
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
				htmltext = (player.getLevel() < 36) ? "30926-01a.htm" : "30926-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int fangs = (int) getQuestItemsCount(player, FANG);
				if (fangs == 0)
				{
					htmltext = "30926-03.htm";
				}
				else
				{
					final int reward = (250 * fangs) + (fangs > 10 ? 5730 : 2000);
					htmltext = "30926-04.htm";
					takeItems(player, 5881, -1);
					giveAdena(player, reward, true);
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
		if (st == null)
		{
			return null;
		}
		
		if (getRandom(1000000) < CHANCES.get(npc.getId()))
		{
			giveItems(st.getPlayer(), FANG, 1);
			playSound(st.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
