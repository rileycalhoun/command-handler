package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.cli.ConsoleCommandHandler;
import me.rileycalhoun.commandhandler.core.base.BaseCommandDispatcher;

import java.util.Scanner;

public class CLIDispatcher extends BaseCommandDispatcher {

    private final ConsoleCommandHandler commandHandler;

    public CLIDispatcher(ConsoleCommandHandler commandHandler) {
        super(commandHandler);
        this.commandHandler = commandHandler;
    }

    void begin(Scanner reader) {
        while(reader.hasNext()) {
            String input = reader.nextLine();
            super.execute(new ClIContext(commandHandler.getSubject()), input.split(" "));
        }
    }

}
