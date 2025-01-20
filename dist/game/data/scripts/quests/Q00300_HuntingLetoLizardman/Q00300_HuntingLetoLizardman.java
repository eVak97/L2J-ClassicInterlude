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
package quests.Q00300_HuntingLetoLizardman;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00300_HuntingLetoLizardman extends Quest
{
	// Monsters
	private static final int LETO_LIZARDMAN = 20577;
	private static final int LETO_LIZARDMAN_ARCHER = 20578;
	private static final int LETO_LIZARDMAN_SOLDIER = 20579;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	// Item
	private static final int BRACELET = 7139;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(LETO_LIZARDMAN, 300000);
		CHANCES.put(LETO_LIZARDMAN_ARCHER, 320000);
		CHANCES.put(LETO_LIZARDMAN_SOLDIER, 350000);
		CHANCES.put(LETO_LIZARDMAN_WARRIOR, 650000);
		CHANCES.put(LETO_LIZARDMAN_OVERLORD, 700000);
	}
	
	public Q00300_HuntingLetoLizardman()
	{
		super(300);
		registerQuestItems(BRACELET);
		addStartNpc(30126); // Rath
		addTalkId(30126);
		addKillId(LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_OVERLORD);
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
		
		if (event.equals("30126-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30126-05.htm") && (getQuestItemsCount(player, BRACELET) >= 60))
		{
			htmltext = "30126-06.htm";
			takeItems(player, BRACELET, -1);
			
			final int luck = getRandom(3);
			if (luck == 0)
			{
				giveAdena(player, 30000, true);
			}
			else if (luck == 1)
			{
				rewardItems(player, 1867, 50);
			}
			else if (luck == 2)
			{
				rewardItems(player, 1872, 50);
			}
			
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
				htmltext = (player.getLevel() < 34) ? "30126-01.htm" : "30126-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = st.isCond(1) ? "30126-04a.htm" : "30126-04.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getRandomPartyMemberState(player, 1, 3, npc);
		if (st == null)
		{
			return null;
		}
		final Player partyMember = st.getPlayer();
		
		if (getRandom(1000000) < CHANCES.get(npc.getId()))
		{
			giveItems(partyMember, BRACELET, 1);
			if (getQuestItemsCount(partyMember, BRACELET) < 60)
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
