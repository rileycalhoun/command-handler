package me.rileycalhoun.commandhandler.velocity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.velocitypowered.api.command.Command;

import me.rileycalhoun.commandhandler.common.HandledCommand;

public interface TabSuggestionProvider {
    
    TabSuggestionProvider EMPTY = (args, sender, command, velocityCommand) -> Collections.emptyList();

    @NotNull
    Collection<String> getSuggestions(@NotNull List<String> args,
                                      @NotNull VelocityCommandSubject subject,
                                      @NotNull HandledCommand command, 
                                      @NotNull Command velocityCommand) throws Throwable;

    @Contract("null -> this; !null -> !null")
    default TabSuggestionProvider compose(@Nullable TabSuggestionProvider other) {
        if(other == null) return this;
        return (args, sender, command, velocityCommand) -> {
            Set<String> completions = new HashSet<>(other.getSuggestions(args, sender, command, velocityCommand));
            completions.addAll(getSuggestions(args, sender, command, velocityCommand));
            return completions;
        };
    }

}
