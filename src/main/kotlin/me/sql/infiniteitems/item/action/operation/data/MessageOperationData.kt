package me.sql.infiniteitems.item.action.operation.data

import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player

class MessageOperationData(val getMessage: (Player) -> TextComponent) : OperationData {

    override fun toString(player: Player): String {
        return "message: §r${getMessage }}"
    }

}