package me.rileycalhoun.commandhandler.bungee;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.bungee.core.BungeeHandler;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import net.md_5.bungee.api.plugin.Plugin;

public interface BungeeCommandHandler extends CommandHandler {
    
    BungeeCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider);
    BungeeCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions);
    BungeeCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions);
    BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider);
    BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID);
    @NotNull Plugin getPlugin();

    static BungeeCommandHandler create(@NotNull Plugin plugin) {
        return new BungeeHandler(plugin);
    }

}
