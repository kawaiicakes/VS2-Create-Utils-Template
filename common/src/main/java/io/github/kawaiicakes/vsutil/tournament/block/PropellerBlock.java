package io.github.kawaiicakes.vsutil.tournament.block;

import io.github.kawaiicakes.vsutil.api.LocalPlayerInterfaceMixin;
import io.github.kawaiicakes.vsutil.tournament.util.block.DirectionalBaseEntityBlock;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import io.github.kawaiicakes.vsutil.tournament.util.RotShapes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PropellerBlock extends DirectionalBaseEntityBlock implements RedstoneConnectingBlock {
    private static final RotShapes.RotShape SHAPE = RotShapes.box(0.1, 0.1, 8.1, 15.9, 15.9, 15.9);

    private static final RotShapes.DirectionalShape DIRECTIONAL_SHAPE = RotShapes.DirectionalShape.south(SHAPE);

    public static int getPropSignal(BlockState state, Level level, BlockPos pos) {
        Set<Direction> sides = Arrays.stream(Direction.values())
                .filter(p -> !p.equals(state.getValue(FACING).getOpposite()))
                .collect(Collectors.toSet());

        int best = 0;
        for (Direction direction : sides) {
            if (level.getSignal(pos.relative(direction), direction) > best)
                best = level.getSignal(pos.relative(direction), direction);
        }
        return best;
    }

    public final BiFunction<BlockPos, BlockState, BlockEntity> beConstr;

    public PropellerBlock(BiFunction<BlockPos, BlockState, BlockEntity> beConstr) {
        super(
                Properties.of(Material.STONE)
                        .sound(SoundType.STONE)
                        .strength(1.0f, 2.0f)
        );

        registerDefaultState(
                defaultBlockState().setValue(FACING, Direction.NORTH)
        );

        this.beConstr = beConstr;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DIRECTIONAL_SHAPE.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(
                builder.add(FACING)
        );
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!(level instanceof ServerLevel)) return;

        int signal = getPropSignal(state, level, pos);

        if (!(level.getBlockEntity(pos) instanceof PropellerBlockEntity<?> be)) return;

        be.signal = signal;
        be.update();
        be.attachPhysics();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel)
            PropellerBlockEntity.removePhysics(serverLevel, pos);

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (!(level instanceof ServerLevel)) return;

        int signal = getPropSignal(state, level, pos);

        PropellerBlockEntity<?> be = level.getBlockEntity(pos) instanceof PropellerBlockEntity<?> prop
                ? prop
                : null;

        if (be == null) return;

        be.signal = signal;
        be.update();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getPlayer() != null && context.getPlayer().isCrouching()
                ? context.getNearestLookingDirection().getOpposite()
                : context.getNearestLookingDirection();

        return this.defaultBlockState().setValue(FACING, dir);
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof PropellerBlockEntity<?> prop) {
            if (player.level.isClientSide)
                ((LocalPlayerInterfaceMixin) ((Object) player)).vsutil$openPropeller(prop);
            return prop.isUneditable()
                    ? InteractionResult.PASS
                    : InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.beConstr.apply(pos, state);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> blockEntityType
    ) {
        return (BlockEntityTicker<T>) PropellerBlockEntity.TICKER;
    }

    @Override
    public Boolean canConnectTo(BlockState state, Direction direction) {
        return !state.getValue(FACING).equals(direction);
    }
}
