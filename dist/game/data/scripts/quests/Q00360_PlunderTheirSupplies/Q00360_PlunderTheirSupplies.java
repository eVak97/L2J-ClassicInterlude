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
package quests.Q00360_PlunderTheirSupplies;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

public class Q00360_PlunderTheirSupplies extends Quest
{
	// Items
	private static final int SUPPLY_ITEM = 5872;
	private static final int SUSPICIOUS_DOCUMENT = 5871;
	private static final int RECIPE_OF_SUPPLY = 5870;
	// Drops
	private static final Map<Integer, Integer> MONSTER_DROP_CHANCES = new HashMap<>();
	static
	{
		MONSTER_DROP_CHANCES.put(20666, 50); // Taik Orc Seeker
		MONSTER_DROP_CHANCES.put(20669, 75); // Taik Orc Supply Leader
	}
	
	public Q00360_PlunderTheirSupplies()
	{
		super(360);
		registerQuestItems(RECIPE_OF_SUPPLY, SUPPLY_ITEM, SUSPICIOUS_DOCUMENT);
		addStartNpc(30873); // Coleman
		addTalkId(30873);
		addKillId(MONSTER_DROP_CHANCES.keySet());
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30873-2.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30873-6.htm"))
		{
			takeItems(player, SUPPLY_ITEM, -1);
			takeItems(player, SUSPICIOUS_DOCUMENT, -1);
			takeItems(player, RECIPE_OF_SUPPLY, -1);
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
				htmltext = (player.getLevel() < 52) ? "30873-0a.htm" : "30873-0.htm";
				break;
			}
			case State.STARTED:
			{
				final int supplyItems = (int) getQuestItemsCount(player, SUPPLY_ITEM);
				if (supplyItems == 0)
				{
					htmltext = "30873-3.htm";
				}
				else
				{
					final int reward = (int) (6000 + (supplyItems * 100) + (getQuestItemsCount(player, RECIPE_OF_SUPPLY) * 6000));
					htmltext = "30873-5.htm";
					takeItems(player, SUPPLY_ITEM, -1);
					takeItems(player, RECIPE_OF_SUPPLY, -1);
					giveAdena(player, reward, true);
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs == null) || !Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, false))
		{
			return super.onKill(npc, killer, isPet);
		}
		
		if (getRandom(100) < MONSTER_DROP_CHANCES.get(npc.getId()))
		{
			giveItems(killer, SUPPLY_ITEM, 1);
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		if (getRandom(100) < 10)
		{
			if (getQuestItemsCount(killer, SUSPICIOUS_DOCUMENT) < 4)
			{
				giveItems(killer, SUSPICIOUS_DOCUMENT, 1);
			}
			else
			{
				giveItems(killer, RECIPE_OF_SUPPLY, 1);
				takeItems(killer, SUSPICIOUS_DOCUMENT, -1);
			}
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return super.onKill(npc, killer, isPet);
	}
}
