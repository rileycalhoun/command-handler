package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;

public interface CommandContext {

    @NotNull CommandSubject getSubject();

}
