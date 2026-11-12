---
title: Configuration
description: Rows, the selector, the blacklist, and sounds in config.yml.
sidebar:
  order: 1
---

`config.yml` is the settings file. `vaults.db` is the items. `config-version` is internal. Leave it alone.

`storage.type` accepts `sqlite`. Any other value is logged as an error and the plugin still uses sqlite. `storage.file` is read when the plugin enables. Reload does not switch files.

## Vault size

`vault.rows` is the number of rows in a vault. Each row is 9 slots. The plugin floors the slot count to a multiple of 9, keeps at least one row, and never goes past 54 slots. `6` is a double chest. `7` is still 54 slots.

Items that do not fit the current size stay in the database. Raising `vault.rows` later shows them again. Lowering it does not delete them.

`vault.title` is the chest title. `{vault}` is the number. `{player}` is the owner, which matters when an admin opens someone else's vault.

## Selector

`/rexvaults open` with no number opens the selector. `selector.rows` uses the same row rules as a vault.

Each vault button is that vault's icon slot.

- Left-click, right-click, or shift-click with an empty cursor opens the vault.
- Click the button with an item on your cursor, or press a hotbar number while hovering it, and the button takes that material. The item stays in your inventory.
- `selector.default-icon` is used until a player sets one.

`selector.previous.slot` and `selector.next.slot` are the page buttons. `-1`, or a slot outside the menu, hides that button and frees the slot for a vault icon. If both slots are the same, the previous button keeps the slot.

Filler fills the slots that are not a vault and not a visible page button. Set `selector.filler.enabled` to `false` to leave those slots empty.

A shift-click in your inventory is cancelled while the selector is open, so the item is not pushed into a button slot. Other clicks in your inventory are left alone. Clicks outside the window are left alone.

## Blacklist

`blacklist` is a list of material names, such as `BEDROCK` or `SPAWNER`. A listed item cannot be placed into a vault. The click that would move it in is cancelled, and other clicks are left alone. An item that is already inside can still be taken out.

Clicks in an ender chest, or in the player inventory, are cancelled only when that click would move a listed item into the open vault. Clicks that do not target the vault are not cancelled. Clicks outside the window are not cancelled.

## Sounds

`sounds.open`, `sounds.close`, `sounds.denied`, and `sounds.icon` each have `enabled`, `sound`, `volume`, and `pitch`. `sound` is a Bukkit sound name, such as `BLOCK_CHEST_OPEN`, or a namespaced id such as `minecraft:block.chest.open`. An unknown name is logged once. An empty name plays nothing.

`open` plays when the selector or a vault opens. `close` plays when a vault closes. `denied` plays when a blacklisted item is stopped. `icon` plays when an icon is set.

Reload applies the next time a menu opens or a click is checked. A vault that is already open keeps its current size until it is closed.
