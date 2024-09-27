package bigchadguys.sellingbin.block;

import bigchadguys.sellingbin.block.entity.SellingBinBlockEntity;
import bigchadguys.sellingbin.init.ModBlocks;
import bigchadguys.sellingbin.item.SellingBinItem;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.List;

public class SellingBinBlock extends BlockWithEntity implements InventoryProvider {

    public static final EnumProperty<BinMaterial> MATERIAL = EnumProperty.of("material", BinMaterial.class);
    public static final EnumProperty<BinMode> MODE = EnumProperty.of("mode", BinMode.class);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public SellingBinBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(MATERIAL, BinMaterial.WOOD)
                .with(MODE, BinMode.BLOCK_BOUND)
                .with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch(state.get(FACING)) {
            case NORTH, SOUTH -> SHAPES[0];
            case EAST, WEST -> SHAPES[1];
            default -> throw new IllegalStateException("Unexpected value: " + state.get(FACING));
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState()
                .with(MATERIAL, SellingBinItem.getMaterial(context.getStack()).orElse(BinMaterial.WOOD))
                .with(MODE, SellingBinItem.getMode(context.getStack()).orElse(BinMode.BLOCK_BOUND))
                .with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        SellingBinItem.setMaterial(stack, state.get(MATERIAL));
        SellingBinItem.setMode(stack, state.get(MODE));
        return stack;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(MATERIAL).add(MODE).add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SellingBinBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlocks.Entities.SELLING_BIN.get(), SellingBinBlockEntity::tick);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof SellingBinBlockEntity bin ? bin.getInventory() : null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.SUCCESS;
        }

        if(world.getBlockEntity(pos) instanceof ExtendedMenuProvider menu) {
            MenuRegistry.openExtendedMenu(serverPlayer, menu);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(!world.isClient && world.getBlockEntity(pos) instanceof SellingBinBlockEntity bin) {
            ItemStack stack = SellingBinItem.create(state.get(MATERIAL), state.get(MODE));
            bin.setStackNbt(stack);

            if(player.isCreative() && BlockItem.getBlockEntityNbt(stack) != null) {
                ItemEntity item = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                item.setToDefaultPickupDelay();
                world.spawnEntity(item);
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        ItemStack stack = SellingBinItem.create(state.get(MATERIAL), state.get(MODE));

        if(builder.getOptional(LootContextParameters.BLOCK_ENTITY) instanceof SellingBinBlockEntity bin) {
            bin.setStackNbt(stack);
        }

        drops.add(stack);
        return drops;
    }

    private static final VoxelShape[] SHAPES = {
        VoxelShapes.combine(
            Block.createCuboidShape(0, 0, 0, 16, 16, 16),
            VoxelShapes.union(
                Block.createCuboidShape(2, 2, 2, 14, 16, 14),
                Block.createCuboidShape(2, 2, 0, 14, 14, 1),
                Block.createCuboidShape(0, 2, 2, 1, 14, 14),
                Block.createCuboidShape(2, 2, 15, 14, 14, 16),
                Block.createCuboidShape(15, 2, 2, 16, 14, 14),
                Block.createCuboidShape(1, 10, 6, 2, 12, 10),
                Block.createCuboidShape(14, 10, 6, 15, 12, 10)),
            BooleanBiFunction.ONLY_FIRST),
        VoxelShapes.combine(
            Block.createCuboidShape(0, 0, 0, 16, 16, 16),
            VoxelShapes.union(
                Block.createCuboidShape(2, 2, 2, 14, 16, 14),
                Block.createCuboidShape(2, 2, 0, 14, 14, 1),
                Block.createCuboidShape(0, 2, 2, 1, 14, 14),
                Block.createCuboidShape(2, 2, 15, 14, 14, 16),
                Block.createCuboidShape(15, 2, 2, 16, 14, 14),
                Block.createCuboidShape(6, 10, 1, 10, 12, 2),
                Block.createCuboidShape(6, 10, 14, 10, 12, 15)),
            BooleanBiFunction.ONLY_FIRST)
    };

}
