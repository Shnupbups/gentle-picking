package com.shnupbups.gentlepicking;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GentlePicking implements ModInitializer {
	public static final String MOD_ID = "gentlepicking";

	public static final Tag<Block> PICKABLE = TagRegistry.block(id("pickable"));

	@Override
	public void onInitialize() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = world.getBlockState(pos);
			if(state.isIn(PICKABLE) && !player.shouldCancelInteraction() && !player.isSpectator()) {
				Block.dropStacks(state, world, pos);
				world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
				world.removeBlock(pos, false);
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS;
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
