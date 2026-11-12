---
title: Permissions
description: RexSellwands permission nodes.
sidebar:
  order: 2
---

| Permission | Default | Description |
|---|---|---|
| `rexsellwands.use` | true | Left click and right click a container with a wand. |
| `rexsellwands.give` | op | `/rexsellwands give`. |
| `rexsellwands.reload` | op | `/rexsellwands reload`. |
| `rexsellwands.multiplier.<wand>.<number>` | not set | Replaces the wand's multiplier for that player. |

`<wand>` is the wand id. `<number>` is a decimal, such as `1.5` or `2`.

If a player has several nodes for the same wand, the highest number is used. If they have none, the sale uses the `multiplier` in `wands.yml`.

The number on the permission is the whole multiplier, not a bonus added to the wand. A node of `0.5` sells at half the configured prices. A node of `3` sells at three times.

```
rexsellwands.multiplier.basic.2
rexsellwands.multiplier.basic.1.5
```

A player with both of those, using the `basic` wand, sells at 2.

The lore placeholder `{multiplier}` shows the value from `wands.yml`. The permission is applied when the price is calculated.
