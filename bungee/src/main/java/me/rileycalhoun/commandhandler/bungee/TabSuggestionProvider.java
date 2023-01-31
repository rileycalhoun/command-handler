package me.rileycalhoun.commandhandler.bungee;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.common.HandledCommand;
import net.md_5.bungee.api.plugin.Command;

public interface TabSuggestionProvider {
    
    TabSuggestionProvider EMPTY = (args, sender, command, bungeeCommand) -> Collections.emptyList();

    @NotNull
    Collection<String> getSuggestions(@NotNull List<String> args,
                                      @NotNull BungeeCommandSubject subject,
                                      @NotNull HandledCommand command, 
                                      @NotNull Command bungeeCommand) throws Throwable;

    @Contract("null -> this; !null -> !null")
    default TabSuggestionProvider compose(@Nullable TabSuggestionProvider other) {
        if(other == null) return this;
        return (args, sender, command, bungeeCommand) -> {
            Set<String> completions = new HashSet<>(other.getSuggestions(args, sender, command, bungeeCommand));
            completions.addAll(getSuggestions(args, sender, command, bungeeCommand));
            return completions;
        };
    }

}
