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
package quests.Q00328_SenseForBusiness;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00328_SenseForBusiness extends Quest
{
	// Items
	private static final int MONSTER_EYE_LENS = 1366;
	private static final int MONSTER_EYE_CARCASS = 1347;
	private static final int BASILISK_GIZZARD = 1348;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(20055, 48);
		CHANCES.put(20059, 52);
		CHANCES.put(20067, 68);
		CHANCES.put(20068, 76);
		CHANCES.put(20070, 500000);
		CHANCES.put(20072, 510000);
	}
	
	public Q00328_SenseForBusiness()
	{
		super(328);
		registerQuestItems(MONSTER_EYE_LENS, MONSTER_EYE_CARCASS, BASILISK_GIZZARD);
		addStartNpc(30436); // Sarien
		addTalkId(30436);
		addKillId(20055, 20059, 20067, 20068, 20070, 20072);
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
		
		if (event.equals("30436-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30436-06.htm"))
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
				htmltext = (player.getLevel() < 21) ? "30436-01.htm" : "30436-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int carcasses = (int) getQuestItemsCount(player, MONSTER_EYE_CARCASS);
				final int lenses = (int) getQuestItemsCount(player, MONSTER_EYE_LENS);
				final int gizzards = (int) getQuestItemsCount(player, BASILISK_GIZZARD);
				final int all = carcasses + lenses + gizzards;
				if (all == 0)
				{
					htmltext = "30436-04.htm";
				}
				else
				{
					htmltext = "30436-05.htm";
					takeItems(player, MONSTER_EYE_CARCASS, -1);
					takeItems(player, MONSTER_EYE_LENS, -1);
					takeItems(player, BASILISK_GIZZARD, -1);
					giveAdena(player, (30 * carcasses) + (2000 * lenses) + (75 * gizzards) + ((all >= 10) ? 618 : 0), true);
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
		
		final int npcId = npc.getId();
		final int chance = CHANCES.get(npcId);
		if (npcId < 20069)
		{
			final int rnd = getRandom(100);
			if (rnd < (chance + 1))
			{
				giveItems(player, rnd < chance ? MONSTER_EYE_CARCASS : MONSTER_EYE_LENS, 1);
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		else if (getRandom(1000000) < chance)
		{
			giveItems(player, BASILISK_GIZZARD, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
