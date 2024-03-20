package me.sql.infiniteitems.item.action.operation.data

import org.bukkit.entity.Player

interface OperationData {

    fun toString(player: Player): String

}