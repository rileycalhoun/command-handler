package me.rileycalhoun.commandhandler.bungee.core;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.bungee.SenderNotPlayerException;
import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.core.BaseDispatcher;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeDispatcher extends BaseDispatcher {

    public BungeeDispatcher(BaseCommandHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isPossibleSender(@NotNull Class<?> v) {
        return isSender(v);
    }

    public static boolean isSender(@NotNull Class<?> v) {
        return ProxiedPlayer.class.isAssignableFrom(v);
    }

    @Override
    protected Object handlePossibleSender(Class<?> type, @NotNull CommandContext commandContext) {
        BungeeContext context = (BungeeContext) commandContext;
        if (ProxiedPlayer.class.isAssignableFrom(type))
            if (!(context.getSubject().isPlayer())) {
                throw new SenderNotPlayerException();
            }

        return context.getSubject().getSender();
    }

}
