package com.zerosixteen.lootsurge.armorfx;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

/** Draws a Unique's accessory model on top of its vanilla armor layer, following the wearer's pose. */
public final class AccessoryRenderer {
	private AccessoryRenderer() { }

	public static void render(PoseStack poseStack, SubmitNodeCollector collector, ItemStack stack, EquipmentSlot slot, int light,
			HumanoidRenderState state, HumanoidModel<HumanoidRenderState> contextModel) {
		Identifier id = stack.get(DataComponents.ITEM_MODEL);
		if (id == null) {
			return;
		}
		Equippable eq = stack.get(DataComponents.EQUIPPABLE);
		if (eq == null || eq.slot() != slot) {
			return;
		}
		AccessoryModels.Accessory acc = AccessoryModels.get(id);
		if (acc == null) {
			return;
		}
		ArmorRenderer.submitTransformCopyingModel(contextModel, state, acc.model(), state, false, collector.order(0), poseStack,
				RenderTypes.armorCutoutNoCull(acc.texture()), light, OverlayTexture.NO_OVERLAY, 0);
	}
}
