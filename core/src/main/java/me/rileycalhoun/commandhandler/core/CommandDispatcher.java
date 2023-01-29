package me.rileycalhoun.commandhandler.core;

public interface CommandDispatcher {

    void execute(CommandContext context, String[] argsArray);
    void invokeCommand(CommandData commandData, ArgumentStack args, CommandContext context) throws Throwable;

}
