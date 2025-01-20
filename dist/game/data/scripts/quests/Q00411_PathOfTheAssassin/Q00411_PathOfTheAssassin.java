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
package quests.Q00411_PathOfTheAssassin;

import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00411_PathOfTheAssassin extends Quest
{
	// NPCs
	private static final int TRISKEL = 30416;
	private static final int ARKENIA = 30419;
	private static final int LEIKAN = 30382;
	// Items
	private static final int SHILEN_CALL = 1245;
	private static final int ARKENIA_LETTER = 1246;
	private static final int LEIKAN_NOTE = 1247;
	private static final int MOONSTONE_BEAST_MOLAR = 1248;
	private static final int SHILEN_TEARS = 1250;
	private static final int ARKENIA_RECOMMENDATION = 1251;
	private static final int IRON_HEART = 1252;
	
	public Q00411_PathOfTheAssassin()
	{
		super(411);
		registerQuestItems(SHILEN_CALL, ARKENIA_LETTER, LEIKAN_NOTE, MOONSTONE_BEAST_MOLAR, SHILEN_TEARS, ARKENIA_RECOMMENDATION);
		addStartNpc(TRISKEL);
		addTalkId(TRISKEL, ARKENIA, LEIKAN);
		addKillId(27036, 20369);
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
			case "30416-05.htm":
			{
				if (player.getClassId() != ClassId.DARK_FIGHTER)
				{
					htmltext = (player.getClassId() == ClassId.ASSASSIN) ? "30416-02a.htm" : "30416-02.htm";
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "30416-03.htm";
				}
				else if (hasQuestItems(player, IRON_HEART))
				{
					htmltext = "30416-04.htm";
				}
				else
				{
					st.startQuest();
					giveItems(player, SHILEN_CALL, 1);
				}
				break;
			}
			case "30419-05.htm":
			{
				st.setCond(2, true);
				takeItems(player, SHILEN_CALL, 1);
				giveItems(player, ARKENIA_LETTER, 1);
				break;
			}
			case "30382-03.htm":
			{
				st.setCond(3, true);
				takeItems(player, ARKENIA_LETTER, 1);
				giveItems(player, LEIKAN_NOTE, 1);
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
				htmltext = "30416-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case TRISKEL:
					{
						if (cond == 1)
						{
							htmltext = "30416-11.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30416-07.htm";
						}
						else if ((cond == 3) || (cond == 4))
						{
							htmltext = "30416-08.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30416-09.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30416-10.htm";
						}
						else if (cond == 7)
						{
							htmltext = "30416-06.htm";
							takeItems(player, ARKENIA_RECOMMENDATION, 1);
							giveItems(player, IRON_HEART, 1);
							addExpAndSp(player, 3200, 3930);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(true, true);
						}
						break;
					}
					case ARKENIA:
					{
						if (cond == 1)
						{
							htmltext = "30419-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30419-07.htm";
						}
						else if ((cond == 3) || (cond == 4))
						{
							htmltext = "30419-10.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30419-11.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30419-08.htm";
							st.setCond(7, true);
							takeItems(player, SHILEN_TEARS, -1);
							giveItems(player, ARKENIA_RECOMMENDATION, 1);
						}
						else if (cond == 7)
						{
							htmltext = "30419-09.htm";
						}
						break;
					}
					case LEIKAN:
					{
						if (cond == 2)
						{
							htmltext = "30382-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = (!hasQuestItems(player, MOONSTONE_BEAST_MOLAR)) ? "30382-05.htm" : "30382-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30382-07.htm";
							st.setCond(5, true);
							takeItems(player, MOONSTONE_BEAST_MOLAR, -1);
							takeItems(player, LEIKAN_NOTE, -1);
						}
						else if (cond == 5)
						{
							htmltext = "30382-09.htm";
						}
						else if (cond > 5)
						{
							htmltext = "30382-08.htm";
						}
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
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		if (npc.getId() == 20369)
		{
			if (st.isCond(3))
			{
				giveItems(player, MOONSTONE_BEAST_MOLAR, 1);
				if (getQuestItemsCount(player, MOONSTONE_BEAST_MOLAR) < 10)
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					st.setCond(4, true);
				}
			}
		}
		else if (st.isCond(5))
		{
			st.setCond(6, true);
			giveItems(player, SHILEN_TEARS, 1);
		}
		
		return null;
	}
}
