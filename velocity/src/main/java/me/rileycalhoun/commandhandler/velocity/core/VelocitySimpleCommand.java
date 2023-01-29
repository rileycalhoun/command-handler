package me.rileycalhoun.commandhandler.velocity.core;

import com.velocitypowered.api.command.SimpleCommand;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandHandler;

public class VelocitySimpleCommand implements SimpleCommand {

    private final VelocityCommandHandler commandHandler;

    public VelocitySimpleCommand(VelocityCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(Invocation invocation) {
        VelocityDispatcher dispatcher = (VelocityDispatcher) commandHandler.getCommandDispatcher();
        dispatcher.execute(this, invocation);
    }

}
