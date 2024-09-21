package net.agent59.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.agent59.item.custom.WandItem;
import net.agent59.recipe.StSRecipes;
import net.agent59.recipe.WandMakerRecipe;
import net.agent59.screen.WandMakerScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WandMakerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
    public static final int RESULT_SLOT = 0;
    public static final int WOOD_SLOT = 1;
    public static final int CORE_SLOT = 2;
    private static final int[] TOP_SLOTS = new int[]{CORE_SLOT, RESULT_SLOT};
    private static final int[] BOTTOM_SLOTS = new int[]{RESULT_SLOT};
    private static final int[] SIDE_SLOTS = new int[]{WOOD_SLOT, RESULT_SLOT};
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int craftingTime = 0;
    private int craftingTimeTotal = 0;
    protected final PropertyDelegate propertyDelegate;
    public static final int PROPERTYDELEGATE_CRAFTING_TIME_INDEX = 0;
    public static final int PROPERTYDELEGATE_CRAFTING_TIME_TOTAL_INDEX = 1;
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeManager.MatchGetter<Inventory, WandMakerRecipe> matchGetter;

    public WandMakerBlockEntity(BlockPos pos, BlockState state) {
        super(StSBlockEntities.WANDMAKER_BLOCK_ENTITY, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(StSRecipes.WAND_MAKER_RECIPE_TYPE);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case PROPERTYDELEGATE_CRAFTING_TIME_INDEX -> WandMakerBlockEntity.this.craftingTime;
                    case PROPERTYDELEGATE_CRAFTING_TIME_TOTAL_INDEX -> WandMakerBlockEntity.this.craftingTimeTotal;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case PROPERTYDELEGATE_CRAFTING_TIME_INDEX -> WandMakerBlockEntity.this.craftingTime = value;
                    case PROPERTYDELEGATE_CRAFTING_TIME_TOTAL_INDEX -> WandMakerBlockEntity.this.craftingTimeTotal = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory);
        this.craftingTime = nbt.getShort("CraftingTime");
        this.craftingTimeTotal = nbt.getShort("CraftingTimeTotal");
        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putShort("CraftingTime", (short)this.craftingTime);
        nbt.putShort("CraftingTimeTotal", (short)this.craftingTimeTotal);
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbtCompound);
    }

    public static void tick(World world, BlockPos pos, BlockState state, WandMakerBlockEntity blockEntity) {
        Recipe<?> recipe = blockEntity.matchGetter.getFirstMatch(blockEntity, world).orElse(null);
        if (recipe != null && blockEntity.getStack(RESULT_SLOT).isEmpty()) {

            --blockEntity.craftingTime;

            if (blockEntity.craftingTime == 0 && blockEntity.craftRecipe(world.getRegistryManager(), recipe)) {
                blockEntity.removeStack(WOOD_SLOT, 1);
                blockEntity.removeStack(CORE_SLOT, 1);
                blockEntity.setLastRecipe(recipe);
            }
        }
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("Wand Maker");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new WandMakerScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(this.world, this.pos), this.propertyDelegate);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == RESULT_SLOT;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(this.inventory, slot, amount);
        updateCrafting();
        if (slot == RESULT_SLOT) {
            updateRender();
        }
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = Inventories.removeStack(this.inventory, slot);
        updateCrafting();
        if (slot == RESULT_SLOT) {
            updateRender();
        }
        this.markDirty();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        updateCrafting();
        if (slot == RESULT_SLOT) {
            updateRender();
        }
        this.markDirty();
    }

    private void updateCrafting() {
        this.craftingTime = getCraftingTime(this.world, this);
        this.craftingTimeTotal = getCraftingTime(this.world, this);
    }

    private void updateRender() {
        assert this.world != null;
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        assert this.world != null;
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == RESULT_SLOT) {
            return stack.getItem() instanceof WandItem;
        }
        return !(stack.getItem() instanceof WandItem);
    }

    @Override
    public void clear() {
        this.inventory.clear();
        updateCrafting();
        updateRender();
        this.markDirty();
    }

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }
    }

    @Nullable
    @Override
    public Recipe<?> getLastRecipe() {
        return null;
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.addInput(itemStack);
        }
    }

    private static int getCraftingTime(World world, WandMakerBlockEntity blockEntity) {
        if (blockEntity.getStack(RESULT_SLOT).isEmpty()) {
            return blockEntity.matchGetter.getFirstMatch(blockEntity, world).map(WandMakerRecipe::getCraftingTime).orElse(0);
        }
        return 0;
    }

    private boolean craftRecipe(DynamicRegistryManager registryManager, @Nullable Recipe<?> recipe) {
        if (recipe == null || !this.getStack(RESULT_SLOT).isEmpty()) {
            return false;
        }
        ItemStack result = recipe.getOutput(registryManager);
        this.setStack(RESULT_SLOT, result.copy());
        return true;
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
