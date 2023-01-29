package me.rileycalhoun.commandhandler.velocity.core;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import me.rileycalhoun.commandhandler.core.base.BaseCommandData;
import me.rileycalhoun.commandhandler.core.base.BaseCommandHandler;
import me.rileycalhoun.commandhandler.velocity.TabSuggestionProvider;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class VelocityHandler extends BaseCommandHandler implements VelocityCommandHandler {

    private final ProxyServer proxyServer;
    private final Optional<PluginContainer> plugin;

    public VelocityHandler(Object plugin, ProxyServer proxyServer) {
        super();
        setExceptionHandler(new VelocityExceptionHandler());
        this.proxyServer = proxyServer;
        this.plugin = proxyServer.getPluginManager().fromInstance(plugin);
        this.registerCommands(plugin);
    }

    @Override
    public void registerCommands(@NotNull Object instance) {
        VelocityCommandData data = new VelocityCommandData(this, instance, null, null);
        if(data.getName() == null) return;
        commands.put(data.getName(), data);
        for(String alias : data.getAliases())
            commands.put(alias, data);
    }

    @Override
    public VelocityCommandHandler registerTabSuggestion(@NotNull String providerID, @NotNull TabSuggestionProvider provider) {
        return null;
    }

    @Override
    public VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull Collection<String> completions) {
        return null;
    }

    @Override
    public VelocityCommandHandler registerStaticTabSuggestion(@NotNull String providerID, @NotNull String... completions) {
        return null;
    }

    @Override
    public @NotNull Optional<PluginContainer> getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull ProxyServer getProxyServer() {
        return proxyServer;
    }

}
