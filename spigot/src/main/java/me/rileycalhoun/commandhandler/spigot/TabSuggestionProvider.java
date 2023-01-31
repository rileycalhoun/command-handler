package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.common.HandledCommand;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface TabSuggestionProvider {

    TabSuggestionProvider EMPTY = (args, sender, command, bukkitCommand) -> Collections.emptyList();

    @NotNull
    Collection<String> getSuggestions(@NotNull List<String> args,
                                               @NotNull SpigotCommandSubject sender,
                                               @NotNull HandledCommand command,
                                               @NotNull Command bukkitCommand) throws Throwable;

    @Contract("null -> this; !null -> !null")
    default TabSuggestionProvider compose(@Nullable TabSuggestionProvider other) {
        if(other == null) return this;
        return (args, sender, command, bukkitCommand) -> {
            Set<String> completions = new HashSet<>(other.getSuggestions(args, sender, command, bukkitCommand));
            completions.addAll(getSuggestions(args, sender, command, bukkitCommand));
            return completions;
        };
    }

}
