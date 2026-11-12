---
title: Messages
description: Every player-facing string in config.yml.
sidebar:
  order: 2
---

All player-facing strings are under `messages`. They accept MiniMessage, legacy `&` codes, `&#RRGGBB`, and `#RRGGBB`.

`{prefix}` and `<prefix>` insert `messages.prefix`. A blank message is not sent.

Inserted values are literal. Color belongs in the template. A player name cannot inject MiniMessage tags.

PlaceholderAPI placeholders in a message are resolved for players when PlaceholderAPI is installed.

## Keys

| Key | When it is sent |
|---|---|
| `prefix` | Inserted into other messages. |
| `no-permission` | Missing `rexsellwands.use`, `rexsellwands.give`, or `rexsellwands.reload`. |
| `reload` | After a successful reload. |
| `give-usage` | `/rexsellwands give` is missing a player or a wand. |
| `give` | The sender is told the wand was given. |
| `received` | The target player is told they received a wand. |
| `unknown-wand` | The wand id is not in `wands.yml`. |
| `unknown-player` | That player is not online. |
| `invalid-amount` | The amount is not a whole number from 1 to 64. |
| `list-header` | First line of `/rexsellwands list`. |
| `list-line` | One line per wand. |
| `list-empty` | `wands.yml` has no wands. |
| `inventory-full` | Part of a give did not fit and was dropped at the player's feet. |
| `preview` | Left click, and at least one stack has a price. |
| `nothing-to-sell` | Nothing in the container can be sold. |
| `sold` | Right click, after the deposit succeeds. |
| `unsellable` | One or more stacks have no price. |
| `cooldown` | Right click while that wand id is still on cooldown for that player. |
| `deposit-failed` | The money was not deposited. The items are back in the container. |
| `wand-broke` | A finite wand hit 0 uses and was removed. |
| `help-header` | First line of `/rexsellwands help`. |
| `help` | The rest of the help, one string per line. |

## Placeholders

| Token | Where it works |
|---|---|
| `{prefix}` | Any message. |
| `{player}` | `give`, `received`, `unknown-player`. |
| `{wand}` | Give, received, unknown wand, list. |
| `{amount}` | Give and received. |
| `{material}` | `list-line`. |
| `{multiplier}` | `list-line`. This is the multiplier written on the wand, not a permission override. |
| `{uses}` | `list-line`. `Infinite` when the wand's uses are 0. |
| `{price}` | `preview`, `sold`, and the wand hologram. |
| `{items}` | `unsellable`. Material names that had no price. |
| `{seconds}` | `cooldown`. |

The wand item's own name and lore are in `wands.yml`. Those lines use `{uses}` and `{multiplier}` for the item text. See [Wands](/rexsellwands/guides/wands/).
