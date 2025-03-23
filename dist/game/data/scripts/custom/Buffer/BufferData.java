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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.l2jmobius.Config;
import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.data.xml.ItemData; // Nueva clase para manejar ítems
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillBuffType;

import custom.Buffer.util.ItemRequirement;
import custom.Buffer.util.XMLUtils;
import custom.Buffer.util.htmltmpls.HTMLTemplatePlaceholder;

/**
 * @author HorridoJoho
 */
final class BufferData
{
	public static enum HtmlType
	{
		NPC,
		COMMUNITY
	}
	
	public final class Buff
	{
		protected final String ident;
		protected final Skill skill;
		protected final SkillBuffType type;
		protected final Map<String, ItemRequirement> items;
		/** HTMLTemplatePlaceholder */
		protected final HTMLTemplatePlaceholder placeholder;
		
		protected Buff(String ident, Skill skill, SkillBuffType type, Element elem)
		{
			this.ident = ident;
			this.skill = skill;
			this.type = type;
			this.items = _parseItems(elem);
			
			this.placeholder = new HTMLTemplatePlaceholder("buff", null);
			this.placeholder.addChild("ident", ident).addChild("skill_id", String.valueOf(skill.getId())).addChild("skill_name", skill.getName()).addChild("skill_icon", _getClientSkillIconSource(skill.getId())).addChild("type", type.toString());
			if (!this.items.isEmpty())
			{
				HTMLTemplatePlaceholder itemsPlaceholder = this.placeholder.addChild("items", null).getChild("items");
				for (Entry<String, ItemRequirement> item : this.items.entrySet())
				{
					itemsPlaceholder.addAliasChild(String.valueOf(itemsPlaceholder.getChildsSize()), item.getValue().placeholder);
				}
			}
		}
		
		private Map<String, ItemRequirement> _parseItems(Element elem)
		{
			Map<String, ItemRequirement> items = new LinkedHashMap<>();
			Node curNode = elem.getFirstChild();
			while (curNode != null)
			{
				switch (curNode.getNodeType())
				{
					case Node.ELEMENT_NODE:
						Element curElem = (Element) curNode;
						String ident = curElem.getAttribute("ident");
						ItemRequirement req = _itemRequirements.get(ident);
						if (req == null)
						{
							_LOGGER.warning("Buffer - buffs.xml: Item requirement with ident " + ident + " does not exists!");
						}
						else
						{
							items.put(ident, req);
						}
						break;
				}
				
				curNode = curNode.getNextSibling();
			}
			return Collections.unmodifiableMap(items);
		}
		
		private String _getClientSkillIconSource(int skillId)
		{
			String format = "";
			if (skillId < 100)
			{
				format = "00" + skillId;
			}
			else if ((skillId > 99) && (skillId < 1000))
			{
				format = "0" + skillId;
			}
			else if (skillId == 1517)
			{
				format = "1499";
			}
			else if (skillId == 1518)
			{
				format = "1502";
			}
			else
			{
				if ((skillId > 4698) && (skillId < 4701))
				{
					format = "1331";
				}
				else if ((skillId > 4701) && (skillId < 4704))
				{
					format = "1332";
				}
				else
				{
					format = Integer.toString(skillId);
				}
			}
			
			return "icon.skill" + format;
		}
		
		public SkillBuffType getType()
		{
			return type;
		}
	}
	
	public final class BuffCategory
	{
		protected final String ident;
		protected final Map<String, Buff> buffs;
		/** HTMLTemplatePlaceholder */
		protected final HTMLTemplatePlaceholder placeholder;
		
		protected BuffCategory(String ident, String name, Element elem)
		{
			this.ident = ident;
			this.buffs = _parseBuffs(elem);
			
			this.placeholder = new HTMLTemplatePlaceholder("category", null);
			this.placeholder.addChild("ident", ident).addChild("name", name);
			if (!this.buffs.isEmpty())
			{
				HTMLTemplatePlaceholder buffsPlaceholder = this.placeholder.addChild("buffs", null).getChild("buffs");
				for (Entry<String, Buff> buff : this.buffs.entrySet())
				{
					buffsPlaceholder.addAliasChild(String.valueOf(buffsPlaceholder.getChildsSize()), buff.getValue().placeholder);
				}
			}
		}
		
