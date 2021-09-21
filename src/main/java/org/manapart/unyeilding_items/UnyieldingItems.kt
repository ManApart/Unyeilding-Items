package org.manapart.unyeilding_items

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem
import net.minecraftforge.event.entity.player.UseHoeEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

// The value here should match an entry in the META-INF/mods.toml file
@Mod("unyielding_items")
class UnyieldingItems {

    init {
        FORGE_BUS.addListener { event: RightClickItem -> repairItemInHand(event.player, event.hand) }
        FORGE_BUS.addListener { event: LeftClickBlock -> repairItemInHand(event.player, event.hand) }
        FORGE_BUS.addListener { event: UseHoeEvent -> repairItemInHand(event.player, Hand.MAIN_HAND) }
        FORGE_BUS.addListener { event: EntityItemPickupEvent -> repairItem(event.item.item) }
        FORGE_BUS.addListener(::onDestroy)
        FORGE_BUS.addListener(::onHurt)
    }

    fun onDestroy(event: PlayerDestroyItemEvent) {
        val item = event.original
        if (shouldRepair(item)) {
            item.item.setDamage(item, 1)
            event.player.addItem(item)
        }
    }

    fun onHurt(event: LivingHurtEvent) {
        if (event.entityLiving is PlayerEntity) {
            val player = event.entityLiving as PlayerEntity
            repairAll(player)
        }
    }

    private fun repairAll(player: PlayerEntity) {
        repairItemInHand(player, Hand.MAIN_HAND)
        repairItemInHand(player, Hand.OFF_HAND)
        for (stack in player.armorSlots) {
            repairItem(stack)
        }
    }

    private fun repairItemInHand(player: PlayerEntity, hand: Hand) {
        repairItem(player.getItemInHand(hand))
    }

    private fun repairItem(stack: ItemStack) {
        if (shouldRepair(stack)) {
//            System.out.println("Healing " + stack.getDisplayName() + " on swing");
            stack.item.setDamage(stack, 1)
        }
    }

    private fun shouldRepair(stack: ItemStack?): Boolean {
        return if (stack != null) {
            stack.item.isDamageable(stack) || stack.item.isRepairable(stack)
        } else false
    }
}