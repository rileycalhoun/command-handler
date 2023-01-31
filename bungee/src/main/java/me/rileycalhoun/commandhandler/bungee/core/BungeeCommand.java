package me.rileycalhoun.commandhandler.bungee.core;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.bungee.BungeeCommandSubject;
import me.rileycalhoun.commandhandler.bungee.BungeeHandledCommand;
import me.rileycalhoun.commandhandler.bungee.TabSuggestionProvider;
import me.rileycalhoun.commandhandler.bungee.annotation.CommandPermission;
import me.rileycalhoun.commandhandler.bungee.annotation.TabCompletion;
import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.core.BaseHandledCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import static me.rileycalhoun.commandhandler.common.core.BaseDispatcher.SPLIT;
import static me.rileycalhoun.commandhandler.common.core.Utils.c;

public class BungeeCommand extends BaseHandledCommand implements BungeeHandledCommand {

    private static final Pattern BY_WALL = Pattern.compile("|");
    private final List<TabSuggestionProvider> tabCompletions = new ArrayList<>();

    public BungeeCommand (Plugin plugin,
                          BungeeHandler handler,
                          Object instance,
                          @Nullable BaseHandledCommand parent,
                          @NotNull AnnotatedElement ae) {
        super(handler, instance, parent, ae);
        setProperties2();
        if (parent == null && name != null)
            registerCommandToBungee(handler, plugin);
    }

    private void setProperties2() {
        TabCompletion tc = annotationReader.get(TabCompletion.class);
        List<String> completions = tc == null || tc.value().isEmpty() ? Collections.emptyList() : Arrays.asList(SPLIT.split(tc.value()));
        if (completions.isEmpty()) {
            for (CommandParameter parameter : getParameters()) {
                if (parameter.isSwitch() || parameter.isFlag()) continue;
                if (parameter.getResolver() instanceof ParameterResolver.ContextResolver ||
                        (parameter.getMethodIndex() == 0 && BungeeDispatcher.isSender(parameter.getType()))) continue;
                TabSuggestionProvider found = ((BungeeHandler) handler).getTabs(parameter.getType());
                tabCompletions.add(found);
            }
        } else {
            for (String id : completions) {
                if (id.startsWith("@")) {
                    tabCompletions.add(c(((BungeeHandler) handler).tab.get(id), "Invalid tab completion ID: " + id));
                } else {
                    List<String> values = Arrays.asList(BY_WALL.split(id));
                    tabCompletions.add((args, sender, command, bungeeCommand) -> values);
                }
            }
        }

        CommandPermission permission = annotationReader.get(CommandPermission.class);
        if (permission != null) {
            String node = permission.value();
            this.permission = sender -> ((BungeeCommandSubject) sender).getSender().hasPermission(node);
        }
    }

    private void registerCommandToBungee(BungeeHandler handler, Plugin plugin) {
        BungeeDispatcher dispatcher = new BungeeDispatcher(handler);
        BungeePluginCommand cmd = new BungeePluginCommand(dispatcher, handler, getName(), getAliases().toArray(new String[0]));
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, cmd);
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, AnnotatedElement ae) {
        return new BungeeCommand(((BungeeHandler)handler).getPlugin(), (BungeeHandler)handler, o, parent, ae);
    }

    @NotNull Collection<String> resolveTab(ArgumentStack args, BungeeCommandSubject sender, Command bungeeCommand) {
        if (isPrivate() || !permission.has(sender)) return Collections.emptyList();
        if (tabCompletions.isEmpty() || args.size() == 0) return Collections.emptyList();
        int index = args.size() - 1;
        try {
            return tabCompletions.get(index)
                    .getSuggestions(args.asImmutableList(), sender, this, bungeeCommand);
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }

    @Override public @NotNull List<TabSuggestionProvider> getTabCompletions() {
        return tabCompletions;
    }
    
}