		private Map<String, Buff> _parseBuffs(Element elem)
		{
			Map<String, Buff> buffs = new LinkedHashMap<>();
			Node curNode = elem.getFirstChild();
			while (curNode != null)
			{
				switch (curNode.getNodeType())
				{
					case Node.ELEMENT_NODE:
						Element curElem = (Element) curNode;
						String ident = curElem.getAttribute("ident");
						Buff buff = _buffs.get(ident);
						if (buff == null)
						{
							_LOGGER.warning("Buffer - buff_categories.xml: Buff with ident " + ident + " does not exists!");
						}
						else
						{
							buffs.put(ident, buff);
						}
						break;
				}
				
				curNode = curNode.getNextSibling();
			}
			return Collections.unmodifiableMap(buffs);
		}
		
		public Buff getBuff(String buffIdent)
		{
			return buffs.get(buffIdent);
		}
	}
	
	public abstract class Buffer
	{
		/** Can heal a player */
		protected final boolean canHeal;
		/** Can cancel a player */
		protected final boolean canCancel;
		/** buff categories */
		protected final Map<String, BuffCategory> buffCats;
		/** preset bufflists */
		protected final Map<String, BuffCategory> presetBufflists;
		/** HTMLTemplatePlaceholder */
		protected final HTMLTemplatePlaceholder placeholder;
		
		protected Buffer(String xmlFile, String bypassPrefix, String bufferName, Element elem)
		{
			this.canHeal = Boolean.parseBoolean(elem.getAttribute("can_heal"));
			this.canCancel = Boolean.parseBoolean(elem.getAttribute("can_cancel"));
			this.buffCats = _parseBuffCats(xmlFile, elem, false);
			this.presetBufflists = _parseBuffCats(xmlFile, elem, true);
			
			this.placeholder = new HTMLTemplatePlaceholder("buffer", null);
			this.placeholder.addChild("bypass_prefix", "bypass -h " + bypassPrefix).addChild("name", bufferName).addChild("max_unique_lists", String.valueOf(_maxUniqueLists)).addChild("unique_max_buffs", String.valueOf(_uniqueMaxBuffs)).addChild("unique_max_songs_dances", String.valueOf(_uniqueMaxSongsDances));
			if (this.canHeal)
			{
				this.placeholder.addChild("can_heal", null);
			}
			if (this.canCancel)
			{
				this.placeholder.addChild("can_cancel", null);
			}
			if (!this.buffCats.isEmpty())
			{
				HTMLTemplatePlaceholder buffCatsPlaceholder = this.placeholder.addChild("categories", null).getChild("categories");
				for (Entry<String, BuffCategory> buffCat : this.buffCats.entrySet())
				{
					buffCatsPlaceholder.addAliasChild(String.valueOf(buffCatsPlaceholder.getChildsSize()), buffCat.getValue().placeholder);
				}
			}
			if (!this.presetBufflists.isEmpty())
			{
				HTMLTemplatePlaceholder presetBufflistsPlaceholder = this.placeholder.addChild("presets", null).getChild("presets");
				for (Entry<String, BuffCategory> presetBufflist : this.presetBufflists.entrySet())
				{
					presetBufflistsPlaceholder.addAliasChild(String.valueOf(presetBufflistsPlaceholder.getChildsSize()), presetBufflist.getValue().placeholder);
				}
			}
		}
		
