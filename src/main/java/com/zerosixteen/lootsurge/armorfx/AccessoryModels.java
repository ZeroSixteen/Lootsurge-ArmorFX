package com.zerosixteen.lootsurge.armorfx;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads accessory definitions from {@code assets/<ns>/accessories/<unique_id>.json} in any resource pack.
 *
 * <p>Cube coordinates use vanilla humanoid model space (x+ = player's left, y+ = down, front = -z) relative to the
 * named bone ({@code head, body, right_arm, left_arm, right_leg, left_leg}), exactly like vanilla {@code addBox}.
 * The unique's id is the {@code minecraft:item_model} id of the worn stack ({@code lootsurge:<id>}).
 */
public final class AccessoryModels {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String[] BONES = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};
	private static final Map<String, float[]> PIVOTS = Map.of(
			"right_arm", new float[]{-5f, 2f, 0f}, "left_arm", new float[]{5f, 2f, 0f},
			"right_leg", new float[]{-1.9f, 12f, 0f}, "left_leg", new float[]{1.9f, 12f, 0f});

	public record Accessory(Identifier texture, HumanoidModel<HumanoidRenderState> model) { }

	private static volatile Map<Identifier, Accessory> models = Map.of();

	public static Accessory get(Identifier itemModelId) {
		return models.get(itemModelId);
	}

	public static void reload(ResourceManager manager) {
		Map<Identifier, Accessory> out = new HashMap<>();
		Map<Identifier, Resource> found = manager.listResources("accessories", id -> id.getPath().endsWith(".json"));
		for (Map.Entry<Identifier, Resource> e : found.entrySet()) {
			Identifier file = e.getKey();
			String path = file.getPath();
			String name = path.substring("accessories/".length(), path.length() - ".json".length());
			try (BufferedReader reader = e.getValue().openAsReader()) {
				out.put(Identifier.fromNamespaceAndPath(file.getNamespace(), name), build(JsonParser.parseReader(reader).getAsJsonObject()));
			} catch (Exception ex) {
				LOGGER.warn("[Loot Surge Armor FX] could not load accessory {}: {}", file, ex.toString());
			}
		}
		models = Map.copyOf(out);
		LOGGER.info("[Loot Surge Armor FX] loaded {} armor accessory definition(s): {}", out.size(), out.keySet());
	}

	private static float f(JsonArray a, int i) {
		return a.get(i).getAsFloat();
	}

	private static Accessory build(JsonObject json) {
		Identifier texture = Identifier.parse(json.get("texture").getAsString());
		JsonArray ts = json.getAsJsonArray("texture_size");
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		Map<String, PartDefinition> bones = new HashMap<>();
		for (String b : BONES) {
			float[] p = PIVOTS.getOrDefault(b, new float[]{0f, 0f, 0f});
			bones.put(b, root.addOrReplaceChild(b, CubeListBuilder.create(), PartPose.offset(p[0], p[1], p[2])));
		}
		// HumanoidModel looks up "hat" as a child of "head" (not of the root)
		bones.get("head").addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		int n = 0;
		for (JsonElement el : json.getAsJsonArray("cubes")) {
			JsonObject c = el.getAsJsonObject();
			PartDefinition bone = bones.get(c.get("bone").getAsString());
			JsonArray o = c.getAsJsonArray("origin");
			JsonArray s = c.getAsJsonArray("size");
			JsonArray uv = c.getAsJsonArray("uv");
			JsonArray pv = c.has("pivot") ? c.getAsJsonArray("pivot") : o;
			float rx = 0f, ry = 0f, rz = 0f;
			if (c.has("rotation")) {
				JsonArray r = c.getAsJsonArray("rotation");
				rx = (float) Math.toRadians(f(r, 0));
				ry = (float) Math.toRadians(f(r, 1));
				rz = (float) Math.toRadians(f(r, 2));
			}
			CubeListBuilder cubes = CubeListBuilder.create()
					.texOffs(uv.get(0).getAsInt(), uv.get(1).getAsInt())
					.addBox(f(o, 0) - f(pv, 0), f(o, 1) - f(pv, 1), f(o, 2) - f(pv, 2), f(s, 0), f(s, 1), f(s, 2));
			bone.addOrReplaceChild("c" + (n++), cubes, PartPose.offsetAndRotation(f(pv, 0), f(pv, 1), f(pv, 2), rx, ry, rz));
		}
		ModelPart baked = LayerDefinition.create(mesh, ts.get(0).getAsInt(), ts.get(1).getAsInt()).bakeRoot();
		return new Accessory(texture, new HumanoidModel<>(baked));
	}
}
