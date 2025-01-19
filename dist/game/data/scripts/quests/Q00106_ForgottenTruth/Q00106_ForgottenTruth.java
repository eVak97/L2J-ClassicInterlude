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
package quests.Q00106_ForgottenTruth;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
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

public class Q00106_ForgottenTruth extends Quest
{
	// NPCs
	private static final int THIFIELL = 30358;
	private static final int KARTIA = 30133;
	// Items
	private static final int ONYX_TALISMAN_1 = 984;
	private static final int ONYX_TALISMAN_2 = 985;
	private static final int ANCIENT_SCROLL = 986;
	private static final int ANCIENT_CLAY_TABLET = 987;
	private static final int KARTIA_TRANSLATION = 988;
	// Rewards
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int ELDRITCH_DAGGER = 989;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	private static final int LESSER_HEALING_POTION = 1060;
	
	public Q00106_ForgottenTruth()
	{
		super(106);
		registerQuestItems(ONYX_TALISMAN_1, ONYX_TALISMAN_2, ANCIENT_SCROLL, ANCIENT_CLAY_TABLET, KARTIA_TRANSLATION);
		addStartNpc(THIFIELL);
		addTalkId(THIFIELL, KARTIA);
		addKillId(27070); // Tumran Orc Brigand
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
		
		if (event.equals("30358-05.htm"))
		{
			st.startQuest();
			giveItems(player, ONYX_TALISMAN_1, 1);
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
					htmltext = "30358-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "30358-02.htm";
				}
				else
				{
					htmltext = "30358-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case THIFIELL:
					{
						if (cond == 1)
						{
							htmltext = "30358-06.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30358-06.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30358-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30358-07.htm";
							takeItems(player, KARTIA_TRANSLATION, 1);
							giveItems(player, ELDRITCH_DAGGER, 1);
							giveItems(player, LESSER_HEALING_POTION, 100);
							
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
					case KARTIA:
					{
						if (cond == 1)
						{
							htmltext = "30133-01.htm";
							st.setCond(2, true);
							takeItems(player, ONYX_TALISMAN_1, 1);
							giveItems(player, ONYX_TALISMAN_2, 1);
						}
						else if (cond == 2)
						{
							htmltext = "30133-02.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30133-03.htm";
							st.setCond(4, true);
							takeItems(player, ONYX_TALISMAN_2, 1);
							takeItems(player, ANCIENT_SCROLL, 1);
							takeItems(player, ANCIENT_CLAY_TABLET, 1);
							giveItems(player, KARTIA_TRANSLATION, 1);
						}
						else if (cond == 4)
						{
							htmltext = "30133-04.htm";
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
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(2) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true) && (getRandom(100) < 20) && hasQuestItems(killer, ONYX_TALISMAN_2))
		{
			if (!hasQuestItems(killer, ANCIENT_SCROLL))
			{
				giveItems(killer, ANCIENT_SCROLL, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
			}
			else if (!hasQuestItems(killer, ANCIENT_CLAY_TABLET))
			{
				qs.setCond(3, true);
				giveItems(killer, ANCIENT_CLAY_TABLET, 1);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