		private Map<String, BuffCategory> _parseBuffCats(String xmlFile, Element elem, boolean preset)
		{
			Map<String, BuffCategory> buffCats = new LinkedHashMap<>();
			Node curNode = elem.getFirstChild();
			while (curNode != null)
			{
				switch (curNode.getNodeType())
				{
					case Node.ELEMENT_NODE:
						Element curElem = (Element) curNode;
						String ident = curElem.getAttribute("ident");
						boolean isPreset = Boolean.parseBoolean(curElem.getAttribute("is_preset"));
						if (preset != isPreset)
						{
							break;
						}
						BuffCategory buffCat = _buffCats.get(ident);
						if (buffCat == null)
						{
							_LOGGER.warning("Buffer - " + xmlFile + ": Buff category with ident " + ident + " does not exists!");
						}
						else
						{
							buffCats.put(ident, buffCat);
						}
						break;
				}
				
				curNode = curNode.getNextSibling();
			}
			return Collections.unmodifiableMap(buffCats);
		}
		
		public BuffCategory getBuffCat(String buffCatIdent)
		{
			return buffCats.get(buffCatIdent);
		}
		
		public BuffCategory getPresetBufflist(String presetBufflistIdent)
		{
			return presetBufflists.get(presetBufflistIdent);
		}
		
		public Map<String, BuffCategory> getBuffCats()
		{
			return buffCats;
		}
	}
	
	public final class BufferNpc extends Buffer
	{
		/** npc id */
		protected final NpcTemplate npc;
		
		protected BufferNpc(NpcTemplate npc, Element elem)
		{
			super("buffer_npcs.xml", BufferNpcBypassHandler.BYPASS, npc.getName(), elem);
			this.npc = npc;
		}
	}
	
	public final class VoicedBuffer extends Buffer
	{
		public final boolean enabled;
		public final int requiredItem;
		
		protected VoicedBuffer(Element elem)
		{
			super("voiced_buffer.xml", "voice ." + BufferVoicedCommandHandler.VOICED_COMMAND, "Voiced Command Buffer", elem);
			this.enabled = Boolean.parseBoolean(elem.getAttribute("enabled"));
			if (elem.hasAttribute("required_item"))
			{
				this.requiredItem = Integer.parseInt(elem.getAttribute("required_item"));
			}
			else
			{
				this.requiredItem = 0;
			}
		}
	}
	
	protected static final Logger _LOGGER = Logger.getLogger(BufferData.class.getName());
	private static BufferData _INSTANCE = null;
	
	public synchronized static void initInstance() throws Exception
	{
		_INSTANCE = new BufferData();
	}
	
	public synchronized static BufferData getInstance()
	{
		return _INSTANCE;
	}
	
	private final HtmlType _htmlType;
	private final long _healCooldown;
	protected final int _maxUniqueLists;
	protected final int _uniqueMaxBuffs;
	protected final int _uniqueMaxSongsDances;
	protected final Map<String, ItemRequirement> _itemRequirements = new HashMap<>();
	protected final Map<String, Buff> _buffs = new HashMap<>();
	protected final Map<String, BuffCategory> _buffCats = new HashMap<>();
	private final Map<Integer, BufferNpc> _bufferNpcs = new HashMap<>();
	private final VoicedBuffer _voicedBuffer;
	protected final ConcurrentHashMap<Integer, Map<Integer, UniqueBufflist>> _uniqueBufflists = new ConcurrentHashMap<>();
	
	private BufferData() throws Exception
	{
		Path xmlPath = Paths.get(Config.DATAPACK_ROOT.getAbsolutePath(), "data", "scripts", "custom", "Buffer", "data", "xml");
		Path xsdPath = Paths.get(xmlPath.toString(), "xsd");
		
		Element elem = XMLUtils.CreateDocument(xmlPath.resolve("buffer.xml"), xsdPath.resolve("buffer.xsd")).getDocumentElement();
		_htmlType = HtmlType.valueOf(elem.getAttribute("dialog_type"));
		_healCooldown = Integer.parseInt(elem.getAttribute("heal_cooldown")) * 1000;
		_maxUniqueLists = Integer.parseInt(elem.getAttribute("max_unique_lists"));
		_uniqueMaxBuffs = Integer.parseInt(elem.getAttribute("unique_max_buffs"));
		_uniqueMaxSongsDances = Integer.parseInt(elem.getAttribute("unique_max_songs_dances"));
		
		elem = XMLUtils.CreateDocument(xmlPath.resolve("item_requirements.xml"), xsdPath.resolve("item_requirements.xsd")).getDocumentElement();
		_parseItemRequirements(elem);
		
		elem = XMLUtils.CreateDocument(xmlPath.resolve("buffs.xml"), xsdPath.resolve("buffs.xsd")).getDocumentElement();
		_parseBuffs(elem);
		
		elem = XMLUtils.CreateDocument(xmlPath.resolve("buff_categories.xml"), xsdPath.resolve("buff_categories.xsd")).getDocumentElement();
		_parseBuffCats(elem, _buffCats);
		
		elem = XMLUtils.CreateDocument(xmlPath.resolve("buffer_npcs.xml"), xsdPath.resolve("buffer_npcs.xsd")).getDocumentElement();
		_parseBufferNpcs(elem);
		
		elem = XMLUtils.CreateDocument(xmlPath.resolve("voiced_buffer.xml"), xsdPath.resolve("voiced_buffer.xsd")).getDocumentElement();
		_voicedBuffer = new VoicedBuffer(elem);
		
		_loadUniqueBufflists();
	}
	
