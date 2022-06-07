package net.agent59.stp.item.custom;

import net.agent59.stp.Main;
import net.agent59.stp.speech.Sphinx4SpeechThread;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import javax.sound.sampled.LineUnavailableException;

// TODO prevent client crash when giving wandItem with give command

public class WandItem extends Item {

    public WandItem(Settings settings) {
        super(settings);
    }

    Sphinx4SpeechThread sphinx4SpeechThread;
    Thread speechThread;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            try {
                this.sphinx4SpeechThread = new Sphinx4SpeechThread(user);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            this.speechThread = new Thread(sphinx4SpeechThread);
            this.speechThread.start();

            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient()) {
            this.sphinx4SpeechThread.end();
        }
    }

    // adds nbt on item creation (needed to render spell-hotbar on client)
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            NbtCompound nbt = new NbtCompound();
            nbt.putInt(Main.MOD_ID + ".spellHotbarSelectedSlot", 1);
            ItemStack itemStack = new ItemStack(this);
            itemStack.setNbt(nbt);

            stacks.add(itemStack);
        }
    }
}