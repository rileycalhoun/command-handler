package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.spigot.SpigotCommandContext;
import me.rileycalhoun.commandhandler.spigot.SpigotCommandSubject;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

public record SpigotContext(SpigotCommandSubject subject, Command bukkitCommand) implements SpigotCommandContext {

    @Override
    public @NotNull SpigotCommandSubject getSubject() {
        return subject;
    }

    @Override
    public @NotNull Command getBukkitCommand() {
        return bukkitCommand;
    }

}
