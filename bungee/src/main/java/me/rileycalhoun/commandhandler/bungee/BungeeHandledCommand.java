package me.rileycalhoun.commandhandler.bungee;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.common.HandledCommand;

public interface BungeeHandledCommand extends HandledCommand {
    
    @NotNull List<TabSuggestionProvider> getTabCompletions();

}
