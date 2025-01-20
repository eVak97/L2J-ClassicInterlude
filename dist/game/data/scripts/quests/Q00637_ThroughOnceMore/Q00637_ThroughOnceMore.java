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
package quests.Q00637_ThroughOnceMore;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00637_ThroughOnceMore extends Quest
{
	// NPC
	private static final int FLAURON = 32010;
	// Items
	private static final int FADED_VISITOR_MARK = 8065;
	private static final int NECROMANCER_HEART = 8066;
	// Reward
	private static final int PAGAN_MARK = 8067;
	
	public Q00637_ThroughOnceMore()
	{
		super(637);
		registerQuestItems(NECROMANCER_HEART);
		addStartNpc(FLAURON);
		addTalkId(FLAURON);
		addKillId(21565, 21566, 21567);
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
		
		if (event.equals("32010-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("32010-10.htm"))
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
				if ((player.getLevel() < 73) || !hasQuestItems(player, FADED_VISITOR_MARK))
				{
					htmltext = "32010-01a.htm";
				}
				else if (hasQuestItems(player, PAGAN_MARK))
				{
					htmltext = "32010-00.htm";
				}
				else
				{
					htmltext = "32010-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(2))
				{
					if (getQuestItemsCount(player, NECROMANCER_HEART) == 10)
					{
						htmltext = "32010-06.htm";
						takeItems(player, FADED_VISITOR_MARK, 1);
						takeItems(player, NECROMANCER_HEART, -1);
						giveItems(player, PAGAN_MARK, 1);
						giveItems(player, 8273, 10);
						st.exitQuest(true, true);
					}
					else
					{
						st.setCond(1);
					}
				}
				else
				{
					htmltext = "32010-05.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState qs = getRandomPartyMemberState(player, 1, 3, npc);
		if (qs == null)
		{
			return null;
		}
		final Player partyMember = qs.getPlayer();
		
		final QuestState st = getQuestState(partyMember, false);
		if (st == null)
		{
			return null;
		}
		
		if (getRandom(10) < 4)
		{
			giveItems(partyMember, NECROMANCER_HEART, 1);
			if (getQuestItemsCount(partyMember, NECROMANCER_HEART) < 10)
			{
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				st.setCond(2, true);
			}
		}
		
		return null;
	}
}
