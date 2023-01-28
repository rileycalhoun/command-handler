package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandSubject;
import me.rileycalhoun.commandhandler.core.exception.ExceptionHandler;
import me.rileycalhoun.commandhandler.core.exception.InvalidCommandException;
import me.rileycalhoun.commandhandler.core.exception.MissingSubCommandException;
import me.rileycalhoun.commandhandler.core.exception.SimpleCommandException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultExceptionHandler implements ExceptionHandler {

    @Override
    public void handleException(@NotNull CommandSubject sender,
                                @NotNull CommandHandler commandHandler,
                                @Nullable CommandData command,
                                @NotNull CommandContext context,
                                @NotNull Throwable e) {
        if(e instanceof InvalidCommandException)
        {
            sender.reply("Invalid command: " + e.getMessage());
        } else if (e instanceof SimpleCommandException) {
            sender.reply(e.getMessage());
        } else if (e instanceof MissingSubCommandException) {
            if(command == null)  {
                sender.reply("Invalid subcommand!");
                return;
            }

            String message = commandHandler.getHelpWriter().write(command.getSubCommands());
            sender.reply(message);
        } else {
            sender.reply("An error ocurred while executing this command. Check the console for more details.");
            e.printStackTrace();
        }
    }

}
