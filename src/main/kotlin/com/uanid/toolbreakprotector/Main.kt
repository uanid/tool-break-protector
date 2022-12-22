package com.uanid.toolbreakprotector

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object Main : ClientModInitializer {

    private fun decisionBlock(player: PlayerEntity): ActionResult {
        if (player.isSpectator) {
            return ActionResult.PASS
        }

        val itemStack: ItemStack = player.mainHandStack ?: return ActionResult.PASS
        val group: ItemGroup = itemStack.item?.group ?: return ActionResult.PASS

        if (group == ItemGroup.TOOLS || group == ItemGroup.COMBAT) {
            if (itemStack.isDamageable) {
                if ((itemStack.maxDamage - itemStack.damage) == 1) {
                    return ActionResult.FAIL
                }
            }
        }
        return ActionResult.PASS
    }

    private fun blockInteract(player: PlayerEntity, world: World, hand: Hand, pos: BlockPos, direction: Direction): ActionResult {
        if (decisionBlock(player) == ActionResult.FAIL) {
            player.sendMessage(Text.literal("You hand 1 durability remained tool !!"), true)
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }

    private fun entityInteract(player: PlayerEntity, world: World, hand: Hand, entity: Entity, hitResult: EntityHitResult?): ActionResult {
        if (!entity.isAttackable) {
            return ActionResult.PASS
        }

        if (decisionBlock(player) == ActionResult.FAIL) {
            player.sendMessage(Text.literal("You hand 1 durability remained tool !!"), true)
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }

    override fun onInitializeClient() {
        AttackBlockCallback.EVENT.register(this::blockInteract)
        AttackEntityCallback.EVENT.register(this::entityInteract)
        println("onInitializeClient() tool-break-protector mod has been initialized.")
    }
}
