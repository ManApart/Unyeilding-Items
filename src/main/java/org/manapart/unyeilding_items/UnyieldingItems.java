package org.manapart.unyeilding_items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("unyielding_items")
public class UnyieldingItems {

    public UnyieldingItems() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        repairItemInHand(event.getEntityPlayer(), event.getHand());
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        repairItemInHand(event.getEntityPlayer(), event.getHand());
    }

    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        repairItemInHand(event.getEntityPlayer(), event.getHand());
    }

    @SubscribeEvent
    public void onPickup(PlayerEvent.ItemPickupEvent event) {
        repairItem(event.getStack());
    }

    @SubscribeEvent
    public void onDestroy(PlayerDestroyItemEvent event) {
        ItemStack item = event.getOriginal();
        repairItem(item);
        event.getEntityPlayer().setHeldItem(event.getHand(), item);
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            for (ItemStack stack : player.getArmorInventoryList()) {
                repairItem(stack);
            }
        }
    }

    private void repairItemInHand(PlayerEntity player, Hand hand) {
        repairItem(player.getHeldItem(hand));
    }

    private void repairItem(ItemStack stack) {
        if (shouldRepair(stack)) {
//            System.out.println("Healing " + stack.getDisplayName() + " on swing");
            stack.getItem().setDamage(stack, 1);
        }
    }

    private boolean shouldRepair(ItemStack stack) {
        if (stack != null) {
            return stack.getItem().isDamageable() || stack.getItem().isRepairable();
        }
        return false;
    }

}
