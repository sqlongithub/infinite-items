package me.sql.infiniteitems

import co.aikar.commands.PaperCommandManager
import me.sql.infiniteitems.command.InfiniteItemsCommand
import me.sql.infiniteitems.event.ActionListener
import me.sql.infiniteitems.item.CustomItemRegistry
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

open class InfiniteItems : JavaPlugin() {

    companion object {
        const val DEBUGGING: Boolean = true
        lateinit var instance: JavaPlugin
        lateinit var commandManager: PaperCommandManager
    }

    override fun onEnable() {
        instance = this

        commandManager = PaperCommandManager(this)
        commandManager.enableUnstableAPI("help")
        commandManager.commandCompletions.registerAsyncCompletion("custom_items") { c ->
            CustomItemRegistry.registeredAliases
        }
        commandManager.registerCommand(InfiniteItemsCommand())

        Bukkit.getPluginManager().registerEvents(ActionListener(), this)

        CustomItemRegistry.loadFromConfigAsync()
    }

    override fun onDisable() {
        // some weird error about classnotdef kotlin thingy
        // CustomItemRegistry.saveToConfigSync()
    }

}