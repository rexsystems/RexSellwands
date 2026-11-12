---
title: Prices
description: How prices.yml decides what sells.
sidebar:
  order: 3
---

`prices.yml` has two lists: `prices` and `blacklist`.

```yaml
prices:
  DIAMOND: 100
  COBBLESTONE: 0.5
blacklist:
  - BEDROCK
```

Names are Bukkit material names. `diamond` and `DIAMOND` are the same key.

## What sells

A stack sells when all of these are true:

1. The material is not on the blacklist.
2. `prices` has an entry for that material.
3. `price * amount * multiplier` is greater than 0.

The payout for one stack is `unit price * stack amount * multiplier`. A 64 stack of diamonds at 10, with multiplier 1.5, pays 960.

A missing price does not sell the stack for 0. The stack stays in the container and is named in the unsellable message.

A price of 0 or below also stays in the container. The plugin does not delete items it is not paying for.

Blacklisted materials stay in the container. They are not listed as unsellable, and a price on that material is ignored.

## Shulker box items

This is separate from clicking a placed shulker box.

When `settings.sell-shulker-contents` is `false` (the default), a shulker box item is one stack. The price is the shulker material's price times the stack amount times the multiplier. Diamonds inside the box are not added. If that material has a price, the whole item is removed, contents included. If it has no price, the box stays and is listed as unsellable.

When the flag is `true`, stacks inside the shulker are priced with the same rules. Sold stacks are removed from the box. The box item stays in the container. A blacklisted shulker is not opened.

Colored boxes use their own materials, such as `WHITE_SHULKER_BOX`. `SHULKER_BOX` does not cover them.

## Double chests

A chest and the block beside it are one inventory. The sale reads that inventory once, so both halves are included and the player is paid once.
