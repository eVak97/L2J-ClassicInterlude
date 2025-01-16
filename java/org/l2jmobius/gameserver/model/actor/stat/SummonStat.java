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
package org.l2jmobius.gameserver.model.actor.stat;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Summon;

public class SummonStat extends PlayableStat
{
	public SummonStat(Summon activeChar)
	{
		super(activeChar);
	}
	
	@Override
	public Summon getActiveChar()
	{
		return super.getActiveChar().asSummon();
	}
	
	@Override
	public double getRunSpeed()
	{
		final double val = super.getRunSpeed() + Config.RUN_SPD_BOOST;
		
		// Apply max run speed cap.
		if (val > Config.MAX_RUN_SPEED_SUMMON) // In retail maximum run speed is 350 for summons and 300 for players
		{
			return Config.MAX_RUN_SPEED_SUMMON;
		}
		return val;
	}
	
	@Override
	public double getWalkSpeed()
	{
		final double val = super.getWalkSpeed() + Config.RUN_SPD_BOOST;
		
		// Apply max run speed cap.
		if (val > Config.MAX_RUN_SPEED_SUMMON) // In retail maximum run speed is 350 for summons and 300 for players
		{
			return Config.MAX_RUN_SPEED_SUMMON;
		}
		return val;
	}
}
