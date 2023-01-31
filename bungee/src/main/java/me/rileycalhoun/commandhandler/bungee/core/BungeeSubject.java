package me.rileycalhoun.commandhandler.bungee.core;

import java.util.UUID;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.bungee.BungeeCommandSubject;
import me.rileycalhoun.commandhandler.bungee.SenderNotPlayerException;
import me.rileycalhoun.commandhandler.common.core.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeSubject implements BungeeCommandSubject {

    private final UUID CONSOLE_UUID = new UUID(0, 0);
    private final CommandSender sender;

    public BungeeSubject (CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull String getName() {
        return this.sender.getName();
    }

    @Override
    public @NotNull UUID getUUID() {
        return isPlayer() ? requirePlayer().getUniqueId() : CONSOLE_UUID;
    }

    @Override
    public void reply(@NotNull String message) {
        this.sender.sendMessage(new TextComponent(Utils.colorize(message)));
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean isPlayer() {
        return (sender instanceof ProxiedPlayer);
    }

    @Override
    @Nullable
    public ProxiedPlayer asPlayer() {
        return (ProxiedPlayer) sender;
    }

    @Override
    public @NotNull ProxiedPlayer requirePlayer() throws SenderNotPlayerException {
        if(!isPlayer()) throw new SenderNotPlayerException();
        return (ProxiedPlayer)sender;
    }
    
}
