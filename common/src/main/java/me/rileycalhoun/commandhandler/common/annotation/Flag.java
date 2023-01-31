package me.rileycalhoun.commandhandler.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Flag {

    /**
     * The flag name. If left empty, the parameter will be used.
     * @return The flag name.
     */
    String value() default "";

}
