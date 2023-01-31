package me.rileycalhoun.commandhandler.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextResolver {

    /**
     * @return The return type of this resolver
     */
    Class<?> value();

}
