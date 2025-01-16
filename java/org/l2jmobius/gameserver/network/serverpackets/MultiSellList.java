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
package org.l2jmobius.gameserver.network.serverpackets;

import static org.l2jmobius.gameserver.data.xml.MultisellData.PAGE_SIZE;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.model.ItemInfo;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemChanceHolder;
import org.l2jmobius.gameserver.model.holders.MultisellEntryHolder;
import org.l2jmobius.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class MultiSellList extends AbstractItemPacket
{
	private final Player _player;
	private int _size;
	private int _index;
	private final PreparedMultisellListHolder _list;
	private final boolean _finished;
	private final int _type;
	
	public MultiSellList(Player player, PreparedMultisellListHolder list, int index, int type)
	{
		_player = player;
		_list = list;
		_index = index;
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
		{
			_finished = true;
		}
		_type = type;
	}
	
	public MultiSellList(Player player, PreparedMultisellListHolder list, int index)
	{
		this(player, list, index, 0);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.MULTI_SELL_LIST.writeId(this, buffer);
		buffer.writeByte(0); // Helios
		buffer.writeInt(_list.getId()); // list id
		buffer.writeByte(_type); // 196?
		buffer.writeInt(1 + (_index / PAGE_SIZE)); // page started from 1
		buffer.writeInt(_finished); // finished
		buffer.writeInt(PAGE_SIZE); // size of pages
		buffer.writeInt(_size); // list length
		buffer.writeByte(0); // Grand Crusade
		buffer.writeByte(_list.isChanceMultisell()); // new multisell window
		buffer.writeInt(32); // Helios - Always 32
		while (_size-- > 0)
		{
			ItemInfo itemEnchantment = _list.getItemEnchantment(_index);
			final MultisellEntryHolder entry = _list.getEntries().get(_index++);
			if ((itemEnchantment == null) && _list.isMaintainEnchantment())
			{
				SEARCH: for (ItemChanceHolder holder : entry.getIngredients())
				{
					final Item item = _player.getInventory().getItemByItemId(holder.getId());
					if ((item != null) && item.isEquipable() && !item.isEquipped())
					{
						itemEnchantment = new ItemInfo(item);
						break SEARCH;
					}
				}
			}
			buffer.writeInt(_index); // Entry ID. Start from 1.
			buffer.writeByte(entry.isStackable());
			// Those values will be passed down to MultiSellChoose packet.
			buffer.writeShort(itemEnchantment != null ? itemEnchantment.getEnchantLevel() : 0); // enchant level
			writeItemAugment(itemEnchantment, buffer);
			writeItemElemental(itemEnchantment, buffer);
			writeItemEnsoulOptions(itemEnchantment, buffer);
			buffer.writeShort(entry.getProducts().size());
			buffer.writeShort(entry.getIngredients().size());
			for (ItemChanceHolder product : entry.getProducts())
			{
				final ItemTemplate template = ItemData.getInstance().getTemplate(product.getId());
				final ItemInfo displayItemEnchantment = _list.isMaintainEnchantment() && (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getItem().getClass()) ? itemEnchantment : null;
				if (template != null)
				{
					buffer.writeInt(template.getDisplayId());
					buffer.writeLong(template.getBodyPart());
					buffer.writeShort(template.getType2());
				}
				else
				{
					buffer.writeInt(product.getId());
					buffer.writeLong(0);
					buffer.writeShort(65535);
				}
				buffer.writeLong(_list.getProductCount(product));
				buffer.writeShort(product.getEnchantmentLevel() > 0 ? product.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				buffer.writeInt((int) Math.ceil(product.getChance())); // chance
				writeItemAugment(displayItemEnchantment, buffer);
				writeItemElemental(displayItemEnchantment, buffer);
				writeItemEnsoulOptions(displayItemEnchantment, buffer);
			}
			for (ItemChanceHolder ingredient : entry.getIngredients())
			{
				final ItemTemplate template = ItemData.getInstance().getTemplate(ingredient.getId());
				final ItemInfo displayItemEnchantment = (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getItem().getClass()) ? itemEnchantment : null;
				if (template != null)
				{
					buffer.writeInt(template.getDisplayId());
					buffer.writeShort(template.getType2());
				}
				else
				{
					buffer.writeInt(ingredient.getId());
					buffer.writeShort(65535);
				}
				buffer.writeLong(_list.getIngredientCount(ingredient));
				buffer.writeShort(ingredient.getEnchantmentLevel() > 0 ? ingredient.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0); // enchant level
				writeItemAugment(displayItemEnchantment, buffer);
				writeItemElemental(displayItemEnchantment, buffer);
				writeItemEnsoulOptions(displayItemEnchantment, buffer);
			}
		}
	}
}