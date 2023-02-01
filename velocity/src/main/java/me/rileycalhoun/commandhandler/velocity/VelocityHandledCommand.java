package me.rileycalhoun.commandhandler.velocity;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.common.HandledCommand;

public interface VelocityHandledCommand extends HandledCommand {
    
    @NotNull List<TabSuggestionProvider> getTabCompletions();

}
