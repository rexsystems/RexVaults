---
title: Messages
description: Prefix, help text, and the colors RexVaults accepts.
sidebar:
  order: 2
---

Every string under `messages`, plus the selector title and the vault title, goes through the same parser.

You can mix all of these in one line:

- MiniMessage: `<gray>`, `<red>`, `<bold>`, `<gradient:#ff0000:#00ff00>`
- Legacy: `&a`, `&l`
- Hex: `&#ff0000` or `#ff0000`

`{prefix}` inserts `messages.prefix`. A blank message is not sent.

A broken MiniMessage tag does not stop the message. That line falls back to legacy codes.

## Command text

| Key | When it is sent |
|---|---|
| `messages.help-header` | First line of `/rexvaults help` |
| `messages.help` | The rest of the help, one list entry per line |
| `messages.reload` | After a successful reload |
| `messages.no-permission` | Missing permission |
| `messages.players-only` | Console tried to open a menu |
| `messages.no-vaults` | The player has no numbered vault permission |
| `messages.vault-missing` | The number is above the player's highest vault |
| `messages.invalid-vault` | The number is not 1 or higher |
| `messages.in-use` | That vault already has a viewer |
| `messages.blacklist` | A listed material was blocked |
| `messages.unknown-player` | Admin open could not find a player who has joined |
| `messages.load-failed` | The saved items could not be read, so the vault was not opened |
| `messages.icon-set` | The player set a vault icon |
| `messages.admin-usage` | `/rexvaults admin` was missing `open`, a player, or a number |
| `messages.admin-opened` | An admin opened someone else's vault |
| `messages.list` | `/rexvaults list` when the player has at least one vault |

## Tokens

`{vault}`, `{player}`, `{icon}`, `{count}`, and `{prefix}` are RexVaults tokens. Inserted names are plain text. A name that contains `<` or `&` cannot recolor the rest of the line.

`{page}` and `{count}` also work in `selector.title`. The title is set when the selector opens.

Other `%placeholder%` values are passed to PlaceholderAPI for the player who is looking at the menu or running the command.
