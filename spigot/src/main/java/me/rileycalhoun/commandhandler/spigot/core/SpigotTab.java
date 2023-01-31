package me.rileycalhoun.commandhandler.spigot.core;

import com.google.common.collect.ImmutableList;
import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.HandledCommand;
import me.rileycalhoun.commandhandler.common.core.LinkedArgumentStack;
import me.rileycalhoun.commandhandler.spigot.SpigotCommandSubject;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static me.rileycalhoun.commandhandler.common.core.BaseDispatcher.splitWithoutQuotes;

public class SpigotTab implements TabCompleter {

    private static final Map<Class<?>, List<String>> enumCache = new ConcurrentHashMap<>();
    private final SpigotHandler handler;

    public SpigotTab (SpigotHandler handler) {
        this.handler = handler;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command bukkitCommand, @NotNull String label, @NotNull String[] args) {
        ArgumentStack stack = new LinkedArgumentStack(splitWithoutQuotes(args), handler);
        if(stack.isEmpty()) return ImmutableList.of();
        Set<String> completions = new HashSet<>();
        SpigotSubject subject = new SpigotSubject(sender);
        HandledCommand parent = handler.getCommands().get(bukkitCommand.getName());
        if(parent == null) return ImmutableList.of();
        if(args.length == 1) {
            for(HandledCommand subcommand : parent.getSubcommands().values())
                completions.add(subcommand.getName());
        }

        HandledCommand found = findCommand(parent, stack);
        for(HandledCommand subcommand : found.getSubcommands().values())
            completions.add(subcommand.getName());

        if(!((SpigotHandledCommand) found).getTabCompletions().isEmpty()) {
            completions.addAll(((SpigotHandledCommand) found).resolveTab(stack, subject, bukkitCommand));
        }

        String last = stack.asImmutableList().get(stack.asImmutableList().size() - 1);
        if(last.startsWith(handler.getSwitchPrefix())) {
            for(CommandParameter parameter : found.getParameters()) {
                if(parameter.isSwitch())
                    if(!stack.asImmutableList().contains(handler.getSwitchPrefix() + parameter.getSwitchName()))
                        completions.add(handler.getSwitchPrefix() + parameter.getSwitchName());
            }
        }

        for(CommandParameter parameter : found.getParameters()) {
            try {
                if(parameter.isFlag()) {
                    int index = stack.asImmutableList().indexOf(handler.getFlagPrefix() + parameter.getFlagName());
                    if (index == -1) {
                        completions.add(handler.getFlagPrefix() + parameter.getFlagName());
                    } else if (index == stack.asImmutableList().size() - 2) {
                        completions.addAll(handler.getTabs(parameter.getType()).getSuggestions(stack.asImmutableList(), subject, found, bukkitCommand));
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        return completions.stream()
                .filter(c -> StringUtils.startsWithIgnoreCase(c, last))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .distinct()
                .collect(Collectors.toList());
    }

    private HandledCommand findCommand(@NotNull HandledCommand search, @NotNull ArgumentStack stack) {
        try {
            String nextSearch = stack.getFirst();
            HandledCommand found = search.getSubcommands().get(nextSearch);
            if(found != null) {
                stack.pop();
                return findCommand(found, stack);
            }

            return search;
        } catch (NoSuchElementException e) {
            return search;
        }
    }

    static List<String> playerList(@NotNull String lastWord, @NotNull SpigotCommandSubject sender) {
        Player senderPlayer = sender.asPlayer();
        List<String> players = new ArrayList<>();
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            String name = player.getName();
            if((senderPlayer == null || senderPlayer.canSee(player)) && StringUtil.startsWithIgnoreCase(name, lastWord)) {
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
