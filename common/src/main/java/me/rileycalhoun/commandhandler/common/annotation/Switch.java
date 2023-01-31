package me.rileycalhoun.commandhandler.common.annotation;

public @interface Switch {

    /**
     * The switch / flag name. If left empty, parameter name will be used.
     * @return The switch / flag name
     */
    String value() default "";

    /**
     * @return The default value of the switch.
     */
    boolean defaultValue() default false;

}
