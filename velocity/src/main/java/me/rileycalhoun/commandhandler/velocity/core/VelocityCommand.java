package me.rileycalhoun.commandhandler.velocity.core;

import java.lang.reflect.AnnotatedElement;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.plugin.Plugin;

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.core.BaseCommandHandler;
import me.rileycalhoun.commandhandler.common.core.BaseHandledCommand;
import me.rileycalhoun.commandhandler.velocity.VelocityHandledCommand;
import me.rileycalhoun.commandhandler.velocity.annotation.CommandPermission;
import me.rileycalhoun.commandhandler.velocity.annotation.TabCompletion;
import me.rileycalhoun.commandhandler.velocity.TabSuggestionProvider;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;

import static me.rileycalhoun.commandhandler.common.core.BaseDispatcher.SPLIT;
import static me.rileycalhoun.commandhandler.common.core.Utils.c;

public class VelocityCommand extends BaseHandledCommand implements VelocityHandledCommand {

    private static final Pattern BY_WALL = Pattern.compile("|");
    private final List<TabSuggestionProvider> tabCompletions = new ArrayList<>();

    public VelocityCommand (VelocityHandler handler,
                          Object instance,
                          @Nullable BaseHandledCommand parent,
                          @NotNull AnnotatedElement ae) {
        super(handler, instance, parent, ae);
        setProperties2();
        if (parent == null && name != null)
            registerCommandToBungee(handler);
    }

    private void setProperties2() {
        TabCompletion tc = annotationReader.get(TabCompletion.class);
        List<String> completions = tc == null || tc.value().isEmpty() ? Collections.emptyList() : Arrays.asList(SPLIT.split(tc.value()));
        if (completions.isEmpty()) {
            for (CommandParameter parameter : getParameters()) {
                if (parameter.isSwitch() || parameter.isFlag()) continue;
                if (parameter.getResolver() instanceof ParameterResolver.ContextResolver ||
                        (parameter.getMethodIndex() == 0 && VelocityDispatcher.isSender(parameter.getType()))) continue;
                TabSuggestionProvider found = ((VelocityHandler) handler).getTabs(parameter.getType());
                tabCompletions.add(found);
            }
        } else {
            for (String id : completions) {
                if (id.startsWith("@")) {
                    tabCompletions.add(c(((VelocityHandler) handler).tab.get(id), "Invalid tab completion ID: " + id));
                } else {
                    List<String> values = Arrays.asList(BY_WALL.split(id));
                    tabCompletions.add((args, sender, command, bungeeCommand) -> values);
                }
            }
        }

        CommandPermission permission = annotationReader.get(CommandPermission.class);
        if (permission != null) {
            String node = permission.value();
            this.permission = sender -> ((VelocitySubject) sender).getSender().hasPermission(node);
        }
    }

    private void registerCommandToBungee(VelocityHandler handler) {
        VelocityDispatcher dispatcher = new VelocityDispatcher(handler);
        VelocityPluginCommand cmd = new VelocityPluginCommand(dispatcher, handler, getName(), getAliases().toArray(new String[0]));
        handler.getServer().getCommandManager().register(name, cmd, getAliases().toArray(new String[0]));
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, AnnotatedElement ae) {
        return new VelocityCommand((VelocityHandler)handler, o, parent, ae);
    }

    @NotNull Collection<String> resolveTab(ArgumentStack args, VelocityCommandSubject sender, Command velocityCommand) {
        if (isPrivate() || !permission.has(sender)) return Collections.emptyList();
        if (tabCompletions.isEmpty() || args.size() == 0) return Collections.emptyList();
        int index = args.size() - 1;
        try {
            return tabCompletions.get(index)
                    .getSuggestions(args.asImmutableList(), sender, this, velocityCommand);
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }

    @Override public @NotNull List<TabSuggestionProvider> getTabCompletions() {
        return tabCompletions;
    }
    
}
