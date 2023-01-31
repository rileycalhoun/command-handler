package me.rileycalhoun.commandhandler.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public interface CommandParameter {

    String getName();
    int getMethodIndex();
    @NotNull Class<?> getType();
    @NotNull Type getFullType();
    @Nullable String getDefaultValue();
    boolean consumesAllString();
    Parameter getJavaParameter();
    boolean isOptional();
    <A extends Annotation> A getAnnotation(@NotNull Class<A> annotation);
    boolean isSwitch();
    @Nullable String getSwitchName();
    boolean isFlag();
    @Nullable String getFlagName();
    boolean getDefaultSwitch();
    boolean hasAnnotation(Class<? extends Annotation> annotation);
    @NotNull <T> ParameterResolver<?, T> getResolver();
    @NotNull CommandHandler getCommandHandler();
    @NotNull HandledCommand getDeclaringCommand();

}
