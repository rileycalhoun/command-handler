package me.rileycalhoun.commandhandler.spigot.core;

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.core.BaseHandledCommand;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.core.Utils;
import me.rileycalhoun.commandhandler.spigot.TabSuggestionProvider;
import me.rileycalhoun.commandhandler.spigot.annotation.CommandPermission;
import me.rileycalhoun.commandhandler.spigot.annotation.TabCompletion;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

import static me.rileycalhoun.commandhandler.common.core.BaseDispatcher.SPLIT;
import static me.rileycalhoun.commandhandler.common.core.Utils.c;

public class SpigotHandledCommand extends BaseHandledCommand implements me.rileycalhoun.commandhandler.spigot.SpigotHandledCommand {

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

    private static final Pattern BY_WALL = Pattern.compile("|");
    private final List<TabSuggestionProvider> tabCompletions = new ArrayList<>();
    private final Plugin plugin;

    public SpigotHandledCommand(Plugin plugin, SpigotHandler handler, Object instance, @Nullable SpigotHandledCommand parent, @Nullable AnnotatedElement ae) {
        super(handler, instance, parent, ae);
        this.plugin = plugin;
        setProperties2();
        if(parent == null && name != null) registerCommandToBukkit(plugin);
    }

    private void setProperties2() {
        TabCompletion tc = annotationReader.get(TabCompletion.class);
        List<String> completions = tc == null || tc.value().isEmpty() ? Collections.emptyList() : Arrays.asList(SPLIT.split(tc.value()));
        if(completions.isEmpty()) {
            for(CommandParameter parameter : getParameters()) {
                if(parameter.isSwitch() || parameter.isFlag()) continue;
                if(parameter.getResolver() instanceof ParameterResolver.ContextResolver
                        || (parameter.getMethodIndex() == 0
                        && SpigotDispatcher.isSender(parameter.getType()))) continue;
                TabSuggestionProvider found = ((SpigotHandler) handler).getTabs(parameter.getType());
                tabCompletions.add(found);
            }
        } else {
            for(String id : completions) {
                if(id.startsWith("@")) {
                    tabCompletions.add(c(((SpigotHandler) handler).tab.get(id), "Invalid tab completion ID: " + id));
                } else {
                    List<String> values = Arrays.asList(BY_WALL.split(id));
                    tabCompletions.add((args, sender, command, bukkitCommand) -> values);
                }
            }
        }

        CommandPermission permission = annotationReader.get(CommandPermission.class);
        if(permission != null) {
            Permission p = new Permission(permission.value(), permission.access());
            this.permission = sender -> ((SpigotSubject) sender).getSender().hasPermission(p);
        } else if (this.getParent() != null) {
            this.permission = this.getParent().getPermission();
        }
    }

    @Override
    public @NotNull List<TabSuggestionProvider> getTabCompletions() {
        return tabCompletions;
    }

    @Override
    protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, AnnotatedElement ae) {
        return new SpigotHandledCommand(this.plugin, (SpigotHandler) handler, o, (SpigotHandledCommand) parent, ae);
    }

    private void registerCommandToBukkit(Plugin plugin) {
        try {
            PluginCommand cmd = commandConstructor.newInstance(name, plugin);
            commandMap.register(plugin.getName(), cmd);
            cmd.setExecutor(new SpigotDispatcher(handler));
            cmd.setDescription(getDescription() == null ? "" : getDescription());
            cmd.setAliases(getAliases());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull  Collection<String> resolveTab(ArgumentStack args, SpigotSubject sender, Command bukkitCommand) {
        if(isPrivate() || !permission.has(sender)) return Collections.emptyList();
        if(tabCompletions.isEmpty() || args.size() == 0) return Collections.emptyList();
        int index = args.size() - 1;
        try {
            return tabCompletions.get(index)
                    .getSuggestions(args.asImmutableList(), sender, this, bukkitCommand);
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }
}
