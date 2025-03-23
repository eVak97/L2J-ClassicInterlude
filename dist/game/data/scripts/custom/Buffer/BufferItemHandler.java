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

import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.enums.InstanceType;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;

/**
 * @author HorridoJoho
 */
public final class BufferItemHandler implements IItemHandler
{
	private static final class SingletonHolder
	{
		protected static final BufferItemHandler INSTANCE = new BufferItemHandler();
	}
	
	static BufferItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	BufferItemHandler()
	{
	}
	
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isInstanceTypes(InstanceType.Player))
		{
			return false;
		}
		
		Buffer.getInstance().executeCommand((Player) playable, null, null);
		return true;
	}
}
