package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.spigot.SpigotCommandSubject;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpigotSubject implements SpigotCommandSubject {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    final CommandSender sender;

    public SpigotSubject(CommandSender sender) {
        this.sender = sender;
    }

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
        sender.sendMessage(message);
    }

    @Override
    public CommandSender getSender() {
        return this.sender;
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
        return null;
    }

}
