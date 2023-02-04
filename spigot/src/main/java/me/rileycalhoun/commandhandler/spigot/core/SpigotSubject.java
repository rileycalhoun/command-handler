package me.rileycalhoun.commandhandler.spigot.core;

import me.rileycalhoun.commandhandler.common.core.Utils;
import me.rileycalhoun.commandhandler.spigot.SpigotCommandSubject;
import me.rileycalhoun.commandhandler.spigot.SenderNotPlayerException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record SpigotSubject(CommandSender sender) implements SpigotCommandSubject {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    @Override
    public @NotNull String getName() {
        return sender.getName();
    }

    @Override
    public @NotNull UUID getUUID() {
        return sender instanceof ConsoleCommandSender ? CONSOLE_UUID : ((Player) sender).getUniqueId();
    }

    @Override
    public void reply(@NotNull String message) {
        sender.sendMessage(Utils.colorize(message));
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public @Nullable Player asPlayer() {
        return (Player) sender;
    }

    @Override
    public @NotNull Player requirePlayer() {
        if (!(sender instanceof Player))
            throw new SenderNotPlayerException();
        return (Player) sender;
    }

}
