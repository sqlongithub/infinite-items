package me.sql.infiniteitems.item.action.condition

class NoneCondition : Condition {

    override val description = "None"
    override fun isSatisfied(): Boolean {
        return true
    }
}