package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.core.BaseDispatcher;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Scanner;

public class CLIDispatcher extends BaseDispatcher {

    private final CLIHandler commandHandler;

    public CLIDispatcher(CLIHandler commandHandler) {
        super(commandHandler);
        this.commandHandler = commandHandler;
    }

    void begin(Scanner reader) {
        while(reader.hasNext()) {
            String input = reader.nextLine();
            execute(((CLIHandler) handler).console, () -> ((CLIHandler) handler).console, SPLIT.split(input));
        }
    }

    @Override
    protected boolean isPossibleSender(@NotNull Class<?> v) {
        return PrintStream.class.isAssignableFrom(v);
    }

    @Override
    protected Object handlePossibleSender(Class<?> type, @NotNull CommandContext context) {
        CLISubject subject = (CLISubject) context.getSubject();
        if(PrintStream.class.isAssignableFrom(type))
            return subject.out;
        return null;
    }

}
