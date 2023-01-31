package me.rileycalhoun.commandhandler.common;

import me.rileycalhoun.commandhandler.common.exception.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.function.Supplier;

public interface CommandHandler {

    CommandHandler registerCommands(@NotNull Object... commands);

    CommandHandler registerResolvers(@NotNull Object... resolvers);

    CommandHandler registerCondition(@NotNull String conditionID, @NotNull CommandCondition condition);
    CommandHandler registerGlobalCondition(@NotNull CommandCondition condition);

    <T> CommandHandler registerParameterValidator(@NotNull Class<T> parameterType, @NotNull ParameterValidator<T> validator);

    <T> CommandHandler registerTypeResolver(@NotNull Class<T> type, @NotNull ParameterResolver.ValueResolver<T> resolver);
    <T> CommandHandler registerContextResolver(@NotNull Class<T> type, @NotNull ParameterResolver.ContextResolver<T> resolver);

    <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, Supplier<T> supplier);
    <T> CommandHandler registerDependency(@NotNull Class<T> dependencyType, T value);

    <T> CommandHandler registerResponseHandler(@NotNull Class<T> responseType, @NotNull ResponseHandler<T> responseHandler);

    @NotNull @UnmodifiableView Map<String, HandledCommand> getCommands();

    <T> @NotNull CommandHelpWriter<T> getHelpWriter();
    <T> CommandHandler setHelpWriter(@NotNull CommandHelpWriter<T> writer);

    @NotNull ExceptionHandler getExceptionHandler();
    CommandHandler setExceptionHandler(@NotNull ExceptionHandler exceptionHandler);

    @NotNull String getSwitchPrefix();
    CommandHandler setSwitchPrefix(@NotNull String prefix);

    @NotNull String getFlagPrefix();
    CommandHandler setFlagPrefix(@NotNull String prefix);

    CommandHandler registerValueResolverFactory(@NotNull ValueResolverFactory factory);
    CommandHandler registerContextResolverFactory(@NotNull ContextResolverFactory factory);


}
