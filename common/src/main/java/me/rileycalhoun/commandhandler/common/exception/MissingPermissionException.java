package me.rileycalhoun.commandhandler.common.exception;

import me.rileycalhoun.commandhandler.common.CommandPermission;

public class MissingPermissionException extends CommandException {

    private final CommandPermission permission;

    public MissingPermissionException(CommandPermission permission) {
        this.permission = permission;
    }

}
