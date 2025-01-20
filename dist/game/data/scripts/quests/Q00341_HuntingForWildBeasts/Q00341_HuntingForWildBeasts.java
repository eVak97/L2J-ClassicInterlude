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
package quests.Q00341_HuntingForWildBeasts;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00341_HuntingForWildBeasts extends Quest
{
	// Item
	private static final int BEAR_SKIN = 4259;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(20021, 500000); // Red Bear
		CHANCES.put(20203, 900000); // Dion Grizzly
		CHANCES.put(20310, 500000); // Brown Bear
		CHANCES.put(20335, 700000); // Grizzly Bear
	}
	
	public Q00341_HuntingForWildBeasts()
	{
		super(341);
		registerQuestItems(BEAR_SKIN);
		addStartNpc(30078); // Pano
		addTalkId(30078);
		addKillId(20021, 20203, 20310, 20335);
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
		
		if (event.equals("30078-02.htm"))
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
				htmltext = (player.getLevel() < 20) ? "30078-00.htm" : "30078-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, BEAR_SKIN) < 20)
				{
					htmltext = "30078-03.htm";
				}
				else
				{
					htmltext = "30078-04.htm";
					takeItems(player, BEAR_SKIN, -1);
					giveAdena(player, 3710, true);
					st.exitQuest(true, true);
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
		
		if ((getQuestItemsCount(player, BEAR_SKIN) < 20) && (getRandom(1000000) < CHANCES.get(npc.getId())))
		{
			giveItems(player, BEAR_SKIN, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
