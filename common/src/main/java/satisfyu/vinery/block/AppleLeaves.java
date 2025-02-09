package satisfyu.vinery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AppleLeaves extends LeavesBlock {

    public static final BooleanProperty VARIANT = BooleanProperty.create("can_have_apples");

    public static final BooleanProperty HAS_APPLES = BooleanProperty.create("has_apples");
    public AppleLeaves(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(PERSISTENT, false).setValue(DISTANCE, 7).setValue(VARIANT, false).setValue(HAS_APPLES, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if(state.getValue(VARIANT) && state.getValue(HAS_APPLES) && stack.isEmpty()) {
            if(!world.isClientSide()) {
                ItemStack appleStack = new ItemStack(Items.APPLE, world.getRandom().nextIntBetweenInclusive(1, 3));
                player.addItem(appleStack);
                world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1F, 1F);
                world.setBlockAndUpdate(pos, state.setValue(HAS_APPLES, false));
            }
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        if(state.getValue(VARIANT) && !state.getValue(HAS_APPLES)) return true;
        return super.isRandomlyTicking(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Player p = ctx.getPlayer();
        boolean b = p != null && p.isShiftKeyDown();
        return super.getStateForPlacement(ctx).setValue(VARIANT, ctx.getPlayer().getAbilities().instabuild && b);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VARIANT, HAS_APPLES);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if(state.getValue(VARIANT) && !state.getValue(HAS_APPLES)) world.setBlockAndUpdate(pos, state.setValue(HAS_APPLES, true));
        super.randomTick(state, world, pos, random);
    }
}
