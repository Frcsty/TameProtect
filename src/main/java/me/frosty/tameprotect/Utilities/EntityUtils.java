package me.frosty.tameprotect.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EntityUtils
{

    private static final Set<EntityDamageEvent.DamageCause> damageCauses = new HashSet<>(
            Arrays.asList(EntityDamageEvent.DamageCause.SUICIDE,
                          EntityDamageEvent.DamageCause.STARVATION, EntityDamageEvent.DamageCause.VOID,
                          EntityDamageEvent.DamageCause.SUFFOCATION, EntityDamageEvent.DamageCause.FALL,
                          EntityDamageEvent.DamageCause.DROWNING, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                          EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.LIGHTNING,
                          EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.CONTACT,
                          EntityDamageEvent.DamageCause.FALLING_BLOCK, EntityDamageEvent.DamageCause.POISON,
                          EntityDamageEvent.DamageCause.MAGIC, EntityDamageEvent.DamageCause.FIRE,
                          EntityDamageEvent.DamageCause.FIRE_TICK));

    public static Set<EntityDamageEvent.DamageCause> getDamageCauses()
    {
        return damageCauses;
    }

    public static Entity getEntity(UUID id)
    {
        for (World w : Bukkit.getWorlds())
        {
            for (Entity e : w.getEntities())
            {
                if (e.getUniqueId().equals(id))
                {
                    return e;
                }
            }
        }
        return null;
    }

    public static String getHumanName(Entity entity)
    {
        switch (entity.getType())
        {
            case HORSE:
                return "Horse";
            case LLAMA:
                return "Llama";
            case WOLF:
                return "Wolf";
            case OCELOT:
                return "Ocelot";
            case DONKEY:
                return "Donkey";
            case MULE:
                return "Mule";
            case SKELETON_HORSE:
                return "Skeleton Horse";
            case ZOMBIE_HORSE:
                return "Zombie Horse";
            case PARROT:
                return "Parrot";
            default:
                return "Unknown";
        }
    }

}
