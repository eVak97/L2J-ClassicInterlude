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
package quests.Q00623_TheFinestFood;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00623_TheFinestFood extends Quest
{
	// NPC
	private static final int JEREMY = 31521;
	// Monsters
	private static final int FLAVA = 21316;
	private static final int BUFFALO = 21315;
	private static final int ANTELOPE = 21318;
	// Items
	private static final int LEAF_OF_FLAVA = 7199;
	private static final int BUFFALO_MEAT = 7200;
	private static final int ANTELOPE_HORN = 7201;
	
	public Q00623_TheFinestFood()
	{
		super(623);
		registerQuestItems(LEAF_OF_FLAVA, BUFFALO_MEAT, ANTELOPE_HORN);
		addStartNpc(JEREMY);
		addTalkId(JEREMY);
		addKillId(FLAVA, BUFFALO, ANTELOPE);
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
		
		if (event.equals("31521-02.htm"))
		{
			if (player.getLevel() >= 71)
			{
				st.startQuest();
			}
			else
			{
				htmltext = "31521-03.htm";
			}
		}
		else if (event.equals("31521-05.htm"))
		{
			takeItems(player, LEAF_OF_FLAVA, -1);
			takeItems(player, BUFFALO_MEAT, -1);
			takeItems(player, ANTELOPE_HORN, -1);
			
			final int luck = getRandom(100);
			if (luck < 11)
			{
				giveAdena(player, 25000, true);
				giveItems(player, 6849, 1);
			}
			else if (luck < 23)
			{
				giveAdena(player, 65000, true);
				giveItems(player, 6847, 1);
			}
			else if (luck < 33)
			{
				giveAdena(player, 25000, true);
				giveItems(player, 6851, 1);
			}
			else
			{
				giveAdena(player, 73000, true);
				addExpAndSp(player, 230000, 18250);
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
				htmltext = "31521-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "31521-06.htm";
				}
				else if (cond == 2)
				{
					if ((getQuestItemsCount(player, LEAF_OF_FLAVA) >= 100) && (getQuestItemsCount(player, BUFFALO_MEAT) >= 100) && (getQuestItemsCount(player, ANTELOPE_HORN) >= 100))
					{
						htmltext = "31521-04.htm";
					}
					else
					{
						htmltext = "31521-07.htm";
					}
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
		
		switch (npc.getId())
		{
			case FLAVA:
			{
				if (getQuestItemsCount(partyMember, LEAF_OF_FLAVA) < 100)
				{
					giveItems(partyMember, LEAF_OF_FLAVA, 1);
					if ((getQuestItemsCount(partyMember, LEAF_OF_FLAVA) >= 100) && (getQuestItemsCount(partyMember, BUFFALO_MEAT) >= 100) && (getQuestItemsCount(partyMember, ANTELOPE_HORN) >= 100))
					{
						st.setCond(2, true);
					}
					else
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case BUFFALO:
			{
				if (getQuestItemsCount(partyMember, BUFFALO_MEAT) < 100)
				{
					giveItems(partyMember, BUFFALO_MEAT, 1);
					if ((getQuestItemsCount(partyMember, BUFFALO_MEAT) >= 100) && (getQuestItemsCount(partyMember, LEAF_OF_FLAVA) >= 100) && (getQuestItemsCount(partyMember, ANTELOPE_HORN) >= 100))
					{
						st.setCond(2, true);
					}
					else
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case ANTELOPE:
			{
				if (getQuestItemsCount(partyMember, ANTELOPE_HORN) < 100)
				{
					giveItems(partyMember, ANTELOPE_HORN, 1);
					if ((getQuestItemsCount(partyMember, ANTELOPE_HORN) >= 100) && (getQuestItemsCount(partyMember, LEAF_OF_FLAVA) >= 100) && (getQuestItemsCount(partyMember, BUFFALO_MEAT) >= 100))
					{
						st.setCond(2, true);
					}
					else
					{
						playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
		}
		
		return null;
	}
}
