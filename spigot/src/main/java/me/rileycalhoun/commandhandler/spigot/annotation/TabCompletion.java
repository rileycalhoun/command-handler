package me.rileycalhoun.commandhandler.spigot.annotation;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TabCompletion {

    @Pattern("@?([\\w ]+)\\|?")
    String value();

}
