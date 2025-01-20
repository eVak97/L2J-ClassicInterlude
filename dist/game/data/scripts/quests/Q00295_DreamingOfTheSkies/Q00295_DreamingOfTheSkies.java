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
package quests.Q00295_DreamingOfTheSkies;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00295_DreamingOfTheSkies extends Quest
{
	// Item
	private static final int FLOATING_STONE = 1492;
	// Reward
	private static final int RING_OF_FIREFLY = 1509;
	
	public Q00295_DreamingOfTheSkies()
	{
		super(295);
		registerQuestItems(FLOATING_STONE);
		addStartNpc(30536); // Arin
		addTalkId(30536);
		addKillId(20153); // Magical Weaver
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
		
		if (event.equals("30536-03.htm"))
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
				htmltext = (player.getLevel() < 11) ? "30536-01.htm" : "30536-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "30536-04.htm";
				}
				else
				{
					takeItems(player, FLOATING_STONE, -1);
					
					if (!hasQuestItems(player, RING_OF_FIREFLY))
					{
						htmltext = "30536-05.htm";
						giveItems(player, RING_OF_FIREFLY, 1);
					}
					else
					{
						htmltext = "30536-06.htm";
						giveAdena(player, 2400, true);
					}
					
					addExpAndSp(player, 0, 500);
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
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		giveItems(player, FLOATING_STONE, getRandom(100) > 25 ? 1 : 2);
		if (getQuestItemsCount(player, FLOATING_STONE) < 50)
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else
		{
			st.setCond(2, true);
		}
		
		return null;
	}
}
