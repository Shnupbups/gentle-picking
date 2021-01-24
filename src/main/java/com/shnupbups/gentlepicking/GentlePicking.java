package com.shnupbups.gentlepicking;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@Mod(GentlePicking.MOD_ID)
public class GentlePicking {
    public static final String MOD_ID = "gentlepicking";

    public static final ITag.INamedTag<Block> PICKABLE = BlockTags.makeWrapperTag("gentlepicking:pickable");

    public GentlePicking() {
        MinecraftForge.EVENT_BUS.addListener(this::onBlockRightClick);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(GlobalLootModifierSerializer.class ,this::registerModifierSerializers);
    }

    public void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(
                new PickableLootModifer.Serializer().setRegistryName(new ResourceLocation(MOD_ID, "destroy_pickable"))
        );
    }

    public void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = event.getPlayer();
        if (state.isIn(PICKABLE) && !player.isSecondaryUseActive() && player.isAllowEdit() && world.isBlockModifiable(player, pos) && !player.isSpectator()) {
            Block.spawnDrops(state, world, pos);
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.removeBlock(pos, false);
            event.setUseBlock(Event.Result.ALLOW);
        }
        event.setUseBlock(Event.Result.DEFAULT);
    }

    public static class PickableLootModifer extends LootModifier {
        protected PickableLootModifer(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Nonnull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            if (context.get(LootParameters.BLOCK_STATE).isIn(PICKABLE)){
                return Collections.emptyList();
            }
            return generatedLoot;
        }

        private static class Serializer extends GlobalLootModifierSerializer<PickableLootModifer> {
            @Override
            public PickableLootModifer read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
                return new PickableLootModifer(ailootcondition);
            }

            @Override
            public JsonObject write(PickableLootModifer instance) {
                return super.makeConditions(instance.conditions);
            }
        }
    }
}
