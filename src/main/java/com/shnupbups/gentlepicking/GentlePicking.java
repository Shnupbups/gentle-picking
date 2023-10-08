package com.shnupbups.gentlepicking;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class GentlePicking implements ModInitializer {
	public static final String MOD_ID = "gentlepicking";

	public static final TagKey<Block> PICKABLE = TagKey.of(Registries.BLOCK.getKey(), id("pickable"));

	@Override
	public void onInitialize() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			BlockPos pos = hitResult.getBlockPos();
			BlockState state = world.getBlockState(pos);
			if (state.isIn(PICKABLE) && player.canModifyBlocks() && !player.shouldCancelInteraction() && world.canPlayerModifyAt(player, pos) && !player.isSpectator()) {
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