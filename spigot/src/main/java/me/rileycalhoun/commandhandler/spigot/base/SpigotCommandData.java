package me.rileycalhoun.commandhandler.spigot.base;

import me.rileycalhoun.commandhandler.core.base.BaseCommandData;
import me.rileycalhoun.commandhandler.core.base.BaseCommandHandler;
import me.rileycalhoun.commandhandler.core.base.Utils;
import me.rileycalhoun.commandhandler.spigot.TabSuggestionProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

public class SpigotCommandData extends BaseCommandData implements me.rileycalhoun.commandhandler.spigot.SpigotCommandData {

    private static final Constructor<PluginCommand> commandConstructor;
    private static final CommandMap commandMap;

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

    public SpigotCommandData(SpigotHandler handler, Object instance, @Nullable SpigotCommandData parent, @Nullable AnnotatedElement ae) {
        super(handler, instance, parent, ae);
        if(parent == null && name != null) registerCommandToBukkit(handler.plugin);
    }

    @Override
    public @NotNull List<TabSuggestionProvider> getTabCompletions() {
        return null;
    }

    @Override
    protected BaseCommandData newCommand(BaseCommandHandler handler, Object o, BaseCommandData parent, AnnotatedElement ae) {
        return new SpigotCommandData((SpigotHandler) handler, o, (SpigotCommandData) parent, ae);
    }

    private void registerCommandToBukkit(Plugin plugin) {
        try {
            PluginCommand cmd = commandConstructor.newInstance(name, plugin);
            commandMap.register(plugin.getName(), cmd);
            SpigotDispatcher resolver = new SpigotDispatcher(handler);
            cmd.setExecutor(resolver);
            cmd.setDescription(getDescription() == null ? "" : getDescription());
            cmd.setAliases(Arrays.asList(getAliases()));
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
