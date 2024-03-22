package me.sql.infiniteitems.util

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun TextComponent.withoutItalics(): TextComponent {
    return this.decoration(TextDecoration.ITALIC, false)
}

val Material.formattedName: String
        get() {
            val nameWords = name.split("_")
            val formattedNameWords = ArrayList<String>()
            for(word in nameWords) {
                if(word == "OF") {
                    formattedNameWords.add(word.lowercase())
                    continue
                }
                formattedNameWords.add(word.substring(0, 1) + word.substring(1).lowercase())
            }
            return formattedNameWords.joinToString(" ")
        }

fun String.asTextComponent(): TextComponent {
    return Component.text(this)
}

fun ArrayList<TextComponent>.add(string: String) {
    add(string.asTextComponent().withoutItalics())
}

fun getBackgroundPane(rows: Int): OutlinePane {
    val pane = OutlinePane(0, 0, 9, rows)

    val item = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
    val meta = item.itemMeta!!
    meta.displayName(Component.empty())
    item.itemMeta = meta

    pane.addItem(GuiItem(item))
    pane.setRepeat(true)
    pane.priority = Priority.LOWEST
    return pane
}

fun stringToMultiLineLore(loreString: String, lineLength: Int, prefix: String = "", color: String = "§7"): ArrayList<TextComponent> {
    val lore = ArrayList<TextComponent>()
    var line: String = color.plus(prefix)
    var charCount = prefix.length
    for(char in loreString.chars()) {
        if(charCount >= lineLength && (char.toChar() == ' ' || char.toChar() == '-')) {
            if(char.toChar() != ' ')
                line += char.toChar()
            lore.add(line)
            line = color
            charCount = 0
        } else {
            line = line.plus(char.toChar())
        }
        charCount++
    }
    lore.add(line)
    return lore
}

fun Boolean.toInt(): Int = if(this) 1 else 0