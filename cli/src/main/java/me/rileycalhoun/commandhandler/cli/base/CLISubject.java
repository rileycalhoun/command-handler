package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.cli.ConsoleSubject;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.UUID;

public class CLISubject implements ConsoleSubject {

    private static final String NAME = "Console";
    private static final UUID UUID = new UUID(0, 0);

    private final PrintStream out;

    public CLISubject(PrintStream out)
    {
        this.out = out;
    }

    @Override
    public @NotNull String getName() {
        return NAME;
    }

    @Override
    public java.util.@NotNull UUID getUUID() {
        return UUID;
    }

    @Override
    public void reply(@NotNull String message) {
        out.println(message);
    }

}
