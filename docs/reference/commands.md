---
title: Commands
description: RexVaults commands and their permissions.
sidebar:
  order: 1
---

| Command | Description | Permission |
|---|---|---|
| `/rexvaults` | Show help | everyone |
| `/rexvaults help` | Show help | everyone |
| `/rexvaults open` | Open the selector | `rexvaults.use` |
| `/rexvaults open <vault>` | Open that vault | `rexvaults.use` and a high enough vault number |
| `/rexvaults list` | Print `1` to the player's vault count | `rexvaults.use` |
| `/rexvaults reload` | Reload `config.yml` | `rexvaults.reload` |
| `/rexvaults admin open <player> <vault>` | Open a player's vault, online or offline | `rexvaults.admin` |

Aliases: `/rvault`, `/vaults`

Vault numbers start at 1. `open 0`, a negative number, or a word that is not a number sends `messages.invalid-vault`. A number above the player's own count sends `messages.vault-missing`. Admin open does not check the target's permissions. It still refuses a number below 1, and it refuses a name that has never joined.

Help text comes from `messages.help-header` and `messages.help`.

Reload reads `config.yml` again. It does not close an open vault and it does not touch `vaults.db`.
