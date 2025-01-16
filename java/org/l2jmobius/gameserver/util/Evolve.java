/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.l2jmobius.gameserver.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.data.xml.PetDataTable;
import org.l2jmobius.gameserver.model.PetData;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.Pet;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * UnAfraid: TODO: MOVE IT TO DP AI
 */
public class Evolve
{
	protected static final Logger LOGGER = Logger.getLogger(Evolve.class.getName());
	
	public static boolean doEvolve(Player player, Npc npc, int itemIdtake, int itemIdgive, int petminLevel)
	{
		if ((itemIdtake == 0) || (itemIdgive == 0) || (petminLevel == 0))
		{
			return false;
		}
		
		final Summon pet = player.getPet();
		if (pet == null)
		{
			return false;
		}
		
		final Pet currentPet = pet.asPet();
		if (currentPet.isAlikeDead())
		{
			Util.handleIllegalPlayerAction(player, player + " tried to use death pet exploit!", Config.DEFAULT_PUNISH);
			return false;
		}
		
		Item item = null;
		long petexp = currentPet.getStat().getExp();
		final String oldname = currentPet.getName();
		final int oldX = currentPet.getX();
		final int oldY = currentPet.getY();
		final int oldZ = currentPet.getZ();
		final PetData oldData = PetDataTable.getInstance().getPetDataByItemId(itemIdtake);
		if (oldData == null)
		{
			return false;
		}
		
		final int oldnpcID = oldData.getNpcId();
		if ((currentPet.getStat().getLevel() < petminLevel) || (currentPet.getId() != oldnpcID))
		{
			return false;
		}
		
		final PetData petData = PetDataTable.getInstance().getPetDataByItemId(itemIdgive);
		if (petData == null)
		{
			return false;
		}
		
		final int npcID = petData.getNpcId();
		if (npcID == 0)
		{
			return false;
		}
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcID);
		currentPet.unSummon(player);
		
		// deleting old pet item
		currentPet.destroyControlItem(player, true);
		item = player.getInventory().addItem("Evolve", itemIdgive, 1, player, npc);
		
		// Summoning new pet
		final Pet petSummon = Pet.spawnPet(npcTemplate, player, item);
		if (petSummon == null)
		{
			return false;
		}
		
		// Fix for non-linear baby pet exp
		final long _minimumexp = petSummon.getStat().getExpForLevel(petminLevel);
		if (petexp < _minimumexp)
		{
			petexp = _minimumexp;
		}
		
		petSummon.getStat().addExp(petexp);
		petSummon.setCurrentHp(petSummon.getMaxHp());
		petSummon.setCurrentMp(petSummon.getMaxMp());
		petSummon.setCurrentFed(petSummon.getMaxFed());
		petSummon.setTitle(player.getName());
		petSummon.setName(oldname);
		petSummon.setRunning();
		petSummon.storeMe();
		
		player.setPet(petSummon);
		
		player.sendPacket(new MagicSkillUse(npc, 2046, 1, 1000, 600000));
		player.sendPacket(SystemMessageId.SUMMONING_YOUR_PET);
		petSummon.spawnMe(oldX, oldY, oldZ);
		petSummon.startFeed();
		item.setEnchantLevel(petSummon.getLevel());
		
		ThreadPool.schedule(new EvolveFinalizer(player, petSummon), 900);
		if (petSummon.getCurrentFed() <= 0)
		{
			ThreadPool.schedule(new EvolveFeedWait(player, petSummon), 60000);
		}
		else
		{
			petSummon.startFeed();
		}
		
