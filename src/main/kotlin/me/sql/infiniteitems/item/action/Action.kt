package me.sql.infiniteitems.item.action

import org.bukkit.entity.Player

interface Action {

    val type: ActionType
    var player: Player

}