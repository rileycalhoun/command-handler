package me.rileycalhoun.commandhandler.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import me.rileycalhoun.commandhandler.core.CommandHandler;
import me.rileycalhoun.commandhandler.velocity.core.VelocityHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface VelocityCommandHandler extends CommandHandler  {

    VelocityCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider);

    VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions);

    VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions);

    @NotNull Optional<PluginContainer> getPlugin();

    @NotNull ProxyServer getProxyServer();

    static VelocityCommandHandler create(@NotNull Object plugin, @NotNull ProxyServer proxyServer) {
        return new VelocityHandler(plugin, proxyServer);
    }

}
