package net.agent59.stp.mixin;

import net.agent59.stp.item.ModItems;
import net.agent59.stp.spell.spells.CistemAperio;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ViewerCountManager.class)
public class ViewerCountManagerMixin {

    @ModifyVariable(method = "getInRangeViewerCount", at = @At(value = "STORE"), ordinal = 0)
    private Box renderHeldItemTooltip(Box box) {
        int size = ((CistemAperio) ModItems.CISTEM_APERIO).getRange();
        return box.expand(size, size, size);
    }
}
