package me.rileycalhoun.commandhandler.velocity.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import me.rileycalhoun.commandhandler.common.CommandContext;
import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandHandler;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;
import me.rileycalhoun.commandhandler.velocity.annotation.TabResolver;
import net.kyori.adventure.text.Component;
import me.rileycalhoun.commandhandler.velocity.PlayerSelector;
import me.rileycalhoun.commandhandler.velocity.TabSuggestionProvider;

import static me.rileycalhoun.commandhandler.common.core.Utils.*;

@Internal
public class VelocityHandler extends BaseCommandHandler implements VelocityCommandHandler {
    
    public static ProxyServer proxyServer;
    final ProxyServer server;
    final Object plugin;

    final Map<String, TabSuggestionProvider> tab = new HashMap<>();
    final Map<Class<?>, TabSuggestionProvider> tabByParam = new HashMap<>();

    public VelocityHandler(ProxyServer server, Object plugin) {
        super();
        this.server = server;
        this.plugin = plugin;
        if(proxyServer == null) this.proxyServer = server;
        registerTypeResolver(Player.class, (args, subject, parameter) -> {
            String name = args.pop();
            if (name.equalsIgnoreCase("me")) return ((VelocitySubject) subject).requirePlayer();
            Optional<Player> player = server.getPlayer(name);
            if (player.isEmpty()) throw new InvalidValueException(InvalidValueException.PLAYER, name);
            return player.get();
        });
        registerTypeResolver(PlayerSelector.class, SelectorParam.INSTANCE);
        registerStaticTabSuggestion("nothing", Collections.emptyList());
        registerTabSuggestion("selectors", (args, sender, command, velocityCommand) -> {
            List<String> completions = new ArrayList<>();
            completions.add("@a");
            completions.add("@r");
            completions.add("@p");
            for (Player player : server.getAllPlayers())
                completions.add(player.getUsername());
            return completions;
        });
        registerParameterTab(PlayerSelector.class, "selectors");
        registerTabSuggestion("players", (args, sender, command, bungeeCommand) -> VelocityPluginCommand.playerList(args.get(args.size() - 1)));
        registerParameterTab(Player.class, "players");
        registerContextResolver(plugin.getClass(), (ParameterResolver.ContextResolver) ParameterResolver.ContextResolver.of(plugin));
        registerContextResolver(VelocityCommandSubject.class, (args, sender, parameter) -> (VelocityCommandSubject) sender);
        registerContextResolver(CommandSource.class, (args, sender, parameter) -> ((VelocityCommandSubject) sender).getSender());
        registerContextResolver(ProxyServer.class, ParameterResolver.ContextResolver.of(server));
        registerResponseHandler(Component.class, (response, subject, command, context) -> ((VelocitySubject) subject).getSender().sendMessage(response));
        setExceptionHandler(DefaultExceptionHandler.INSTANCE);
    }

    public void addResolvers(Method method, Object resolver) {
        TabResolver ce = method.getAnnotation(TabResolver.class);
        if(ce != null) {
            try {
                ensureAccessible(method);
                MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                Class<?>[] pTypes = method.getParameterTypes();
                registerTabSuggestion(ce.value(), (args, sender, command, vcmd) -> {
                    List<Object> ia = new ArrayList<>();
                    for(Class<?> pType : pTypes) {
                        if(List.class.isAssignableFrom(pType))
                            ia.add(args);
                        else if (CommandSubject.class.isAssignableFrom(pType))
                            ia.add(sender);
                        else if (HandledCommand.class.isAssignableFrom(pType))
                            ia.add(command);
                        else if (Command.class.isAssignableFrom(pType))
                            ia.add(vcmd);
                        else if (CommandSource.class.isAssignableFrom(pType))
                            ia.add(sender.getSender());
                        else if (Player.class.isAssignableFrom(pType))
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
            VelocityCommand command = new VelocityCommand(this, instance, null, null);
            if (command.getName() != null) addCommand(command);
            setDependencies(instance);
        }

        return this;
    }

    @Override
    public VelocityCommandHandler registerTabSuggestion(@NotNull String providerID,
            @NotNull TabSuggestionProvider provider) {
        tab.put(n(providerID, "id"), n(provider, "provider"));
        return this;
    }

    @Override
    public VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID,
            @NotNull Collection<String> completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(providerID, "id"), (args,sender, command, bungeeCommand) -> values);
        return this;
    }

    @Override
    public VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID,
            @NotNull String... completions) {
        ImmutableList<String> values = ImmutableList.copyOf(completions);
        tab.put(n(providerID, "id"), (args,sender, command, bungeeCommand) -> values);
        return this;
    }

    @Override
    public VelocityCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID) {
        tabByParam.put(n(parameterType, "parameterType"), n(tab.get(providerID), "No such suggestion provider: " + providerID));
        return this;
    }

    @Override
    public VelocityCommandHandler registerParameterTab(@NotNull Class<?> parameterType,
            @NotNull TabSuggestionProvider provider) {
        tabByParam.put(n(parameterType, "parameterType"), n(provider, "provider"));
        return this;
    }

    public TabSuggestionProvider getTabs(@NotNull Class<?> type) {
        TabSuggestionProvider provider = tabByParam.getOrDefault(type, TabSuggestionProvider.EMPTY);
        if(provider == TabSuggestionProvider.EMPTY && type.isEnum()) {
            List<String> completions = VelocityPluginCommand.enums(type);
            tabByParam.put(type, provider = (args, sender, command, bcmd) -> completions);
        }
        
        return provider;
    }

    @Override
    protected void injectValues(Class<?> type, @NotNull CommandSubject sender, @NotNull List<String> args,
            @NotNull HandledCommand command, @NotNull CommandContext bcmd, List<Object> ia) {
        if(CommandSource.class.isAssignableFrom(type))
            ia.add(((VelocitySubject) sender).getSender());
    }

    @Override
    public @NotNull Object getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull ProxyServer getServer() {
        return this.server;
    }

    public static ProxyServer getProxyServer() {
        return proxyServer;
    }

}
