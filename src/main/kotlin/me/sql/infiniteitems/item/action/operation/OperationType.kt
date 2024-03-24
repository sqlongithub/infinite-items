package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.Selectable
import org.bukkit.Material
import kotlin.reflect.KClass

enum class OperationType(override val parentClass: KClass<out Operation>,
                         override val material: Material,
                         override val description: String) : Selectable {

    NONE(NoneOperation::class, Material.BARRIER, "Do nothing"),
    SEND_MESSAGE(SendMessageOperation::class, Material.WRITABLE_BOOK, "Send a message"),
    BAN_PLAYER(BanPlayerOperation::class, Material.LAVA_BUCKET, "Ban a player")

}