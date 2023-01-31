package me.rileycalhoun.commandhandler.bungee.core;

import java.util.List;

import javax.annotation.Nullable;

import java.lang.Throwable;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.bungee.SenderNotPlayerException;
import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.exception.CooldownException;
import me.rileycalhoun.commandhandler.common.exception.ExceptionHandler;
import me.rileycalhoun.commandhandler.common.exception.InvalidCommandException;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import me.rileycalhoun.commandhandler.common.exception.MissingParameterException;
import me.rileycalhoun.commandhandler.common.exception.MissingPermissionException;
import me.rileycalhoun.commandhandler.common.exception.ResolverFailedException;
import me.rileycalhoun.commandhandler.common.exception.SimpleCommandException;

public class DefaultExceptionHandler implements ExceptionHandler {

    private static final String VOWELS = "aeiou";
    public static final DefaultExceptionHandler INSTANCE = new DefaultExceptionHandler();

    @Override public String toString() {
        return "DefaultExceptionHandler";
    }

    @Override public void handleException(@NotNull CommandSubject sender,
                                          @NotNull CommandHandler commandHandler,
                                          @Nullable HandledCommand command,
                                          @NotNull List<String> arguments,
                                          @NotNull CommandContext context,
                                          @NotNull Throwable e,
                                          boolean async) {
        if (e instanceof InvalidValueException) {
            sender.reply("&cInvalid " + ((InvalidValueException) e).getValueType().getId() + ": &e" + e.getMessage());
        } else if (e instanceof SenderNotPlayerException) {
            sender.reply("&cYou must be a player to use this command!");
        } else if (e instanceof InvalidCommandException) {
            sender.reply("&cInvalid command: &e" + ((InvalidCommandException) e).getInput());
        } else if (e instanceof MissingParameterException mpe) {
            String article = VOWELS.indexOf(Character.toLowerCase(mpe.getParameter().getName().charAt(0))) != -1 ? "an" : "a";
            sender.reply("&cYou must specify " + article + " " + mpe.getParameter().getName() + "!");
        } else if (e instanceof MissingPermissionException) {
            sender.reply("&cYou do not have permission to execute this command!");
        } else if (e instanceof ResolverFailedException rfe) {
            sender.reply("&cCannot resolve " + rfe.getParameter().getName() + " from value &e" + rfe.getInput());
        } else if (e instanceof SimpleCommandException) {
            sender.reply(e.getMessage());
        } else if (e instanceof CooldownException ce) {
            sender.reply("&cYou must wait &e" + ce.getTimeFancy() + " &cbefore using this command again.");
        } else {
            sender.reply("&cAn error occured while executing this command. Check console for details.");
            e.printStackTrace();
        }
    }

}
