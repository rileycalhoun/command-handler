package me.rileycalhoun.commandhandler.velocity;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import me.rileycalhoun.commandhandler.common.CommandSubject;

public interface VelocityCommandSubject extends CommandSubject {
    
    CommandSource getSender();
    boolean isPlayer();
    @Nullable Player asPlayer();
    @NotNull Player requirePlayer() throws SenderNotPlayerException;

}
