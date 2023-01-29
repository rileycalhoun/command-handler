package me.rileycalhoun.commandhandler.velocity.core;

import static com.velocitypowered.api.command.SimpleCommand.Invocation;

import com.velocitypowered.api.command.SimpleCommand;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.base.BaseCommandDispatcher;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandContext;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandDispatcher;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;
import static me.rileycalhoun.commandhandler.core.base.Utils.addElementsInFront;

import java.util.List;

public class VelocityDispatcher extends BaseCommandDispatcher implements VelocityCommandDispatcher {

    public VelocityDispatcher(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public void execute(SimpleCommand command, Invocation invocation) {
        VelocityCommandSubject subject = new VelocitySubject(invocation.source());
        VelocityCommandContext commandContext = new VelocityContext(subject, command);

        this.execute(commandContext, addElementsInFront(invocation.arguments(), invocation.alias()));
    }

}
