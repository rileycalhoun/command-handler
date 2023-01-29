package me.rileycalhoun.commandhandler.velocity.core;

import com.velocitypowered.api.command.Command;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandContext;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;
import org.jetbrains.annotations.NotNull;

public record VelocityContext(VelocityCommandSubject subject, Command command) implements VelocityCommandContext {

    @Override
    public @NotNull VelocityCommandSubject getSubject() {
        return subject;
    }

    @Override
    public @NotNull Command getVelocityCommand() {
        return command;
    }

}
