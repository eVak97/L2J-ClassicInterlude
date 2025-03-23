/*
 * This file is part of YANModPack: https://github.com/HorridoJoho/YANModPack
 * Copyright (C) 2015  Christian Buck
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package custom.Buffer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import ai.AbstractNpcAI;
import custom.Buffer.util.ItemRequirement;
import custom.Buffer.util.htmltmpls.HTMLTemplateParser;
import custom.Buffer.util.htmltmpls.HTMLTemplatePlaceholder;
import custom.Buffer.util.htmltmpls.funcs.ChildsCountFunc;
import custom.Buffer.util.htmltmpls.funcs.ExistsFunc;
import custom.Buffer.util.htmltmpls.funcs.ForeachFunc;
import custom.Buffer.util.htmltmpls.funcs.IfChildsFunc;
import custom.Buffer.util.htmltmpls.funcs.IfFunc;
import custom.Buffer.util.htmltmpls.funcs.IncludeFunc;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.HtmlActionScope;
import org.l2jmobius.gameserver.handler.BypassHandler;
import org.l2jmobius.gameserver.handler.ItemHandler;
import org.l2jmobius.gameserver.handler.VoicedCommandHandler;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.network.serverpackets.ShowBoard;
import org.l2jmobius.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * @author HorridoJoho
 */
public final class Buffer extends AbstractNpcAI
{
	private static final class SingletonHolder
	{
		protected static final Buffer INSTANCE = new Buffer();
	}

	private static final int MINUTE_IN_SECONDS = 60;

	private static final Logger _LOGGER = Logger.getLogger(Buffer.class.getName());
	public static final Path SCRIPTS_SUBFOLDER = Paths.get("custom");
	public static final Path SCRIPT_TOP_FOLDER = Paths.get("Buffer");
	public static final Path SCRIPT_SUBFOLDER = Paths.get(SCRIPTS_SUBFOLDER.toString(), SCRIPT_TOP_FOLDER.toString());

	static Buffer getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	public static void main(String[] args)
	{
		try
		{
			BufferData.initInstance();
		}
		catch (Exception ex)
		{
			_LOGGER.log(Level.WARNING, "Buffer - Data: Exception while loading npc buffer data, not registering mod!", ex);
			return;
		}
		
		Buffer instance = getInstance();
		
		for (Entry<Integer, BufferData.BufferNpc> npc : BufferData.getInstance().getBufferNpcs().entrySet())
		{
			instance.addFirstTalkId(npc.getKey());
			instance.addStartNpc(npc.getKey());
			instance.addTalkId(npc.getKey());
		}
	}
	
	private static final ConcurrentHashMap<Integer, Long> _LAST_PLAYABLES_HEAL_TIME = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Integer, String> _LAST_PLAYER_HTMLS = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Integer, String> _ACTIVE_PLAYER_BUFFLISTS = new ConcurrentHashMap<>();
	
	Buffer()
	{
//		super(-1, SCRIPT_TOP_FOLDER.toString(), SCRIPTS_SUBFOLDER.toString());
		super();

		BypassHandler.getInstance().registerHandler(BufferNpcBypassHandler.getInstance());
		
		if (BufferData.getInstance().getVoicedBuffer().enabled)
		{
			VoicedCommandHandler.getInstance().registerHandler(BufferVoicedCommandHandler.getInstance());
			ItemHandler.getInstance().registerHandler(BufferItemHandler.getInstance());
		}
	}
	
