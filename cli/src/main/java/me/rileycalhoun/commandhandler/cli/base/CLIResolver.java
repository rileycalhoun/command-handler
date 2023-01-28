package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.cli.ConsoleCommandHandler;
import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.base.BaseCommandResolver;

import java.util.Scanner;

public class CLIResolver extends BaseCommandResolver {

    private final ConsoleCommandHandler commandHandler;

    public CLIResolver(ConsoleCommandHandler commandHandler) {
        super(commandHandler);
        this.commandHandler = commandHandler;
    }

    void begin(Scanner reader) {
        while(reader.hasNext()) {
            String input = reader.nextLine();
            super.execute(input, new ClIContext(this.commandHandler.getSubject()));
        }
    }

}
