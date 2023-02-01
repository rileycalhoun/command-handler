package me.rileycalhoun.commandhandler.common.core;

import me.rileycalhoun.commandhandler.common.*;
import me.rileycalhoun.commandhandler.common.annotation.*;
import me.rileycalhoun.commandhandler.common.exception.ExceptionHandler;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import me.rileycalhoun.commandhandler.common.exception.MissingPermissionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.rileycalhoun.commandhandler.common.core.Utils.*;

public class BaseCommandHandler implements CommandHandler {

    final Map<String, HandledCommand> commands = new HashMap<>();
    final Map<String, CommandCondition> conditions = new HashMap<>();
    final List<CommandCondition> globalConditions = new ArrayList<>();
    final HashSetMultimap<Class<?>, ParameterValidator<?>> validators = new HashSetMultimap<>();
    final Map<Class<?>, ResponseHandler> responseHandlers = new HashMap<>();

    protected final Map<Class<?>, Supplier<?>> dependencies = new HashMap<>();
    protected final List<ResolverFactory<?>> resolverFactories = new ArrayList<>();
    @Nullable CommandHelpWriter<?> helpWriter;

    String switchPrefix = "-";
    String flagPrefix = "-";

    private ExceptionHandler exceptionHandler = null;

    public BaseCommandHandler () {
        registerParameterValidator(Number.class, NumberRangeValidator.INSTANCE);
        registerTypeResolver(String.class, (a, b, parameter) -> a.popForParameter(parameter));
        registerTypeResolver(int.class, (a, b, parameter) -> num(a, Integer::parseInt));
        registerTypeResolver(Integer.class, (a, b, parameter) -> num(a, Integer::parseInt));
        registerTypeResolver(double.class, (a, b, parameter) -> num(a, Double::parseDouble));
        registerTypeResolver(Double.class, (a, b, parameter) -> num(a, Double::parseDouble));
        registerTypeResolver(float.class, (a, b, parameter) -> num(a, Float::parseFloat));
        registerTypeResolver(Float.class, (a, b, parameter) -> num(a, Float::parseFloat));
        registerTypeResolver(byte.class, (a, b, parameter) -> num(a, Byte::parseByte));
        registerTypeResolver(Byte.class, (a, b, parameter) -> num(a, Byte::parseByte));
        registerTypeResolver(short.class, (a, b, parameter) -> num(a, Short::parseShort));
        registerTypeResolver(Short.class, (a, b, parameter) -> num(a, Short::parseShort));
        registerTypeResolver(long.class, (a, b, parameter) -> num(a, Long::parseLong));
        registerTypeResolver(Long.class, (a, b, parameter) -> num(a, Long::parseLong));
        registerTypeResolver(boolean.class, (a, b, parameter) -> resolveBoolean(a));
        registerTypeResolver(Boolean.class, (a, b, parameter) -> resolveBoolean(a));
        registerTypeResolver(TargetHandledCommand.class, (args, subject, parameter) -> {
            HandledCommand found = parameter.getDeclaringCommand().getParent();
            if(found == null) found = parameter.getDeclaringCommand();

            String text;
            found = found.getSubcommands().get(text = args.popForParameter(parameter));
            if(found == null) throw new InvalidValueException(InvalidValueException.SUBCOMMAND, text);
            HandledCommand finalFound = found;
            return () -> finalFound;
        });

        registerContextResolver(CommandHandler.class, (args, sender, parameter) -> BaseCommandHandler.this);
        registerContextResolver(CommandSubject.class, (args, sender, parameter) -> sender);
        registerContextResolver(HandledCommand.class, (args, sender, parameter) -> parameter.getDeclaringCommand());

        registerGlobalCondition((subject, args, command, context) -> {
            if(!command.getPermission().has(subject))
                throw new MissingPermissionException(command.getPermission());
        });
        registerGlobalCondition(new CooldownCondition());

        addFactory(new EnumValueFactory());
        addFactory((ContextResolverFactory) (parameter, command, handler) -> {
            if(!parameter.hasAnnotation(Dependency.class)) return null;
            return ParameterResolver.ContextResolver.of(c(dependencies.get(parameter.getType()), "No dependency supplier registered for " + parameter.getType()));
        });
    }

    @Override
    public CommandHandler registerCommands(@NotNull Object... commands) {
        for(Object instance : commands) {
            addCommand(new BaseHandledCommand(this, instance, null, null));
            setDependencies(instance);
        }
        return this;
    }

