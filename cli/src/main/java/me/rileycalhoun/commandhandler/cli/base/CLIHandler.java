package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.cli.ConsoleCommandHandler;
import me.rileycalhoun.commandhandler.cli.ConsoleSubject;
import me.rileycalhoun.commandhandler.core.base.BaseCommandHandler;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class CLIHandler extends BaseCommandHandler implements ConsoleCommandHandler {

    final Scanner reader;
    final OutputStream out;
    final CLISubject console;

    public CLIHandler(Scanner reader, PrintStream out)
    {
        super();
        setCommandDispatcher(new CLIDispatcher(this));
        this.reader = reader;
        this.out = out;
        this.console = new CLISubject(out);
    }

    @Override
    public void requestInput() {
        ((CLIDispatcher)getCommandDispatcher())
                .begin(reader);
    }

    @Override
    public ConsoleSubject getSubject() {
        return console;
    }

}
