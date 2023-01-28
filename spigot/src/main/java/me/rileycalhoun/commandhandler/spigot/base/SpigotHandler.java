package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.base.BaseCommandHandler;
import me.rileycalhoun.commandhandler.spigot.SpigotCommandHandler;
import me.rileycalhoun.commandhandler.spigot.SpigotCommandSubject;
import me.rileycalhoun.commandhandler.spigot.TabSuggestionProvider;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpigotHandler extends BaseCommandHandler implements SpigotCommandHandler {

    final Map<String, TabSuggestionProvider> tab = new HashMap<>();
    final Map<Class<?>, TabSuggestionProvider> tabByParam = new HashMap<>();

    final Plugin plugin;

    public SpigotHandler(@NotNull Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void registerCommands(@NotNull Object instance) {
        commands.add(new SpigotCommandData(this, instance, null, null));
    }

    @Override
    public SpigotCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider) {
        return null;
    }

    @Override
    public SpigotCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions) {
        return null;
    }

    @Override
    public SpigotCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions) {
        return null;
    }

    @Override
    public SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider) {
        return null;
    }

    @Override
    public SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID) {
        return null;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }
}
