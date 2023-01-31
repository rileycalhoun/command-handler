package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.common.CommandContext;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

public interface SpigotCommandContext extends CommandContext {

    @NotNull SpigotCommandSubject getSubject();

    @NotNull Command getBukkitCommand();

}
