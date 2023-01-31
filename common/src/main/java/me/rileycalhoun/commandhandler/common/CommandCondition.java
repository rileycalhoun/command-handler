package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandCondition {

    void test(@NotNull CommandSubject subject,
              @NotNull List<String> args,
              @NotNull HandledCommand command,
              @NotNull CommandContext context) throws Throwable;

}
