package handlers.chathandlers;

import java.util.Random;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.PlayerCondOverride;
import org.l2jmobius.gameserver.handler.IChatHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.CreatureSay;

/**
 * Clan chat handler
 */
public class ChatClan implements IChatHandler
{
    private static final ChatType[] CHAT_TYPES =
    {
        ChatType.CLAN,
    };
    
    private static final Random RANDOM = new Random();
    
    @Override
    public void handleChat(ChatType type, Player activeChar, String target, String text)
    {
        if (activeChar.getClan() == null)
        {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_IN_A_CLAN);
            return;
        }
        
        if (activeChar.isChatBanned() && Config.BAN_CHAT_CHANNELS.contains(type))
        {
            activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
            return;
        }
        if (Config.JAIL_DISABLE_CHAT && activeChar.isJailed() && !activeChar.canOverrideCond(PlayerCondOverride.CHAT_CONDITIONS))
        {
            activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
            return;
        }
        
        if (text.equalsIgnoreCase("/azar"))
        {
            int rollResult = RANDOM.nextInt(100) + 1;
            String message = activeChar.getName() + " obtuvo un " + rollResult + "!";
            activeChar.getClan().broadcastCSToOnlineMembers(new CreatureSay(activeChar, type, "[AZAR]", message), activeChar);
        }
        else
        {
            activeChar.getClan().broadcastCSToOnlineMembers(new CreatureSay(activeChar, type, activeChar.getName(), text), activeChar);
        }
    }
    
    @Override
    public ChatType[] getChatTypeList()
    {
        return CHAT_TYPES;
    }
}