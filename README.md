# RexVaults

Extra vaults for Paper, Purpur, and Folia. Each player gets as many as their highest numbered permission, and the items stay in `vaults.db`.

<p>
  <img src="https://img.shields.io/badge/Minecraft-1.21%2B-62B47A?style=for-the-badge" alt="Minecraft 1.21+">
  <img src="https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
</p>

## Features

- One vault per number, from 1 up to the highest `rexvaults.vault.<n>` or `rexvaults.vaults.<n>`
- Selector with pages, filler, and previous or next buttons you can hide
- An icon per vault, chosen by clicking an item onto that vault's button
- Any item, stored with Paper's item byte format, including bundles and shulker contents
- A material blacklist that blocks only the click that would put the item in
- One viewer at a time
- `/rexvaults admin open <player> <n>` for online or offline players
- Saves when the inventory changes, when it closes, and when the plugin disables
- Reload refreshes `config.yml` and leaves open vaults and `vaults.db` alone

## Requirements

- Java 21
- Paper, Purpur, or Folia 1.21+
- PlaceholderAPI is optional

## Install

1. Put the jar in `plugins/`
2. Restart
3. Edit `plugins/RexVaults/config.yml`
4. Run `/rexvaults reload`

Give a player `rexvaults.vault.3` and they can open vaults 1, 2, and 3.

## Commands

| Command | Permission |
|---|---|
| `/rexvaults help` | everyone |
| `/rexvaults open [vault]` | `rexvaults.use` |
| `/rexvaults list` | `rexvaults.use` |
| `/rexvaults reload` | `rexvaults.reload` |
| `/rexvaults admin open <player> <vault>` | `rexvaults.admin` |

Aliases: `/rvault`, `/vaults`

## Documentation

The pages in [docs/](docs/index.md) drop into a Starlight site at `src/content/docs/rexvaults/`. Paste `docs/sidebar.mjs` into the site sidebar.

Full docs: [https://rexsystems.me/docs/rexvaults](https://rexsystems.me/docs/rexvaults)

## License

Licensed under the [GNU Affero General Public License v3.0](LICENSE).
