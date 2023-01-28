package me.rileycalhoun.commandhandler.core;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CommandSubject {

    @NotNull String getName();
    @NotNull UUID getUUID();
    void reply(@NotNull String message);

}
