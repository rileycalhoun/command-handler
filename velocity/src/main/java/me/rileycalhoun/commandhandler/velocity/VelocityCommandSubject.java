package me.rileycalhoun.commandhandler.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.rileycalhoun.commandhandler.core.CommandSubject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VelocityCommandSubject extends CommandSubject {

    CommandSource sender();

    boolean isPlayer();

    @Nullable Player asPlayer();

    /**
     * TODO: Add Exception Handler
     * @return
     */
    @NotNull Player requirePlayer();

}
