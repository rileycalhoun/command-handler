package me.rileycalhoun.commandhandler.core;

import org.jetbrains.annotations.NotNull;

public interface CommandContext {

    @NotNull CommandSubject getSubject();

}
