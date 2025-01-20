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
package quests.Q00641_AttackSailren;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;

import quests.Q00126_TheNameOfEvil2.Q00126_TheNameOfEvil2;

public class Q00641_AttackSailren extends Quest
{
	// NPC
	private static final int STATUE = 32109;
	// Quest Item
	private static final int GAZKH_FRAGMENT = 8782;
	private static final int GAZKH = 8784;
	
	public Q00641_AttackSailren()
	{
		super(641);
		registerQuestItems(GAZKH_FRAGMENT);
		addStartNpc(STATUE);
		addTalkId(STATUE);
		addKillId(22196, 22197, 22198, 22199, 22218, 22223);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		String htmltext = event;
		
		if (event.equals("32109-5.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("32109-8.htm"))
		{
			if (getQuestItemsCount(player, GAZKH_FRAGMENT) >= 30)
			{
				npc.broadcastPacket(new MagicSkillUse(npc, player, 5089, 1, 3000, 0));
				takeItems(player, GAZKH_FRAGMENT, -1);
				giveItems(player, GAZKH, 1);
				st.exitQuest(true, true);
			}
			else
			{
				htmltext = "32109-6.htm";
				st.setCond(1);
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
				if (player.getLevel() < 77)
				{
					htmltext = "32109-3.htm";
				}
				else
				{
					final QuestState st2 = player.getQuestState(Q00126_TheNameOfEvil2.class.getSimpleName());
					htmltext = ((st2 != null) && st2.isCompleted()) ? "32109-1.htm" : "32109-2.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "32109-5.htm";
				}
				else if (cond == 2)
				{
					htmltext = "32109-7.htm";
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
		
		if (getRandom(100) < 5)
		{
			giveItems(partyMember, GAZKH_FRAGMENT, 1);
			if (getQuestItemsCount(partyMember, GAZKH_FRAGMENT) < 30)
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
