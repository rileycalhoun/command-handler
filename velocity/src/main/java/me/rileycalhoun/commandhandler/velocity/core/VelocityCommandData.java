package me.rileycalhoun.commandhandler.velocity.core;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import me.rileycalhoun.commandhandler.core.base.BaseCommandData;
import me.rileycalhoun.commandhandler.core.base.BaseCommandHandler;
import me.rileycalhoun.commandhandler.velocity.TabSuggestionProvider;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

public class VelocityCommandData extends BaseCommandData implements me.rileycalhoun.commandhandler.velocity.VelocityCommandData {

    private final SimpleCommand executor;

    public VelocityCommandData(BaseCommandHandler handler, Object instance, @Nullable BaseCommandData parent, @Nullable AnnotatedElement ae) {
        super(handler, instance, parent, ae);
        VelocityCommandHandler velocityHandler = (VelocityCommandHandler)handler;
        this.executor = new VelocitySimpleCommand(velocityHandler);
        if(parent == null && name != null) registerCommandToVelocity();
    }

    @Override
    public @NotNull List<TabSuggestionProvider> getTabCompletions() {
        return null;
    }

    @Override
    protected VelocityCommandData newCommand(BaseCommandHandler handler, Object o, BaseCommandData parent, AnnotatedElement ae) {
        return new VelocityCommandData(handler, o, parent, ae);
    }

    private void registerCommandToVelocity() {
        VelocityCommandHandler velocityHandler = (VelocityCommandHandler)handler;
        velocityHandler.getProxyServer().getCommandManager().register(this.getName(), executor, this.getAliases());
    }
}
