package me.sql.infiniteitems.item.action.condition

interface Condition {

    val description: String
    fun isSatisfied(): Boolean

}