---
title: RexSellwands
description: Sell container contents with a wand on Paper, Purpur, and Folia.
---

RexSellwands sells the items in a container when a player right-clicks it with a wand. Left click shows the price and does not sell.

A click is cancelled only when the item in that hand is a sell wand and the block is one of that wand's containers. Breaking, opening, and every other interaction stay as they were.

Messages use MiniMessage. Legacy `&` codes, `&#RRGGBB`, and `#RRGGBB` also work.

## Files

```
plugins/RexSellwands/
  config.yml
  wands.yml
  prices.yml
```

`config.yml` is the only file the updater rewrites. A reload that changes it writes `config.yml.bak.<timestamp>` first, then adds keys that were missing. Values you already set stay as they are.

`wands.yml` and `prices.yml` are copied from the jar only when that file is not there. Reload reads them. It does not overwrite them.

## Next

- [Installation](/rexsellwands/getting-started/installation/)
- [Wands](/rexsellwands/guides/wands/)
- [Prices](/rexsellwands/configuration/prices/)
- [Commands](/rexsellwands/reference/commands/)
