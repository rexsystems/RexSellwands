---
title: Wands
description: Wand definitions in wands.yml.
sidebar:
  order: 1
---

Each key under `wands` is a wand id. Ids use letters, numbers, underscores, and hyphens, up to 48 characters. `Basic` is stored as `basic`.

```yaml
wands:
  basic:
    material: BLAZE_ROD
    name: "<!italic><gold>Sell Wand"
    lore:
      - "<!italic><gray>Uses: <white>{uses}"
      - "<!italic><gray>Multiplier: <white>{multiplier}"
    custom-model-data: 0
    glow: true
    multiplier: 1.0
    uses: 0
    unbreakable: true
    cooldown-seconds: 0
    allowed-containers:
      - CHEST
      - BARREL
      - ENDER_CHEST
      - TRAPPED_CHEST
      - SHULKER_BOX
    particles: HAPPY_VILLAGER
    sound: ENTITY_EXPERIENCE_ORB_PICKUP
    hologram: "<green>+{price}"
```

There is no limit on how many wands you add. The file shipped in the jar is only written when `wands.yml` is missing.

## Keys

| Key | What it does |
|---|---|
| `material` | Bukkit item name. The wand is skipped if this is not an item. |
| `name` | MiniMessage display name. Blank keeps the material name. |
| `lore` | MiniMessage lines. `{uses}` and `{multiplier}` are filled in when the item is built. |
| `custom-model-data` | Integer model data. `0` sets nothing. |
| `glow` | `true` uses the enchantment glint, without adding an enchantment. |
| `multiplier` | Used when the player has no `rexsellwands.multiplier.<id>.<number>` node. |
| `uses` | `0` means the wand does not run out. Any other number is how many successful sales that item can make. |
| `unbreakable` | Passed through to the item. |
| `cooldown-seconds` | Per player, per wand id. `0` means no cooldown. The timer starts after a successful sale. |
| `allowed-containers` | `CHEST`, `BARREL`, `ENDER_CHEST`, `TRAPPED_CHEST`, `SHULKER_BOX`. If the list is missing, all five are allowed. |
| `particles` | Bukkit particle name. Blank disables particles. `VILLAGER_HAPPY` is read as `HAPPY_VILLAGER`. |
| `sound` | Bukkit sound name. Blank, or an empty string, plays nothing. |
| `hologram` | MiniMessage for the TextDisplay. `{price}` is the amount paid. Blank disables it for this wand. `settings.hologram-seconds` of `0` disables every hologram. |

`SHULKER_BOX` in `allowed-containers` also matches colored shulker boxes.

Item names and lore are italic in vanilla unless the line turns italic off. The default wand uses `<!italic>`.

A finite wand does not stack. An infinite wand stacks up to the material's normal stack size. Uses on the item drop by one only after the deposit succeeds. At 0, the item is removed and `messages.wand-broke` is sent.

Left click on an allowed container shows `messages.preview` and does not take items, money, uses, or cooldown. Right click sells. See the [FAQ](/rexsellwands/faq/) for clicks that are not cancelled.

The ender chest uses the clicking player's ender inventory. A normal chest uses the full double-chest inventory when the block is half of a pair.
