---
title: Storage
description: Where vault items are written and how a save works.
sidebar:
  order: 3
---

RexVaults stores items in SQLite. The file is `plugins/RexVaults/vaults.db` unless `storage.file` is an absolute path. The driver is shaded into `me.rexsystems.rexVaults.libs` and ships inside the jar.

`storage.type` must be `sqlite`. Anything else is logged with `severe` and sqlite is used anyway.

The table is `vaults`:

| Column | What it holds |
|---|---|
| `owner` | The player's UUID |
| `vault_number` | The vault number, starting at 1 |
| `icon` | A material name, or empty when the player has not picked one |
| `contents` | The item blob |

There is no delete command. Reload does not drop the table, replace the file, or close a vault that a player still has open. Disable saves open vaults, checkpoints the database, and then closes the connection.

## Item bytes

`contents` is the byte array from Paper's `ItemStack.serializeItemsAsBytes`. The array is one entry per slot, in order. An empty slot is written as an empty item, so the length of the array is the slot count. On load, `ItemStack.deserializeItemsFromBytes` rebuilds that array.

That format keeps data components, so a shulker box, a bundle, a written book, or a potion stays intact.

If the saved array is longer than the current vault size, the extra slots are kept at the end and written back on the next save. They show up again when `vault.rows` is large enough. A failed read does not open the vault and does not replace the blob.

Saves run when the vault inventory changes, when that inventory closes, and when the plugin disables. A change is written from the player's region thread into a single database thread. Close and disable wait until that write finishes.
