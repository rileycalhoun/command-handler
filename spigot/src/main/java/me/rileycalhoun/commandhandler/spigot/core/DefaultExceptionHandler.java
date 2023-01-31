package me.rileycalhoun.commandhandler.spigot.core;

import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.exception.ExceptionHandler;
import me.rileycalhoun.commandhandler.common.exception.InvalidCommandException;
import me.rileycalhoun.commandhandler.common.exception.SimpleCommandException;
import me.rileycalhoun.commandhandler.spigot.SenderNotPlayerException;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultExceptionHandler implements ExceptionHandler {

    public static DefaultExceptionHandler INSTANCE = new DefaultExceptionHandler();

    @Override
    public void handleException(CommandSubject subject,
                                CommandHandler handler,
                                HandledCommand command,
                                List<String> asImmutableList,
                                CommandContext context,
                                Throwable sanitizeStackTrace,
                                boolean b) {

    }
}
