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
package quests.Q00107_MercilessPunishment;

import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00107_MercilessPunishment extends Quest
{
	// NPCs
	private static final int HATOS = 30568;
	private static final int PARUGON = 30580;
	// Items
	private static final int HATOS_ORDER_1 = 1553;
	private static final int HATOS_ORDER_2 = 1554;
	private static final int HATOS_ORDER_3 = 1555;
	private static final int LETTER_TO_HUMAN = 1557;
	private static final int LETTER_TO_DARKELF = 1556;
	private static final int LETTER_TO_ELF = 1558;
	// Rewards
	private static final int BUTCHER_SWORD = 1510;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	private static final int LESSER_HEALING_POTION = 1060;
	
	public Q00107_MercilessPunishment()
	{
		super(107);
		registerQuestItems(HATOS_ORDER_1, HATOS_ORDER_2, HATOS_ORDER_3, LETTER_TO_HUMAN, LETTER_TO_DARKELF, LETTER_TO_ELF);
		addStartNpc(HATOS);
		addTalkId(HATOS, PARUGON);
		addKillId(27041); // Baranka's Messenger
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState st = player.getQuestState(getName());
		final String htmltext = event;
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30568-03.htm":
			{
				st.startQuest();
				giveItems(player, HATOS_ORDER_1, 1);
				break;
			}
			case "30568-06.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "30568-07.htm":
			{
				st.setCond(4, true);
				takeItems(player, HATOS_ORDER_1, 1);
				giveItems(player, HATOS_ORDER_2, 1);
				break;
			}
			case "30568-09.htm":
			{
				st.setCond(6, true);
				takeItems(player, HATOS_ORDER_2, 1);
				giveItems(player, HATOS_ORDER_3, 1);
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
				if (player.getRace() != Race.ORC)
				{
					htmltext = "30568-00.htm";
				}
				else if (player.getLevel() < 12)
				{
					htmltext = "30568-01.htm";
				}
				else
				{
					htmltext = "30568-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case HATOS:
					{
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "30568-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30568-05.htm";
						}
						else if ((cond == 4) || (cond == 6))
						{
							htmltext = "30568-09.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30568-08.htm";
						}
						else if (cond == 7)
						{
							htmltext = "30568-10.htm";
							takeItems(player, HATOS_ORDER_3, -1);
							takeItems(player, LETTER_TO_DARKELF, -1);
							takeItems(player, LETTER_TO_HUMAN, -1);
							takeItems(player, LETTER_TO_ELF, -1);
							
							giveItems(player, BUTCHER_SWORD, 1);
							giveItems(player, LESSER_HEALING_POTION, 100);
							
							final PlayerVariables vars = player.getVariables();
							// Give newbie reward if player is eligible
							if ((player.getLevel() < 25) && !vars.getBoolean("NEWBIE_SHOTS", false))
							{
								st.showQuestionMark(26);
								st.playTutorialVoice("tutorial_voice_026");
								giveItems(player, SOULSHOT_FOR_BEGINNERS, 7000);
							}
							
							giveItems(player, ECHO_BATTLE, 10);
							giveItems(player, ECHO_LOVE, 10);
							giveItems(player, ECHO_SOLITUDE, 10);
							giveItems(player, ECHO_FEAST, 10);
							giveItems(player, ECHO_CELEBRATION, 10);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(false, true);
						}
						break;
					}
					case PARUGON:
					{
						htmltext = "30580-01.htm";
						if (cond == 1)
						{
							st.setCond(2, true);
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
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isStarted())
		{
			return super.onKill(npc, player, isPet);
		}
		
		final int cond = st.getCond();
		if (cond == 2)
		{
			st.setCond(3, true);
			giveItems(player, LETTER_TO_HUMAN, 1);
		}
		else if (cond == 4)
		{
			st.setCond(5, true);
			giveItems(player, LETTER_TO_DARKELF, 1);
		}
		else if (cond == 6)
		{
			st.setCond(7, true);
			giveItems(player, LETTER_TO_ELF, 1);
		}
		
		return super.onKill(npc, player, isPet);
	}
}
