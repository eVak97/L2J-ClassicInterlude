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
package quests.Q00633_InTheForgottenVillage;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00633_InTheForgottenVillage extends Quest
{
	// NPCs
	private static final int MINA = 31388;
	// Items
	private static final int RIB_BONE = 7544;
	private static final int ZOMBIE_LIVER = 7545;
	// Monsters / Drop chances
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	static
	{
		MOBS.put(21557, 328000); // Bone Snatcher
		MOBS.put(21558, 328000); // Bone Snatcher
		MOBS.put(21559, 337000); // Bone Maker
		MOBS.put(21560, 337000); // Bone Shaper
		MOBS.put(21563, 342000); // Bone Collector
		MOBS.put(21564, 348000); // Skull Collector
		MOBS.put(21565, 351000); // Bone Animator
		MOBS.put(21566, 359000); // Skull Animator
		MOBS.put(21567, 359000); // Bone Slayer
		MOBS.put(21572, 365000); // Bone Sweeper
		MOBS.put(21574, 383000); // Bone Grinder
		MOBS.put(21575, 383000); // Bone Grinder
		MOBS.put(21580, 385000); // Bone Caster
		MOBS.put(21581, 395000); // Bone Puppeteer
		MOBS.put(21583, 397000); // Bone Scavenger
		MOBS.put(21584, 401000); // Bone Scavenger
	}
	private static final Map<Integer, Integer> UNDEADS = new HashMap<>();
	static
	{
		UNDEADS.put(21553, 347000); // Trampled Man
		UNDEADS.put(21554, 347000); // Trampled Man
		UNDEADS.put(21561, 450000); // Sacrificed Man
		UNDEADS.put(21578, 501000); // Behemoth Zombie
		UNDEADS.put(21596, 359000); // Requiem Lord
		UNDEADS.put(21597, 370000); // Requiem Behemoth
		UNDEADS.put(21598, 441000); // Requiem Behemoth
		UNDEADS.put(21599, 395000); // Requiem Priest
		UNDEADS.put(21600, 408000); // Requiem Behemoth
		UNDEADS.put(21601, 411000); // Requiem Behemoth
	}
	
	public Q00633_InTheForgottenVillage()
	{
		super(633);
		registerQuestItems(RIB_BONE, ZOMBIE_LIVER);
		addStartNpc(MINA);
		addTalkId(MINA);
		addKillId(MOBS.keySet());
		addKillId(UNDEADS.keySet());
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
			case "31388-04.htm":
			{
				st.startQuest();
				break;
			}
			case "31388-10.htm":
			{
				takeItems(player, RIB_BONE, -1);
				st.exitQuest(true, true);
				break;
			}
			case "31388-09.htm":
			{
				if (getQuestItemsCount(player, RIB_BONE) >= 200)
				{
					htmltext = "31388-08.htm";
					takeItems(player, RIB_BONE, 200);
					giveAdena(player, 25000, true);
					addExpAndSp(player, 305235, 0);
				}
				st.setCond(1, true);
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
				htmltext = (player.getLevel() < 65) ? "31388-03.htm" : "31388-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "31388-06.htm";
				}
				else if (cond == 2)
				{
					htmltext = "31388-05.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final int npcId = npc.getId();
		if (UNDEADS.containsKey(npcId))
		{
			final QuestState st = getRandomPartyMemberState(player, -1, 3, npc);
			if ((st == null) || !st.isStarted())
			{
				return null;
			}
			final Player partyMember = st.getPlayer();
			
			if (getRandom(1000000) < UNDEADS.get(npcId))
			{
				giveItems(partyMember, ZOMBIE_LIVER, 1);
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		else if (MOBS.containsKey(npcId))
		{
			final Player partyMember = getRandomPartyMember(player, 1);
			if (partyMember == null)
			{
				return null;
			}
			
			final QuestState st = getQuestState(partyMember, false);
			if (getRandom(1000000) < MOBS.get(npcId))
			{
				giveItems(partyMember, RIB_BONE, 1);
				if (getQuestItemsCount(partyMember, RIB_BONE) < 200)
				{
					playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					st.setCond(2, true);
				}
			}
		}
		
		return null;
	}
}
