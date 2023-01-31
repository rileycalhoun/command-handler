package me.rileycalhoun.commandhandler.bungee.core;

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

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.core.LinkedArgumentStack;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import static me.rileycalhoun.commandhandler.common.core.BaseDispatcher.splitWithoutQuotes;

public class BungeePluginCommand extends Command implements TabExecutor {

    private static final Map<Class<?>, List<String>> enumCache = new ConcurrentHashMap<>();

    private final BungeeDispatcher dispatcher;
    private final BungeeHandler handler;

    public BungeePluginCommand(BungeeDispatcher dispatcher, BungeeHandler handler, String name, String... aliases) {
        super(name, null, aliases);
        this.dispatcher = dispatcher;
        this.handler = handler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        list.add(getName());
        Collections.addAll(list, args);
        BungeeSubject subject = new BungeeSubject(sender);
        dispatcher.execute(subject, new BungeeContext(subject, this), list.toArray(new String[0]));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ArgumentStack stack = new LinkedArgumentStack(splitWithoutQuotes(args), handler);
        if (stack.isEmpty()) return ImmutableList.of();
        Set<String> completions = new HashSet<>();
        BungeeSubject subject = new BungeeSubject(sender);
        HandledCommand parent = handler.getCommands().get(getName());
        if (parent == null) return ImmutableList.of();
        if (args.length == 1) {
            for (HandledCommand subcommand : parent.getSubcommands().values())
                completions.add(subcommand.getName());
        }

        HandledCommand found = findCommand(parent, stack);
        for (HandledCommand subcommand : found.getSubcommands().values())
            completions.add(subcommand.getName());
        if (!((BungeeCommand) found).getTabCompletions().isEmpty()) {
            completions.addAll(((BungeeCommand) found).resolveTab(stack, subject, this));
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
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            String name = player.getName();
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
