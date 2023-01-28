package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.annotation.Command;
import me.rileycalhoun.commandhandler.core.base.BaseCommandData;
import me.rileycalhoun.commandhandler.core.base.Utils;
import me.rileycalhoun.commandhandler.spigot.TabSuggestionProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SpigotCommandData extends BaseCommandData implements me.rileycalhoun.commandhandler.spigot.SpigotCommandData {

    private static Constructor<PluginCommand> commandConstructor;
    private static CommandMap commandMap;

    static {
        try {
            commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            commandConstructor.setAccessible(true);
            Field cmdf = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            Utils.ensureAccessible(cmdf);
            commandMap = (CommandMap) cmdf.get(Bukkit.getServer());
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SpigotCommandData(Plugin plugin, Command info, boolean isRootCommand, Object instance, Method method, SpigotCommandData parent, SpigotHandler handler) {
        super(info, isRootCommand, instance, method, parent, handler);
        if(parent == null) registerCommandToBukkit(handler, plugin, this);
    }

    @Override
    public @NotNull List<TabSuggestionProvider> getTabCompletions() {
        return null;
    }

    private void registerCommandToBukkit(SpigotHandler handler, Plugin plugin, SpigotCommandData command) {
        try {
            PluginCommand cmd = commandConstructor.newInstance(command.getName(), plugin);
            commandMap.register(plugin.getName(), cmd);

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
