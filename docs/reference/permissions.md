---
title: Permissions
description: Permission nodes shipped with RexVaults.
sidebar:
  order: 2
---

| Permission | Default | Description |
|---|---|---|
| `rexvaults.use` | true | Open and list your own vaults |
| `rexvaults.reload` | op | Reload `config.yml` |
| `rexvaults.admin` | op | Open another player's vault |
| `rexvaults.vault.<n>` | not set | Count this number when deciding how many vaults the player has |
| `rexvaults.vaults.<n>` | not set | Same count, other node name |

Only the numbered nodes decide the count. The plugin reads the highest `n` on nodes the player has. See [Permissions and icons](/rexvaults/guides/permissions-and-icons/).

`rexvaults.admin` can open vault `n` for a player who does not have that many permissions. The items are still that player's row in the database.
