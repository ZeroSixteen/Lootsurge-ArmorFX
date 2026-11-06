package com.zerosixteen.lootsurge.armorfx;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * Loot Surge: Armor FX - client-only, purely visual.
 * Reads accessory geometry (horns, wings, spikes, fins...) from the Loot Surge resource pack and draws it on
 * top of the vanilla armor model. It touches no gameplay, loot, items, networking or server code.
 */
public final class ArmorFxClient implements ClientModInitializer {
	public static final String MOD_ID = "lootsurge_armorfx";

	@Override
	public void onInitializeClient() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				Identifier.fromNamespaceAndPath(MOD_ID, "accessories"),
				(ResourceManagerReloadListener) AccessoryModels::reload);
	}
}
