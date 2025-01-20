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
package quests.Q00632_NecromancersRequest;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00632_NecromancersRequest extends Quest
{
	// Monsters
	private static final int[] VAMPIRES =
	{
		21568,
		21573,
		21582,
		21585,
		21586,
		21587,
		21588,
		21589,
		21590,
		21591,
		21592,
		21593,
		21594,
		21595
	};
	private static final int[] UNDEADS =
	{
		21547,
		21548,
		21549,
		21551,
		21552,
		21555,
		21556,
		21562,
		21571,
		21576,
		21577,
		21579
	};
	// Items
	private static final int VAMPIRE_HEART = 7542;
	private static final int ZOMBIE_BRAIN = 7543;
	
	public Q00632_NecromancersRequest()
	{
		super(632);
		registerQuestItems(VAMPIRE_HEART, ZOMBIE_BRAIN);
		addStartNpc(31522); // Mysterious Wizard
		addTalkId(31522);
		addKillId(VAMPIRES);
		addKillId(UNDEADS);
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
		
		if (event.equals("31522-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31522-06.htm"))
		{
			if (getQuestItemsCount(player, VAMPIRE_HEART) >= 200)
			{
				st.setCond(1, true);
				takeItems(player, VAMPIRE_HEART, -1);
				giveAdena(player, 120000, true);
			}
			else
			{
				htmltext = "31522-09.htm";
			}
		}
		else if (event.equals("31522-08.htm"))
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
				htmltext = (player.getLevel() < 63) ? "31522-01.htm" : "31522-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (getQuestItemsCount(player, VAMPIRE_HEART) >= 200) ? "31522-05.htm" : "31522-04.htm";
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		final Player partyMember = st.getPlayer();
		
		for (int undead : UNDEADS)
		{
			if ((undead == npc.getId()) && (getRandom(100) < 33))
			{
				giveItems(partyMember, ZOMBIE_BRAIN, 1);
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				return null;
			}
		}
		
		if (st.isCond(1) && getRandomBoolean())
		{
			giveItems(partyMember, VAMPIRE_HEART, 1);
			if (getQuestItemsCount(partyMember, VAMPIRE_HEART) < 200)
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
