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
package quests.Q00101_SwordOfSolidarity;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00101_SwordOfSolidarity extends Quest
{
	// NPCs
	private static final int ROIEN = 30008;
	private static final int ALTRAN = 30283;
	// Items
	private static final int BROKEN_SWORD_HANDLE = 739;
	private static final int BROKEN_BLADE_BOTTOM = 740;
	private static final int BROKEN_BLADE_TOP = 741;
	private static final int ROIENS_LETTER = 796;
	private static final int DIR_TO_RUINS = 937;
	private static final int ALTRANS_NOTE = 742;
	private static final int SWORD_OF_SOLIDARITY = 738;
	private static final ItemHolder SOULSHOT_FOR_BEGINNERS = new ItemHolder(5789, 7000);
	private static final ItemHolder SPIRITSHOT_FOR_BEGINNERS = new ItemHolder(5790, 3000);
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	// Message
	private static final ExShowScreenMessage MESSAGE = new ExShowScreenMessage("Acquisition of Soulshot for beginners complete. \\n Go find the Newbie Guide.", 2, 5000);
	
	public Q00101_SwordOfSolidarity()
	{
		super(101);
		registerQuestItems(BROKEN_SWORD_HANDLE, BROKEN_BLADE_BOTTOM, BROKEN_BLADE_TOP);
		addStartNpc(ROIEN);
		addTalkId(ROIEN, ALTRAN);
		addKillId(20361, 20362);
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
		
		switch (event)
		{
			case "30008-03.htm":
			{
				st.startQuest();
				giveItems(player, ROIENS_LETTER, 1);
				break;
			}
			case "30283-02.htm":
			{
				st.setCond(2, true);
				takeItems(player, ROIENS_LETTER, 1);
				giveItems(player, DIR_TO_RUINS, 1);
				break;
			}
			case "30283-06.htm":
			{
				takeItems(player, BROKEN_SWORD_HANDLE, 1);
				giveItems(player, SWORD_OF_SOLIDARITY, 1);
				giveItems(player, LESSER_HEALING_POT, 100);
				
				final PlayerVariables vars = player.getVariables();
				// Give newbie reward if player is eligible
				if ((player.getLevel() < 25) && !vars.getBoolean("NEWBIE_SHOTS", false))
				{
					st.showQuestionMark(26);
					giveNewbieReward(player);
				}
				giveItems(player, ECHO_BATTLE, 10);
				giveItems(player, ECHO_LOVE, 10);
				giveItems(player, ECHO_SOLITUDE, 10);
				giveItems(player, ECHO_FEAST, 10);
				giveItems(player, ECHO_CELEBRATION, 10);
				player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
				st.exitQuest(false, true);
				break;
			}
		}
		
		return htmltext;
	}
	
	/**
	 * Give basic newbie reward.
	 * @param player the player to reward
	 */
	public static void giveNewbieReward(Player player)
	{
		final PlayerVariables vars = player.getVariables();
		if ((player.getLevel() < 25) && !vars.getBoolean("NEWBIE_SHOTS", false))
		{
			if (player.isMageClass())
			{
				giveItems(player, SPIRITSHOT_FOR_BEGINNERS.getId(), SPIRITSHOT_FOR_BEGINNERS.getCount());
				playSound(player, "tutorial_voice_27");
			}
			else
			{
				giveItems(player, SOULSHOT_FOR_BEGINNERS.getId(), SOULSHOT_FOR_BEGINNERS.getCount());
				playSound(player, "tutorial_voice_26");
			}
			vars.set("NEWBIE_SHOTS", true);
		}
		if (vars.getString("GUIDE_MISSION", null) == null)
		{
			vars.set("GUIDE_MISSION", 1000);
			player.sendPacket(MESSAGE);
		}
		else if (((vars.getInt("GUIDE_MISSION") % 10000) / 1000) != 1)
		{
			vars.set("GUIDE_MISSION", vars.getInt("GUIDE_MISSION") + 1000);
			player.sendPacket(MESSAGE);
		}
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
				if (player.getRace() != Race.HUMAN)
				{
					htmltext = "30008-01a.htm";
				}
				else if (player.getLevel() < 9)
				{
					htmltext = "30008-01.htm";
				}
				else
				{
					htmltext = "30008-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = (st.getCond());
				switch (npc.getId())
				{
					case ROIEN:
					{
						if (cond == 1)
						{
							htmltext = "30008-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30008-03a.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30008-06.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30008-05.htm";
							st.setCond(5, true);
							takeItems(player, ALTRANS_NOTE, 1);
							giveItems(player, BROKEN_SWORD_HANDLE, 1);
						}
						else if (cond == 5)
						{
							htmltext = "30008-05a.htm";
						}
						break;
					}
					case ALTRAN:
					{
						if (cond == 1)
						{
							htmltext = "30283-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30283-03.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30283-04.htm";
							st.setCond(4, true);
							takeItems(player, DIR_TO_RUINS, 1);
							takeItems(player, BROKEN_BLADE_TOP, 1);
							takeItems(player, BROKEN_BLADE_BOTTOM, 1);
							giveItems(player, ALTRANS_NOTE, 1);
						}
						else if (cond == 4)
						{
							htmltext = "30283-04a.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30283-05.htm";
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
		if ((qs != null) && qs.isCond(2) && (getRandom(5) == 0))
		{
			if (!hasQuestItems(killer, BROKEN_BLADE_TOP))
			{
				giveItems(killer, BROKEN_BLADE_TOP, 1);
				if (hasQuestItems(killer, BROKEN_BLADE_BOTTOM))
				{
					qs.setCond(3, true);
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
			else if (!hasQuestItems(killer, BROKEN_BLADE_BOTTOM))
			{
				giveItems(killer, BROKEN_BLADE_BOTTOM, 1);
				if (hasQuestItems(killer, BROKEN_BLADE_TOP))
				{
					qs.setCond(3, true);
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
