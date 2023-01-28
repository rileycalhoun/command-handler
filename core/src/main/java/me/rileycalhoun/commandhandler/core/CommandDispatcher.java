package me.rileycalhoun.commandhandler.core;

import java.util.List;

public interface CommandDispatcher {

    void execute(CommandContext context, String[] argsArray);
    void invokeCommand(CommandData commandData, ArgumentStack args, CommandContext context);

}
