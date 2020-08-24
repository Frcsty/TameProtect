package me.frosty.tameprotect.Classes;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TamePlayer
{

    private boolean seen, online;
    private String name;
    private UUID   uniqueId;

    public TamePlayer(Player player)
    {
        seen = player.hasPlayedBefore();
        name = player.getName();
        uniqueId = player.getUniqueId();
        online = player.isOnline();
    }

    public TamePlayer(OfflinePlayer player)
    {
        seen = player.hasPlayedBefore();
        name = player.getName();
        uniqueId = player.getUniqueId();
        online = player.isOnline();
    }

    public boolean hasPlayedBefore()
    {
        return seen;
    }

    public boolean isOnline()
    {
        return online;
    }

    public String getName()
    {
        return name;
    }

    public UUID getUniqueId()
    {
        return uniqueId;
    }

}