package me.rileycalhoun.commandhandler.velocity.core;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.proxy.Player;

import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.core.BaseDispatcher;
import me.rileycalhoun.commandhandler.velocity.SenderNotPlayerException;

public class VelocityDispatcher extends BaseDispatcher {

    public VelocityDispatcher(BaseCommandHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isPossibleSender(@NotNull Class<?> v) {
        return isSender(v);
    }

    public static boolean isSender(@NotNull Class<?> v) {
        return Player.class.isAssignableFrom(v);
    }

    @Override
    protected Object handlePossibleSender(Class<?> type, @NotNull CommandContext commandContext) {
        VelocityContext context = (VelocityContext) commandContext;
        if (Player.class.isAssignableFrom(type))
            if (!(context.getSubject().isPlayer())) {
                throw new SenderNotPlayerException();
            }

        return context.getSubject().getSender();
    }

}
