package me.rileycalhoun.commandhandler.core.annotation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    @NotNull String name();
    @Nullable String description() default "";
    @Nullable String[] aliases() default {};

}
