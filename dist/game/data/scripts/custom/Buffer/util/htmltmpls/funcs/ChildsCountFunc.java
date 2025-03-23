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
package custom.Buffer.util.htmltmpls.funcs;

import java.util.Map;

import org.l2jmobius.gameserver.model.actor.Player;

import custom.Buffer.util.htmltmpls.HTMLTemplateFunc;
import custom.Buffer.util.htmltmpls.HTMLTemplatePlaceholder;
import custom.Buffer.util.htmltmpls.HTMLTemplateUtils;

/**
 * @author HorridoJoho
 */
public final class ChildsCountFunc extends HTMLTemplateFunc
{
	public static final ChildsCountFunc INSTANCE = new ChildsCountFunc();

	protected ChildsCountFunc()
	{
		super("CHILDSCOUNT", "ENDCHILDSCOUNT", false);
	}

	@Override
	public Map<String, HTMLTemplatePlaceholder> handle(StringBuilder content, Player player, Map<String, HTMLTemplatePlaceholder> placeholders, HTMLTemplateFunc[] funcs)
	{
		HTMLTemplatePlaceholder placeholder = HTMLTemplateUtils.getPlaceholder(content.toString(), placeholders);
		content.setLength(0);
		if (placeholder != null)
		{
			content.append(String.valueOf(placeholder.getChildsSize()));
		}
		return null;
	}
	
}
