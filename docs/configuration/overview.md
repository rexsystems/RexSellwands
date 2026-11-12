---
title: Configuration
description: Settings and currency in config.yml.
sidebar:
  order: 1
---

`config.yml` holds settings, the currency, and messages. Wand definitions live in [wands.yml](/rexsellwands/guides/wands/). Prices live in [prices.yml](/rexsellwands/configuration/prices/).

`config-version` is internal. Leave it alone.

## settings

| Key | What it does |
|---|---|
| `sell-shulker-contents` | `false` by default. A shulker box item sells as one item at the shulker material price. Items inside are not priced. `true` prices the items inside and leaves the box in the container. |
| `hologram-seconds` | How long the TextDisplay stays above the container after a sale. `0` disables holograms. |
| `money-format` | A `DecimalFormat` pattern for `{price}`, using US grouping. The default is `#,##0.##`. |

`sell-shulker-contents` is about a shulker box item sitting inside a container. A placed shulker box is a container: its inventory is sold like a chest, one slot at a time.

## currency

| Key | What it does |
|---|---|
| `type` | `vault` or `placeholder`. Anything else is treated as `vault` and logged. |
| `balance-placeholder` | Used when `type` is `placeholder`. Example: `%vault_eco_balance%`. |
| `give-command` | Console command that pays the player. `{player}` and `{amount}`. |
| `take-command` | Console command that takes money back. Used when the give command ran but the balance did not rise. |

`{amount}` in a command is a plain number, such as `12.5`, not the grouped chat format.

With `type: vault`, the plugin deposits through Vault after the sold items are removed. If Vault is missing, or the deposit does not succeed, the snapshot is written back into the container.

With `type: placeholder`, the plugin reads the balance, runs `give-command`, and reads the balance again. If the placeholder is empty, the command's own success flag is used. If the balance does not rise by the sale amount, `take-command` runs and the items are put back.

Payment happens after the items are removed. Uses and the cooldown change only after a deposit succeeds.
