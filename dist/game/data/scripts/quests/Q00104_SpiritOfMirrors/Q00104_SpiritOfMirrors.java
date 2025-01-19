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
package quests.Q00104_SpiritOfMirrors;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

import quests.Q00101_SwordOfSolidarity.Q00101_SwordOfSolidarity;

public class Q00104_SpiritOfMirrors extends Quest
{
	// NPCs
	private static final int GALLINT = 30017;
	private static final int ARNOLD = 30041;
	private static final int JOHNSTONE = 30043;
	private static final int KENYOS = 30045;
	// Items
	private static final int GALLINS_OAK_WAND = 748;
	private static final int WAND_SPIRITBOUND_1 = 1135;
	private static final int WAND_SPIRITBOUND_2 = 1136;
	private static final int WAND_SPIRITBOUND_3 = 1137;
	// Rewards
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int WAND_OF_ADEPT = 747;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public Q00104_SpiritOfMirrors()
	{
		super(104);
		registerQuestItems(GALLINS_OAK_WAND, WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3);
		addStartNpc(GALLINT);
		addTalkId(GALLINT, ARNOLD, JOHNSTONE, KENYOS);
		
		addKillId(27003, 27004, 27005);
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
		
		if (event.equals("30017-03.htm"))
		{
			st.startQuest();
			giveItems(player, GALLINS_OAK_WAND, 1);
			giveItems(player, GALLINS_OAK_WAND, 1);
			giveItems(player, GALLINS_OAK_WAND, 1);
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
				if (player.getRace() != Race.HUMAN)
				{
					htmltext = "30017-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "30017-01.htm";
				}
				else
				{
					htmltext = "30017-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case GALLINT:
					{
						if ((cond == 1) || (cond == 2))
						{
							htmltext = "30017-04.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30017-05.htm";
							
							takeItems(player, WAND_SPIRITBOUND_1, -1);
							takeItems(player, WAND_SPIRITBOUND_2, -1);
							takeItems(player, WAND_SPIRITBOUND_3, -1);
							
							giveItems(player, WAND_OF_ADEPT, 1);
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
					case KENYOS:
					case JOHNSTONE:
					case ARNOLD:
					{
						htmltext = npc.getId() + "-01.htm";
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
		
		if (player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) == GALLINS_OAK_WAND)
		{
			switch (npc.getId())
			{
				case 27003:
					if (!hasQuestItems(player, WAND_SPIRITBOUND_1))
					{
						takeItems(player, GALLINS_OAK_WAND, 1);
						giveItems(player, WAND_SPIRITBOUND_1, 1);
						if (hasQuestItems(player, WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3))
						{
							st.setCond(3, true);
						}
						else
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				
				case 27004:
					if (!hasQuestItems(player, WAND_SPIRITBOUND_2))
					{
						takeItems(player, GALLINS_OAK_WAND, 1);
						giveItems(player, WAND_SPIRITBOUND_2, 1);
						if (hasQuestItems(player, WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_3))
						{
							st.setCond(3, true);
						}
						else
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				
				case 27005:
					if (!hasQuestItems(player, WAND_SPIRITBOUND_3))
					{
						takeItems(player, GALLINS_OAK_WAND, 1);
						giveItems(player, WAND_SPIRITBOUND_3, 1);
						if (hasQuestItems(player, WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2))
						{
							st.setCond(3, true);
						}
						else
						{
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
			}
		}
		
		return super.onKill(npc, player, isPet);
	}
}
