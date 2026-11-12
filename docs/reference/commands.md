---
title: Commands
description: RexSellwands commands and their permissions.
sidebar:
  order: 1
---

| Command | Description | Permission |
|---|---|---|
| `/rexsellwands` | Show help | everyone |
| `/rexsellwands help` | Show help | everyone |
| `/rexsellwands list` | List wand ids from `wands.yml` | everyone |
| `/rexsellwands give <player> <wand> [amount]` | Give a wand to an online player | `rexsellwands.give` |
| `/rexsellwands reload` | Reload `config.yml`, `wands.yml`, and `prices.yml` | `rexsellwands.reload` |

Aliases: `/rsw`, `/sellwand`

The player must be online. The wand id is the key in `wands.yml`, such as `basic`. Amount defaults to 1 and must be from 1 to 64. If the inventory cannot hold the items, the rest is dropped at the player's feet.

Help text comes from `messages.help-header` and `messages.help`.

Reload reads the files for the next click. It does not edit a sale that is already running. Cooldowns already counting stay in memory. `wands.yml` and `prices.yml` are not overwritten.

Using a wand on a container is not a command. It needs `rexsellwands.use`, which defaults to true.
