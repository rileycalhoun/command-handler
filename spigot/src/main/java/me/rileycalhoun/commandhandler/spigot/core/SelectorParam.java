package me.rileycalhoun.commandhandler.spigot.core;

import me.rileycalhoun.commandhandler.common.ArgumentStack;
import me.rileycalhoun.commandhandler.common.CommandParameter;
import me.rileycalhoun.commandhandler.common.CommandSubject;
import me.rileycalhoun.commandhandler.common.ParameterResolver;
import me.rileycalhoun.commandhandler.common.exception.InvalidValueException;
import me.rileycalhoun.commandhandler.spigot.PlayerSelector;
import me.rileycalhoun.commandhandler.spigot.SpigotCommandSubject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SelectorParam implements ParameterResolver.ValueResolver<PlayerSelector> {

    public static final SelectorParam INSTANCE = new SelectorParam();

    @Override
    public PlayerSelector resolve(@NotNull ArgumentStack args, @NotNull CommandSubject commandSubject, @NotNull CommandParameter parameter) throws Throwable {
        SpigotCommandSubject subject = (SpigotCommandSubject) commandSubject;
        String value = args.pop().toLowerCase();
        List<Player> coll = new ArrayList<>();
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
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
                Player player = Bukkit.getPlayer(value);
                if (player == null)
                    throw new InvalidValueException(InvalidValueException.PLAYER, value);
                coll.add(player);
                return coll::iterator;
            }
        }
    }
}
