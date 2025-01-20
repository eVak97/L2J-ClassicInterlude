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
package quests.Q00051_OFullesSpecialBait;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00051_OFullesSpecialBait extends Quest
{
	// Item
	private static final int LOST_BAIT = 7622;
	// Reward
	private static final int ICY_AIR_LURE = 7611;
	
	public Q00051_OFullesSpecialBait()
	{
		super(51);
		registerQuestItems(LOST_BAIT);
		addStartNpc(31572); // O'Fulle
		addTalkId(31572);
		addKillId(20552); // Fettered Soul
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
		
		if (event.equals("31572-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31572-07.htm"))
		{
			htmltext = "31572-06.htm";
			takeItems(player, LOST_BAIT, -1);
			rewardItems(player, ICY_AIR_LURE, 4);
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
				htmltext = (player.getLevel() < 36) ? "31572-02.htm" : "31572-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (getQuestItemsCount(player, LOST_BAIT) == 100) ? "31572-04.htm" : "31572-05.htm";
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
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Player partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null)
		{
			return null;
		}
		
		final QuestState qs = getQuestState(partyMember, false);
		if (getQuestItemsCount(partyMember, LOST_BAIT) < 100)
		{
			final float chance = 33 * Config.RATE_QUEST_DROP;
			if (getRandom(100) < chance)
			{
				giveItems(partyMember, LOST_BAIT, 1);
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		if (getQuestItemsCount(partyMember, LOST_BAIT) >= 100)
		{
			qs.setCond(2, true);
		}
		
		return super.onKill(npc, player, isSummon);
	}
}
