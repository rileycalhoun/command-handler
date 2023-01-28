package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.base.BaseCommandDispatcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SpigotDispatcher extends BaseCommandDispatcher implements CommandExecutor {

    public SpigotDispatcher(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        StringBuilder commandString = new StringBuilder(label);
        if(args.length > 1) commandString.append(" ").append(String.join(" ", args));
        else if (args.length == 1) commandString.append(" ").append(args[0]);
        SpigotSubject subject = new SpigotSubject(sender);
        SpigotContext context = new SpigotContext(subject, command);
        execute(context, commandString.toString().split(" "));
        return true;
    }

}
