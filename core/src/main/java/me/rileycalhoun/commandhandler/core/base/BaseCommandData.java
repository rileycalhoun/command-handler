package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.annotation.AnnotationReader;
import me.rileycalhoun.commandhandler.core.annotation.Command;
import me.rileycalhoun.commandhandler.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

import static me.rileycalhoun.commandhandler.core.base.Utils.ensureAccessible;
import static me.rileycalhoun.commandhandler.core.base.Utils.getType;

@SuppressWarnings("ConstantConditions")
public class BaseCommandData implements CommandData {

    protected String name;
    private String description;
    private String[] aliases;

    private Method method;
    private final Map<String, CommandData> subCommands;
    private CommandData parent;

    protected final BaseCommandHandler handler;
    private final Object instance;

    protected final AnnotationReader annotationReader;

    public BaseCommandData (BaseCommandHandler handler,
                            Object instance,
                            @Nullable BaseCommandData parent,
                            @Nullable AnnotatedElement ae)
    {
        this.instance = instance;
        this.handler = handler;
        this.subCommands = new HashMap<>();

        if(ae == null) ae = getType(instance);
        if(ae instanceof Class) {
            AnnotationReader classAnnotations = this.annotationReader = new AnnotationReader(ae);
            if(classAnnotations.has(Command.class)) {
                Command info = classAnnotations.get(Command.class);
                this.name = info.name();
                this.description = info.description();
                this.aliases = info.aliases();
                this.method = null;
                this.parent = null;
            }

            for(Method method : ((Class<?>) ae).getDeclaredMethods()) {
                AnnotationReader reader = new AnnotationReader(method);
                if(reader.has(SubCommand.class)) {
                    CommandData subCommand = newCommand(handler, instance, this, method);
                    this.addSubCommand(subCommand);
                } else if (reader.has(Command.class)) {
                    CommandData command = newCommand(handler, instance, null, method);
                    if(command.getName() == null) return;
                    handler.commands.put(command.getName(), command);
                }
            }
        } else {
            AnnotationReader annotations = this.annotationReader = new AnnotationReader(ae);
            this.method = (Method) ae;
            ensureAccessible(this.method);
            if(annotations.has(SubCommand.class)) {
                if(parent != this) this.parent = parent;
                SubCommand info = annotations.get(SubCommand.class);
                this.name = info.name();
                this.description = info.description();
                this.aliases = info.aliases();
            } else if (annotations.has(Command.class)) {
                Command info = annotations.get(Command.class);
                this.parent = null;
                this.name = info.name();
                this.description = info.description();
                this.aliases = info.aliases();
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @Nullable String[] getAliases() {
        return aliases;
    }

    @Override
    public boolean isRootCommand() {
        return this.parent == null;
    }

    @Override
    public @Nullable CommandData getParent() {
        return parent;
    }

    @Override
    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @Nullable Object getInstance() {
        return instance;
    }

    @Override
    public @NotNull Map<String, CommandData> getSubCommands() {
        return subCommands;
    }

    protected BaseCommandData newCommand(BaseCommandHandler handler, Object o, BaseCommandData parent, AnnotatedElement ae) {
        return new BaseCommandData(handler, o, parent, ae);
    }

    public void addSubCommand(@NotNull CommandData subCommand) {
        if(subCommand.getName() == null) return;
        this.subCommands.put(subCommand.getName(), subCommand);
        for(String alias : subCommand.getAliases())
            this.subCommands.put(alias, subCommand);
    }

}
