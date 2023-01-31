package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;

public interface CommandPermission {

    boolean has(@NotNull CommandSubject subject);

}