	private void _parseItemRequirements(Element elem)
	{
		Node curNode = elem.getFirstChild();
		while (curNode != null)
		{
			switch (curNode.getNodeType())
			{
				case Node.ELEMENT_NODE:
					Element curElem = (Element) curNode;
					String ident = curElem.getAttribute("ident");
					int itemId = Integer.parseInt(curElem.getAttribute("id"));
					long itemAmount = Long.parseLong(curElem.getAttribute("amount"));
					ItemTemplate item = ItemData.getInstance().getTemplate(itemId); // Reemplazo de ItemTable
					if (item == null)
					{
						_LOGGER.warning("Buffer - item_requirements.xml: Item with id " + itemId + " does not exists!");
					}
					else
					{
						_itemRequirements.put(ident, new ItemRequirement(item, itemAmount));
					}
					break;
			}
			
			curNode = curNode.getNextSibling();
		}
	}
	
	private void _parseBuffs(Element elem)
	{
		Node curNode = elem.getFirstChild();
		while (curNode != null)
		{
			switch (curNode.getNodeType())
			{
				case Node.ELEMENT_NODE:
					Element curElem = (Element) curNode;
					String ident = curElem.getAttribute("ident");
					int skillId = Integer.parseInt(curElem.getAttribute("skill_id"));
					int skillLevel = Integer.parseInt(curElem.getAttribute("skill_level"));
					Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
					SkillBuffType buffType = SkillBuffType.valueOf(curElem.getAttribute("type"));
					if (skill == null)
					{
						_LOGGER.warning("Buffer - buffs.xml: Skill with id " + skillId + " and level " + skillLevel + " does not exists!");
					}
					else
					{
						_buffs.put(ident, new Buff(ident, skill, buffType, curElem));
					}
					break;
			}
			
			curNode = curNode.getNextSibling();
		}
	}
	
	private void _parseBuffCats(Element elem, Map<String, BuffCategory> buffCats)
	{
		Node curNode = elem.getFirstChild();
		while (curNode != null)
		{
			switch (curNode.getNodeType())
			{
				case Node.ELEMENT_NODE:
					Element curElem = (Element) curNode;
					String ident = curElem.getAttribute("ident");
					buffCats.put(ident, new BuffCategory(ident, curElem.getAttribute("name"), curElem));
					break;
			}
			
			curNode = curNode.getNextSibling();
		}
	}
	
	private void _parseBufferNpcs(Element elem)
	{
		Node curNode = elem.getFirstChild();
		while (curNode != null)
		{
			switch (curNode.getNodeType())
			{
				case Node.ELEMENT_NODE:
					Element curElem = (Element) curNode;
					int npcId = Integer.parseInt(curElem.getAttribute("id"));
					NpcTemplate npc = NpcData.getInstance().getTemplate(npcId);
					if (npc == null)
					{
						_LOGGER.warning("Buffer - buffer_npcs.xml: Npc with id " + npcId + " does not exists!");
					}
					else
					{
						_bufferNpcs.put(npc.getId(), new BufferNpc(npc, curElem));
					}
					break;
			}
			
			curNode = curNode.getNextSibling();
		}
	}
	
