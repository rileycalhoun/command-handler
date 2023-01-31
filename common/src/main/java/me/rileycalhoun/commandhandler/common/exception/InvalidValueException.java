package me.rileycalhoun.commandhandler.common.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidValueException extends CommandException {

    /**
     * Passed when an invalid player is inputted.
     */
    public static final ValueType PLAYER = new ValueType("player");

    /**
     * Passed when an invalid number (e.g a non-number char) is inputted.
     */
    public static final ValueType NUMBER = new ValueType("number");

    /**
     * Passed when an invalid world is inputted.
     */
    public static final ValueType WORLD = new ValueType("world");

    /**
     * Passed when an invalid world is inputted.
     */
    public static final ValueType SUBCOMMAND = new ValueType("subcommand");

    private final ValueType valueType;
    private final Object value;

    public InvalidValueException(@NotNull InvalidValueException.ValueType valueType, @NotNull Object value) {
        super(value.toString());
        this.valueType = valueType;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public static class ValueType {

        private final String id;

        public ValueType (String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override public String toString() {
            return id;
        }

    }

}
