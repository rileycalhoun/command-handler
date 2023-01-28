package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.core.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SpigotCommandData extends CommandData {

    @NotNull List<TabSuggestionProvider> getTabCompletions();

}
