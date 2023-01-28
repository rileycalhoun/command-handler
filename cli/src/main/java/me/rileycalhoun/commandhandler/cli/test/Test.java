package me.rileycalhoun.commandhandler.cli.test;

import me.rileycalhoun.commandhandler.cli.ConsoleCommandHandler;
import me.rileycalhoun.commandhandler.core.ArgumentStack;
import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.annotation.Command;
import me.rileycalhoun.commandhandler.core.annotation.SubCommand;

@Command(name = "command", aliases = { "advancedcommand" })
public class Test {

    public static void main(String[] args) {
        ConsoleCommandHandler handler = ConsoleCommandHandler.create(System.in, System.out);
        handler.registerCommands(new Test());
        handler.requestInput();
    }

    @SubCommand(name = "subcommand", aliases = "firstsubcommand")
    public void subcommand(CommandContext context, ArgumentStack args) {
        context.getSubject().reply("Subcommand!");
    }

}
