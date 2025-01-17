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
package quests.Q00350_EnhanceYourWeapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.LevelUpCrystalData;
import org.l2jmobius.gameserver.enums.AbsorbCrystalType;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.AbsorberInfo;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.LevelingSoulCrystalInfo;
import org.l2jmobius.gameserver.model.holders.SoulCrystal;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * Enhance Your Weapon (350)
 * @author Gigiikun
 */
public class Q00350_EnhanceYourWeapon extends Quest
{
	private static final int MIN_LEVEL = 40;
	
	// NPCs
	private static final int[] STARTING_NPCS =
	{
		30115,
		30856,
		30194
	};
	// Items
	private static final int RED_SOUL_CRYSTAL0_ID = 4629;
	private static final int GREEN_SOUL_CRYSTAL0_ID = 4640;
	private static final int BLUE_SOUL_CRYSTAL0_ID = 4651;
	
	public Q00350_EnhanceYourWeapon()
	{
		super(350);
		addStartNpc(STARTING_NPCS);
		addTalkId(STARTING_NPCS);
		
		for (int npcId : LevelUpCrystalData.getInstance().getNpcsSoulInfo().keySet())
		{
			addSkillSeeId(npcId);
			addKillId(npcId);
		}
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (event.endsWith("-04.htm"))
		{
			qs.startQuest();
		}
		else if (event.endsWith("-09.htm"))
		{
			giveItems(player, RED_SOUL_CRYSTAL0_ID, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		else if (event.endsWith("-10.htm"))
		{
			giveItems(player, GREEN_SOUL_CRYSTAL0_ID, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		else if (event.endsWith("-11.htm"))
		{
			giveItems(player, BLUE_SOUL_CRYSTAL0_ID, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		else if (event.equalsIgnoreCase("exit.htm"))
		{
			qs.exitQuest(true, true);
			
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile(player, "data/scripts/quests/Q00350_EnhanceYourWeapon/exit.htm");
			html.replace("%npcname%", npc.getName());
			player.sendPacket(html);
			return null;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.isAttackable() && LevelUpCrystalData.getInstance().getNpcsSoulInfo().containsKey(npc.getId()))
		{
			levelSoulCrystals(npc.asAttackable(), killer);
		}
		
		return null;
	}
	
	@Override
	public String onSkillSee(Npc npc, Player caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		super.onSkillSee(npc, caster, skill, targets, isSummon);
		
		if ((skill == null) || (skill.getId() != 2096))
		{
			return null;
		}
		else if ((caster == null) || caster.isDead())
		{
			return null;
		}
		if (!npc.isAttackable() || npc.isDead() || !LevelUpCrystalData.getInstance().getNpcsSoulInfo().containsKey(npc.getId()))
		{
			return null;
		}
		
		try
		{
			npc.asAttackable().addAbsorber(caster);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "", e);
		}
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.getState() == State.CREATED)
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				htmltext = npc.getId() + "-lvl.htm";
			}
			else
			{
				htmltext = npc.getId() + "-01.htm";
			}
		}
		else if (check(player))
		{
			htmltext = npc.getId() + "-03.htm";
		}
		else if (!hasQuestItems(player, RED_SOUL_CRYSTAL0_ID) && !hasQuestItems(player, GREEN_SOUL_CRYSTAL0_ID) && !hasQuestItems(player, BLUE_SOUL_CRYSTAL0_ID))
		{
			htmltext = npc.getId() + "-21.htm";
		}
		return htmltext;
	}
	
	private static boolean check(Player player)
	{
		for (int i = 4629; i < 4665; i++)
		{
			if (hasQuestItems(player, i))
			{
				return true;
			}
		}
		return false;
	}
	
	private static void exchangeCrystal(Player player, Attackable mob, int takeId, int giveId, boolean broke)
	{
		Item item = player.getInventory().destroyItemByItemId("SoulCrystal", takeId, 1, player, mob);
		if (item != null)
		{
			// Prepare inventory update packet
			final InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addRemovedItem(item);
			
			// Add new crystal to the killer's inventory
			item = player.getInventory().addItem("SoulCrystal", giveId, 1, player, mob);
			playerIU.addItem(item);
			
			// Send a sound event and text message to the player
			if (broke)
			{
				player.sendPacket(SystemMessageId.THE_SOUL_CRYSTAL_BROKE_BECAUSE_IT_WAS_NOT_ABLE_TO_ENDURE_THE_SOUL_ENERGY);
			}
			else
			{
				player.sendPacket(SystemMessageId.THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL);
			}
			
			// Send system message
			final SystemMessage sms = new SystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
			sms.addItemName(giveId);
			player.sendPacket(sms);
			
			// Send inventory update packet
			player.sendPacket(playerIU);
		}
	}
	
	private static SoulCrystal getSCForPlayer(Player player)
	{
		final QuestState qs = player.getQuestState(Q00350_EnhanceYourWeapon.class.getSimpleName());
		if ((qs == null) || !qs.isStarted())
		{
			return null;
		}
		
		SoulCrystal ret = null;
		for (Item item : player.getInventory().getItems())
		{
			final int itemId = item.getId();
			if (!LevelUpCrystalData.getInstance().getSoulCrystals().containsKey(itemId))
			{
				continue;
			}
			
			if (ret != null)
			{
				return null;
			}
			ret = LevelUpCrystalData.getInstance().getSoulCrystals().get(itemId);
		}
		return ret;
	}
	
	private static boolean isPartyLevelingMonster(int npcId)
	{
		for (LevelingSoulCrystalInfo info : LevelUpCrystalData.getInstance().getNpcSoulInfo(npcId).values())
		{
			if (info.getAbsorbCrystalType() != AbsorbCrystalType.LAST_HIT)
			{
				return true;
			}
		}
		return false;
	}
	
	private static void levelCrystal(Player player, SoulCrystal sc, Attackable mob)
	{
		if ((sc == null) || !LevelUpCrystalData.getInstance().getNpcsSoulInfo().containsKey(mob.getId()))
		{
			return;
		}
		
		// If the crystal level is way too high for this mob, say that we can't increase it
		if (!LevelUpCrystalData.getInstance().getNpcsSoulInfo().get(mob.getId()).containsKey(sc.getLevel()))
		{
			player.sendPacket(SystemMessageId.THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_THE_SOUL);
			return;
		}
		
		if (getRandom(100) <= LevelUpCrystalData.getInstance().getNpcsSoulInfo().get(mob.getId()).get(sc.getLevel()).getChance())
		{
			exchangeCrystal(player, mob, sc.getItemId(), sc.getLeveledItemId(), false);
		}
		else
		{
			player.sendPacket(SystemMessageId.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL);
		}
	}
	
	/**
	 * Calculate the leveling chance of Soul Crystals based on the attacker that killed this Attackable
	 * @param mob
	 * @param killer The player that last killed this Attackable $ Rewrite 06.12.06 - Yesod $ Rewrite 08.01.10 - Gigiikun
	 */
	public static void levelSoulCrystals(Attackable mob, Player killer)
	{
		// Only Player can absorb a soul
		if (killer == null)
		{
			mob.resetAbsorbList();
			return;
		}
		
		final Map<Player, SoulCrystal> players = new HashMap<>();
		int maxSCLevel = 0;
		
		// TODO: what if mob support last_hit + party?
		if (isPartyLevelingMonster(mob.getId()) && (killer.getParty() != null))
		{
			// firts get the list of players who has one Soul Cry and the quest
			for (Player pl : killer.getParty().getMembers())
			{
				if (pl == null)
				{
					continue;
				}
				
				if (pl.calculateDistance3D(killer) > Config.ALT_PARTY_RANGE)
				{
					continue;
				}
				
				final SoulCrystal sc = getSCForPlayer(pl);
				if (sc == null)
				{
					continue;
				}
				
				players.put(pl, sc);
				if ((maxSCLevel < sc.getLevel()) && LevelUpCrystalData.getInstance().getNpcsSoulInfo().get(mob.getId()).containsKey(sc.getLevel()))
				{
					maxSCLevel = sc.getLevel();
				}
			}
		}
		else
		{
			final SoulCrystal sc = getSCForPlayer(killer);
			if (sc != null)
			{
				players.put(killer, sc);
				if ((maxSCLevel < sc.getLevel()) && LevelUpCrystalData.getInstance().getNpcsSoulInfo().get(mob.getId()).containsKey(sc.getLevel()))
				{
					maxSCLevel = sc.getLevel();
				}
			}
		}
		
		// Init some useful vars
		final LevelingSoulCrystalInfo mainlvlInfo = LevelUpCrystalData.getInstance().getNpcsSoulInfo().get(mob.getId()).get(maxSCLevel);
		if (mainlvlInfo == null)
		{
			return;
		}
		
		// If this mob is not require skill, then skip some checkings
		if (mainlvlInfo.isSkillNeeded())
		{
			// Fail if this Attackable isn't absorbed or there's no one in its _absorbersList
			if (!mob.isAbsorbed() /* || _absorbersList == null */)
			{
				mob.resetAbsorbList();
				return;
			}
			
			// Fail if the killer isn't in the _absorbersList of this Attackable and mob is not boss
			final AbsorberInfo ai = mob.getAbsorbersList().get(killer.getObjectId());
			boolean isSuccess = true;
			if ((ai == null) || (ai.getObjectId() != killer.getObjectId()))
			{
				isSuccess = false;
			}
			
			// Check if the soul crystal was used when HP of this Attackable wasn't higher than half of it
			if ((ai != null) && (ai.getAbsorbedHp() > (mob.getMaxHp() / 2.0)))
			{
				isSuccess = false;
			}
			
			if (!isSuccess)
			{
				mob.resetAbsorbList();
				return;
			}
		}
		
		switch (mainlvlInfo.getAbsorbCrystalType())
		{
			case PARTY_ONE_RANDOM:
			{
				// This is a naive method for selecting a random member. It gets any random party member and
				// then checks if the member has a valid crystal. It does not select the random party member
				// among those who have crystals, only. However, this might actually be correct (same as retail).
				if (killer.getParty() != null)
				{
					final Player lucky = killer.getParty().getMembers().get(getRandom(killer.getParty().getMemberCount()));
					levelCrystal(lucky, players.get(lucky), mob);
				}
				else
				{
					levelCrystal(killer, players.get(killer), mob);
				}
				break;
			}
			case PARTY_RANDOM:
			{
				if (killer.getParty() != null)
				{
					final List<Player> luckyParty = new ArrayList<>();
					luckyParty.addAll(killer.getParty().getMembers());
					while ((getRandom(100) < 33) && !luckyParty.isEmpty())
					{
						final Player lucky = luckyParty.remove(getRandom(luckyParty.size()));
						if (players.containsKey(lucky))
						{
							levelCrystal(lucky, players.get(lucky), mob);
						}
					}
				}
				else if (getRandom(100) < 33)
				{
					levelCrystal(killer, players.get(killer), mob);
				}
				break;
			}
			case FULL_PARTY:
			{
				if (killer.getParty() != null)
				{
					for (Player pl : killer.getParty().getMembers())
					{
						levelCrystal(pl, players.get(pl), mob);
					}
				}
				else
				{
					levelCrystal(killer, players.get(killer), mob);
				}
				break;
			}
			case LAST_HIT:
			{
				levelCrystal(killer, players.get(killer), mob);
				break;
			}
		}
	}
}
