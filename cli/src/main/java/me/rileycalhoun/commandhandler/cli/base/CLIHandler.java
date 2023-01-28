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
    final CLIResolver resolver;

    public CLIHandler(Scanner reader, PrintStream out)
    {
        super();
        this.reader = reader;
        this.out = out;
        this.console = new CLISubject(out);
        this.resolver = new CLIResolver(this);
    }

    @Override
    public void requestInput() {
        resolver.begin(reader);
    }

    @Override
    public ConsoleSubject getSubject() {
        return console;
    }

}
