package net.agent59.stp.mixin;

import net.agent59.stp.item.custom.WandItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// sets the arm pose, when using a wand

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart head;

    @Shadow protected abstract void positionRightArm(T entity);

    @Shadow @Final public ModelPart leftArm;

    @Shadow protected abstract void positionLeftArm(T entity);

    @Shadow protected abstract void animateArms(T entity, float animationProgress);

    // hold the wand correctly
    @Redirect(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;positionLeftArm(Lnet/minecraft/entity/LivingEntity;)V"))
    private void setLeftArm(BipedEntityModel<T> instance, T entity) {
        if (entity.getActiveItem().getItem() instanceof WandItem) {
            this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.5707964F, -3.6199816F, 3.3F);
            this.leftArm.yaw = this.head.yaw;

        } else { //resume as always
            this.positionLeftArm(entity);
        }
    }

    @Redirect(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;positionRightArm(Lnet/minecraft/entity/LivingEntity;)V"))
    private void setRightArm(BipedEntityModel<T> instance, T entity) {
        if (entity.getActiveItem().getItem() instanceof WandItem) {
            this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.5707964F, -3.6199816F, 3.3F);
            this.rightArm.yaw = this.head.yaw;

        } else { //resume as always
            this.positionRightArm(entity);
        }
    }


    // prevents the unwanted animation on use wand
    @Redirect(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V"))
    private void animateArms(BipedEntityModel<T> instance, T entity, float animationProgress) {
        if (!(entity.getActiveItem().getItem() instanceof WandItem)) {
            this.animateArms(entity, animationProgress);
        }
    }
}
