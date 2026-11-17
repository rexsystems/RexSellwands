# RexSellwands

Sell the contents of a chest, barrel, trapped chest, ender chest, or shulker box with a wand. Left click shows the price. Right click sells.

<p>
  <img src="https://img.shields.io/badge/Minecraft-1.21%2B-62B47A?style=for-the-badge" alt="Minecraft 1.21+">
  <img src="https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
</p>

## Features

- Unlimited wand definitions in `wands.yml`
- Prices and a material blacklist in `prices.yml`
- Left click previews. Right click sells. Any other click is left alone
- Vault, or a placeholder balance with give and take commands
- Permission multipliers per wand
- Uses, cooldowns, particles, sounds, and a TextDisplay hologram
- If the payment fails, the items go back and the wand does not lose a use

## Requirements

- Java 21
- Paper, Purpur, or Folia 1.21+
- Vault is optional. PlaceholderAPI is optional

## Install

1. Put the jar in `plugins/`
2. Restart
3. Edit `plugins/RexSellwands/config.yml`, `wands.yml`, and `prices.yml`
4. Run `/rexsellwands reload`

`wands.yml` and `prices.yml` are written only when the file is missing. Reload does not replace them.

## Commands

| Command | Permission |
|---|---|
| `/rexsellwands help` | everyone |
| `/rexsellwands list` | everyone |
| `/rexsellwands give <player> <wand> [amount]` | `rexsellwands.give` |
| `/rexsellwands reload` | `rexsellwands.reload` |

Aliases: `/rsw`, `/sellwand`

## Documentation

The pages in [docs/](docs/index.md) drop into a Starlight site at `src/content/docs/rexsellwands/`. Paste `docs/sidebar.mjs` into the site sidebar.

Full docs: [https://docs.rexsystems.me/rexsellwands/](https://docs.rexsystems.me/rexsellwands/)

## License

Licensed under the [GNU Affero General Public License v3.0](LICENSE).
