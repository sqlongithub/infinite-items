package me.sql.infiniteitems.util

import me.sql.infiniteitems.InfiniteItems
import org.bukkit.Bukkit

fun debug(message: String) {
    if(InfiniteItems.DEBUGGING) {
        Bukkit.getLogger().info(message)
    }
}