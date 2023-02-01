package me.rileycalhoun.commandhandler.velocity.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.proxy.Player;

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import me.rileycalhoun.commandhandler.velocity.PlayerSelector;
import me.rileycalhoun.commandhandler.velocity.VelocityCommandSubject;

public class SelectorParam implements ParameterResolver.ValueResolver<PlayerSelector> {

    public static final SelectorParam INSTANCE = new SelectorParam();

    @Override
    public PlayerSelector resolve(@NotNull ArgumentStack args, @NotNull CommandSubject commandSubject,
            @NotNull CommandParameter parameter) throws Throwable {
                VelocityCommandSubject subject = (VelocityCommandSubject) commandSubject;
                String value = args.pop().toLowerCase();
                List<Player> coll = new ArrayList<>();
                Player[] players = VelocityHandler.getProxyServer().getAllPlayers().toArray(new Player[0]);
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
                        Optional<Player> player = VelocityHandler.getProxyServer().getPlayer(value);
                        if (player.isEmpty())
                            throw new InvalidValueException(InvalidValueException.PLAYER, value);
                        coll.add(player.get());
                        return coll::iterator;
                    }
                }        
    }

}
