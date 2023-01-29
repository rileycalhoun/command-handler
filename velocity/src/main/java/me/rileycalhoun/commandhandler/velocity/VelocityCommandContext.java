package me.rileycalhoun.commandhandler.velocity;

import com.velocitypowered.api.command.Command;
import me.rileycalhoun.commandhandler.core.CommandContext;
import org.jetbrains.annotations.NotNull;

public interface VelocityCommandContext extends CommandContext {

    @NotNull VelocityCommandSubject getSubject();

    @NotNull Command getVelocityCommand();

}
