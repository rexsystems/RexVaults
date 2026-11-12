---
title: FAQ
description: One viewer, saving, and opening an offline player's vault.
sidebar:
  order: 1
---

## A second player cannot open the same vault

A vault has one viewer. The lock is the owner UUID plus the vault number, and it lives in memory while the server is up.

If the owner has vault 2 open, an admin who runs `/rexvaults admin open` for that same vault gets `messages.in-use`. The same message is sent if the owner tries to open it again. Closing the inventory releases the lock. A crash releases it on the next start, because the lock is not written to the database.

## When is a vault saved?

The items are written when the vault inventory changes, when that inventory closes, and when RexVaults disables. Close and disable wait for the write. A reload does not close the inventory and does not replace `vaults.db`, so a vault that is still open keeps its items and keeps accepting clicks.

Taking an item out, putting an allowed item in, shift-clicking, or dragging inside the vault all count as a change. Setting an icon writes the material only. It does not rewrite the item blob.

If the saved bytes cannot be read, the vault stays closed and the file is left as it is.

## Can I open a vault for an offline player?

Yes. `/rexvaults admin open <player> <vault>` needs `rexvaults.admin`. The name has to match a player who has joined the server at least once. They do not need to be online, and they do not need `rexvaults.vault.<n>` for that number.

The menu opens for you, not for them. While you have it open, they cannot open that same vault. Your clicks are saved to their row. Console cannot open the menu.
