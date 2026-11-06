package com.zerosixteen.lootsurge.armorfx.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zerosixteen.lootsurge.armorfx.AccessoryRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Additive only: never cancels or replaces vanilla armor rendering. */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends RenderLayer<S, M> {
	public HumanoidArmorLayerMixin(RenderLayerParent<S, M> parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "renderArmorPiece", at = @At("HEAD"))
	private void lootsurgeArmorFx$accessories(PoseStack poseStack, SubmitNodeCollector collector, ItemStack stack, EquipmentSlot slot, int light, S state, CallbackInfo ci) {
		AccessoryRenderer.render(poseStack, collector, stack, slot, light, state, (HumanoidModel<HumanoidRenderState>) (Object) getParentModel());
	}
}
