package me.rileycalhoun.commandhandler.velocity.core;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;
import me.rileycalhoun.commandhandler.velocity.exception.SenderNotPlayerException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VelocitySubject implements VelocityCommandSubject {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private final CommandSource source;

    public VelocitySubject(CommandSource source) {
        this.source = source;
    }

    @Override
    public @NotNull String getName() {
        return (source instanceof Player p) ? p.getUsername() : "CONSOLE";
    }

    @Override
    public @NotNull UUID getUUID() {
        return (source instanceof Player p) ? p.getUniqueId() : CONSOLE_UUID;
    }

    @Override
    public void reply(@NotNull String message) {
        this.reply(Component.text(message));
    }

    public void reply(TextComponent component) {
        source.sendMessage(component);
    }

    @Override
    public CommandSource sender() {
        return source;
    }

    @Override
    public boolean isPlayer() {
        return (source instanceof Player);
    }

    @Override
    public @Nullable Player asPlayer() {
        return ((Player) source);
    }

    @Override
    public @NotNull Player requirePlayer() {
        if(!(source instanceof Player))
            throw new SenderNotPlayerException();
        return (Player) source;
    }
}
