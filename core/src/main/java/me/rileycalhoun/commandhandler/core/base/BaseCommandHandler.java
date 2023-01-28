package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandHelpWriter;
import me.rileycalhoun.commandhandler.core.CommandResolver;
import me.rileycalhoun.commandhandler.core.annotation.Command;
import me.rileycalhoun.commandhandler.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BaseCommandHandler implements CommandHandler {

    protected final List<CommandData> commands = new ArrayList<>();
    private CommandHelpWriter writer;

    public BaseCommandHandler () {
        this.writer = new BaseCommandHelpWriter();
    }

    @Override
    public void registerCommands(@NotNull Object instance) {
        commands.add(new BaseCommandData(this, instance, null, null));
    }

    @Override
    public @NotNull CommandHelpWriter getHelpWriter() {
        return writer;
    }

    @Override
    public void setHelpWriter(@NotNull CommandHelpWriter writer) {
        this.writer = writer;
    }

    @Override
    public @NotNull @UnmodifiableView List<CommandData> getCommands() {
        return commands;
    }

}
