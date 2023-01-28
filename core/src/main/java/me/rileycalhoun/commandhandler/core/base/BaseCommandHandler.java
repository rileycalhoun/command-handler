package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandHelpWriter;
import me.rileycalhoun.commandhandler.core.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.HashMap;
import java.util.Map;

public class BaseCommandHandler implements CommandHandler {

    protected final Map<String, CommandData> commands = new HashMap<>();
    private ExceptionHandler exceptionHandler;
    private CommandHelpWriter writer;

    public BaseCommandHandler () {
        this.exceptionHandler = new DefaultExceptionHandler();
        this.writer = new BaseCommandHelpWriter();
    }

    @Override
    public void registerCommands(@NotNull Object instance) {
        BaseCommandData data = new BaseCommandData(this, instance, null, null);
        if(data.name == null) return;
        commands.put(data.name, data);
        for(String alias : data.getAliases())
            commands.put(alias, data);
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
    public @NotNull ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public void setExceptionHandler(@NotNull ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, CommandData> getCommands() {
        return commands;
    }

}
