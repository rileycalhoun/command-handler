package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.cli.ConsoleCommandHandler;
import me.rileycalhoun.commandhandler.cli.ConsoleSubject;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class CLIHandler extends BaseCommandHandler implements ConsoleCommandHandler {

    final Scanner reader;
    final OutputStream out;
    final CLISubject console;
    final CLIDispatcher dispatcher;

    public CLIHandler(Scanner reader, PrintStream out) {
        super();
        setExceptionHandler(DefaultExceptionHandler.INSTANCE);
        this.reader = reader;
        this.out = out;
        console = new CLISubject(out);
        dispatcher = new CLIDispatcher(this);

        registerContextResolver(ConsoleSubject.class, (args, subject, parameter) -> (ConsoleSubject) subject);
        registerDependency(ConsoleSubject.class, console);
        registerDependency(PrintStream.class, out);
        registerDependency(Scanner.class, reader);
    }

    @Override
    public void requestInput() {
        dispatcher.begin(reader);
    }

    @Override
    public ConsoleSubject getSubject() {
        return console;
    }

}
