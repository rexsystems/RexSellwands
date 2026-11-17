# RexSellwands

Sell what is inside a chest, barrel, trapped chest, ender chest, or shulker box. Left click shows the price. Right click sells. A click that is not a wand on one of those containers is not cancelled.

**Java 21. Paper, Purpur, or Folia 1.21+.** Vault and PlaceholderAPI are optional.

## Features

- One file for every wand: material, name, lore, model data, glow, multiplier, uses, cooldown, containers, particles, sound, hologram
- `prices.yml` sets a price per material. A missing price is skipped, not sold for 0
- Blacklist materials stay in the container
- `settings.sell-shulker-contents` defaults to false, so a shulker box item sells at the shulker price
- Vault, or `currency.type: placeholder` with `{player}` and `{amount}` commands
- `rexsellwands.multiplier.<wand>.<number>` uses the highest number the player has
- Uses drop only after the money is deposited. A failed deposit puts the items back
- TextDisplay hologram, removed on the container's region scheduler. No extra hologram plugin
- An empty sound name plays nothing

## Commands

| Command | Description | Permission |
|---|---|---|
| `/rexsellwands help` | Show help | everyone |
| `/rexsellwands list` | List wand ids | everyone |
| `/rexsellwands give <player> <wand> [amount]` | Give a wand | `rexsellwands.give` |
| `/rexsellwands reload` | Reload the three files | `rexsellwands.reload` |

Aliases: `/rsw`, `/sellwand`

## Permissions

| Permission | Default | Description |
|---|---|---|
| `rexsellwands.use` | true | Use a sell wand |
| `rexsellwands.give` | op | Give wands |
| `rexsellwands.reload` | op | Reload the plugin |
| `rexsellwands.multiplier.<wand>.<number>` | none | Sale multiplier for that wand. The highest number wins |

Documentation: https://docs.rexsystems.me/rexsellwands/
