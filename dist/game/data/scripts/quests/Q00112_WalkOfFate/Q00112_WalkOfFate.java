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
package quests.Q00112_WalkOfFate;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00112_WalkOfFate extends Quest
{
	// NPCs
	private static final int LIVINA = 30572;
	private static final int KARUDA = 32017;
	// Rewards
	private static final int ENCHANT_D = 956;
	
	public Q00112_WalkOfFate()
	{
		super(112);
		addStartNpc(LIVINA);
		addTalkId(LIVINA, KARUDA);
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
		
		if (event.equals("30572-02.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("32017-02.htm"))
		{
			giveItems(player, ENCHANT_D, 1);
			giveAdena(player, 4665, true);
			st.exitQuest(false, true);
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
				htmltext = (player.getLevel() < 20) ? "30572-00.htm" : "30572-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LIVINA:
					{
						htmltext = "30572-03.htm";
						break;
					}
					case KARUDA:
					{
						htmltext = "32017-01.htm";
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
