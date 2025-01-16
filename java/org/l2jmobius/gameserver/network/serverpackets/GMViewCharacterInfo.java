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

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.data.xml.ExperienceData;
import org.l2jmobius.gameserver.enums.AttributeType;
import org.l2jmobius.gameserver.model.VariationInstance;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class GMViewCharacterInfo extends ServerPacket
{
	private final Player _player;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	
	public GMViewCharacterInfo(Player player)
	{
		_player = player;
		_moveMultiplier = player.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = player.isFlying() ? _runSpd : 0;
		_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GM_VIEW_CHARACTER_INFO.writeId(this, buffer);
		buffer.writeInt(_player.getX());
		buffer.writeInt(_player.getY());
		buffer.writeInt(_player.getZ());
		buffer.writeInt(_player.getHeading());
		buffer.writeInt(_player.getObjectId());
		buffer.writeString(_player.getName());
		buffer.writeInt(_player.getRace().ordinal());
		final PlayerAppearance appearance = _player.getAppearance();
		buffer.writeInt(appearance.isFemale());
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeInt(_player.getLevel());
		buffer.writeLong(_player.getExp());
		buffer.writeDouble((float) (_player.getExp() - ExperienceData.getInstance().getExpForLevel(_player.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_player.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_player.getLevel()))); // High Five exp %
		buffer.writeInt(_player.getSTR());
		buffer.writeInt(_player.getDEX());
		buffer.writeInt(_player.getCON());
		buffer.writeInt(_player.getINT());
		buffer.writeInt(_player.getWIT());
		buffer.writeInt(_player.getMEN());
		buffer.writeInt(0); // LUC
		buffer.writeInt(0); // CHA
		buffer.writeInt(_player.getMaxHp());
		buffer.writeInt((int) _player.getCurrentHp());
		buffer.writeInt(_player.getMaxMp());
		buffer.writeInt((int) _player.getCurrentMp());
		buffer.writeLong(_player.getSp());
		buffer.writeInt(_player.getCurrentLoad());
		buffer.writeInt(_player.getMaxLoad());
		buffer.writeInt(_player.getPkKills());
		for (int slot : getPaperdollOrder())
		{
			buffer.writeInt(_player.getInventory().getPaperdollObjectId(slot));
		}
		for (int slot : getPaperdollOrder())
		{
			buffer.writeInt(_player.getInventory().getPaperdollItemDisplayId(slot));
		}
		for (int slot = 0; slot < 11; slot++)
		{
			final VariationInstance augment = _player.getInventory().getPaperdollAugmentation(slot);
			buffer.writeInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
			buffer.writeInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
		}
		buffer.writeByte(_player.getInventory().getTalismanSlots()); // CT2.3
		buffer.writeByte(_player.getInventory().canEquipCloak()); // CT2.3
		buffer.writeInt(0);
		buffer.writeShort(0);
		buffer.writeInt(_player.getPAtk());
		buffer.writeInt(_player.getPAtkSpd());
		buffer.writeInt(_player.getPDef());
		buffer.writeInt(_player.getEvasionRate());
		buffer.writeInt(_player.getAccuracy());
		buffer.writeInt(_player.getCriticalHit());
		buffer.writeInt(_player.getMAtk());
		buffer.writeInt(_player.getMAtkSpd());
		buffer.writeInt(_player.getPAtkSpd());
		buffer.writeInt(_player.getMDef());
		buffer.writeInt(_player.getMagicEvasionRate());
		buffer.writeInt(_player.getMagicAccuracy());
		buffer.writeInt(_player.getMCriticalHit());
		buffer.writeInt(_player.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		buffer.writeInt(_player.getReputation());
		buffer.writeInt(_runSpd);
		buffer.writeInt(_walkSpd);
		buffer.writeInt(_swimRunSpd);
		buffer.writeInt(_swimWalkSpd);
		buffer.writeInt(_flyRunSpd);
		buffer.writeInt(_flyWalkSpd);
		buffer.writeInt(_flyRunSpd);
		buffer.writeInt(_flyWalkSpd);
		buffer.writeDouble(_moveMultiplier);
		buffer.writeDouble(_player.getAttackSpeedMultiplier()); // 2.9); //
		buffer.writeDouble(_player.getCollisionRadius()); // scale
		buffer.writeDouble(_player.getCollisionHeight()); // y offset ??!? fem dwarf 4033
		buffer.writeInt(appearance.getHairStyle());
		buffer.writeInt(appearance.getHairColor());
		buffer.writeInt(appearance.getFace());
		buffer.writeInt(_player.isGM()); // builder level
		buffer.writeString(_player.getTitle());
		buffer.writeInt(_player.getClanId()); // pledge id
		buffer.writeInt(_player.getClanCrestId()); // pledge crest id
		buffer.writeInt(_player.getAllyId()); // ally id
		buffer.writeByte(_player.getMountType().ordinal()); // mount type
		buffer.writeByte(_player.getPrivateStoreType().getId());
		buffer.writeByte(_player.hasDwarvenCraft());
		buffer.writeInt(_player.getPkKills());
		buffer.writeInt(_player.getPvpKills());
		buffer.writeShort(_player.getRecomLeft());
		buffer.writeShort(_player.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeInt(0); // special effects? circles around player...
		buffer.writeInt(_player.getMaxCp());
		buffer.writeInt((int) _player.getCurrentCp());
		buffer.writeByte(_player.isRunning()); // changes the Speed display on Status Window
		buffer.writeByte(321);
		buffer.writeInt(_player.getPledgeClass()); // changes the text above CP on Status Window
		buffer.writeByte(_player.isNoble());
		buffer.writeByte(_player.isHero());
		buffer.writeInt(appearance.getNameColor());
		buffer.writeInt(appearance.getTitleColor());
		final AttributeType attackAttribute = _player.getAttackElement();
		buffer.writeShort(attackAttribute.getClientId());
		buffer.writeShort(_player.getAttackElementValue(attackAttribute));
		for (AttributeType type : AttributeType.ATTRIBUTE_TYPES)
		{
			buffer.writeShort(_player.getDefenseElementValue(type));
		}
		buffer.writeInt(_player.getFame());
		buffer.writeInt(_player.getVitalityPoints());
		buffer.writeInt(0);
		buffer.writeInt(0);
	}
}
