package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.base.BaseCommandResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SpigotResolver extends BaseCommandResolver implements CommandExecutor {

    public SpigotResolver(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        StringBuilder commandString = new StringBuilder(label);
        if(args.length > 1) commandString.append(" ").append(String.join(" ", args));
        else if (args.length == 1) commandString.append(" ").append(args[0]);
        SpigotSubject subject = new SpigotSubject(sender);
        SpigotContext context = new SpigotContext(subject, command);
        execute(commandString.toString(), context);
        System.out.println(commandString);
        return true;
    }

}
