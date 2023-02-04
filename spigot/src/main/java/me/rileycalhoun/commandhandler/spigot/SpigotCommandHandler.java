package me.rileycalhoun.commandhandler.spigot;

import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.spigot.core.SpigotHandler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static me.rileycalhoun.commandhandler.common.core.Utils.n;

public interface SpigotCommandHandler extends CommandHandler {

    SpigotCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider);

    SpigotCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions);

    SpigotCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions);

    SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider);

    SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID);

    @NotNull Plugin getPlugin();

    static SpigotCommandHandler create(@NotNull Plugin plugin) {
        return new SpigotHandler(n(plugin, "Plugin cannot be null!"));
    }


}
