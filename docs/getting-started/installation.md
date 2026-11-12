---
title: Installation
description: Put RexVaults on a Paper, Purpur, or Folia server.
sidebar:
  order: 1
---

1. Download the jar.
2. Put it in the server `plugins/` folder.
3. Start the server once so `plugins/RexVaults/config.yml` and `vaults.db` are written.
4. Edit `config.yml`.
5. Give players `rexvaults.vault.<n>` for the number of vaults they should have.
6. Run `/rexvaults reload`, or restart.

PlaceholderAPI is not required. If it is installed when RexVaults enables, the `%rexvaults_count%` placeholder registers on its own. Restart after adding PlaceholderAPI.
