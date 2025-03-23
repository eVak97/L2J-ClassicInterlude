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
package custom.Buffer.util;

import custom.Buffer.util.htmltmpls.HTMLTemplatePlaceholder;

import org.l2jmobius.gameserver.model.item.ItemTemplate;

/**
 * @author HorridoJoho
 */
public final class ItemRequirement
{
	/** Item id */
	public final ItemTemplate item;
	/** Item amount */
	public final long amount;
	/** HTMLTemplatePlaceholder */
	public final HTMLTemplatePlaceholder placeholder;
	
	public ItemRequirement(final ItemTemplate item, final long amount)
	{
		this.item = item;
		this.amount = amount;
		this.placeholder = new HTMLTemplatePlaceholder("item_requirement", null);
		this.placeholder.addChild("id", String.valueOf(item.getId())).addChild("icon", item.getIcon()).addChild("name", item.getName()).addChild("amount", String.valueOf(amount));
	}
}
