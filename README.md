![Header](https://i.imgur.com/AqONaeU.png)

## Forge tools. Refine materials. Make the result yours.

Modern Foundry is an **unofficial** and **independent** NeoForge port of [Tinker's Construct](https://www.curseforge.com/minecraft/mc-mods/tinkers-construct) for 1.21.1. It brings modular tools, material parts, molten fluids, casting, modifiers, armor, ranged equipment, slime content, and foundry machinery into one coherent Minecraft experience.

The central idea is simple: the tool is not finished when it is crafted. Gather the right materials, shape the parts, melt and alloy resources, cast components, assemble the tool, and then tune it with upgrades and traits for the work ahead.

**Requires:** [Hilt](https://www.curseforge.com/minecraft/mc-mods/hilt)

## What is in the foundry

### Modular toolcrafting

Modern Foundry supports the full workshop loop through:

*   Part Builder patterns and material-aware part recipes
*   Tinker Station tool assembly, repair, part swapping, and renaming
*   Modifier Worktable upgrades and modifier management
*   Tinkers' and Scorched Anvils for advanced station layouts
*   Data-driven tool definitions, station slot layouts, material stats, and material traits
*   Repair kits, casts, tool recycling, and material-based repair paths

Tool parts include pick heads, hammer heads, axe heads, blades, adze heads, plates, bindings, handles, bow limbs, bow grips, arrow components, shield cores, armor plating, maille, and other specialized components.

### A working smeltery and foundry

The processing line is built around both seared and scorched structures. Depending on the job, players can use:

*   Melters, heaters, alloyers, Smeltery Controllers, and Foundry Controllers
*   Seared and scorched tanks, casting tanks, gauges, drains, ducts, chutes, channels, and faucets
*   Native NeoForge fluid capabilities on smeltery drains and ducts for compatible fluid pipes and transport mods
*   Seared melters and fuel tanks use the common glass tag and are harvestable with a stone pickaxe
*   Casting Tables and Casting Basins for parts, ingots, nuggets, gems, plates, gears, wires, and special components
*   Fluid cannons, portable tanks, copper cans, and other fluid-handling tools
*   Data-driven melting, alloying, casting, container-filling, and cast-duplication recipes

This makes the foundry more than a single machine: it is a connected production system for turning raw resources and byproducts into useful materials and equipment.

### Tools for every job

The current tool library covers mining, excavation, farming, combat, ranged combat, exploration, and utility. Examples include pickaxes, sledge hammers, vein hammers, mattocks, pickadzes, shovels, excavators, hand axes, broad axes, kamas, scythes, daggers, swords, cleavers, longbows, crossbows, fishing rods, javelins, arrows, shurikens, throwing axes, staffs, melting pans, war picks, battlesigns, and swashers.

The bundle also includes modifiable shields, slime-themed armor, slime wings, crystalshot ammunition, and other equipment built on the same material-and-modifier systems.

Ordinary Modern Foundry tools and weapons use vanilla-style first-person equip, attack-speed cooldown, and swing animations.

Server-side modifiable-tool block breaking uses the shared harvest path, preserving tool-specific area-of-effect mining and other block-break behavior.

FTB Ultimine can use Modern Foundry's primary and stone mining tools when both mods are installed; ordinary tools and weapons remain unchanged.

### Materials, fluids, and traits

Materials affect the statistics and behavior of the parts they make up. The repository includes materials such as cobalt, steel, slimesteel, amethyst bronze, rose gold, pig iron, cinderslime, Queen's Slime, manyullyn, hepatizon, knightmetal, Knightslime, soulsteel, and additional material families loaded from data.

Modern Foundry metals also work with vanilla systems: the more expensive metals (such as cobalt, manyullyn, and Queen's Slime) build beacon bases and pay for beacon effects, and most Modern Foundry ingots and all four slime crystals are armor trim materials.

The fluid system includes molten metals and alloys alongside material-specific fluids such as slime, blood, venom, magma, soul, and other processing or food-related fluids. Modern Foundry's fluid block-entity and projectile renderers use Minecraft's shader-compatible translucent path so tank contents and fluid effects remain visible with shader packs. Material stats, repair values, traits, tool slots, and modifier effects are kept separate so the combinations can be expanded without hard-coding every tool recipe.

### Modifiers and equipment progression

Modifiers let a tool specialize after assembly. Upgrade, ability, and trait families cover effects such as autosmelting, silk harvesting, magnetic pickup, fiery or freezing attacks, ranged projectile behavior, tank and energy storage, durability changes, mobility, armor defenses, and tool interactions.

Tools store their materials, statistics, modifiers, and additional data as part of the tool stack. This allows the same construction system to support ordinary mining tools, combat weapons, bows, shields, armor, and unusual utility equipment.

### Slime and world content

Modern Foundry extends the foundry theme into the world with slime materials, crystals, foliage, trees, grass, vines, blocks, heads, particles, structures, and custom entities. Earth, sky, ender, magma, and other slime-related content connect exploration and world generation back to the materials used in tool construction.

Nether cobalt ore is a Diamond+ harvest block: diamond and netherite pickaxes can mine it for drops.

Slime wood behaves like vanilla wood: it works in vanilla wooden recipes and connects to vanilla fences, but it does not burn as furnace fuel.

## The core progression

1.  Gather ores, wood, patterns, and crafting components.
2.  Build a Crafting Station, Part Builder, and Tinker Station.
3.  Use patterns and the Part Builder to create parts from the materials you have found.
4.  Build a Melter or a larger Smeltery/Foundry to process molten materials and alloys.
5.  Cast tool parts, ingots, upgrades, and special components.
6.  Assemble a tool from its parts and choose the combination that fits the job.
7.  Apply modifiers, repair the tool, and continue refining it as the world becomes more dangerous.

The goal is for material choice, part choice, and modifier choice to create meaningful differences in durability, mining speed, attack behavior, reach, utility, and survivability.

## Development status

Modern Foundry is under active development. The current source is forked from the original MIT Source Code, and AI was utilzied to assist in the porting process, as well as create promotional material such as the thumbnail and banner art.

## Automated builds and releases

Pushes to `Neo/1.21.1` run the GitHub Actions workflow in `.github/workflows/build.yml`. It fetches the pinned [Hilt](https://github.com/Modern-Mods/Hilt) release JAR required by the source, then builds only Modern Foundry. Hilt is not checked out or built by this workflow.

Each new `mod_version` publishes a GitHub release marked **Latest** with the current `ModernFoundry-1.21.1-<version>-NeoForge.jar`. Builds still run on every push, but release creation is skipped when that version already has a release. Increment `mod_version` when a new Modern Foundry release/build number is ready; Hilt is released independently.

## Credits and attribution

[Tinker's Construct - Original Mod](https://www.curseforge.com/minecraft/mc-mods/tinkers-construct)

Thanks to the SlimeKnights projects and contributors, NeoForge, Just Enough Items, and the other libraries and integrations that make this work possible.

Project source: [Modern-Mods/ModernFoundry](https://github.com/Modern-Mods/ModernFoundry)

### AOE selection and book compatibility

For box-area tools (including the sledge hammer), hold Shift and right-click in
empty air to cycle the mining area. The tooltip lists available sizes in gray and
the selected size in green. Selection is saved on each tool. Expanded upgrades
unlock larger surfaces and deeper modes; removing an upgrade falls back to the
normal supported area if the saved size is no longer available.

Akashic Tome 1.8-30 for Minecraft 1.21.1 is optional. When installed, all six
Modern Foundry guide books implement its book API and can be attached to a tome.

In 4.1.9, AOE mode sizes share one comma-separated tooltip row. Crossbow charges
and Overslime consumption persist immediately on the item. Severing uses the
normal recipe chances, and wither skeletons replace their bone drops with
necrotic bones when `witherBoneDrop` is enabled (the default). Breaking casting
tables or basins drops their stored cast and output inventory.

### 4.2.0: mining modes, trees and Paxel

Mining modes follow this order: `1×1`, `1×2`, `1×3+3×1` (a five-block plus),
`3×3`, `3×3×3`, `5×3`, `5×5`, `5×5×5`, `7×7`, `9×9`.
For tools starting at 1×1, Expanded I/II/III unlock through the plus/5×3/5×5×5.
Tools starting at 3×3 unlock through 5×5/7×7/9×9. The selected mode is still
stored on the individual item and switched with Shift + right-click in air.
Vein Hammer keeps vein searching and gains five blocks of search distance per
Expanded level. Broad Axe fells connected tree trunks and branches only when
nonpersistent (naturally generated) leaves are found; bare logs and trees with
only player-placed leaves fall back to the primary block. Tree scanning is bounded
and does not load chunks.

With Polymorph installed, the normal wooden, stone, iron and diamond
pickaxe/axe/shovel/hoe/sword patterns also offer Modern Foundry results. Vanilla
recipes remain available through Polymorph. Alternatives are pickaxe, hand axe,
pickadze, kama and sword respectively, with wooden handles/bindings and the
matching head material. In 4.2.0, diamond-pattern results use iron heads with the
existing Diamond I modifier already applied, consuming one upgrade slot. There
is no new diamond material and no golden alternative recipe. Gold retains only
its pre-existing material support. Netherite smithing recipes are unchanged.

Paxel (`modernfoundry:paxel`) combines a pick head, small axe head, adze head
(the existing shovel component) and tool handle. Its four-part recipe needs an
anvil. It mines pickaxe, axe and shovel blocks, starts at 1×1, and has no innate
log stripping or path creation. It appears in the tool creative tab, station
layouts and the English/Russian Mighty Smelting guide.

Smeltery now requires the same complete frame/base rules as Foundry. A 1×1
interior needs a solid 3×3 base, including the perimeter and corners.
The materials stats command reports unsupported stat/material combinations as
normal command errors rather than sending unencodable objects in system chat.

Broad Axe has no selectable AOE modes, mode tooltip, extra-block outlines or
area crack overlays. It always fells a natural tree (subject to the safety
limits above) or harvests only the targeted block. Expanded does not expose
rectangular modes on Broad Axe.

### Additional 4.2.0 crafting-table recipes (Polymorph)

Sledge Hammer, Excavator and Broad Axe each have wooden (logs), stone (smooth
stone), iron (iron ingots) and diamond-pattern (diamonds) recipes. Diamond-pattern
results use iron head parts with Diamond I and its normal one-upgrade-slot cost.
Handles and bindings are wooden. In these patterns, `X` is the tier ingredient,
`S` is a stick, and `-` is an empty slot:

```text
Sledge Hammer   Excavator   Broad Axe
XXX             XSX         XXX
XSX             XXX         XS-
-S-             -S-         XS-
```

The vanilla bow pattern (three sticks and three strings) also offers a Modern
Foundry longbow: both limbs and the grip are wood, and the bowstring is string.
The vanilla arrow pattern (flint above stick above feather) also offers eight
Modern Foundry arrows with flint heads, wooden shafts and feather fletching.
All these direct-crafting additions require Polymorph; vanilla bow and arrow
results remain available through its result selector.

### 4.2.1: Shovel and custom tool textures

Shovel is assembled in a Tinker Station or anvil from an adze head, tool handle
and tool binding. It digs shovel blocks, creates paths and starts at 1×1, with
the usual small-tool Expanded progression. Polymorph alternatives for the four
vanilla shovel tiers now produce Shovel instead of Pickadze, with wooden handles
and bindings. The diamond pattern produces an iron-headed Shovel with Diamond I
and one upgrade slot consumed. Pickadze remains available through tool assembly.

Paxel and Shovel use the new custom textures and modifier overlays, with working
broken models and material indices matching their assembly parts. Existing
Paxel part order and saved materials are preserved.
