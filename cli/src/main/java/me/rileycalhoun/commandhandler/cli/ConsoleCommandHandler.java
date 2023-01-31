package me.rileycalhoun.commandhandler.cli;

import me.rileycalhoun.commandhandler.cli.base.CLIHandler;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public interface ConsoleCommandHandler extends CommandHandler {

    void requestInput();

    ConsoleSubject getSubject();

    static @NotNull ConsoleCommandHandler create() {
        return create(System.in, System.out);
    }

    static @NotNull ConsoleCommandHandler create(@NotNull InputStream in) {
        return create(in, System.out);
    }

    static @NotNull ConsoleCommandHandler create(@NotNull InputStream in, @NotNull OutputStream out) {
        Scanner scanner = new Scanner(in);
        return new CLIHandler(scanner, new PrintStream(out));
    }

}
