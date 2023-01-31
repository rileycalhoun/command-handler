package me.rileycalhoun.commandhandler.spigot.annotation;

import org.bukkit.permissions.PermissionDefault;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface CommandPermission {

    String value();

    PermissionDefault access() default PermissionDefault.OP;

}
