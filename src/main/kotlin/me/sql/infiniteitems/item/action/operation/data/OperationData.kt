package me.sql.infiniteitems.item.action.operation.data

import me.sql.infiniteitems.Selectable
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.function.Consumer
import kotlin.reflect.KClass

interface OperationData {

    val name: String
    val description: String
    val material: Material
    fun showConfigurationGUI(player: Player, onReturn: Consumer<in OperationData>)
    fun getFormattedValue(player: Player): String
    fun toString(player: Player): String

}