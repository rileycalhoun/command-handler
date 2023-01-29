package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandSubject;
import me.rileycalhoun.commandhandler.core.exception.ExceptionHandler;
import me.rileycalhoun.commandhandler.core.exception.InvalidCommandException;
import me.rileycalhoun.commandhandler.core.exception.SimpleCommandException;
import me.rileycalhoun.commandhandler.spigot.exception.SenderNotPlayerException;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotExceptionHandler implements ExceptionHandler {

    @Override
    public void handleException(@NotNull CommandSubject sender,
                                @NotNull CommandHandler commandHandler,
                                @Nullable CommandData command,
                                @NotNull CommandContext context,
                                @NotNull Throwable e) {
        if(e instanceof InvalidCommandException ce) {
            sender.reply(ChatColor.RED + "Invalid command: " + ChatColor.GRAY + ce.getInput());
        } else if (e instanceof SimpleCommandException) {
            sender.reply(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
        } else if (e instanceof SenderNotPlayerException) {
            sender.reply(ChatColor.RED + "You must be a player to execute this command!");
        } else {
            sender.reply("An error occured while executing this command! Please contact your administrator for more details.");
            e.printStackTrace();
        }
    }

}
