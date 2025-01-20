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
package quests.Q00338_AlligatorHunter;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00338_AlligatorHunter extends Quest
{
	// Item
	private static final int ALLIGATOR_PELT = 4337;
	
	public Q00338_AlligatorHunter()
	{
		super(338);
		registerQuestItems(ALLIGATOR_PELT);
		addStartNpc(30892); // Enverun
		addTalkId(30892);
		addKillId(20135); // Alligator
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
			case "30892-02.htm":
			{
				st.startQuest();
				break;
			}
			case "30892-05.htm":
			{
				final int pelts = (int) getQuestItemsCount(player, ALLIGATOR_PELT);
				int reward = pelts * 40;
				if (pelts > 10)
				{
					reward += 3430;
				}
				takeItems(player, ALLIGATOR_PELT, -1);
				giveAdena(player, reward, true);
				break;
			}
			case "30892-08.htm":
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
				htmltext = (player.getLevel() < 40) ? "30892-00.htm" : "30892-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (hasQuestItems(player, ALLIGATOR_PELT)) ? "30892-03.htm" : "30892-04.htm";
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
		
		giveItems(player, ALLIGATOR_PELT, 1);
		playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		
		return null;
	}
}
