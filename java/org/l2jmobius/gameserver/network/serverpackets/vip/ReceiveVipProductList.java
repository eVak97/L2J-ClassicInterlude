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
package org.l2jmobius.gameserver.network.serverpackets.vip;

import java.util.Collection;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.PrimeShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.primeshop.PrimeShopGroup;
import org.l2jmobius.gameserver.model.primeshop.PrimeShopItem;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

public class ReceiveVipProductList extends ServerPacket
{
	private final Player _player;
	
	public ReceiveVipProductList(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		if (!Config.VIP_SYSTEM_ENABLED)
		{
			return;
		}
		
		final Collection<PrimeShopGroup> products = PrimeShopData.getInstance().getPrimeItems().values();
		final PrimeShopGroup gift = PrimeShopData.getInstance().getVipGiftOfTier(_player.getVipTier());
		ServerPackets.RECIVE_VIP_PRODUCT_LIST.writeId(this, buffer);
		buffer.writeLong(_player.getAdena());
		buffer.writeLong(_player.getGoldCoin()); // Gold Coin Amount
		buffer.writeLong(_player.getSilverCoin()); // Silver Coin Amount
		buffer.writeByte(1); // Show Reward tab
		if (gift != null)
		{
			buffer.writeInt(products.size() + 1);
			writeProduct(gift, buffer);
		}
		else
		{
			buffer.writeInt(products.size());
		}
		for (PrimeShopGroup product : products)
		{
			writeProduct(product, buffer);
		}
	}
	
	private void writeProduct(PrimeShopGroup product, WritableBuffer buffer)
	{
		buffer.writeInt(product.getBrId());
		buffer.writeByte(product.getCat());
		buffer.writeByte(product.getPaymentType());
		buffer.writeInt(product.getPrice()); // L2 Coin | Gold Coin seems to use the same field based on payment type
		buffer.writeInt(product.getSilverCoin());
		buffer.writeByte(product.getPanelType()); // NEW - 6; HOT - 5 ... Unk
		buffer.writeByte(product.getVipTier());
		buffer.writeByte(10);
		buffer.writeByte(product.getItems().size());
		for (PrimeShopItem item : product.getItems())
		{
			buffer.writeInt(item.getId());
			buffer.writeInt((int) item.getCount());
		}
	}
}
