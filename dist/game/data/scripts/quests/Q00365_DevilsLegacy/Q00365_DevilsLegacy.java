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
package quests.Q00365_DevilsLegacy;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.skill.Skill;

public class Q00365_DevilsLegacy extends Quest
{
	// NPCs
	private static final int RANDOLF = 30095;
	private static final int COLLOB = 30092;
	// Item
	private static final int PIRATE_TREASURE_CHEST = 5873;
	
	public Q00365_DevilsLegacy()
	{
		super(365);
		registerQuestItems(PIRATE_TREASURE_CHEST);
		addStartNpc(RANDOLF);
		addTalkId(RANDOLF, COLLOB);
		addKillId(20836, 20845, 21629, 21630); // Pirate Zombie && Pirate Zombie Captain.
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
			case "30095-02.htm":
			{
				st.startQuest();
				break;
			}
			case "30095-06.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "30092-05.htm":
			{
				if (!hasQuestItems(player, PIRATE_TREASURE_CHEST))
				{
					htmltext = "30092-02.htm";
				}
				else if (getQuestItemsCount(player, 57) < 600)
				{
					htmltext = "30092-03.htm";
				}
				else
				{
					takeItems(player, PIRATE_TREASURE_CHEST, 1);
					takeItems(player, 57, 600);
					
					int i0;
					if (getRandom(100) < 80)
					{
						i0 = getRandom(100);
						if (i0 < 1)
						{
							giveItems(player, 955, 1);
						}
						else if (i0 < 4)
						{
							giveItems(player, 956, 1);
						}
						else if (i0 < 36)
						{
							giveItems(player, 1868, 1);
						}
						else if (i0 < 68)
						{
							giveItems(player, 1884, 1);
						}
						else
						{
							giveItems(player, 1872, 1);
						}
						
						htmltext = "30092-05.htm";
					}
					else
					{
						i0 = getRandom(1000);
						if (i0 < 10)
						{
							giveItems(player, 951, 1);
						}
						else if (i0 < 40)
						{
							giveItems(player, 952, 1);
						}
						else if (i0 < 60)
						{
							giveItems(player, 955, 1);
						}
						else if (i0 < 260)
						{
							giveItems(player, 956, 1);
						}
						else if (i0 < 445)
						{
							giveItems(player, 1879, 1);
						}
						else if (i0 < 630)
						{
							giveItems(player, 1880, 1);
						}
						else if (i0 < 815)
						{
							giveItems(player, 1882, 1);
						}
						else
						{
							giveItems(player, 1881, 1);
						}
						
						htmltext = "30092-06.htm";
						
						// Curse effect !
						final Skill skill = SkillData.getInstance().getSkill(4082, 1);
						if ((skill != null) && !player.isAffectedBySkill(skill.getId()))
						{
							skill.applyEffects(npc, player);
						}
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = Quest.getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 39) ? "30095-00.htm" : "30095-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case RANDOLF:
					{
						if (!hasQuestItems(player, PIRATE_TREASURE_CHEST))
						{
							htmltext = "30095-03.htm";
						}
						else
						{
							htmltext = "30095-05.htm";
							
							final int reward = (int) (getQuestItemsCount(player, PIRATE_TREASURE_CHEST) * 1600);
							
							takeItems(player, PIRATE_TREASURE_CHEST, -1);
							giveAdena(player, reward, true);
						}
						break;
					}
					case COLLOB:
					{
						htmltext = "30092-01.htm";
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(player, -1, 3, npc);
		if (qs != null)
		{
			giveItemRandomly(qs.getPlayer(), npc, PIRATE_TREASURE_CHEST, 1, 0, npc.getId() == 20836 ? 0.36 : 0.52, true);
		}
		return super.onKill(npc, player, isSummon);
	}
}
