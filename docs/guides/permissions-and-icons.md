---
title: Permissions and icons
description: How many vaults a player has, and how they pick an icon.
sidebar:
  order: 1
---

The number of vaults a player has is the highest `n` in the permission nodes they actually have.

Both of these count:

- `rexvaults.vault.<n>`
- `rexvaults.vaults.<n>`

`rexvaults.vault.1`, `rexvaults.vault.3`, and `rexvaults.vaults.2` together mean 3 vaults. The player can open 1, 2, and 3. `rexvaults.vault.0` does not add a vault. If none of the nodes are present, the player has 0 vaults.

The node has to contain the number. A wildcard such as `rexvaults.vault.*` is not expanded into 1, 2, 3, and so on. Give `rexvaults.vault.5` to grant five vaults.

`rexvaults.use` is the gate for `/rexvaults open` and `/rexvaults list`. It defaults to true. It does not grant a vault by itself.

Operator status does not invent a number. An operator can still use `/rexvaults admin open` when they have `rexvaults.admin`.

## Icons

The selector shows one button per vault. That button is the icon slot.

The material is `selector.default-icon` until the player changes it. The default in the jar is `CHEST`.

To change it, pick an item up onto the cursor and click the vault button, or hover the button and press a hotbar key. RexVaults reads the material and cancels the click, so the item is not moved and is not stored in the vault. The choice is saved on that vault's row in `vaults.db`.

Left-click, right-click, or shift-click with nothing on the cursor opens the vault instead.
