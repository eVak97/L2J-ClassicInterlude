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
package quests.Q00247_PossessorOfAPreciousSoul4;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00247_PossessorOfAPreciousSoul4 extends Quest
{
	// NPCs
	private static final int CARADINE = 31740;
	private static final int LADY_OF_THE_LAKE = 31745;
	// Items
	private static final int CARADINE_LETTER = 7679;
	private static final int NOBLESS_TIARA = 7694;
	
	public Q00247_PossessorOfAPreciousSoul4()
	{
		super(247);
		addStartNpc(CARADINE);
		addTalkId(CARADINE, LADY_OF_THE_LAKE);
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
			case "31740-03.htm":
			{
				st.startQuest();
				takeItems(player, CARADINE_LETTER, 1);
				break;
			}
			case "31740-05.htm":
			{
				st.setCond(2);
				player.teleToLocation(143209, 43968, -3038);
				break;
			}
			case "31745-05.htm":
			{
				player.setNoble(true);
				giveItems(player, NOBLESS_TIARA, 1);
				addExpAndSp(player, 93836, 0);
				player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
				st.exitQuest(false, true);
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
				if (hasQuestItems(player, CARADINE_LETTER))
				{
					htmltext = (!player.isSubClassActive() || (player.getLevel() < 75)) ? "31740-02.htm" : "31740-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (!player.isSubClassActive())
				{
					break;
				}
				
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case CARADINE:
					{
						if (cond == 1)
						{
							htmltext = "31740-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31740-06.htm";
						}
						break;
					}
					case LADY_OF_THE_LAKE:
					{
						if (cond == 2)
						{
							htmltext = (player.getLevel() < 75) ? "31745-06.htm" : "31745-01.htm";
						}
						break;
					}
				}
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
}
