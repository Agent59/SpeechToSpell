package net.agent59.stp.item.custom;

import net.agent59.stp.speech.Sphinx4SpeechThread;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WandItem extends Item {

    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            Sphinx4SpeechThread.getInstance().resumeRecognition(user);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient()) {
            Sphinx4SpeechThread.getInstance().pauseRecognition();
        }
    }
}