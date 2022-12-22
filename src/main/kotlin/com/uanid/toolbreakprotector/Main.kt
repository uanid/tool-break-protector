package com.uanid.toolbreakprotector

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object Main : ClientModInitializer {

    private fun interact(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        pos: BlockPos,
        direction: Direction
    ): ActionResult {
        val itemStack: ItemStack = player.mainHandStack!!
        val group: ItemGroup = itemStack.item?.group!!

        if (group == ItemGroup.TOOLS || group == ItemGroup.COMBAT) {
            if (itemStack.isDamageable) {
                if ((itemStack.maxDamage - itemStack.damage) == 1) {
                    player.sendMessage(Text.literal("You hand 1 durability remaining tool!"), true)
                    return ActionResult.FAIL
                }
            }
        }
        return ActionResult.PASS
    }

    override fun onInitializeClient() {
        AttackBlockCallback.EVENT.register(this::interact)
        println("onInitializeClient() tool-break-protector mod has been initialized.")
    }
}