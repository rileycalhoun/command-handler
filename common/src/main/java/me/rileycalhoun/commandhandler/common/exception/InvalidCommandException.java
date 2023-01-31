package me.rileycalhoun.commandhandler.common.exception;

public class InvalidCommandException extends CommandException {

    private final String input;

    public InvalidCommandException(String input) {
        super(input);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
