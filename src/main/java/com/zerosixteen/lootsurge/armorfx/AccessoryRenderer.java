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
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Draws a Unique's accessory model on top of its vanilla armor layer, following the wearer's pose. */
public final class AccessoryRenderer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Set<String> SEEN = ConcurrentHashMap.newKeySet();

	private AccessoryRenderer() { }

	private static void once(String key, String msg) {
		if (SEEN.add(key)) {
			LOGGER.info("[Loot Surge Armor FX] {}", msg);
		}
	}

	public static void render(PoseStack poseStack, SubmitNodeCollector collector, ItemStack stack, EquipmentSlot slot, int light,
			HumanoidRenderState state, HumanoidModel<HumanoidRenderState> contextModel) {
		once("hook", "armor layer hook is running");
		Identifier id = stack.get(DataComponents.ITEM_MODEL);
		if (id == null) {
			return;
		}
		once("item:" + id + ":" + slot, "worn piece " + id + " in slot " + slot + " -> accessory " + (AccessoryModels.get(id) != null ? "FOUND" : "not defined"));
		Equippable eq = stack.get(DataComponents.EQUIPPABLE);
		if (eq == null || eq.slot() != slot) {
			once("slot:" + id, "slot mismatch for " + id + ": equippable=" + (eq == null ? "none" : eq.slot()) + " worn=" + slot);
			return;
		}
		AccessoryModels.Accessory acc = AccessoryModels.get(id);
		if (acc == null) {
			return;
		}
		ArmorRenderer.submitTransformCopyingModel(contextModel, state, acc.model(), state, false, collector.order(0), poseStack,
				RenderTypes.armorCutoutNoCull(acc.texture()), light, OverlayTexture.NO_OVERLAY, 0, null);
	}
}
