package net.agent59.spell.spells;

import net.agent59.Main;
import net.agent59.entity.ModEntities;
import net.agent59.entity.custom.PortkeyEntity;
import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class Portus extends Item implements SpellInterface {
    private static final String NAME = "Portus";
    private static final int RANGE = 5;
    private static final int CASTING_COOLDOWN = 1000;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final int BOX_SIZE = 2;

    public Portus(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Lets you place a portkey into a block. If you then hit or use the block it will teleport you and players close to you to the given location. After teleportation the portkey will be destroyed.";
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
        HitResult hitResult = player.getCameraEntity().raycast(RANGE, 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            PacketByteBuf buf = PacketByteBufs.create();
            ServerPlayNetworking.send(player, new Identifier(Main.MOD_ID, "portus_screen"), buf);
        }
    }

    public void setPortkey(BlockPos targetBlockPos, ServerPlayerEntity player) {
        HitResult hitResult = player.getCameraEntity().raycast(RANGE, 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            BlockPos pos = blockHitResult.getBlockPos();
            World world = player.getWorld();

            Box box = new Box(pos);
            List<Entity> entities = world.getOtherEntities(null, box);
            for (Entity entity : entities) {
                if (entity.getType() == ModEntities.PORTKEY) {
                entity.kill();
                }
            }

            PortkeyEntity portkeyEntity = new PortkeyEntity(ModEntities.PORTKEY, world);
            portkeyEntity.configureEntity(player, -1, SPELLTYPE, NAME);
            portkeyEntity.updatePositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0);
            portkeyEntity.setTargetBlockPos(targetBlockPos);
            world.spawnEntity(portkeyEntity);

            //set cooldown
            player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
        }
    }
}
