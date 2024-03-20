package me.sql.infiniteitems

import co.aikar.commands.PaperCommandManager
import me.sql.infiniteitems.command.InfiniteItemsCommand
import me.sql.infiniteitems.event.ActionListener
import me.sql.infiniteitems.item.CustomItemRegistry
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

open class InfiniteItems : JavaPlugin() {

    companion object {
        val debugging: Boolean = false
        lateinit var instance: JavaPlugin
        lateinit var commandManager: PaperCommandManager
    }

    override fun onEnable() {
        instance = this

        commandManager = PaperCommandManager(this)
        commandManager.enableUnstableAPI("help")
        commandManager.commandCompletions.registerAsyncCompletion("custom_items") { c ->
            CustomItemRegistry.registeredIdentifiers
        }
        commandManager.registerCommand(InfiniteItemsCommand())

        Bukkit.getPluginManager().registerEvents(ActionListener(), this)

    }

}