	private void _loadUniqueBufflists() throws Exception
	{
		try (Connection con = DatabaseFactory.getConnection();)
		{
			try (Statement stmt = con.createStatement();
				ResultSet rset = stmt.executeQuery("SELECT id,character_id,list_name FROM buffer_user_lists ORDER BY character_id ASC");)
			{
				while (rset.next())
				{
					int charId = rset.getInt("character_id");
					int ulistId = rset.getInt("id");
					String ulistName = rset.getString("list_name");
					
					Map<Integer, UniqueBufflist> ulists = _getPlayersULists(charId);
					ulists.put(ulistId, new UniqueBufflist(ulistId, ulistName));
				}
			}
			
			for (Entry<Integer, Map<Integer, UniqueBufflist>> ulists : _uniqueBufflists.entrySet())
			{
				for (Entry<Integer, UniqueBufflist> ulist : ulists.getValue().entrySet())
				{
					try (PreparedStatement stmt = con.prepareStatement("SELECT buff_id FROM buffer_user_list_buffs WHERE user_buff_list_id=?");)
					{
						stmt.setInt(1, ulist.getKey());
						try (ResultSet rs = stmt.executeQuery();)
						{
							while (rs.next())
							{
								String buffIdent = rs.getString("buff_id");
								Buff buff = getBuff(buffIdent);
								if (buff == null)
								{
									_LOGGER.warning("Buffer - Data: Buff with ident does not exists!");
								}
								else
								{
									ulist.getValue().add(buff);
								}
							}
						}
					}
				}
			}
		}
		catch (SQLException sqle)
		{
			throw new SQLException(sqle);
		}
	}
	
	private Map<Integer, UniqueBufflist> _getPlayersULists(int playerObjectId)
	{
		Map<Integer, UniqueBufflist> ulists = _uniqueBufflists.get(playerObjectId);
		if (ulists == null)
		{
			ulists = new LinkedHashMap<>();
			_uniqueBufflists.put(playerObjectId, ulists);
		}
		
		return ulists;
	}
	
	private UniqueBufflist _getPlayersUList(int playerObjectId, String ulistName)
	{
		Map<Integer, UniqueBufflist> ulists = _getPlayersULists(playerObjectId);
		for (Entry<Integer, UniqueBufflist> entry : ulists.entrySet())
		{
			if (entry.getValue().ulistName.equals(ulistName))
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	public boolean createUniqueBufflist(int playerObjectId, String ulistName)
	{
		// prevent duplicate entry
		if (_getPlayersUList(playerObjectId, ulistName) != null)
		{
			return false;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO buffer_user_lists (character_id,list_name) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);)
		{
			stmt.setInt(1, playerObjectId);
			stmt.setString(2, ulistName);
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			int newId = rs.getInt(1);
			_getPlayersULists(playerObjectId).put(newId, new UniqueBufflist(newId, ulistName));
			return true;
		}
		catch (SQLException sqle)
		{
			_LOGGER.log(Level.WARNING, "Failed to insert unique bufflist!", sqle);
			return false;
		}
	}
	
	public void deleteUniqueBufflist(int playerObjectId, String ulistName)
	{
		UniqueBufflist ulist = _getPlayersUList(playerObjectId, ulistName);
		if (ulist == null)
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("DELETE FROM buffer_user_lists WHERE character_id=? AND id=?");)
		{
			stmt.setInt(1, playerObjectId);
			stmt.setInt(2, ulist.ulistId);
			stmt.executeUpdate();
			_getPlayersULists(playerObjectId).remove(ulist.ulistId);
		}
		catch (SQLException sqle)
		{
			_LOGGER.log(Level.WARNING, "Failed to delete unique bufflist!", sqle);
		}
	}
	
