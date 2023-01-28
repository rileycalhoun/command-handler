package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.core.CommandSubject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SpigotCommandSubject extends CommandSubject {

    CommandSender sender();

    boolean isPlayer();

    @Nullable Player asPlayer();

    /**
     * TODO: Add Exception Handler
     * @return
     */
    @NotNull Player requirePlayer();

}
