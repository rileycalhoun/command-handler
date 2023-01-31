package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.*;
import me.rileycalhoun.commandhandler.common.annotation.*;
import me.rileycalhoun.commandhandler.common.annotation.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static me.rileycalhoun.commandhandler.common.core.Utils.*;

@SuppressWarnings("ConstantConditions")
public class BaseHandledCommand implements HandledCommand {

    private static final Executor ASYNC = Executors.newSingleThreadExecutor();
    private static final Executor SYNC = Runnable::run;

    protected String name;
    private List<String> aliases = new ArrayList<>();
    ResponseHandler responseHandler = ResponseHandler.VOID;
    private String description;
    private String usage;
    private List<CommandCondition> conditions;
    private boolean async;
    private Executor executor;
    private boolean isPrivate;
    private @Nullable MethodHandle method;
    @Nullable MethodHandle fallback;
    List<Parameter> fallbackParameters = new ArrayList<>();
    private @Nullable BaseHandledCommand parent;
    protected CommandPermission permission = sender -> true; // platform dependen
    protected final BaseCommandHandler handler;
    protected final AnnotationReader annotationReader;
    final Map<String, HandledCommand> subcommands = new HashMap<>();
    private final List<CommandParameter> params = new ArrayList<>();

    public BaseHandledCommand(BaseCommandHandler handler,
                              Object instance,
                              @Nullable BaseHandledCommand parent,
                              @Nullable AnnotatedElement ae)
    {
        this.handler = handler;
        handler.setDependencies(instance);
        if(ae == null) ae = getType(instance);
        if(ae instanceof Class) {
            AnnotationReader classAnnotations = this.annotationReader = new AnnotationReader(ae);
            if(classAnnotations.has(Command.class)) {
                Command annotation = classAnnotations.get(Command.class);
                method = null;
                this.parent = null;
                name = annotation.name();
                aliases = Utils.immutable(annotation.aliases());
            } else if (classAnnotations.has(Subcommand.class)) {
                if(parent == null) throw new IllegalArgumentException("@Subcommand " + ae + " has no parent!");
                Subcommand subcommand = classAnnotations.get(Subcommand.class);
                name = subcommand.name();
                aliases = Utils.immutable(subcommand.aliases());
            }

            setProperties0();
            for(Method method : ((Class<?>) ae).getDeclaredMethods()) {
                AnnotationReader reader = new AnnotationReader(method);
                if(reader.has(Subcommand.class))
                    registerSubcommand(newCommand(handler, instance, this, method));
                else if (reader.has(Command.class))
                    handler.addCommand(newCommand(handler, instance, null, method));
                else if(reader.has(CatchInvalid.class)) {
                    try {
                        ensureAccessible(method);
                        fallback = bind(MethodHandles.lookup().unreflect(method), instance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                    Collections.addAll(fallbackParameters, method.getParameters());
                }
            }

            for(Class<?> innerClass : ((Class<?>) ae).getDeclaredClasses()) {
                AnnotationReader inner = new AnnotationReader(innerClass);
                try {
                    if (inner.has(Subcommand.class))
                        registerSubcommand(newCommand(handler, innerClass.getDeclaredConstructor().newInstance(), this, innerClass));
                    else if (inner.has(Command.class))
                        handler.addCommand(newCommand(handler, innerClass.getDeclaredConstructor().newInstance(), null, innerClass));
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            AnnotationReader reader = this.annotationReader = new AnnotationReader(ae);
            if(parent != null) {
                if(parent != this) this.parent = parent;
                Subcommand subcommand = reader.get(Subcommand.class);
                name = subcommand.name();
                aliases = Utils.immutable(subcommand.aliases());
            } else {
                Command subcommand = reader.get(Command.class);
                name = subcommand.name();
                aliases = Utils.immutable(subcommand.aliases());
            }

            ensureAccessible((Method) ae);
            Parameter[] parameters = ((Method) ae).getParameters();
            for (int index = 0; index < parameters.length; index++) {
                Parameter parameter = parameters[index];
                AnnotationReader pr = new AnnotationReader(parameter);
                BaseCommandParam param = new BaseCommandParam(
                        parameter,
                        pr.get(Named.class, Named::value, parameter.getName()),
                        index,
                        pr.get(Default.class, Default::value, null),
                        index == ((Method) ae).getParameterCount() - 1 && !pr.has(Single.class),
                        handler,
                        this,
                        pr.has(Optional.class),
                        pr.get(Switch.class),
                        pr.get(Flag.class)
                );
                if(param.isSwitch() && Primitives.unwrap(param.getType()) != Boolean.TYPE)
                    throw new IllegalArgumentException("Cannot use @Switch on non-boolean parameters (" + param.getType().getSimpleName() + " " + param.getName() + ")");
                params.add(param);
            }

            setProperties0();
            if(Future.class.isAssignableFrom(((Method) ae).getReturnType())
                || CompletionStage.class.isAssignableFrom(((Method) ae).getReturnType())) {
                async = true;
                executor = ASYNC;
            }

            try {
                method = bind(MethodHandles.lookup().unreflect((Method) ae), instance);
                Type returnType = ((Method) ae).getGenericReturnType();
                Class<?> crt = ((Method) ae).getReturnType();
                if (CompletionStage.class.isAssignableFrom(crt)) {
                    returnType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                    Type finalReturnType = returnType;
                    responseHandler = (ResponseHandler<CompletionStage<?>>) (response, subject, command, context) -> response.thenAcceptAsync(value -> {
                        handler.responseHandlers.getOrDefault(
                                finalReturnType instanceof Class ? finalReturnType : crt,
                                ResponseHandler.VOID).handleResponse(value, subject, command, context);
                    });
                } else responseHandler = handler.responseHandlers.getOrDefault(crt, ResponseHandler.VOID);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        if (usage == null) usage = generateUsage(this);
    }

    protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object instance, BaseHandledCommand parent, AnnotatedElement ae) {
        return new BaseHandledCommand(handler, instance, parent, ae);
    }

    protected void registerSubcommand(@NotNull BaseHandledCommand command) {
        subcommands.put(command.name, command);
        for(String alias : command.aliases)
            subcommands.put(alias, command);
    }

    private void setProperties0() {
        description = annotationReader.get(Description.class, Description::value, null);
        usage = annotationReader.get(Usage.class, Usage::value, null);
        conditions = Arrays.stream(annotationReader.get(Conditions.class, Conditions::value, new String[0]))
                .map(id -> n(handler.conditions.get(id), "Invalid condition: " + id))
                .collect(Collectors.toList());
        conditions.addAll(handler.globalConditions);
        isPrivate = annotationReader.has(PrivateCommand.class);
        async = annotationReader.has(RunAsync.class);
        executor = async ? ASYNC : SYNC;
        setProperties();
    }

    protected void setProperties() {}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @NotNull String getUsage() {
        return usage;
    }

    @Override
    public @Nullable HandledCommand getParent() {
        return parent;
    }

    @Override
    public @NotNull CommandPermission getPermission() {
        return permission;
    }

    @Override
    public boolean isRootCommand() {
        return parent == null;
    }

    @Override
    public @NotNull @Unmodifiable List<CommandParameter> getParameters() {
        return params;
    }

    @Override
    public @NotNull List<CommandCondition> getConditions() {
        return conditions;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, HandledCommand> getSubcommands() {
        return immutableSubcommands;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation) {
        return annotationReader.get(annotation);
    }

    @Override
    public boolean hasAnnotation(@NotNull Class<? extends Annotation> annotation) {
        return annotationReader.has(annotation);
    }

    @Override
    public @NotNull CommandHandler getCommandHandler() {
        return handler;
    }

    @Override
    public @Nullable Executor getExecutor() {
        return executor;
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    public @Nullable MethodHandle getMethodHandle() {
        return method;
    }

    private final Map<String, HandledCommand> immutableSubcommands = Collections.unmodifiableMap(subcommands);

    @Override public String toString() {
        return "HandledCommand{" +
                "name='" + name + '\'' +
                ", aliases=" + aliases +
                ", responseHandler=" + responseHandler +
                ", description='" + description + '\'' +
                ", usage='" + usage + '\'' +
                ", async=" + async +
                ", isPrivate=" + isPrivate +
                ", method=" + method +
                ", handler=" + handler +
                ", subcommands=" + subcommands +
                ", params=" + params +
                ", immutableSubcommands=" + immutableSubcommands +
                '}';
    }

    private static String generateUsage(HandledCommand command) {
        if (!command.getParameters().isEmpty()) {
            StringJoiner joiner = new StringJoiner(" ");
            for (CommandParameter parameter : command.getParameters())
                joiner.add(parameter.isOptional() ? "[" + parameter.getName() + "]" : "<" + parameter.getName() + ">");
            return joiner.toString();
        } else {
            StringJoiner joiner = new StringJoiner("\n");
            Set<HandledCommand> commands = new LinkedHashSet<>(command.getSubcommands().values());
            for (HandledCommand subcommand : commands) {
                joiner.add(generateUsage(subcommand));
            }
            return joiner.toString();
        }
    }

}