		return true;
	}
	
	public static boolean doRestore(Player player, Npc npc, int itemIdtake, int itemIdgive, int petminLevel)
	{
		if ((itemIdtake == 0) || (itemIdgive == 0) || (petminLevel == 0))
		{
			return false;
		}
		
		final Item item = player.getInventory().getItemByItemId(itemIdtake);
		if (item == null)
		{
			return false;
		}
		
		int oldpetlvl = item.getEnchantLevel();
		if (oldpetlvl < petminLevel)
		{
			oldpetlvl = petminLevel;
		}
		
		final PetData oldData = PetDataTable.getInstance().getPetDataByItemId(itemIdtake);
		if (oldData == null)
		{
			return false;
		}
		
		final PetData petData = PetDataTable.getInstance().getPetDataByItemId(itemIdgive);
		if (petData == null)
		{
			return false;
		}
		
		final int npcId = petData.getNpcId();
		if (npcId == 0)
		{
			return false;
		}
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcId);
		
		// deleting old pet item
		final Item removedItem = player.getInventory().destroyItem("PetRestore", item, player, npc);
		final SystemMessage sm = new SystemMessage(SystemMessageId.S1_DISAPPEARED);
		sm.addItemName(removedItem);
		player.sendPacket(sm);
		
		// Give new pet item
		final Item addedItem = player.getInventory().addItem("PetRestore", itemIdgive, 1, player, npc);
		
		// Summoning new pet
		final Pet petSummon = Pet.spawnPet(npcTemplate, player, addedItem);
		if (petSummon == null)
		{
			return false;
		}
		
		final long _maxexp = petSummon.getStat().getExpForLevel(oldpetlvl);
		petSummon.getStat().addExp(_maxexp);
		petSummon.setCurrentHp(petSummon.getMaxHp());
		petSummon.setCurrentMp(petSummon.getMaxMp());
		petSummon.setCurrentFed(petSummon.getMaxFed());
		petSummon.setTitle(player.getName());
		petSummon.setRunning();
		petSummon.storeMe();
		
		player.setPet(petSummon);
		
		player.sendPacket(new MagicSkillUse(npc, 2046, 1, 1000, 600000));
		player.sendPacket(SystemMessageId.SUMMONING_YOUR_PET);
		petSummon.spawnMe(player.getX(), player.getY(), player.getZ());
		petSummon.startFeed();
		addedItem.setEnchantLevel(petSummon.getLevel());
		
		// Inventory update
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addRemovedItem(removedItem);
		player.sendInventoryUpdate(iu);
		
		player.broadcastUserInfo();
		
		ThreadPool.schedule(new EvolveFinalizer(player, petSummon), 900);
		if (petSummon.getCurrentFed() <= 0)
		{
			ThreadPool.schedule(new EvolveFeedWait(player, petSummon), 60000);
		}
		else
		{
			petSummon.startFeed();
		}
		
		// pet control item no longer exists, delete the pet from the db
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?"))
		{
			ps.setInt(1, removedItem.getObjectId());
			ps.execute();
		}
		catch (Exception e)
		{
			// Ignore.
		}
		return true;
	}
	
	private static class EvolveFeedWait implements Runnable
	{
		private final Player _player;
		private final Pet _petSummon;
		
		EvolveFeedWait(Player player, Pet petSummon)
		{
			_player = player;
			_petSummon = petSummon;
		}
		
		@Override
		public void run()
		{
			try
			{
				if (_petSummon.getCurrentFed() <= 0)
				{
					_petSummon.unSummon(_player);
				}
				else
				{
					_petSummon.startFeed();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "", e);
			}
		}
	}
	
	private static class EvolveFinalizer implements Runnable
	{
		private final Player _player;
		private final Pet _petSummon;
		
		EvolveFinalizer(Player player, Pet petSummon)
		{
			_player = player;
			_petSummon = petSummon;
		}
		
		@Override
		public void run()
		{
			try
			{
				_player.sendPacket(new MagicSkillLaunched(_player, 2046, 1));
				_petSummon.setFollowStatus(true);
				_petSummon.setShowSummonAnimation(false);
			}
			catch (Throwable e)
			{
				LOGGER.log(Level.WARNING, "", e);
			}
		}
	}
}
