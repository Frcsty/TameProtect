package me.frosty.tameprotect.Utilities;

import me.frosty.tameprotect.Classes.TamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {

    public static TamePlayer getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (offlinePlayer == null) {
                return null;
            } else {
                return new TamePlayer(offlinePlayer);
            }
        }
        return new TamePlayer(player);
    }

    public static String getPlayerName(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if (player == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
            if (offlinePlayer == null) {
                return null;
            } else {
                return offlinePlayer.getName();
            }
        } else {
            return player.getName();
        }
    }
}