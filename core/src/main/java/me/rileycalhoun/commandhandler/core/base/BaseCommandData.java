package me.rileycalhoun.commandhandler.core.base;

import me.rileycalhoun.commandhandler.core.CommandContext;
import me.rileycalhoun.commandhandler.core.CommandData;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.core.CommandResolver;
import me.rileycalhoun.commandhandler.core.annotation.AnnotationReader;
import me.rileycalhoun.commandhandler.core.annotation.Command;
import me.rileycalhoun.commandhandler.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.rileycalhoun.commandhandler.core.base.Utils.ensureAccessible;
import static me.rileycalhoun.commandhandler.core.base.Utils.getType;

@SuppressWarnings("ConstantConditions")
public class BaseCommandData implements CommandData {

    protected String name;
    private String description;
    private String[] aliases;

    private Method method;
    private List<CommandData> subCommands;
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

        if(ae == null) ae = getType(instance);
        if(ae instanceof Class) {
            this.subCommands = new ArrayList<>();
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
                    handler.commands.add(command);
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
        System.out.println(this.name);
        return name;
    }

    @Override
    public @Nullable String getDescription() {
        System.out.println(this.description);
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
    public @Nullable List<CommandData> getSubCommands() {
        return subCommands;
    }

    @Override
    public void execute(CommandContext context, String[] args) throws InvocationTargetException, IllegalAccessException {
        if(this.getMethod() != null) {
            System.out.println("test");
            if(this.getInstance() == null && this.getParent() != null)
                this.getMethod().invoke(this.getParent().getInstance(), context, args);
            else this.getMethod().invoke(this.getInstance(), context, args);
        } else {
            if(args.length == 0)
            {
                context.getSubject().reply(new BaseCommandHelpWriter().write(getSubCommands()));
                return;
            }

            String name = args[0];
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            CommandData data = new BaseCommandResolver(handler).find(name, getSubCommands());
            data.execute(context, newArgs);
        }
    }

    protected BaseCommandData newCommand(BaseCommandHandler handler, Object o, BaseCommandData parent, AnnotatedElement ae) {
        return new BaseCommandData(handler, o, parent, ae);
    }

    public void addSubCommand(@NotNull CommandData subCommand) {
        this.subCommands.add(subCommand);
    }

}
