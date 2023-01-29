package me.rileycalhoun.commandhandler.velocity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VelocityCommandData {

    @NotNull List<TabSuggestionProvider> getTabCompletions();

}
