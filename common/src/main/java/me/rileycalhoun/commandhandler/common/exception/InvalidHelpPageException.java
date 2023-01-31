package me.rileycalhoun.commandhandler.common.exception;

import me.rileycalhoun.commandhandler.common.CommandHelp;

public class InvalidHelpPageException extends CommandException {

    private final CommandHelp<?> commandHelp;
    private final int page;

    public InvalidHelpPageException(CommandHelp<?> commandHelp, int page) {
        this.commandHelp = commandHelp;
        this.page = page;
    }

    public CommandHelp<?> getCommandHelp() {
        return commandHelp;
    }

    public int getPage() {
        return page;
    }
}
