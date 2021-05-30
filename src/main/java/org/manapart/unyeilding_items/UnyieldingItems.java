package org.manapart.unyeilding_items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("unyielding_items")
public class UnyieldingItems {

    private static final Logger LOGGER = LogManager.getLogger();

    public UnyieldingItems() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        repairItemInHand(event.getPlayer(), event.getHand());
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        repairItemInHand(event.getPlayer(), event.getHand());
    }

    @SubscribeEvent
    public void onHoeUse(UseHoeEvent event) {
        repairItemInHand(event.getPlayer(), Hand.MAIN_HAND);
    }

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        repairItem(event.getItem().getItem());
    }

    @SubscribeEvent
    public void onDestroy(PlayerDestroyItemEvent event) {
        ItemStack item = event.getOriginal();
        if (shouldRepair(item)) {
            item.getItem().setDamage(item, 1);
            event.getPlayer().addItem(item);
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            repairAll(player);
        }
    }

    private void repairAll(PlayerEntity player) {
        repairItemInHand(player, Hand.MAIN_HAND);
        repairItemInHand(player, Hand.OFF_HAND);
        for (ItemStack stack : player.getArmorSlots()) {
            repairItem(stack);
        }
    }

    private void repairItemInHand(PlayerEntity player, Hand hand) {
        repairItem(player.getItemInHand(hand));
    }

    private void repairItem(ItemStack stack) {
        if (shouldRepair(stack)) {
//            System.out.println("Healing " + stack.getDisplayName() + " on swing");
            stack.getItem().setDamage(stack, 1);
        }
    }

    private boolean shouldRepair(ItemStack stack) {
        if (stack != null) {
            return stack.getItem().isDamageable(stack) || stack.getItem().isRepairable(stack);
        }
        return false;
    }

}
