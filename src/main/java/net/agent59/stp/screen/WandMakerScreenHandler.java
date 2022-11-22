package net.agent59.stp.screen;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.agent59.stp.block.entity.WandMakerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.TranslatableText;

public class WandMakerScreenHandler extends SyncedGuiDescription {
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGT = 166;
    private static final int INVENTORY_SIZE = 4;
    private final PropertyDelegate propertyDelegate;

    public WandMakerScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        this(syncId, playerInventory, context, new ArrayPropertyDelegate(INVENTORY_SIZE));
    }

    public WandMakerScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, PropertyDelegate delegate) {
        super(ModScreenHandlers.WANDMAKER_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));
        this.propertyDelegate = delegate;


        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(GUI_WIDTH, GUI_HEIGT);
        root.setInsets(Insets.ROOT_PANEL);

        // RESULT_SLOT
        WItemSlot resultSlot = WItemSlot.of(blockInventory, 0);
        root.add(resultSlot, 72, 9);

        // WOOD_SLOT
        WLabel woodLabel = new WLabel(new TranslatableText("Wood"));
        root.add(woodLabel, 34, 36, 3, 1);

        WItemSlot woodSlot = WItemSlot.of(blockInventory, 1);
        root.add(woodSlot, 36, 45);

        // CORE_SLOT
        WLabel coreLabel = new WLabel(new TranslatableText("Core"));
        root.add(coreLabel, 106, 36, 3, 1);

        WItemSlot coreSlot = WItemSlot.of(blockInventory, 2);
        root.add(coreSlot, 108, 45);

        root.add(this.createPlayerInventoryPanel(), 0, 72);


        root.validate(this);
        addProperties(delegate);

    }

    public float getCraftingProgress() {
        // returns the crafting progress as a decimal (multiply by 100 to get progress in percent)
        float craftingTime = (float) this.propertyDelegate.get(WandMakerBlockEntity.PROPETYDELEGATE_CRAFTING_TIME_INDEX);
        float craftingTimeTotal = (float) this.propertyDelegate.get(WandMakerBlockEntity.PROPETYDELEGATE_CRAFTING_TIME_TOTAL_INDEX);
        if (craftingTime == 0 || craftingTimeTotal == 0) {
            return 0;
        }
        return 1 - craftingTime / craftingTimeTotal;
    }
}
