package me.rileycalhoun.commandhandler.velocity.core;

import java.util.UUID;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import me.rileycalhoun.commandhandler.common.core.Utils;
import me.rileycalhoun.commandhandler.velocity.SenderNotPlayerException;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;
import net.kyori.adventure.text.Component;

public class VelocitySubject implements VelocityCommandSubject {

    private final UUID CONSOLE_UUID = new UUID(0, 0);
    private final CommandSource sender;

    public VelocitySubject (CommandSource sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull String getName() {
        return isPlayer() ? requirePlayer().getUsername() : "CONSOLE";
    }

    @Override
    public @NotNull UUID getUUID() {
        return isPlayer() ? requirePlayer().getUniqueId() : CONSOLE_UUID;
    }

    @Override
    public void reply(@NotNull String message) {
        this.sender.sendMessage(Component.text(Utils.colorize(message)));
    }

    @Override
    public CommandSource getSender() {
        return sender;
    }

    @Override
    public boolean isPlayer() {
        return (sender instanceof Player);
    }

    @Override
    @Nullable
    public Player asPlayer() {
        return (Player) sender;
    }

    @Override
    public @NotNull Player requirePlayer() throws SenderNotPlayerException {
        if(!isPlayer()) throw new SenderNotPlayerException();
        return (Player)sender;
    }
    
}
