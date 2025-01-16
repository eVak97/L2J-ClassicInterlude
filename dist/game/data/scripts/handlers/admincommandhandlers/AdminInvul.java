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
package handlers.admincommandhandlers;

import org.l2jmobius.gameserver.handler.IAdminCommandHandler;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.util.BuilderUtil;

/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminInvul implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_invul",
		"admin_setinvul",
		"admin_undying",
		"admin_setundying"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_invul"))
		{
			handleInvul(activeChar);
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.equals("admin_undying"))
		{
			handleUndying(activeChar, activeChar);
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		
		else if (command.equals("admin_setinvul"))
		{
			final WorldObject target = activeChar.getTarget();
			if ((target != null) && target.isPlayer())
			{
				handleInvul(target.asPlayer());
			}
		}
		else if (command.equals("admin_setundying"))
		{
			final WorldObject target = activeChar.getTarget();
			if (target.isCreature())
			{
				handleUndying(activeChar, target.asCreature());
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleInvul(Player activeChar)
	{
		String text;
		if (activeChar.isInvul())
		{
			activeChar.setInvul(false);
			text = activeChar.getName() + " is now mortal.";
		}
		else
		{
			activeChar.setInvul(true);
			text = activeChar.getName() + " is now invulnerable.";
		}
		BuilderUtil.sendSysMessage(activeChar, text);
	}
	
	private void handleUndying(Player activeChar, Creature target)
	{
		String text;
		if (target.isUndying())
		{
			target.setUndying(false);
			text = target.getName() + " is now mortal.";
		}
		else
		{
			target.setUndying(true);
			text = target.getName() + " is now undying.";
		}
		BuilderUtil.sendSysMessage(activeChar, text);
	}
}
