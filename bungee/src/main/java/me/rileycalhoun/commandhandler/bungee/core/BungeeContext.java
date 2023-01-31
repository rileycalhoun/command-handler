package me.rileycalhoun.commandhandler.bungee.core;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.bungee.BungeeCommandContext;
import me.rileycalhoun.commandhandler.bungee.BungeeCommandSubject;
import net.md_5.bungee.api.plugin.Command;

public class BungeeContext implements BungeeCommandContext {

    private final BungeeSubject subject;
    private final Command command;

    public BungeeContext(BungeeSubject subject, Command command) {
        this.subject = subject;
        this.command = command;
    }

    @Override
    public @NotNull BungeeCommandSubject getSubject() {
        return subject;
    }

    @Override
    public @NotNull Command getBungeeCommand() {
        return command;
    }

}
