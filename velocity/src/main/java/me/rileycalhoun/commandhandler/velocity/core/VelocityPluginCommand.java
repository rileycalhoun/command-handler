package me.rileycalhoun.commandhandler.velocity.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.core.LinkedArgumentStack;

import static me.rileycalhoun.commandhandler.common.core.BaseDispatcher.splitWithoutQuotes;

public class VelocityPluginCommand implements SimpleCommand {

    private static final Map<Class<?>, List<String>> enumCache = new ConcurrentHashMap<>();

    private final VelocityDispatcher dispatcher;
    private final VelocityHandler handler;

    public VelocityPluginCommand(VelocityDispatcher dispatcher, VelocityHandler handler, String name, String... aliases) {
        this.dispatcher = dispatcher;
        this.handler = handler;
    }

    @Override
    public void execute(Invocation invocation) {
        List<String> list = new ArrayList<>();
        list.add(invocation.alias());
        Collections.addAll(list, invocation.arguments());
        VelocitySubject subject = new VelocitySubject(invocation.source());
        dispatcher.execute(subject, new VelocityContext(subject, this), list.toArray(new String[0]));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        ArgumentStack stack = new LinkedArgumentStack(splitWithoutQuotes(args), handler);
        if (stack.isEmpty()) return ImmutableList.of();
        Set<String> completions = new HashSet<>();
        VelocitySubject subject = new VelocitySubject(invocation.source());
        HandledCommand parent = handler.getCommands().get(invocation.alias());
        if (parent == null) return ImmutableList.of();
        if (args.length == 1) {
            for (HandledCommand subcommand : parent.getSubcommands().values())
                completions.add(subcommand.getName());
        }

        HandledCommand found = findCommand(parent, stack);
        for (HandledCommand subcommand : found.getSubcommands().values())
            completions.add(subcommand.getName());
        if (!((VelocityCommand) found).getTabCompletions().isEmpty()) {
            completions.addAll(((VelocityCommand) found).resolveTab(stack, subject, this));
        }
        String last = stack.asImmutableList().get(stack.asImmutableList().size() - 1);
        if (last.startsWith(handler.getSwitchPrefix())) {
            for (CommandParameter parameter : found.getParameters())
                if (parameter.isSwitch())
                    if (!stack.asImmutableList().contains(handler.getSwitchPrefix() + parameter.getSwitchName()))
                        completions.add(handler.getSwitchPrefix() + parameter.getSwitchName());
        }

        for (CommandParameter parameter : found.getParameters()) {
            try {
                if (parameter.isFlag()) {
                    int index = stack.asImmutableList().indexOf(handler.getFlagPrefix() + parameter.getFlagName());
                    if (index == -1) {
                        completions.add(handler.getFlagPrefix() + parameter.getFlagName() + " ");
                    } else if (index == stack.asImmutableList().size() - 2) {
                        completions.addAll(handler.getTabs(parameter.getType())
                                .getSuggestions(stack.asImmutableList(), subject, found, this));
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        
        return completions.stream()
                .filter(c -> c.toLowerCase().startsWith(last.toLowerCase()))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .distinct()
                .collect(Collectors.toList());
    }

    

    private HandledCommand findCommand(@NotNull HandledCommand search, @NotNull ArgumentStack stack) {
        try {
            String nextSearch = stack.getFirst();
            HandledCommand found = search.getSubcommands().get(nextSearch);
            if (found != null) {
                stack.pop();
                return findCommand(found, stack);
            }
            return search;
        } catch (NoSuchElementException e) {
            return search;
        }
    }

    // identical behavior to what Bungee does. we just don't need null values
    static List<String> playerList(@NotNull String lastWord) {
        List<String> players = new ArrayList<>();
        for (Player player : VelocityHandler.getProxyServer().getAllPlayers()) {
            String name = player.getUsername();
            if (name.toLowerCase().startsWith(lastWord.toLowerCase())) {
                players.add(name);
            }
        }
        return players;
    }

    static List<String> enums(@NotNull Class<?> type) {
        return enumCache.computeIfAbsent(type, cl -> Arrays.stream(cl.getEnumConstants())
                .map(e -> ((Enum) e).name().toLowerCase())
                .collect(Collectors.toList()));
    }
    
}
