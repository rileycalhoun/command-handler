package me.rileycalhoun.commandhandler.velocity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.intellij.lang.annotations.Pattern;

@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface TabCompletion {
    
    @Pattern("@?([\\w ]+)\\|?")
    String value();

}
