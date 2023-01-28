package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.spigot.base.SpigotHandler;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface SpigotCommandHandler extends CommandHandler {

    SpigotCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider);

    SpigotCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions);

    SpigotCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions);

    SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider);

    SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID);

    @NotNull Plugin getPlugin();

    static SpigotCommandHandler create(@NotNull Plugin plugin) {
        return new SpigotHandler(plugin);
    }


}
