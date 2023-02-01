package me.rileycalhoun.commandhandler.velocity;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.proxy.ProxyServer;

import me.rileycalhoun.commandhandler.common.CommandHandler;
import me.rileycalhoun.commandhandler.velocity.core.VelocityHandler;

public interface VelocityCommandHandler extends CommandHandler {
    
    VelocityCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider);
    VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions);
    VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions);
    VelocityCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull TabSuggestionProvider provider);
    VelocityCommandHandler registerParameterTab(@NotNull Class<?> parameterType, @NotNull String providerID);
    @NotNull Object getPlugin();
    @NotNull ProxyServer getServer();

    static VelocityCommandHandler create(@NotNull ProxyServer server, Object plugin) {
        return new VelocityHandler(server, plugin);
    }

}
