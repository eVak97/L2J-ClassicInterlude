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
package quests.Q00159_ProtectTheWaterSource;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00159_ProtectTheWaterSource extends Quest
{
	// Items
	private static final int PLAGUE_DUST = 1035;
	private static final int HYACINTH_CHARM_1 = 1071;
	private static final int HYACINTH_CHARM_2 = 1072;
	
	public Q00159_ProtectTheWaterSource()
	{
		super(159);
		registerQuestItems(PLAGUE_DUST, HYACINTH_CHARM_1, HYACINTH_CHARM_2);
		addStartNpc(30154); // Asterios
		addTalkId(30154);
		addKillId(27017); // Plague Zombie
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
		
		if (event.equals("30154-04.htm"))
		{
			st.startQuest();
			giveItems(player, HYACINTH_CHARM_1, 1);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "30154-00.htm";
				}
				else if (player.getLevel() < 12)
				{
					htmltext = "30154-02.htm";
				}
				else
				{
					htmltext = "30154-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "30154-05.htm";
				}
				else if (cond == 2)
				{
					htmltext = "30154-06.htm";
					st.setCond(3, true);
					takeItems(player, PLAGUE_DUST, -1);
					takeItems(player, HYACINTH_CHARM_1, 1);
					giveItems(player, HYACINTH_CHARM_2, 1);
				}
				else if (cond == 3)
				{
					htmltext = "30154-07.htm";
				}
				else if (cond == 4)
				{
					htmltext = "30154-08.htm";
					takeItems(player, HYACINTH_CHARM_2, 1);
					takeItems(player, PLAGUE_DUST, -1);
					giveAdena(player, 18250, true);
					st.exitQuest(false, true);
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
		if ((qs != null))
		{
			switch (qs.getCond())
			{
				case 1:
				{
					if ((getRandom(100) < 40) && hasQuestItems(killer, HYACINTH_CHARM_1) && !hasQuestItems(killer, PLAGUE_DUST))
					{
						giveItems(killer, PLAGUE_DUST, 1);
						qs.setCond(2, true);
					}
					break;
				}
				case 3:
				{
					long dust = getQuestItemsCount(killer, PLAGUE_DUST);
					if ((getRandom(100) < 40) && (dust < 5) && hasQuestItems(killer, HYACINTH_CHARM_2))
					{
						giveItems(killer, PLAGUE_DUST, 1);
						if ((++dust) >= 5)
						{
							qs.setCond(4, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
