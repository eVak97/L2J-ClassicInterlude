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
package quests.Q00431_WeddingMarch;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00431_WeddingMarch extends Quest
{
	// NPC
	private static final int KANTABILON = 31042;
	// Item
	private static final int SILVER_CRYSTAL = 7540;
	// Reward
	private static final int WEDDING_ECHO_CRYSTAL = 7062;
	
	public Q00431_WeddingMarch()
	{
		super(431);
		registerQuestItems(SILVER_CRYSTAL);
		addStartNpc(KANTABILON);
		addTalkId(KANTABILON);
		addKillId(20786, 20787);
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
		
		if (event.equals("31042-02.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31042-05.htm"))
		{
			if (getQuestItemsCount(player, SILVER_CRYSTAL) < 50)
			{
				htmltext = "31042-03.htm";
			}
			else
			{
				takeItems(player, SILVER_CRYSTAL, -1);
				giveItems(player, WEDDING_ECHO_CRYSTAL, 25);
				st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 38) ? "31042-00.htm" : "31042-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "31042-02.htm";
				}
				else if (cond == 2)
				{
					htmltext = (getQuestItemsCount(player, SILVER_CRYSTAL) < 50) ? "31042-03.htm" : "31042-04.htm";
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
		
		if (getRandomBoolean())
		{
			giveItems(partyMember, SILVER_CRYSTAL, 1);
			if (getQuestItemsCount(partyMember, SILVER_CRYSTAL) < 50)
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
