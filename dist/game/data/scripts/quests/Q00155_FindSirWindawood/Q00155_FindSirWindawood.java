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
package quests.Q00155_FindSirWindawood;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00155_FindSirWindawood extends Quest
{
	// NPCs
	private static final int ABELLOS = 30042;
	private static final int WINDAWOOD = 30311;
	// Items
	private static final int OFFICIAL_LETTER = 1019;
	private static final int HASTE_POTION = 734;
	
	public Q00155_FindSirWindawood()
	{
		super(155);
		registerQuestItems(OFFICIAL_LETTER);
		addStartNpc(ABELLOS);
		addTalkId(WINDAWOOD, ABELLOS);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30042-02.htm"))
		{
			st.startQuest();
			giveItems(player, OFFICIAL_LETTER, 1);
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
				htmltext = (player.getLevel() < 3) ? "30042-01a.htm" : "30042-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ABELLOS:
					{
						htmltext = "30042-03.htm";
						break;
					}
					case WINDAWOOD:
					{
						if (hasQuestItems(player, OFFICIAL_LETTER))
						{
							htmltext = "30311-01.htm";
							takeItems(player, OFFICIAL_LETTER, 1);
							rewardItems(player, HASTE_POTION, 1);
							st.exitQuest(false, true);
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
