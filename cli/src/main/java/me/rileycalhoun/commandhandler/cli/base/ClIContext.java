package me.rileycalhoun.commandhandler.cli.base;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandSubject;
import org.jetbrains.annotations.NotNull;

public class ClIContext implements CommandContext {

    private CommandSubject subject;

    public ClIContext(CommandSubject subject) {
        this.subject = subject;
    }

    @Override
    public @NotNull CommandSubject getSubject() {
        return subject;
    }

}
