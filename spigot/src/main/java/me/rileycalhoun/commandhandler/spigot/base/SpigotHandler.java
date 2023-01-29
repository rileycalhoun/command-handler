package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.core.CommandHelpWriter;
import me.rileycalhoun.commandhandler.core.base.BaseCommandHandler;
import me.rileycalhoun.commandhandler.core.base.BaseCommandHelpWriter;
import me.rileycalhoun.commandhandler.spigot.SpigotCommandHandler;
import me.rileycalhoun.commandhandler.spigot.TabSuggestionProvider;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpigotHandler extends BaseCommandHandler implements SpigotCommandHandler {

    final Map<String, TabSuggestionProvider> tab = new HashMap<>();
    final Map<Class<?>, TabSuggestionProvider> tabByParam = new HashMap<>();

    final Plugin plugin;
    final SpigotDispatcher dispatcher;

    public SpigotHandler(@NotNull Plugin plugin) {
        super();
        setExceptionHandler(new SpigotExceptionHandler());
        setHelpWriter(new BaseCommandHelpWriter());
        this.plugin = plugin;
        this.dispatcher = new SpigotDispatcher(this);
        this.registerCommands(plugin);
    }

    @Override
    public void registerCommands(@NotNull Object instance) {
        SpigotCommandData data = new SpigotCommandData(this, instance, null, null);
        if(data.getName() == null) return;
        commands.put(data.getName(), data);
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
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

}
