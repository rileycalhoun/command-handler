package me.rileycalhoun.commandhandler.spigot.core;

import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.core.BaseDispatcher;
import me.rileycalhoun.commandhandler.spigot.SenderNotPlayerException;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpigotDispatcher extends BaseDispatcher implements CommandExecutor {

    public SpigotDispatcher(BaseCommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        args = ArrayUtils.add(args, 0, command.getName());
        SpigotSubject subject = new SpigotSubject(sender);
        SpigotContext context = new SpigotContext(subject, command);
        execute(subject, context, args);
        return true;
    }

    @Override
    protected boolean isPossibleSender(@NotNull Class<?> v) {
        return isSender(v);
    }

    public static boolean isSender(@NotNull Class<?> v) {
        return Player.class.isAssignableFrom(v) || CommandSender.class.isAssignableFrom(v);
    }

    @Override
    protected Object handlePossibleSender(Class<?> type, @NotNull CommandContext commandContext) {
        SpigotContext context = (SpigotContext) commandContext;
        if(Player.class.isAssignableFrom(type)) {
            if(!(context.getSubject().isPlayer())) {
                throw new SenderNotPlayerException();
            }
        }

        return context.getSubject().asPlayer();
    }

}
