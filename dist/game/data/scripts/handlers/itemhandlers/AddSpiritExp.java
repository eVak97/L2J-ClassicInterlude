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
package handlers.itemhandlers;

import org.l2jmobius.gameserver.enums.ElementalType;
import org.l2jmobius.gameserver.handler.IItemHandler;
import org.l2jmobius.gameserver.model.ElementalSpirit;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.SystemMessageId;

/**
 * @author Mobius
 */
public class AddSpiritExp implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		final Player player = playable.asPlayer();
		
		ElementalSpirit spirit = null;
		switch (item.getId())
		{
			case 91999:
			case 91035:
			{
				spirit = player.getElementalSpirit(ElementalType.WATER);
				break;
			}
			case 92000:
			case 91036:
			{
				spirit = player.getElementalSpirit(ElementalType.FIRE);
				break;
			}
			case 92001:
			case 91037:
			{
				spirit = player.getElementalSpirit(ElementalType.WIND);
				break;
			}
			case 92002:
			case 91038:
			{
				spirit = player.getElementalSpirit(ElementalType.EARTH);
				break;
			}
		}
		
		if ((spirit != null) && checkConditions(player, spirit))
		{
			player.destroyItem("AddSpiritExp item", item, 1, player, true);
			spirit.addExperience(9300);
			return true;
		}
		
		return false;
	}
	
	private boolean checkConditions(Player player, ElementalSpirit spirit)
	{
		if (player.isInBattle())
		{
			player.sendPacket(SystemMessageId.CANNOT_DRAIN_DURING_BATTLE);
			return false;
		}
		if ((spirit.getLevel() == spirit.getMaxLevel()) && (spirit.getExperience() == spirit.getExperienceToNextLevel()))
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_REACHED_THE_HIGHEST_LEVEL_AND_CANNOT_ABSORB_ANY_FURTHER);
			return false;
		}
		return true;
	}
}