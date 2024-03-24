package me.sql.infiniteitems.util

import org.bukkit.Bukkit

fun Map<*, *>.tryGetBoolean(key: String, error: String): Boolean {
    if(this[key] is Boolean) {
        return this[key] as Boolean
    } else {
        Bukkit.getLogger().warning(error)
        return false
    }
}