	public boolean addToUniqueBufflist(int playerObjectId, String ulistName, Buff buff)
	{
		UniqueBufflist ulist = _getPlayersUList(playerObjectId, ulistName);
		// prevent duplicate entry with ulist.contains(buff)
		if ((ulist == null) || ulist.contains(buff) || ((buff.getType() == SkillBuffType.BUFF) && (ulist.numBuffs >= _uniqueMaxBuffs)) || ((buff.getType() == SkillBuffType.DANCE) && (ulist.numSongsDances >= _uniqueMaxSongsDances)))
		{
			return false;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO buffer_user_list_buffs VALUES(?,?)");)
		{
			stmt.setInt(1, ulist.ulistId);
			stmt.setString(2, buff.ident);
			stmt.executeUpdate();
			ulist.add(buff);
		}
		catch (SQLException sqle)
		{
			_LOGGER.log(Level.WARNING, "Failed to insert buff into unique bufflist!", sqle);
			return false;
		}
		
		return true;
	}
	
	public void removeFromUniqueBufflist(int playerObjectId, String ulistName, Buff buff)
	{
		UniqueBufflist ulist = _getPlayersUList(playerObjectId, ulistName);
		if ((ulist == null) || !ulist.contains(buff))
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("DELETE FROM buffer_user_list_buffs WHERE user_buff_list_id=? AND buff_id=?");)
		{
			stmt.setInt(1, ulist.ulistId);
			stmt.setString(2, buff.ident);
			stmt.executeUpdate();
			ulist.remove(buff);
		}
		catch (SQLException sqle)
		{
			_LOGGER.log(Level.WARNING, "Failed to remove buff from unique bufflist!", sqle);
		}
	}
	
	public HtmlType getHtmlType()
	{
		return _htmlType;
	}
	
	public long getHealCooldown()
	{
		return _healCooldown;
	}
	
	public int getMaxUniqueLists()
	{
		return _maxUniqueLists;
	}
	
	public int getUniqueMaxBuffs()
	{
		return _uniqueMaxBuffs;
	}
	
	public int getUniqueMaxSongsBuffs()
	{
		return _uniqueMaxSongsDances;
	}
	
	public Buff getBuff(String buffIdent)
	{
		return _buffs.get(buffIdent);
	}
	
	public BuffCategory getBuffCat(String buffCatIdent)
	{
		return _buffCats.get(buffCatIdent);
	}
	
	public Map<Integer, BufferNpc> getBufferNpcs()
	{
		return _bufferNpcs;
	}
	
	public BufferNpc getBufferNpc(int npcId)
	{
		return _bufferNpcs.get(npcId);
	}
	
	public VoicedBuffer getVoicedBuffer()
	{
		return _voicedBuffer;
	}
	
	public boolean canHaveMoreBufflists(Player player)
	{
		return _getPlayersULists(player.getObjectId()).size() < _maxUniqueLists;
	}
	
	public boolean hasUniqueBufflist(int playerObjectId, String ulistName)
	{
		return _getPlayersUList(playerObjectId, ulistName) != null;
	}
	
	public List<Buff> getUniqueBufflist(int playerObjectId, String ulistName)
	{
		UniqueBufflist ulist = _getPlayersUList(playerObjectId, ulistName);
		if (ulist == null)
		{
			return null;
		}
		return Collections.unmodifiableList(ulist);
	}
	
	public HTMLTemplatePlaceholder getPlayersUListPlaceholder(int playerObjectId, String ulistName)
	{
		UniqueBufflist ulist = _getPlayersUList(playerObjectId, ulistName);
		if (ulist == null)
		{
			return null;
		}
		return ulist.placeholder;
	}
	
	public HTMLTemplatePlaceholder getPlayersUListsPlaceholder(int playerObjectId)
	{
		Map<Integer, UniqueBufflist> ulists = _getPlayersULists(playerObjectId);
		if (ulists.isEmpty())
		{
			return null;
		}
		
		HTMLTemplatePlaceholder placeholder = new HTMLTemplatePlaceholder("uniques", null);
		for (Entry<Integer, UniqueBufflist> entry : ulists.entrySet())
		{
			placeholder.addAliasChild(String.valueOf(placeholder.getChildsSize()), entry.getValue().placeholder);
		}
		return placeholder;
	}
}