    @Override
    public CommandHandler registerCommands(@NotNull String pkgName) {
        System.setProperty("org.slf4j.simpleLogger.log.org.reflections", "off");
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), Scanners.Resources)
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().includePackage(pkgName)));

        Set<Class<?>> clazzes = reflections.getSubTypesOf(Object.class);
        clazzes.forEach(clazz -> {
            try {
                Object o = clazz.getDeclaredConstructor().newInstance();
                registerCommands(o);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                System.out.println("Unable to register commands from class " + clazz.getName() + ": " + e.getMessage());
            }
        });
        return this;
    }

    protected void addCommand(BaseHandledCommand command) {
        HandledCommand replaced = commands.put(command.getName(), command);
        if(replaced != null) command.subcommands.putAll(replaced.getSubcommands());
        for( String alias : command.getAliases() ) {
            replaced = commands.put(alias, command);
            if(replaced != null) command.subcommands.putAll(replaced.getSubcommands());
        }
    }

    protected void setDependencies(Object instance) {
        for(Field field : getType(instance).getDeclaredFields()) {
            if(field.isAnnotationPresent(Dependency.class)) {
                try {
                    ensureAccessible(field);
                    field.set(instance, c(dependencies.get(field.getType()), "No dependency supplier registered for " + field.getType()).get());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public CommandHandler registerResolvers(@NotNull Object... resolvers) {
        for(Object resolver : resolvers) {
            Class<?> type = getType(resolver);
            for(Method method : type.getDeclaredMethods()) {
                ValueResolver tr = method.getAnnotation(ValueResolver.class);
                if(tr != null) {
                    try {
                        checkReturns(method);
                        ensureAccessible(method);
                        MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                        Class<?>[] pTypes = method.getParameterTypes();
                        addFactory(ValueResolverFactory.forType(((Class) tr.value()), (a, b, parameter) -> {
                            List<Object> ia = new ArrayList<>();
                            for(Class<?> pType : pTypes) {
                                if(ArgumentStack.class.isAssignableFrom(pType))
                                    ia.add(a);
                                else if (List.class.isAssignableFrom(pType))
                                    ia.add(a.asImmutableList());
                                else if(CommandSubject.class.isAssignableFrom(pType))
                                    ia.add(b);
                                else if (CommandParameter.class.isAssignableFrom(pType))
                                    ia.add(parameter);
                                else if (String.class.isAssignableFrom(pType))
                                    ia.add(a.popForParameter(parameter));
                            }

                            return handle.invokeWithArguments(ia);
                        }));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                ContextResolver cr = method.getAnnotation(ContextResolver.class);
                if(cr != null) {
                    try {
                        checkReturns(method);
                        ensureAccessible(method);
                        MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                        Class<?>[] pTypes = method.getParameterTypes();
                        addFactory(ContextResolverFactory.forType((Class) cr.value(), (a, b, parameter) -> {
                            List<Object> ia = new ArrayList<>();
                            for(Class<?> pType : pTypes) {
                                if(List.class.isAssignableFrom(pType))
                                    ia.add(a);
                                else if (CommandSubject.class.isAssignableFrom(pType))
                                    ia.add(b);
                                else if (CommandParameter.class.isAssignableFrom(pType))
                                    ia.add(parameter);
                            }

                            return handle.invokeWithArguments(ia);
                        }));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                ConditionEvaluator ce = method.getAnnotation(ConditionEvaluator.class);
                if(ce != null) {
                    try {
                        ensureAccessible(method);
                        MethodHandle handle = bind(MethodHandles.lookup().unreflect(method), resolver);
                        Class<?>[] pTypes = method.getParameterTypes();
                        registerCondition(ce.value(), (sender, args, command, bcmd) -> {
                            List<Object> ia = new ArrayList<>();
                            for(Class<?> pType : pTypes) {
                                if(List.class.isAssignableFrom(pType))
                                    ia.add(args);
                                else if (CommandSubject.class.isAssignableFrom(pType))
                                    ia.add(sender);
                                else if (HandledCommand.class.isAssignableFrom(pType))
                                    ia.add(command);
                                else if (CommandContext.class.isAssignableFrom(pType))
                                    ia.add(bcmd);
                                else
                                    injectValues(type, sender, args, command, bcmd, ia);
                            }

                            handle.invokeWithArguments(ia);
                        });
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                addResolvers(method, resolver);
            }
        }

        return this;
    }

    protected void addResolvers(Method method, Object resolver) {}

    protected void injectValues(Class<?> type,
                                @NotNull CommandSubject sender,
                                @NotNull List<String> args,
                                @NotNull HandledCommand command,
                                @NotNull CommandContext bcmd,
                                List<Object> ia) {}

    @Override
    public CommandHandler registerCondition(@NotNull String conditionID, @NotNull CommandCondition condition) {
        conditions.put(n(conditionID, "conditionID"), n(condition, "condition"));
        return this;
    }

    @Override
    public CommandHandler registerGlobalCondition(@NotNull CommandCondition condition) {
        globalConditions.add(n(condition, "condition"));
        return this;
    }

    @Override
    public <T> CommandHandler registerParameterValidator(@NotNull Class<T> type, @NotNull ParameterValidator<T> validator) {
        validators.put(type, validator);
        return this;
    }

    @Override
    public <T> CommandHandler registerTypeResolver(@NotNull Class<T> type, ParameterResolver.@NotNull ValueResolver<T> resolver) {
        addFactory(ValueResolverFactory.forType(type, resolver));
        return this;
    }

    @Override
    public <T> CommandHandler registerContextResolver(@NotNull Class<T> type, ParameterResolver.@NotNull ContextResolver<T> resolver) {
        addFactory(ContextResolverFactory.forType(type, resolver));
        return this;
    }

    @Override
    public <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, Supplier<T> supplier) {
        dependencies.put(dependencyType, supplier);
        return this;
    }

    @Override
    public <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, T value) {
        dependencies.put(dependencyType, () -> value);
        return this;
    }

    @Override
    public <T> CommandHandler registerResponseHandler(@NotNull Class<T> responseType, @NotNull ResponseHandler<T> responseHandler) {
        responseHandlers.put(responseType, responseHandler);
        return this;
    }

    @Override
    public @NotNull ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public CommandHandler setExceptionHandler(@NotNull ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, HandledCommand> getCommands() {
        return commands;
    }

    @Override
    public <T> @NotNull CommandHelpWriter<T> getHelpWriter() {
        return (CommandHelpWriter<T>) helpWriter;
    }

    @Override
    public <T> CommandHandler setHelpWriter(@NotNull CommandHelpWriter<T> writer) {
        n(writer, "CommandHelpWriter cannot be null!");
        this.helpWriter = writer;
        return this;
    }

    @Override
    public @NotNull String getSwitchPrefix() {
        return switchPrefix;
    }

    @Override
    public CommandHandler setSwitchPrefix(@NotNull String prefix) {
        this.switchPrefix = prefix;
        return this;
    }

    @Override
    public @NotNull String getFlagPrefix() {
        return flagPrefix;
    }

    @Override
    public CommandHandler setFlagPrefix(@NotNull String prefix) {
        this.switchPrefix = prefix;
        return this;
    }

    @Override
    public CommandHandler registerValueResolverFactory(@NotNull ValueResolverFactory factory) {
        n(factory, "ValueResolverFactory cannot be null!");
        addFactory(factory);
        return this;
    }

    @Override
    public CommandHandler registerContextResolverFactory(@NotNull ContextResolverFactory factory) {
        n(factory, "ContextResolverFactory cannot be null!");
        addFactory(factory);
        return this;
    }

    private void addFactory (ResolverFactory factory) {
        if (factory instanceof ValueResolverFactory) {
            resolverFactories.add(0, (ValueResolverFactory) (parameter, command, handler) -> {
                ParameterResolver resolver = factory.create(parameter, command, handler);
                if(resolver == null) return null;
                return new ParameterResolver.ValueResolver<Object>() {

                    @Override
                    public Object resolve(@NotNull ArgumentStack args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter1) throws Throwable {
                        Object value = resolver.resolve(args, subject, parameter1);
                        for(ParameterValidator validator : getValidators(parameter1.getType())) {
                            validator.validate(value, parameter1, subject);
                        }

                        return value;
                    }

                };
            });
        } else {
            resolverFactories.add(0, (parameter, command, handler) -> {
                ParameterResolver resolver = factory.create(parameter, command, handler);
                if(resolver == null) return null;
                return new ParameterResolver.ContextResolver<Object>() {

                    @Override
                    public Object resolve(@NotNull @Unmodifiable List<String> args, @NotNull CommandSubject subject, @NotNull CommandParameter parameter1) throws Throwable {
                        Object value = resolver.resolve(args, subject, parameter);
                        for(ParameterValidator validator : getValidators(parameter1.getType())) {
                            validator.validate(value, parameter1, subject);
                        }

                        return value;
                    }

                };
            });
        }
    }

    private Set<ParameterValidator<?>> getValidators(@NotNull Class<?> type) {
        Set<ParameterValidator<?>> validators = new HashSet<>();
        for (Map.Entry<Class<?>, Set<ParameterValidator<?>>> ve : this.validators.entries()) {
            for (ParameterValidator<?> validator : ve.getValue()) {
                if (ve.getKey().isAssignableFrom(Primitives.wrap(type)))
                    validators.add(validator);
            }
        }
        return validators;
    }

    ParameterResolver<?, ?> getResolver(CommandParameter parameter) {
        HandledCommand command = parameter.getDeclaringCommand();
        for(ResolverFactory<?> factory : resolverFactories) {
            ParameterResolver<?, ?> resolver = factory.create(parameter, command, this);
            if(resolver != null) return resolver;
        }

        throw new IllegalArgumentException("Unable to resolve parameter '" + parameter.getName() + "' of type " + parameter.getType());
    }

    private static <T> T num(ArgumentStack stack, Function<String, T> s) {
        String num = stack.pop();
        try {
            return s.apply(num);
        } catch (NumberFormatException e) {
            throw new InvalidValueException(InvalidValueException.NUMBER, num);
        }
    }

    private static boolean resolveBoolean(ArgumentStack stack) {
        String value = stack.pop();
        return switch (value.toLowerCase()) {
            case "true", "yes", "ye", "yeah", "ofcourse", "mhm" -> true;
            default -> false;
        };
    }

}
