package me.rileycalhoun.commandhandler.spigot.core;

import annotation.TabResolver;
import com.google.common.collect.ImmutableList;
import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import me.rileycalhoun.commandhandler.spigot.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;

import static me.rileycalhoun.commandhandler.common.core.Utils.*;

public class SpigotHandler extends BaseCommandHandler implements SpigotCommandHandler {

    final Map<String, TabSuggestionProvider> tab = new HashMap<>();
    final Map<Class<?>, TabSuggestionProvider> tabByParam = new HashMap<>();

    final Plugin plugin;

    public SpigotHandler(@NotNull Plugin plugin) {
        super();
        this.plugin = plugin;
        registerDependency((Class) plugin.getClass(), plugin);
        registerTypeResolver(Player.class, (a, b, parameter) -> {
            String name = a.pop();
            if(name.equalsIgnoreCase("me")) return ((Player) b);

            Player player = Bukkit.getPlayer(name);
            if(player == null) throw new InvalidValueException(InvalidValueException.PLAYER, name);
            return player;
        });

        registerTypeResolver(OfflinePlayer.class, (a, b, parameter) -> {
            String name = a.pop();
            if(name.equalsIgnoreCase("me")) return ((Player) b);

            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if(!player.hasPlayedBefore()) throw new InvalidValueException(InvalidValueException.PLAYER, name);
            return player;
        });

        registerTypeResolver(World.class, (a, b, parameter) -> {
            String name = a.popForParameter(parameter);
            if (name.equalsIgnoreCase("me")) {
                if (b instanceof Player) return ((Player) b).getWorld();
                throw new SenderNotPlayerException();
            }
            World world = Bukkit.getWorld(name);
            if (world == null) throw new InvalidValueException(InvalidValueException.NUMBER, name);
            return world;
        });

        registerTypeResolver(PlayerSelector.class, SelectorParam.INSTANCE);
        registerStaticTabSuggestion("nothing", Collections.emptyList());
        registerTabSuggestion("selectors", (args, sender, command, bukkitCommand) -> {
            List<String> completions = new ArrayList<>();
            completions.add("@a");
            completions.add("@r");
            completions.add("@p");
            for (Player player : Bukkit.getOnlinePlayers())
                completions.add(player.getName());
            return completions;
        });
        registerParameterTab(PlayerSelector.class, "selectors");
        registerTabSuggestion("players", (args, sender, command, bukkitCommand) -> SpigotTab.playerList(args.get(args.size() - 1), sender)); // we handle that later on.
        registerParameterTab(Player.class, "players");
        registerParameterTab(OfflinePlayer.class, "players");
        registerTabSuggestion("worlds", (args, sender, command, cmd) -> {
            List<String> suggestions = new ArrayList<>();
            for (World world : Bukkit.getWorlds())
                suggestions.add(world.getName());
            return suggestions;
        });
        registerParameterTab(World.class, "worlds");
        registerContextResolver(CommandSender.class, (args, subject, parameter) -> ((SpigotSubject) subject).getSender());
        registerContextResolver(plugin.getClass(), (ParameterResolver.ContextResolver) ParameterResolver.ContextResolver.of(plugin));
        registerContextResolver(Server.class, ParameterResolver.ContextResolver.of(Bukkit::getServer));
        registerContextResolver(ConsoleCommandSender.class, ParameterResolver.ContextResolver.of(Bukkit.getConsoleSender()));
        registerContextResolver(SpigotCommandSubject.class, (args, sender, parameter) -> (SpigotCommandSubject) sender);
        setExceptionHandler(DefaultExceptionHandler.INSTANCE);
    }

    @Override
    protected void addResolvers(Method method, Object resolver) {
        TabResolver ce = method.getAnnotation(TabResolver.class);
        if (ce != null) {
            try {
                ensureAccessible(method);
                MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                Class<?>[] ptypes = method.getParameterTypes();
                registerTabSuggestion(ce.value(), (args, sender, command, bcmd) -> {
                    List<Object> ia = new ArrayList<>();
                    for (Class<?> ptype : ptypes) {
                        if (List.class.isAssignableFrom(ptype)) {
                            ia.add(args);
                        } else if (CommandSubject.class.isAssignableFrom(ptype)) {
                            ia.add(sender);
                        } else if (HandledCommand.class.isAssignableFrom(ptype)) {
                            ia.add(command);
                        } else if (Command.class.isAssignableFrom(ptype)) {
                            ia.add(bcmd);
                        } else if (CommandSender.class.isAssignableFrom(ptype)) {
                            ia.add(sender.getSender());
                        } else if (Player.class.isAssignableFrom(ptype)) {
                            ia.add(sender.asPlayer());
                        }
                    }
                    return (Collection<String>) handle.invokeWithArguments(ia);
                });
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public SpigotHandler registerCommands(@NotNull Object... instances) {
        for(Object instance : instances) {
            SpigotHandledCommand command = new SpigotHandledCommand(plugin, this, instance, null, null);
            if(command.getName() != null) addCommand(command);
            setDependencies(instance);
        }
        return this;
    }

    @Override
    public SpigotCommandHandler registerTabSuggestion(@NotNull String suggestionID, @NotNull TabSuggestionProvider provider) {
        tab.put(n(suggestionID, "id"), n(provider, "provider"));
        return this;
    }

    @Override
    public SpigotCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull Collection<String> completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);;
        tab.put(n(suggestionID, "id"), (args, sender, command, bukkitCommand) -> values);
        return this;
    }

    @Override
    public SpigotCommandHandler registerStaticTabSuggestion(@NotNull String suggestionID, @NotNull String... completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);;
        tab.put(n(suggestionID, "id"), (args, sender, command, bukkitCommand) -> values);
        return this;
    }

    @Override public SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider) {
        tabByParam.put(n(parameterType, "parameterType"), n(provider, "provider"));
        return this;
    }

    @Override public SpigotCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID) {
        tabByParam.put(n(parameterType, "parameterType"), c(tab.get(providerID), "No such suggestion provider: " + providerID));
        return this;
    }

    @Override
    protected void injectValues(Class<?> type, @NotNull CommandSubject sender, @NotNull List<String> args, @NotNull HandledCommand command, @NotNull CommandContext bcmd, List<Object> ia) {
        if(CommandSender.class.isAssignableFrom(type))
            ia.add(((SpigotSubject)sender).getSender());
    }

    public TabSuggestionProvider getTabs(@NotNull Class<?> type) {
        TabSuggestionProvider provider = tabByParam.getOrDefault(type, TabSuggestionProvider.EMPTY);
        if(provider == TabSuggestionProvider.EMPTY && type.isEnum()) {
            List<String> completions = SpigotTab.enums(type);
            tabByParam.put(type, provider = (args, sender, command, bukkitCommand) -> completions);
        }

        return provider;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

}
