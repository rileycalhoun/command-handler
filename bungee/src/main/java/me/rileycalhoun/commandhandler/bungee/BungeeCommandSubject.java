package me.rileycalhoun.commandhandler.bungee;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.common.CommandSubject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface BungeeCommandSubject extends CommandSubject {
    
    CommandSender getSender();
    boolean isPlayer();
    @Nullable ProxiedPlayer asPlayer();
    @NotNull ProxiedPlayer requirePlayer() throws SenderNotPlayerException;

}
