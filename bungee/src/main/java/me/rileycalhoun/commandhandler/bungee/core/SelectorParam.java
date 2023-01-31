package me.rileycalhoun.commandhandler.bungee.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.netty.util.internal.ThreadLocalRandom;
import me.rileycalhoun.commandhandler.bungee.BungeeCommandSubject;
import me.rileycalhoun.commandhandler.bungee.PlayerSelector;
import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SelectorParam implements ParameterResolver.ValueResolver<PlayerSelector> {
    
    public static final SelectorParam INSTANCE = new SelectorParam();

    @Override
    public PlayerSelector resolve(@NotNull ArgumentStack args, @NotNull CommandSubject commandSubject,
            @NotNull CommandParameter parameter) throws Throwable {
                BungeeCommandSubject subject = (BungeeCommandSubject) commandSubject;
                String value = args.pop().toLowerCase();
                List<ProxiedPlayer> coll = new ArrayList<>();
                ProxiedPlayer[] players = ProxyServer.getInstance().getPlayers().toArray(new ProxiedPlayer[0]);
                switch (value) {
                    case "@r":
                        coll.add(players[ThreadLocalRandom.current().nextInt(players.length)]);
                        return coll::iterator;
                    case "@a": {
                        Collections.addAll(coll, players);
                        return coll::iterator;
                    }
                    case "@s":
                    case "@p": {
                        coll.add(subject.requirePlayer());
                        return coll::iterator;
                    }
                    default: {
                        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(value);
                        if (player == null)
                            throw new InvalidValueException(InvalidValueException.PLAYER, value);
                        coll.add(player);
                        return coll::iterator;
                    }
                }        
    }

}
