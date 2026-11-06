# Loot Surge: Armor FX (client-only)

A tiny, **purely visual** Fabric mod that draws horns, wings, spikes, fins and similar accessories on the equipped
armor of Loot Surge Uniques. It does not touch gameplay, loot, rarity, item abilities, networking or any server code.

## What goes where

| Piece | Where it must be installed | Notes |
|---|---|---|
| **Loot Surge** (`lootsurge-*.jar`) | Server **and** every client (unchanged) | Gives the Uniques their `lootsurge:<id>` item model + equipment asset. |
| **Loot Surge: Armor FX** (`lootsurge-armorfx-*.jar`) | **Clients only** | Never needed on the server; a dedicated server ignores/rejects it harmlessly (`environment: client`). Players without it simply see the normal vanilla-shaped armor. |
| **LootSurge-Visuals.zip** resource pack | Clients (or server resource-pack push) | Holds the textures, inventory sprites **and** the accessory shape files this mod reads. |
| Fabric Loader >= 0.19.5, Fabric API (0.161.0+26.2 or newer for 26.2), Java 25 | Clients | Same as Loot Surge itself. |

Compatible with: Minecraft **26.2** (Fabric). Mod Menu optional. Other mods that replace the humanoid armor layer
wholesale (via Fabric's `ArmorRenderer` for the *same* Loot Surge items) are not supported; this mod never cancels
vanilla armor drawing, so it normally coexists with them.

## Install (players)
1. Install Fabric Loader + Fabric API for 26.2 and Loot Surge as usual.
2. Drop `lootsurge-armorfx-1.0.0.jar` into your `mods` folder.
3. Enable `LootSurge-Visuals` in *Options > Resource Packs* (put it above other packs).
4. Equip a Unique with an accessory; it appears on top of the vanilla armor model and follows your animations.

## How it works
* A `HumanoidArmorLayer` mixin (additive, never cancels) runs when a piece is rendered. If the stack's
  `minecraft:item_model` (`lootsurge:<id>`) has a file `assets/<ns>/accessories/<id>.json`, the mod draws that model
  with the wearer's current pose (Fabric's `ArmorRenderer.submitTransformCopyingModel`).
* Accessory files are plain data in the resource pack, so artists can change shapes without touching Java:

```json
{ "texture": "lootsurge:textures/entity/accessories/<id>.png", "texture_size": [64, 32],
  "cubes": [ { "bone": "head", "origin": [x,y,z], "size": [w,h,d], "uv": [u,v],
               "pivot": [px,py,pz], "rotation": [rx,ry,rz] } ] }
```
Bones: `head, body, right_arm, left_arm, right_leg, left_leg`. Coordinates are vanilla model space (x+ = left, y+ = down,
front = -z), identical to `addBox`. Reload resources (F3+T) to see edits.

## Build
Standard Fabric project (Java 25, Loom). Copy `gradlew`, `gradlew.bat` and `gradle/wrapper/` from the Loot Surge repo
(same versions), push, and the included GitHub Action builds `build/libs/lootsurge-armorfx-1.0.0.jar`.

## Honest status
Written against the 26.2 branch of Fabric API (`ArmorRenderer`, `ResourceLoader`). It could not be compiled or run in
the authoring sandbox (no Minecraft/Fabric maven access), so the first GitHub Action run is the real compile check.
