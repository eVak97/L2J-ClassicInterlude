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
package quests.Q00103_SpiritOfCraftsman;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.util.Util;

import quests.Q00101_SwordOfSolidarity.Q00101_SwordOfSolidarity;

public class Q00103_SpiritOfCraftsman extends Quest
{
	// NPCs
	private static final int KARROD = 30307;
	private static final int CECKTINON = 30132;
	private static final int HARNE = 30144;
	// Items
	private static final int KARROD_LETTER = 968;
	private static final int CECKTINON_VOUCHER_1 = 969;
	private static final int CECKTINON_VOUCHER_2 = 970;
	private static final int SOUL_CATCHER = 971;
	private static final int PRESERVING_OIL = 972;
	private static final int ZOMBIE_HEAD = 973;
	private static final int STEELBENDER_HEAD = 974;
	private static final int BONE_FRAGMENT = 1107;
	// Rewards
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int BLOODSABER = 975;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public Q00103_SpiritOfCraftsman()
	{
		super(103);
		registerQuestItems(KARROD_LETTER, CECKTINON_VOUCHER_1, CECKTINON_VOUCHER_2, BONE_FRAGMENT, SOUL_CATCHER, PRESERVING_OIL, ZOMBIE_HEAD, STEELBENDER_HEAD);
		addStartNpc(KARROD);
		addTalkId(KARROD, CECKTINON, HARNE);
		addKillId(20015, 20020, 20455, 20517, 20518);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30307-05.htm"))
		{
			st.startQuest();
			giveItems(player, KARROD_LETTER, 1);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "30307-00.htm";
				}
				else if (player.getLevel() < 11)
				{
					htmltext = "30307-02.htm";
				}
				else
				{
					htmltext = "30307-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case KARROD:
					{
						if (cond < 8)
						{
							htmltext = "30307-06.htm";
						}
						else if (cond == 8)
						{
							htmltext = "30307-07.htm";
							takeItems(player, STEELBENDER_HEAD, 1);
							giveItems(player, BLOODSABER, 1);
							rewardItems(player, LESSER_HEALING_POT, 100);
							
							if (player.isMageClass())
							{
								giveItems(player, SPIRITSHOT_NO_GRADE, 500);
							}
							else
							{
								giveItems(player, SOULSHOT_NO_GRADE, 1000);
							}
							
							final PlayerVariables vars = player.getVariables();
							// Give newbie reward if player is eligible
							if ((player.getLevel() < 25) && !vars.getBoolean("NEWBIE_SHOTS", false))
							{
								st.showQuestionMark(26);
								Q00101_SwordOfSolidarity.giveNewbieReward(player); // All these newbie quest share same soulshots/spiritshots rewards.
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
					case CECKTINON:
					{
						if (cond == 1)
						{
							htmltext = "30132-01.htm";
							st.setCond(2, true);
							takeItems(player, KARROD_LETTER, 1);
							giveItems(player, CECKTINON_VOUCHER_1, 1);
						}
						else if ((cond > 1) && (cond < 5))
						{
							htmltext = "30132-02.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30132-03.htm";
							st.setCond(6, true);
							takeItems(player, SOUL_CATCHER, 1);
							giveItems(player, PRESERVING_OIL, 1);
						}
						else if (cond == 6)
						{
							htmltext = "30132-04.htm";
						}
						else if (cond == 7)
						{
							htmltext = "30132-05.htm";
							st.setCond(8, true);
							takeItems(player, ZOMBIE_HEAD, 1);
							giveItems(player, STEELBENDER_HEAD, 1);
						}
						else if (cond == 8)
						{
							htmltext = "30132-06.htm";
						}
						break;
					}
					case HARNE:
					{
						if (cond == 2)
						{
							htmltext = "30144-01.htm";
							st.setCond(3, true);
							takeItems(player, CECKTINON_VOUCHER_1, 1);
							giveItems(player, CECKTINON_VOUCHER_2, 1);
						}
						else if (cond == 3)
						{
							htmltext = "30144-02.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30144-03.htm";
							st.setCond(5, true);
							takeItems(player, CECKTINON_VOUCHER_2, 1);
							takeItems(player, BONE_FRAGMENT, 10);
							giveItems(player, SOUL_CATCHER, 1);
						}
						else if (cond == 5)
						{
							htmltext = "30144-04.htm";
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
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if (qs == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		switch (npc.getId())
		{
			case 20015:
			case 20020:
			{
				if (hasQuestItems(killer, PRESERVING_OIL) && (getRandom(10) < 5) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
				{
					giveItems(killer, ZOMBIE_HEAD, 1);
					takeItems(killer, PRESERVING_OIL, -1);
					qs.setCond(7, true);
				}
				break;
			}
			case 20517:
			case 20518:
			case 20455:
			{
				if (hasQuestItems(killer, CECKTINON_VOUCHER_2) && giveItemRandomly(qs.getPlayer(), npc, BONE_FRAGMENT, 1, 10, 1, true))
				{
					qs.setCond(4, true);
				}
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
