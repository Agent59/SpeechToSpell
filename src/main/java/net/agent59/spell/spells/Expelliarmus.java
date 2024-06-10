package net.agent59.spell.spells;

import net.agent59.entity.ModEntities;
import net.agent59.entity.custom.RayEntity;
import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class Expelliarmus extends Item implements SpellInterface {
    private static final String NAME = "Expelliarmus";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 50;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final float RAY_SPEED = 2F;
    private static final int MAX_LIFETIME = 1000;
    private static final int PICKUP_DELAY = 40;

    public Expelliarmus(Item.Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Shoots a magical ray that makes an entity drop the item its holding.";
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    public int getCastingCooldown() {
        return CASTING_COOLDOWN;
    }

    @Override
    public SpellType getSpellType() {
        return SPELLTYPE;
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        World world = player.getWorld();
        RayEntity ray = new RayEntity(ModEntities.EXPELLIARMUS_RAY, world) {

            @Override // propels the items in the offhand and mainhand away from the player
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity instanceof PlayerEntity) {
                    ServerPlayerEntity player1 = (ServerPlayerEntity) entity;
                    World world1 = player1.getWorld();
                    ArrayList<ItemStack> stacks = new ArrayList<>();
                    stacks.add(player1.getEquippedStack(EquipmentSlot.MAINHAND));
                    stacks.add(player1.getEquippedStack(EquipmentSlot.OFFHAND));

                    for (ItemStack stack: stacks) {
                        ItemEntity itemEntity = new ItemEntity(world1, player1.getX(), player1.getEyeY() - 0.5, player1.getZ(), stack);
                        double randomX = ((world1.random.nextBoolean()) ? -0.5 : 0.5) * world1.random.nextDouble();
                        double randomY = ((world1.random.nextBoolean()) ? -0.5 : 0.5) * world1.random.nextDouble();
                        itemEntity.setVelocity(randomX, 0.5D, randomY);
                        itemEntity.setPickupDelay(PICKUP_DELAY);

                        world1.spawnEntity(itemEntity);
                    }
                    PlayerInventory inventory = player1.getInventory();
                    inventory.removeStack(inventory.selectedSlot);
                    inventory.removeStack(PlayerInventory.OFF_HAND_SLOT);
                    this.kill();
                }
            }
        };
        ray.configureEntity(player, MAX_LIFETIME, SPELLTYPE, NAME);
        ray.updatePositionAndAngles(player);
        ray.setVelocity(player, RAY_SPEED, 0);
        world.spawnEntity(ray);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
