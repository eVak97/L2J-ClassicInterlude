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
package quests.Q00643_RiseAndFallOfTheElrokiTribe;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00643_RiseAndFallOfTheElrokiTribe extends Quest
{
	// NPCs
	private static final int SINGSING = 32106;
	private static final int KARAKAWEI = 32117;
	// Items
	private static final int BONES = 8776;
	
	public Q00643_RiseAndFallOfTheElrokiTribe()
	{
		super(643);
		registerQuestItems(BONES);
		addStartNpc(SINGSING);
		addTalkId(SINGSING, KARAKAWEI);
		addKillId(22208, 22209, 22210, 22211, 22212, 22213, 22221, 22222, 22226, 22227);
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
		
		switch (event)
		{
			case "32106-03.htm":
			{
				st.startQuest();
				break;
			}
			case "32106-07.htm":
			{
				final int count = (int) getQuestItemsCount(player, BONES);
				takeItems(player, BONES, count);
				giveAdena(player, count * 1374, true);
				break;
			}
			case "32106-09.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "32117-03.htm":
			{
				final int count = (int) getQuestItemsCount(player, BONES);
				if (count >= 300)
				{
					takeItems(player, BONES, 300);
					rewardItems(player, getRandom(8712, 8722), 5);
				}
				else
				{
					htmltext = "32117-04.htm";
				}
				break;
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
				htmltext = (player.getLevel() < 75) ? "32106-00.htm" : "32106-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SINGSING:
					{
						htmltext = (hasQuestItems(player, BONES)) ? "32106-06.htm" : "32106-05.htm";
						break;
					}
					case KARAKAWEI:
					{
						htmltext = "32117-01.htm";
						break;
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
		final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		final Player partyMember = st.getPlayer();
		
		if (getRandom(100) < 75)
		{
			giveItems(partyMember, BONES, 1);
			playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
