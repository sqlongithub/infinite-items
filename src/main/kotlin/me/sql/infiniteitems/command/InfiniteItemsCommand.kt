package me.sql.infiniteitems.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import me.sql.infiniteitems.gui.CreateItemGUI
import me.sql.infiniteitems.item.CustomItem
import me.sql.infiniteitems.item.CustomItemRegistry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("ii|infiniteitems|infitems")
class InfiniteItemsCommand : BaseCommand() {

    @HelpCommand
    @Syntax("")
    @Description("Displays this menu")
    fun onHelp(sender: CommandSender, help: CommandHelp) {
        sender.sendMessage(Component.text("InfiniteItems - Help").color(NamedTextColor.GOLD))
        for(helpEntry in help.helpEntries) {
            sender.sendMessage(Component.text("§a/${helpEntry.command} ${helpEntry.parameterSyntax}  §7- ${helpEntry.description}"))
        }
    }

    @Subcommand("create|new|make|c|n|m")
    @Description("Create a new custom item")
    fun onCreateItem(player: Player, vararg identifier: String) {
        lateinit var customItem: CustomItem
        if(identifier.isNotEmpty()) {
            val identifierString = identifier.joinToString(" ").lowercase().replace(" ", "_")
            customItem = CustomItem(identifierString)
        } else {
            player.sendMessage("§cYou need to specify an identifier. Example: §a/ii c item1")
            return
        }
        if(player.inventory.itemInMainHand.type != Material.AIR) {
            customItem.material = player.inventory.itemInMainHand.type
        }

        val createItemGUI = CreateItemGUI(customItem, player)
        createItemGUI.show()
    }

    @Subcommand("give|g|get")
    @Description("Give a player a custom item")
    @CommandCompletion("@players @custom_items")
    fun onGiveCustomItem(sender: CommandSender, to: OnlinePlayer, vararg identifier: String) {
        val stringIdentifier = identifier.joinToString(" ").lowercase().replace(" ", "_")
        val customItem = CustomItemRegistry.get(stringIdentifier)
        if(customItem == null) {
            sender.sendMessage("§c§r${stringIdentifier}§c is not a valid custom item! Run §a/ii list§c for a list of valid items.")
            return
        }
        val didntFit = to.player.inventory.addItem(customItem.getItemStack(to.player))
        if(didntFit.size > 0) {
            sender.sendMessage("§cCouldn't give item to ${to.player.name} as their inventory is full.")
            return
        }
        sender.sendMessage("§aSuccessfully gave §r${stringIdentifier}§a to ${to.player.name}!")
    }

    @Subcommand("list|l|listall|ls|listitems|items|customitems")
    @Description("List all custom items")
    fun onListCustomItems(sender: CommandSender) {
        if(CustomItemRegistry.registeredItems.isEmpty()) {
            sender.sendMessage("§cNo custom items have been created yet. Create one with §a/ii create <id>")
            return
        }
        sender.sendMessage("§aAll custom items:")
        for(identifier in CustomItemRegistry.registeredIdentifiers) {
            sender.sendMessage("  §7- §r${identifier}")
        }
    }

}