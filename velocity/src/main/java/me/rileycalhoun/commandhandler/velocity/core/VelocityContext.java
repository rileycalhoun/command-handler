package me.rileycalhoun.commandhandler.velocity.core;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.Command;

import me.rileycalhoun.commandhandler.velocity.VelocityCommandContext;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;

public class VelocityContext implements VelocityCommandContext {

    private final VelocitySubject subject;
    private final Command command;

    public VelocityContext(VelocitySubject subject, Command command) {
        this.subject = subject;
        this.command = command;
    }

    @Override
    public @NotNull VelocityCommandSubject getSubject() {
        return subject;
    }

    @Override
    public @NotNull Command getBungeeCommand() {
        return command;
    }

}
