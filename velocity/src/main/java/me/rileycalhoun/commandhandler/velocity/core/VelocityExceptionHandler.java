package me.rileycalhoun.commandhandler.velocity.core;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandSubject;
import me.rileycalhoun.commandhandler.core.exception.ExceptionHandler;
import me.rileycalhoun.commandhandler.core.exception.InvalidCommandException;
import me.rileycalhoun.commandhandler.core.exception.MissingSubCommandException;
import me.rileycalhoun.commandhandler.core.exception.SimpleCommandException;
import me.rileycalhoun.commandhandler.velocity.exception.SenderNotPlayerException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VelocityExceptionHandler implements ExceptionHandler {

    @Override
    public void handleException(@NotNull CommandSubject sender,
                                @NotNull CommandHandler commandHandler,
                                @Nullable CommandData command,
                                @NotNull CommandContext context,
                                @NotNull Throwable e) {
        if(e instanceof InvalidCommandException)
        {
            sender.reply(LegacyComponentSerializer.legacyAmpersand().serialize(Component.text("Invalid command:")
                    .color(NamedTextColor.RED).append(Component.text(e.getMessage()).color(NamedTextColor.GRAY))));
        } else if (e instanceof SimpleCommandException) {
            sender.reply(e.getMessage());
        } else if (e instanceof MissingSubCommandException) {
            if(command == null)  {
                sender.reply(LegacyComponentSerializer.legacyAmpersand().serialize(Component.text("Invalid command!").color(NamedTextColor.RED)));
                return;
            }

            String message = commandHandler.getHelpWriter().write(command.getSubCommands());
            sender.reply(message);
        } else if (e instanceof SenderNotPlayerException) {
            sender.reply("You must be a player to use this command!");
        } else {
            sender.reply("An error occurred while executing this command. Check the console for more details.");
            e.printStackTrace();
        }
    }

}
