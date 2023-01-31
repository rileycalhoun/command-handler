package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.common.HandledCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SpigotHandledCommand extends HandledCommand {

    @NotNull List<TabSuggestionProvider> getTabCompletions();

}
