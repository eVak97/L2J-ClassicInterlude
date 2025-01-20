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
package quests.Q00624_TheFinestIngredientsPart1;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00624_TheFinestIngredientsPart1 extends Quest
{
	// Monsters
	private static final int NEPENTHES = 21319;
	private static final int ATROX = 21321;
	private static final int ATROXSPAWN = 21317;
	private static final int BANDERSNATCH = 21314;
	// Items
	private static final int TRUNK_OF_NEPENTHES = 7202;
	private static final int FOOT_OF_BANDERSNATCHLING = 7203;
	private static final int SECRET_SPICE = 7204;
	// Rewards
	private static final int ICE_CRYSTAL = 7080;
	private static final int SOY_SAUCE_JAR = 7205;
	
	public Q00624_TheFinestIngredientsPart1()
	{
		super(624);
		registerQuestItems(TRUNK_OF_NEPENTHES, FOOT_OF_BANDERSNATCHLING, SECRET_SPICE);
		addStartNpc(31521); // Jeremy
		addTalkId(31521);
		addKillId(NEPENTHES, ATROX, ATROXSPAWN, BANDERSNATCH);
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
			st.startQuest();
		}
		else if (event.equals("31521-05.htm"))
		{
			if ((getQuestItemsCount(player, TRUNK_OF_NEPENTHES) >= 50) && (getQuestItemsCount(player, FOOT_OF_BANDERSNATCHLING) >= 50) && (getQuestItemsCount(player, SECRET_SPICE) >= 50))
			{
				takeItems(player, TRUNK_OF_NEPENTHES, -1);
				takeItems(player, FOOT_OF_BANDERSNATCHLING, -1);
				takeItems(player, SECRET_SPICE, -1);
				giveItems(player, ICE_CRYSTAL, 1);
				giveItems(player, SOY_SAUCE_JAR, 1);
				st.exitQuest(true, true);
			}
			else
			{
				st.setCond(1);
				htmltext = "31521-07.htm";
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
				htmltext = (player.getLevel() < 73) ? "31521-03.htm" : "31521-01.htm";
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
					if ((getQuestItemsCount(player, TRUNK_OF_NEPENTHES) >= 50) && (getQuestItemsCount(player, FOOT_OF_BANDERSNATCHLING) >= 50) && (getQuestItemsCount(player, SECRET_SPICE) >= 50))
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
			case NEPENTHES:
			{
				if (getQuestItemsCount(partyMember, TRUNK_OF_NEPENTHES) < 50)
				{
					giveItems(partyMember, TRUNK_OF_NEPENTHES, 1);
					if ((getQuestItemsCount(partyMember, TRUNK_OF_NEPENTHES) >= 50) && (getQuestItemsCount(player, FOOT_OF_BANDERSNATCHLING) >= 50) && (getQuestItemsCount(player, SECRET_SPICE) >= 50))
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
			case ATROX:
			case ATROXSPAWN:
			{
				if (getQuestItemsCount(partyMember, SECRET_SPICE) < 50)
				{
					giveItems(partyMember, SECRET_SPICE, 1);
					if ((getQuestItemsCount(partyMember, SECRET_SPICE) >= 50) && (getQuestItemsCount(player, TRUNK_OF_NEPENTHES) >= 50) && (getQuestItemsCount(player, FOOT_OF_BANDERSNATCHLING) >= 50))
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
			case BANDERSNATCH:
			{
				if (getQuestItemsCount(partyMember, FOOT_OF_BANDERSNATCHLING) < 50)
				{
					giveItems(partyMember, FOOT_OF_BANDERSNATCHLING, 1);
					if ((getQuestItemsCount(partyMember, FOOT_OF_BANDERSNATCHLING) >= 50) && (getQuestItemsCount(player, TRUNK_OF_NEPENTHES) >= 50) && (getQuestItemsCount(player, SECRET_SPICE) >= 50))
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
