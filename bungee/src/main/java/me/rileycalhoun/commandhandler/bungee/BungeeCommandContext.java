package me.rileycalhoun.commandhandler.bungee;

import org.jetbrains.annotations.NotNull;

import me.rileycalhoun.commandhandler.common.CommandContext;
import net.md_5.bungee.api.plugin.Command;

public interface BungeeCommandContext extends CommandContext {
 
    @NotNull BungeeCommandSubject getSubject();
    @NotNull Command getBungeeCommand();

}
