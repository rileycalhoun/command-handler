package me.rileycalhoun.commandhandler.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Cooldown {

    /**
     * @return The command cooldown value
     */
    long value();
    TimeUnit unit() default TimeUnit.SECONDS;

}
