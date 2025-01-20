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
package quests.Q00034_InSearchOfCloth;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00037_MakeFormalWear.Q00037_MakeFormalWear;

public class Q00034_InSearchOfCloth extends Quest
{
	// NPCs
	private static final int RADIA = 30088;
	private static final int RALFORD = 30165;
	private static final int VARAN = 30294;
	// Monsters
	private static final int TRISALIM_SPIDER = 20560;
	private static final int TRISALIM_TARANTULA = 20561;
	// Items
	private static final int SPINNERET = 7528;
	private static final int SUEDE = 1866;
	private static final int THREAD = 1868;
	private static final int SPIDERSILK = 7161;
	// Rewards
	private static final int MYSTERIOUS_CLOTH = 7076;
	
	public Q00034_InSearchOfCloth()
	{
		super(34);
		registerQuestItems(SPINNERET, SPIDERSILK);
		addStartNpc(RADIA);
		addTalkId(RADIA, RALFORD, VARAN);
		addKillId(TRISALIM_SPIDER, TRISALIM_TARANTULA);
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
			case "30088-1.htm":
			{
				st.startQuest();
				break;
			}
			case "30294-1.htm":
			{
				st.setCond(2, true);
				break;
			}
			case "30088-3.htm":
			{
				st.setCond(3, true);
				break;
			}
			case "30165-1.htm":
			{
				st.setCond(4, true);
				break;
			}
			case "30165-3.htm":
			{
				st.setCond(6, true);
				takeItems(player, SPINNERET, 10);
				giveItems(player, SPIDERSILK, 1);
				break;
			}
			case "30088-5.htm":
			{
				if ((getQuestItemsCount(player, SUEDE) >= 3000) && (getQuestItemsCount(player, THREAD) >= 5000) && hasQuestItems(player, SPIDERSILK))
				{
					takeItems(player, SPIDERSILK, 1);
					takeItems(player, SUEDE, 3000);
					takeItems(player, THREAD, 5000);
					giveItems(player, MYSTERIOUS_CLOTH, 1);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "30088-4a.htm";
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
				if (player.getLevel() >= 60)
				{
					final QuestState fwear = player.getQuestState(Q00037_MakeFormalWear.class.getSimpleName());
					if ((fwear != null) && fwear.isCond(6))
					{
						htmltext = "30088-0.htm";
					}
					else
					{
						htmltext = "30088-0a.htm";
					}
				}
				else
				{
					htmltext = "30088-0b.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case RADIA:
					{
						if (cond == 1)
						{
							htmltext = "30088-1a.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30088-2.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30088-3a.htm";
						}
						else if (cond == 6)
						{
							if ((getQuestItemsCount(player, SUEDE) < 3000) || (getQuestItemsCount(player, THREAD) < 5000) || !hasQuestItems(player, SPIDERSILK))
							{
								htmltext = "30088-4a.htm";
							}
							else
							{
								htmltext = "30088-4.htm";
							}
						}
						break;
					}
					case VARAN:
					{
						if (cond == 1)
						{
							htmltext = "30294-0.htm";
						}
						else if (cond > 1)
						{
							htmltext = "30294-1a.htm";
						}
						break;
					}
					case RALFORD:
					{
						if (cond == 3)
						{
							htmltext = "30165-0.htm";
						}
						else if ((cond == 4) && (getQuestItemsCount(player, SPINNERET) < 10))
						{
							htmltext = "30165-1a.htm";
						}
						else if (cond == 5)
						{
							htmltext = "30165-2.htm";
						}
						else if (cond > 5)
						{
							htmltext = "30165-3a.htm";
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
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Player member = getRandomPartyMember(player, 4);
		if ((member != null) && getRandomBoolean())
		{
			final QuestState qs = getQuestState(member, false);
			giveItems(member, SPINNERET, 1);
			if (getQuestItemsCount(member, SPINNERET) >= 10)
			{
				qs.setCond(5, true);
			}
			else
			{
				playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
