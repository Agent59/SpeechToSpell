package net.agent59.item.custom;

import net.agent59.Main;
import net.agent59.spell.SpellHandler;
import net.agent59.spell.SpellInterface;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class GuideBookItem extends Item {
    public GuideBookItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack itemStack = context.getStack();
        if (itemStack.getNbt() == null) {
            createNbtData(itemStack);
        }

        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.LECTERN)) {
            return LecternBlock.putBookIfAbsent(context.getPlayer(), world, blockPos, blockState, context.getStack()) ? ActionResult.success(world.isClient) : ActionResult.PASS;
        } else {
            return ActionResult.PASS;
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getNbt() == null) {
            createNbtData(itemStack);
        }
        if (!world.isClient()) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
            WrittenBookItem.resolve(itemStack, serverPlayer.getCommandSource(), serverPlayer);
            ServerPlayNetworking.send(serverPlayer, new Identifier(Main.MOD_ID, "book_screen"), PacketByteBufs.create());
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }

    private void createNbtData(ItemStack itemStack) {
        NbtList pages = new NbtList();
        int pageNumber = 1;

        ArrayList<SpellInterface> spells = SpellHandler.getSpellList();

        // table of spells
        ArrayList<String> page = new ArrayList<>();
        page.add(text("Spells", bold(true)));
        page.add(reset());

        for (SpellInterface spell : spells) {
            int spellIndex = spells.indexOf(spell);
            int spellPageNumber = (int) (Math.ceil(spells.size() / 15d) + spellIndex + 1);
            page.add(text("\n" + spell.getStringName(), jumpToPage(spellPageNumber)));

            if (page.size() == 15 || spells.size() == spellIndex + 1) {
                addPage(pageNumber, pages, page);

                page = new ArrayList<>();
                page.add(text("Spells", bold(true)));
                page.add(reset());

                pageNumber++;
            }
        }

        // spell pages
        for (SpellInterface spell : spells) {
            page = new ArrayList<>();
            page.add(text(spell.getStringName(), bold(true)));
            page.add(reset());
            page.add(text("\n" + spell.getDescription()));

            addPage(pageNumber, pages, page);
            pageNumber++;
        }

        itemStack.setSubNbt("pages", pages);

        itemStack.setSubNbt("title", NbtString.of("A Beginners Guide To Witchcraft"));
        itemStack.setSubNbt("author", NbtString.of(""));
    }

    private String jumpToPage(int pageNumber) {
        return ",\"clickEvent\":{\"action\":\"change_page\",\"value\":" + pageNumber + "}";
    }

    private String color(String color) {
        return ",\"color\":\"" + color + "\"";
    }

    private String bold(boolean bl) {
        return ",\"bold\":" + bl;
    }

    private String reset() {
        // resets the previous formats for the next elements, so they don't adopt the formatting
        return text("", ",\"color\":\"reset\"");
    }

    private String text(String text, String... formats) {
        StringBuilder str = new StringBuilder("{\"text\":\"" + text + "\"");
        for (String format : formats) {
            str.append(format);
        }
        return String.valueOf(str.append("}"));
    }

    private void addPage(int pageNumber, NbtList pages, ArrayList<String> texts) {
        int index = pageNumber - 1;

        StringBuilder page = new StringBuilder("[\"\"");
        for (String text : texts) {
            page.append(",").append(text);
        }

        while (index >= pages.size()) {
            pages.add(NbtString.of(text("")));
        }

        pages.set(index, NbtString.of(String.valueOf(page.append("]"))));
    }
}