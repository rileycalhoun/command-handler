package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.core.CommandContext;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

public interface SpigotCommandContext extends CommandContext {

    @NotNull SpigotCommandSubject subject();

    @NotNull Command bukkitCommand();

}
