package me.rileycalhoun.commandhandler.velocity;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.Command;

import me.rileycalhoun.commandhandler.common.CommandContext;

public interface VelocityCommandContext extends CommandContext {
 
    @NotNull VelocityCommandSubject getSubject();
    @NotNull Command getBungeeCommand();

}
