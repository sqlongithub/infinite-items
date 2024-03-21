package me.sql.infiniteitems

import org.bukkit.Material
import kotlin.reflect.KClass

interface Selectable {

    val parentClass: KClass<*>
    val material: Material
    val name: String
    val description: String

}