	// ////////////////////////////////////
	// AI METHOD OVERRIDES
	// ////////////////////////////////////
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		executeCommand(player, npc, null);
		return null;
	}
	
	// ///////////////////////////////////
	// UTILITY METHODS
	// ///////////////////////////////////
	private BufferData.Buffer determineBuffer(Npc npc, Player player)
	{
		if (npc == null)
		{
			BufferData.VoicedBuffer buffer = BufferData.getInstance().getVoicedBuffer();
			if (!buffer.enabled || ((buffer.requiredItem > 0) && (player.getInventory().getItemByItemId(buffer.requiredItem) == null)))
			{
				return null;
			}
			return buffer;
		}
		return BufferData.getInstance().getBufferNpc(npc.getId());
	}
	
	private String generateAdvancedHtml(Player player, String path, Map<String, HTMLTemplatePlaceholder> placeholders, BufferData.HtmlType dialogType)
	{
		return HTMLTemplateParser.fromCache(
			Path.of(
				"/data/scripts/" + "custom/Buffer" + "/data/html/" + dialogType.toString().toLowerCase(Locale.ENGLISH) + "/" + path
			).toString(),
			player,
			placeholders,
			IncludeFunc.INSTANCE,
			IfFunc.INSTANCE,
			ForeachFunc.INSTANCE,
			ExistsFunc.INSTANCE,
			IfChildsFunc.INSTANCE,
			ChildsCountFunc.INSTANCE
		);
	}
	
	/**
	 * Copy from {@link NpcHtmlMessage}
	 * @param activeChar the player
	 * @param html the html to check
	 */
	private void buildBypassCache(Player activeChar, String html)
	{
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.clearHtmlActions(HtmlActionScope.NPC_HTML);
		int len = html.length();
		for (int i = 0; i < len; i++)
		{
			int start = html.indexOf("\"bypass ", i);
			int finish = html.indexOf("\"", start + 1);
			if ((start < 0) || (finish < 0))
			{
				break;
			}
			
			if (html.substring(start + 8, start + 10).equals("-h"))
			{
				start += 11;
			}
			else
			{
				start += 8;
			}
			
			i = finish;
			int finish2 = html.indexOf("$", start);
			if ((finish2 < finish) && (finish2 > 0))
			{
				activeChar.addHtmlAction(HtmlActionScope.NPC_HTML, html.substring(start, finish2).trim());
			}
			else
			{
				activeChar.addHtmlAction(HtmlActionScope.NPC_HTML, html.substring(start, finish).trim());
			}
		}
	}
	
	/**
	 * Copy from {@link org.l2jmobius.gameserver.communitybbs.Manager.BaseBBSManager}. Modified to allow larger community board htmls.
	 * @param player the player to send to
	 * @param html the html text
	 */
	private void sendBBSHtml(Player player, String html)
	{
		buildBypassCache(player, html);
		
		if (html.length() < 16250)
		{
			player.sendPacket(new ShowBoard(html, "101"));
			player.sendPacket(new ShowBoard(null, "102"));
			player.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < (16250 * 2))
		{
			player.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
			player.sendPacket(new ShowBoard(html.substring(16250), "102"));
			player.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < (16250 * 3))
		{
			player.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
			player.sendPacket(new ShowBoard(html.substring(16250, 16250 * 2), "102"));
			player.sendPacket(new ShowBoard(html.substring(16250 * 2), "103"));
		}
		else
		{
			player.sendPacket(new ShowBoard("<html><body><br><center>Error: HTML was too long!</center></body></html>", "101"));
			player.sendPacket(new ShowBoard(null, "102"));
			player.sendPacket(new ShowBoard(null, "103"));
		}
	}
	
	private void fillItemAmountMap(Map<Integer, Long> items, BufferData.Buff buff)
	{
		for (Entry<String, ItemRequirement> item : buff.items.entrySet())
		{
			Long amount = items.get(item.getValue().item.getId());
			if (amount == null)
			{
				amount = 0L;
			}
			items.put(item.getValue().item.getId(), amount + item.getValue().amount);
		}
	}
	
	private void castBuff(Playable playable, BufferData.Buff buff)
	{
		buff.skill.applyEffects(playable, playable, true, Config.BUFFER_CUSTOM_BUFF_DURATION * MINUTE_IN_SECONDS);
	}
	
	// //////////////////////////////////
	// HTML COMMANDS
	// //////////////////////////////////
	private void showAdvancedHtml(Player player, BufferData.Buffer buffer, Npc npc, String htmlPath, Map<String, HTMLTemplatePlaceholder> placeholders)
	{
		placeholders.put(buffer.placeholder.getName(), buffer.placeholder);
		
		HTMLTemplatePlaceholder ulistsPlaceholder = BufferData.getInstance().getPlayersUListsPlaceholder(player.getObjectId());
		if (ulistsPlaceholder != null)
		{
			placeholders.put(ulistsPlaceholder.getName(), ulistsPlaceholder);
		}
		
		String activeUniqueName = _ACTIVE_PLAYER_BUFFLISTS.get(player.getObjectId());
		if (activeUniqueName != null)
		{
			HTMLTemplatePlaceholder ulistPlaceholder = BufferData.getInstance().getPlayersUListPlaceholder(player.getObjectId(), activeUniqueName);
			if (ulistPlaceholder != null)
			{
				placeholders.put("active_unique", ulistPlaceholder);
			}
		}
		
		BufferData.HtmlType dialogType = BufferData.getInstance().getHtmlType();
		
		String filePath = "data/scripts/" + "custom" + "/Buffer" + "/data/html/" + "npc" + "/" + htmlPath;
		String html = HTMLTemplateParser.fromCache(filePath, player, placeholders, IncludeFunc.INSTANCE, IfFunc.INSTANCE, ForeachFunc.INSTANCE, ExistsFunc.INSTANCE, IfChildsFunc.INSTANCE, ChildsCountFunc.INSTANCE);
		// String html = generateAdvancedHtml(player, htmlPath, placeholders, dialogType);
    // Assuming NpcHtmlMessage is always used
    	player.sendPacket(new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId(), html));

	// 	String html = generateAdvancedHtml(player, htmlPath, placeholders, dialogType);
	// 	switch (dialogType)
	// 	{
	// 		case NPC:
	// 			player.sendPacket(new NpcHtmlMessage(npc == null ? 0 : npc.getObjectId(), html));
	// 			break;
	// 		case COMMUNITY:
	// 			sendBBSHtml(player, html);
	// 			break;
	// 	}
	}
	
	private void htmlShowMain(Player player, BufferData.Buffer buffer, Npc npc)
	{
		showAdvancedHtml(player, buffer, npc, "main.html", new HashMap<String, HTMLTemplatePlaceholder>());
	}
	
	private void htmlShowCategory(Player player, BufferData.Buffer buffer, Npc npc, String categoryIdent)
	{
		BufferData.BuffCategory buffCat = buffer.getBuffCat(categoryIdent);
		if (buffCat == null)
		{
			return;
		}
		
		HashMap<String, HTMLTemplatePlaceholder> placeholders = new HashMap<>();
		
		placeholders.put("category", buffCat.placeholder);
		
		showAdvancedHtml(player, buffer, npc, "category.html", placeholders);
	}
	
	private void htmlShowBuff(Player player, BufferData.Buffer buffer, Npc npc, String categoryIdent, String buffIdent)
	{
		BufferData.BuffCategory buffCat = buffer.getBuffCat(categoryIdent);
		if (buffCat == null)
		{
			return;
		}
		BufferData.Buff buff = buffCat.getBuff(buffIdent);
		if (buff == null)
		{
			return;
		}
		
		HashMap<String, HTMLTemplatePlaceholder> placeholders = new HashMap<>();
		
		placeholders.put("category", buffCat.placeholder);
		placeholders.put("buff", buff.placeholder);
		
		showAdvancedHtml(player, buffer, npc, "buff.html", placeholders);
	}
	
	private void htmlShowPreset(Player player, BufferData.Buffer buffer, Npc npc, String presetBufflistIdent)
	{
		BufferData.BuffCategory presetBufflist = buffer.getPresetBufflist(presetBufflistIdent);
		if (presetBufflist == null)
		{
			return;
		}
		
		HashMap<String, HTMLTemplatePlaceholder> placeholders = new HashMap<>();
		
		placeholders.put("preset", presetBufflist.placeholder);
		
		showAdvancedHtml(player, buffer, npc, "preset.html", placeholders);
	}
	
	private void htmlShowUnique(Player player, BufferData.Buffer buffer, Npc npc, String uniqueName)
	{
		HTMLTemplatePlaceholder uniquePlaceholder = BufferData.getInstance().getPlayersUListPlaceholder(player.getObjectId(), uniqueName);
		if (uniquePlaceholder == null)
		{
			// redirect to main html if uniqueName is not valid, will most likely happen when the player deletes a unique bufflist he is currently viewing
			executeHtmlCommand(player, buffer, npc, "main");
			return;
		}
		
		HashMap<String, HTMLTemplatePlaceholder> placeholders = new HashMap<>();
		
		placeholders.put(uniquePlaceholder.getName(), uniquePlaceholder);
		
		showAdvancedHtml(player, buffer, npc, "unique.html", placeholders);
	}
	
	private void executeHtmlCommand(Player player, BufferData.Buffer buffer, Npc npc, String command)
	{
		_LAST_PLAYER_HTMLS.put(player.getObjectId(), command);
		
		if ("main".equals(command))
		{
			htmlShowMain(player, buffer, npc);
		}
		else if (command.startsWith("category "))
		{
			htmlShowCategory(player, buffer, npc, command.substring(9));
		}
		else if (command.startsWith("preset "))
		{
			htmlShowPreset(player, buffer, npc, command.substring(7));
		}
		else if (command.startsWith("buff "))
		{
			String[] argsSplit = command.substring(5).split(" ", 2);
			if (argsSplit.length != 2)
			{
				return;
			}
			htmlShowBuff(player, buffer, npc, argsSplit[0], argsSplit[1]);
		}
		else if (command.startsWith("unique "))
		{
			htmlShowUnique(player, buffer, npc, command.substring(7));
		}
		else
		{
			// all other malformed bypasses
			htmlShowMain(player, buffer, npc);
		}
	}
	
	//
	// ////////////////////////////////
	
	// /////////////////////////////////////////////
	// TARGET COMMANDS
	// /////////////////////////////////////////////
	private void targetBuffBuff(Player player, Playable target, BufferData.Buffer buffer, String categoryIdent, String buffIdent)
	{
		BufferData.BuffCategory bCat = buffer.getBuffCat(categoryIdent);
		if (bCat == null)
		{
			return;
		}
		BufferData.Buff buff = bCat.getBuff(buffIdent);
		if (buff == null)
		{
			return;
		}
		
		if (!buff.items.isEmpty())
		{
			HashMap<Integer, Long> items = new HashMap<>();
			fillItemAmountMap(items, buff);
			
			for (Entry<Integer, Long> item : items.entrySet())
			{
				if (player.getInventory().getInventoryItemCount(item.getKey(), 0, true) < item.getValue())
				{
					player.sendMessage("Not enough items!");
					return;
				}
			}
			
			for (Entry<Integer, Long> item : items.entrySet())
			{
				player.destroyItemByItemId("Buffer", item.getKey(), item.getValue(), player, true);
			}
		}
		
		castBuff(target, buff);
	}
	
	private void targetBuffUnique(Player player, Playable target, BufferData.Buffer buffer, String uniqueName)
	{
		List<BufferData.Buff> buffs = BufferData.getInstance().getUniqueBufflist(player.getObjectId(), uniqueName);
		
		if (buffs != null)
		{
			HashMap<Integer, Long> items = null;
			for (BufferData.Buff buff : buffs)
			{
				if (!buff.items.isEmpty())
				{
					if (items == null)
					{
						items = new HashMap<>();
					}
					fillItemAmountMap(items, buff);
				}
			}
			
			if (items != null)
			{
				for (Entry<Integer, Long> item : items.entrySet())
				{
					if (player.getInventory().getInventoryItemCount(item.getKey(), 0, true) < item.getValue())
					{
						player.sendMessage("Not enough items!");
						return;
					}
				}
				
				for (Entry<Integer, Long> item : items.entrySet())
				{
					player.destroyItemByItemId("Buffer", item.getKey(), item.getValue(), player, true);
				}
			}
			
			for (BufferData.Buff buff : buffs)
			{
				castBuff(target, buff);
			}
		}
	}
	
	private void targetBuffPreset(Player player, Playable target, BufferData.Buffer buffer, String presetBufflistIdent)
	{
		BufferData.BuffCategory presetBufflist = buffer.getPresetBufflist(presetBufflistIdent);
		if (presetBufflist == null)
		{
			return;
		}
		
		Collection<BufferData.Buff> buffs = presetBufflist.buffs.values();
		
		if (buffs != null)
		{
			HashMap<Integer, Long> items = null;
			for (BufferData.Buff buff : buffs)
			{
				if (!buff.items.isEmpty())
				{
					if (items == null)
					{
						items = new HashMap<>();
					}
					fillItemAmountMap(items, buff);
				}
			}
			
			if (items != null)
			{
				for (Entry<Integer, Long> item : items.entrySet())
				{
					if (player.getInventory().getInventoryItemCount(item.getKey(), 0, true) < item.getValue())
					{
						player.sendMessage("Not enough items!");
						return;
					}
				}
				
				for (Entry<Integer, Long> item : items.entrySet())
				{
					player.destroyItemByItemId("Buffer", item.getKey(), item.getValue(), player, true);
				}
			}
			
			for (BufferData.Buff buff : buffs)
			{
				castBuff(target, buff);
			}
		}
	}
	
	private void targetHeal(Player player, Playable target, BufferData.Buffer buffer)
	{
		if (!buffer.canHeal)
		{
			return;
		}
		
		// prevent heal spamming, process cooldown on heal target
		Long lastPlayableHealTime = _LAST_PLAYABLES_HEAL_TIME.get(target.getObjectId());
		if (lastPlayableHealTime != null)
		{
			Long elapsedTime = System.currentTimeMillis() - lastPlayableHealTime;
			Long healCooldown = BufferData.getInstance().getHealCooldown();
			if (elapsedTime < healCooldown)
			{
				Long remainingTime = healCooldown - elapsedTime;
				if (target == player)
				{
					player.sendMessage("You can heal yourself again in " + (remainingTime / 1000) + " seconds.");
				}
				else
				{
					player.sendMessage("You can heal your pet again in " + (remainingTime / 1000) + " seconds.");
				}
				return;
			}
		}
		
		_LAST_PLAYABLES_HEAL_TIME.put(target.getObjectId(), System.currentTimeMillis());
		
		if (player == target)
		{
			player.setCurrentCp(player.getMaxCp());
		}
		target.setCurrentHp(target.getMaxHp());
		target.setCurrentMp(target.getMaxMp());
		target.broadcastStatusUpdate();
	}
	
	private void targetCancel(Player player, Playable target, BufferData.Buffer buffer)
	{
		if (!buffer.canCancel)
		{
			return;
		}
		target.stopAllEffectsExceptThoseThatLastThroughDeath();
	}
	
	private void executeTargetCommand(Player player, BufferData.Buffer buffer, String command)
	{
		// /////////////////////////////////
		// first determine the target
		Playable target;
		if (command.startsWith("player "))
		{
			target = player;
			command = command.substring(7);
		}
		else if (command.startsWith("summon "))
		{
			target = player.getPet();
			if (target == null)
			{
				return;
			}
			command = command.substring(7);
		}
		else
		{
			return;
		}
		
		// //////////////////////////////////////////
		// run the choosen action on the target
		if (command.startsWith("buff "))
		{
			String[] argsSplit = command.substring(5).split(" ", 2);
			if (argsSplit.length != 2)
			{
				return;
			}
			targetBuffBuff(player, target, buffer, argsSplit[0], argsSplit[1]);
		}
		else if (command.startsWith("unique "))
		{
			targetBuffUnique(player, target, buffer, command.substring(7));
		}
		else if (command.startsWith("preset "))
		{
			targetBuffPreset(player, target, buffer, command.substring(7));
		}
		else if ("heal".equals(command))
		{
			targetHeal(player, target, buffer);
		}
		else if ("cancel".equals(command))
		{
			targetCancel(player, target, buffer);
		}
	}
	
	//
	// ////////////////////////////////
	
	// ////////////////////////////////
	// UNIQUE COMMANDS
	// ////////////////////////////////
	private boolean uniqueCreate(Player player, String uniqueName)
	{
		if (!BufferData.getInstance().canHaveMoreBufflists(player))
		{
			player.sendMessage("Maximum number of unique bufflists reached!");
			return false;
		}
		
		// only allow alpha numeric names because we use this name on the htmls
		if (!uniqueName.matches("[A-Za-z0-9]+"))
		{
			return false;
		}
		
		return BufferData.getInstance().createUniqueBufflist(player.getObjectId(), uniqueName);
	}
	
	private void uniqueDelete(Player player, String uniqueName)
	{
		BufferData.getInstance().deleteUniqueBufflist(player.getObjectId(), uniqueName);
		// also remove from active bufflist when it's the deleted
		String activeUniqueName = _ACTIVE_PLAYER_BUFFLISTS.get(player.getObjectId());
		if ((activeUniqueName != null) && activeUniqueName.equals(uniqueName))
		{
			_ACTIVE_PLAYER_BUFFLISTS.remove(player.getObjectId());
		}
	}
	
	private void uniqueAdd(Player player, BufferData.Buffer buffer, String uniqueName, String categoryIdent, String buffIdent)
	{
		BufferData.BuffCategory bCat = buffer.getBuffCat(categoryIdent);
		if (bCat == null)
		{
			return;
		}
		BufferData.Buff buff = bCat.getBuff(buffIdent);
		if (buff == null)
		{
			return;
		}
		
		BufferData.getInstance().addToUniqueBufflist(player.getObjectId(), uniqueName, buff);
	}
	
	private void uniqueRemove(Player player, String uniqueName, String buffIdent)
	{
		BufferData.Buff buff = BufferData.getInstance().getBuff(buffIdent);
		if (buff == null)
		{
			return;
		}
		
		BufferData.getInstance().removeFromUniqueBufflist(player.getObjectId(), uniqueName, buff);
	}
	
	private void uniqueSelect(Player player, String uniqueName)
	{
		if (BufferData.getInstance().hasUniqueBufflist(player.getObjectId(), uniqueName))
		{
			_ACTIVE_PLAYER_BUFFLISTS.put(player.getObjectId(), uniqueName);
		}
	}
	
	private void uniqueDeselect(Player player)
	{
		_ACTIVE_PLAYER_BUFFLISTS.remove(player.getObjectId());
	}
	
	private void executeUniqueCommand(Player player, BufferData.Buffer buffer, String command)
	{
		if (command.startsWith("create "))
		{
			uniqueCreate(player, command.substring(7));
		}
		else if (command.startsWith("create_from_effects "))
		{
			String uniqueName = command.substring(20);
			if (!uniqueCreate(player, uniqueName))
			{
				return;
			}

			final Collection<BuffInfo> buffs = player.getEffectList().getEffects();
			for (final BuffInfo effect : buffs)
			{
				for (Entry<String, BufferData.BuffCategory> buffCatEntry : buffer.buffCats.entrySet())
				{
					boolean added = false;
					
					for (Entry<String, BufferData.Buff> buffEntry : buffCatEntry.getValue().buffs.entrySet())
					{
						final BufferData.Buff buff = buffEntry.getValue();
						
						if (buff.skill.getId() == effect.getSkill().getId())
						{
							uniqueAdd(player, buffer, uniqueName, buffCatEntry.getKey(), buff.ident);
							added = true;
							break;
						}
					}
					
					if (added)
					{
						break;
					}
				}
			}
		}
		else if (command.startsWith("delete "))
		{
			uniqueDelete(player, command.substring(7));
		}
		else if (command.startsWith("add "))
		{
			String[] argsSplit = command.substring(4).split(" ", 3);
			if (argsSplit.length != 3)
			{
				return;
			}
			uniqueAdd(player, buffer, argsSplit[0], argsSplit[1], argsSplit[2]);
		}
		else if (command.startsWith("remove "))
		{
			String[] argsSplit = command.substring(7).split(" ", 2);
			if (argsSplit.length != 2)
			{
				return;
			}
			uniqueRemove(player, argsSplit[0], argsSplit[1]);
		}
		else if (command.startsWith("select "))
		{
			uniqueSelect(player, command.substring(7));
		}
		else if (command.startsWith("deselect"))
		{
			uniqueDeselect(player);
		}
	}
	
	//
	// ////////////////////////////////
	
	private static boolean isInsideAnyZoneOf(Creature character, ZoneId first, ZoneId... more)
	{
		if (character.isInsideZone(first))
		{
			return true;
		}
		
		if (more != null)
		{
			for (ZoneId zone : more)
			{
				if (character.isInsideZone(zone))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	void executeCommand(Player player, Npc npc, String command)
	{
		if (isInsideAnyZoneOf(player, ZoneId.PVP, ZoneId.SIEGE, ZoneId.WATER, ZoneId.JAIL, ZoneId.DANGER_AREA))
		{
			player.sendMessage("The buffer cannot be used here.");
			return;
		}
		else if (player.isOnEvent() || player.isInOlympiadMode())
		{
			player.sendMessage("The buffer cannot be used in events.");
			return;
		}
		
		else if (player.isInDuel() || (player.getPvpFlag() == 1))
		{
			player.sendMessage("The buffer cannot be used in duells or pvp.");
			return;
		}
		
		else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
		{
			player.sendMessage("The buffer cannot be used while in combat.");
			return;
		}
		
		BufferData.Buffer buffer = determineBuffer(npc, player);
		if (buffer == null)
		{
			// not an authorized npc or npc is null and voiced buffer is disabled
			return;
		}
		
		if ((command == null) || command.isEmpty())
		{
			command = "html main";
		}
		
		if (command.startsWith("html "))
		{
			executeHtmlCommand(player, buffer, npc, command.substring(5));
		}
		else
		{
			if (command.startsWith("target "))
			{
				executeTargetCommand(player, buffer, command.substring(7));
			}
			else if (command.startsWith("unique "))
			{
				executeUniqueCommand(player, buffer, command.substring(7));
			}
			
			// display last html again
			// since somebody could use the chat as a command line(eg.: .buffer target player heal), we check if the player has opened a html before
			String lastHtmlCommand = _LAST_PLAYER_HTMLS.get(player.getObjectId());
			if (lastHtmlCommand != null)
			{
				executeHtmlCommand(player, buffer, npc, _LAST_PLAYER_HTMLS.get(player.getObjectId()));
			}
		}
	}
}
