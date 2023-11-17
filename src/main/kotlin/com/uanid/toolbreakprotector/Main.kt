package com.uanid.toolbreakprotector

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.RangedWeaponItem
import net.minecraft.item.ToolItem
import net.minecraft.item.TridentItem
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
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
        val item: Item = itemStack.item

        if (item is ToolItem || item is RangedWeaponItem || item is TridentItem) {
            if (itemStack.isDamageable) {
                val remainDamage = itemStack.maxDamage - itemStack.damage
                if (remainDamage <= 2) {
                    return ActionResult.FAIL
                }
            }
        }
        return ActionResult.PASS
    }

    private fun blockInteract(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        pos: BlockPos,
        direction: Direction
    ): ActionResult {
        if (decisionBlock(player) == ActionResult.FAIL) {
            player.sendMessage(Text.literal("You have lower durable tool"), true)
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }

    private fun entityInteract(
        player: PlayerEntity,
        world: World,
        hand: Hand,
        entity: Entity,
        hitResult: EntityHitResult?
    ): ActionResult {
        if (!entity.isAttackable) {
            return ActionResult.PASS
        }

        if (decisionBlock(player) == ActionResult.FAIL) {
            player.sendMessage(Text.literal("You have lower durable weapon"), true)
            return ActionResult.FAIL
        }
        return ActionResult.PASS
    }

    private fun useItem(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        if (decisionBlock(player) == ActionResult.FAIL) {
            player.sendMessage(Text.literal("You have lower durable weapon or tool"), true)
            return TypedActionResult.fail(ItemStack.EMPTY)
        }

        return TypedActionResult.pass(ItemStack.EMPTY)
    }

    override fun onInitializeClient() {
        AttackBlockCallback.EVENT.register(this::blockInteract)
        AttackEntityCallback.EVENT.register(this::entityInteract)
        UseItemCallback.EVENT.register(this::useItem)
        println("onInitializeClient() tool-break-protector mod has been initialized.")
    }
}
