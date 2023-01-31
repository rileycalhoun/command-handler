package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Predicate;

public interface CommandHelpWriter<T> {

    @Nullable T generate(@NotNull HandledCommand command,
                         @NotNull CommandSubject subject,
                         @NotNull @Unmodifiable List<String> args);

    default CommandHelpWriter<T> ignore(@NotNull Predicate<HandledCommand> predicate) {
        return (command, subject, args) -> {
            if (predicate.test(command)) return null;
            return generate(command, subject, args);
        };
    }

    default CommandHelpWriter<T> only(@NotNull Predicate<HandledCommand> predicate) {
        return ignore(predicate.negate());
    }

}
