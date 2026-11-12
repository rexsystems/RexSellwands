---
title: FAQ
description: Clicks, shulker boxes, and failed payments.
sidebar:
  order: 1
---

## A click still opens the chest or breaks the block

RexSellwands cancels a click only when both of these are true:

1. The item in that hand is a sell wand (it has this plugin's data and the id is still in `wands.yml`).
2. The block is in that wand's `allowed-containers`.

Left click on that pair shows the price. Right click sells. The event is not cancelled for air, for a furnace, for an empty hand, or for a normal item. An off-hand click is cancelled only when the off-hand item is the wand. The plugin never sets the event back to un-cancelled.

If a wand click does nothing, check `rexsellwands.use`, the cooldown message, and that the container actually holds a material from `prices.yml`.

## Shulker contents were not paid

`settings.sell-shulker-contents` defaults to `false`.

A shulker box item inside a chest is one sale. The price is the shulker material in `prices.yml`, times the stack size, times the multiplier. Items inside the box are not added. If the shulker material has a price, the item is removed and the contents go with it. If it has no price, the box stays and is listed as unsellable.

Set the flag to `true` to price the stacks inside the box and leave the box in the container. A blacklisted shulker is not opened. Colored boxes need their own price keys, such as `WHITE_SHULKER_BOX`.

Clicking a placed shulker box is different. That block is a container, and the slots inside it sell under the normal rules.

## The payment failed and the items came back

The sale takes the sold stacks out of the container, then deposits the money. It does not pay first.

If Vault rejects the deposit, Vault is not installed, the placeholder give command fails, or the balance does not rise, the plugin writes the original contents back. The wand does not lose a use, and the cooldown does not start. The player sees `messages.deposit-failed`.

For `currency.type: placeholder`, a give command that ran without moving the balance is reversed with `take-command` before the items are restored.

## A double chest paid once

Both halves are one inventory. One right click reads that inventory a single time.

## The wand did not lose a use

Uses decrease only after a sale that deposited money. A preview, a cooldown rejection, an empty container, and a failed payment do not spend a use. `uses: 0` in `wands.yml` never runs out.
