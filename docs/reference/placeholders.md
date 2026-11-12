---
title: Placeholders
description: PlaceholderAPI tokens for RexVaults.
sidebar:
  order: 4
---

PlaceholderAPI has to be installed before RexVaults, or loaded early enough that it is present when RexVaults enables. Restart after adding it.

| Placeholder | Description |
|---|---|
| `%rexvaults_count%` | Highest vault number for that player, or `0` |
| `%rexvaults_vaults%` | Same value as `%rexvaults_count%` |

An offline player resolves to `0`, because the count comes from their live permission nodes.

Inside `config.yml`, menu titles and messages can also use PlaceholderAPI. `%rexvaults_count%` is the PlaceholderAPI token. `{count}` in `messages.list` is the RexVaults token for that same number.
