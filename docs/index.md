---
title: RexVaults
description: Extra player vaults stored in SQLite.
---

RexVaults gives each player a row of private chests. How many they can open is the highest number on `rexvaults.vault.<n>` or `rexvaults.vaults.<n>`. Items are written to `plugins/RexVaults/vaults.db`.

Messages, menu titles, and sounds use MiniMessage. Legacy `&` codes, `&#RRGGBB`, and `#RRGGBB` also work.

## Files

```
plugins/RexVaults/
  config.yml
  vaults.db
```

A reload that changes `config.yml` writes `config.yml.bak.<timestamp>` first, then adds any keys that were missing. Values you already set stay as they are. Reload does not close a vault that is open, and it does not delete or recreate `vaults.db`.

## Next

- [Installation](/rexvaults/getting-started/installation/)
- [Permissions and icons](/rexvaults/guides/permissions-and-icons/)
- [Commands](/rexvaults/reference/commands/)
