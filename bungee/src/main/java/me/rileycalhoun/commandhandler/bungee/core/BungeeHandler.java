package me.rileycalhoun.commandhandler.bungee.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.collect.ImmutableList;

import me.rileycalhoun.commandhandler.bungee.BungeeCommandHandler;
import me.rileycalhoun.commandhandler.bungee.BungeeCommandSubject;
import me.rileycalhoun.commandhandler.bungee.PlayerSelector;
import me.rileycalhoun.commandhandler.bungee.TabSuggestionProvider;
import me.rileycalhoun.commandhandler.bungee.annotation.TabResolver;
import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import static me.rileycalhoun.commandhandler.common.core.Utils.*;

@Internal
public class BungeeHandler extends BaseCommandHandler implements BungeeCommandHandler {
    
    final Plugin plugin;

    final Map<String, TabSuggestionProvider> tab = new HashMap<>();
    final Map<Class<?>, TabSuggestionProvider> tabByParam = new HashMap<>();

    public BungeeHandler(Plugin plugin) {
        super();
        this.plugin = plugin;
        registerDependency((Class) plugin.getClass(), plugin);
        registerDependency(Plugin.class, plugin);
        registerTypeResolver(ProxiedPlayer.class, (args, subject, parameter) -> {
            String name = args.pop();
            if (name.equalsIgnoreCase("me")) return ((BungeeSubject) subject).requirePlayer();
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
            if (player == null) throw new InvalidValueException(InvalidValueException.PLAYER, name);
            return player;
        });
        registerTypeResolver(PlayerSelector.class, SelectorParam.INSTANCE);
        registerStaticTabSuggestion("nothing", Collections.emptyList());
        registerTabSuggestion("selectors", (args, sender, command, bungeeCommand) -> {
            List<String> completions = new ArrayList<>();
            completions.add("@a");
            completions.add("@r");
            completions.add("@p");
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
                completions.add(player.getName());
            return completions;
        });
        registerParameterTab(PlayerSelector.class, "selectors");
        registerTabSuggestion("players", (args, sender, command, bungeeCommand) -> BungeePluginCommand.playerList(args.get(args.size() - 1)));
        registerParameterTab(ProxiedPlayer.class, "players");
        registerContextResolver(plugin.getClass(), (ParameterResolver.ContextResolver) ParameterResolver.ContextResolver.of(plugin));
        registerContextResolver(BungeeCommandSubject.class, (args, sender, parameter) -> (BungeeCommandSubject) sender);
        registerContextResolver(CommandSender.class, (args, sender, parameter) -> ((BungeeCommandSubject) sender).getSender());
        registerContextResolver(ProxyServer.class, ParameterResolver.ContextResolver.of(ProxyServer::getInstance));
        registerResponseHandler(BaseComponent.class, (response, subject, command, context) -> ((BungeeSubject) subject).getSender().sendMessage(response));
        setExceptionHandler(DefaultExceptionHandler.INSTANCE);
    }

    public void addResolvers(Method method, Object resolver) {
        TabResolver ce = method.getAnnotation(TabResolver.class);
        if(ce != null) {
            try {
                ensureAccessible(method);
                MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                Class<?>[] pTypes = method.getParameterTypes();
                registerTabSuggestion(ce.value(), (args, sender, command, bcmd) -> {
                    List<Object> ia = new ArrayList<>();
                    for(Class<?> pType : pTypes) {
                        if(List.class.isAssignableFrom(pType))
                            ia.add(args);
                        else if (CommandSubject.class.isAssignableFrom(pType))
                            ia.add(sender);
                        else if (HandledCommand.class.isAssignableFrom(pType))
                            ia.add(command);
                        else if (Command.class.isAssignableFrom(pType))
                            ia.add(bcmd);
                        else if (CommandSender.class.isAssignableFrom(pType))
                            ia.add(sender.getSender());
                        else if (ProxiedPlayer.class.isAssignableFrom(pType))
                            ia.add(sender.requirePlayer());
                    }

                    return (Collection<String>) handle.invokeWithArguments(ia);
                });
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public CommandHandler registerCommands(@NotNull Object... commands) {
        for (Object instance : commands) {
            BungeeCommand command = new BungeeCommand(plugin, this, instance, null, null);
            if (command.getName() != null) addCommand(command);
            setDependencies(instance);
        }

        return this;
    }

    @Override
    public BungeeCommandHandler registerTabSuggestion(@NotNull String providerID,
            @NotNull TabSuggestionProvider provider) {
        tab.put(n(providerID, "id"), n(provider, "provider"));
        return this;
    }

    @Override
    public BungeeCommandHandler registerStaticTabSuggestion(@NotNull String providerID,
            @NotNull Collection<String> completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(providerID, "id"), (args,sender, command, bungeeCommand) -> values);
        return this;
    }

    @Override
    public BungeeCommandHandler registerStaticTabSuggestion(@NotNull String providerID,
            @NotNull String... completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(providerID, "id"), (args,sender, command, bungeeCommand) -> values);
        return this;
    }

    @Override
    public BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID) {
        tabByParam.put(n(parameterType, "parameterType"), n(tab.get(providerID), "No such suggestion provider: " + providerID));
        return this;
    }

    @Override
    public BungeeCommandHandler registerParameterTab(@NotNull Class<?> parameterType,
            @NotNull TabSuggestionProvider provider) {
        tabByParam.put(n(parameterType, "parameterType"), n(provider, "provider"));
        return this;
    }

    public TabSuggestionProvider getTabs(@NotNull Class<?> type) {
        TabSuggestionProvider provider = tabByParam.getOrDefault(type, TabSuggestionProvider.EMPTY);
        if(provider == TabSuggestionProvider.EMPTY && type.isEnum()) {
            List<String> completions = BungeePluginCommand.enums(type);
            tabByParam.put(type, provider = (args, sender, command, bcmd) -> completions);
        }
        
        return provider;
    }

    @Override
    protected void injectValues(Class<?> type, @NotNull CommandSubject sender, @NotNull List<String> args,
            @NotNull HandledCommand command, @NotNull CommandContext bcmd, List<Object> ia) {
        if(CommandSender.class.isAssignableFrom(type))
            ia.add(((BungeeSubject) sender).getSender());
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

}
