---
title: Installation
description: Put RexSellwands on a Paper, Purpur, or Folia server.
sidebar:
  order: 1
---

1. Download the jar.
2. Put it in the server `plugins/` folder.
3. Start the server once so `plugins/RexSellwands/` is written.
4. Edit `config.yml`, `wands.yml`, and `prices.yml`.
5. Run `/rexsellwands reload`, or restart.

Give yourself a wand with `/rexsellwands give <player> basic`.

Vault is not required. If `currency.type` is `vault` and Vault has no economy, a sale puts the items back and tells the player the payment failed.

PlaceholderAPI is not required. It is used when a message contains a `%placeholder%`, and when `currency.type` is `placeholder`.
