package me.rileycalhoun.commandhandler.velocity;

import com.velocitypowered.api.command.SimpleCommand;

import static com.velocitypowered.api.command.SimpleCommand.Invocation;

public interface VelocityCommandDispatcher {

    void execute(SimpleCommand command, Invocation invocation);

}
