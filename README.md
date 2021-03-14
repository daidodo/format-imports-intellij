# JS/TS Import/Export Sorter

![Build](https://github.com/daidodo/format-imports-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/16195.svg)](https://plugins.jetbrains.com/plugin/16195)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16195.svg)](https://plugins.jetbrains.com/plugin/16195)

<!-- Plugin description -->

Automatically format **imports** and **exports** for **JavaScript** and **TypeScript**.

## Features

- Auto format imports and exports on save, or manually from command, shortcut or context menu.
- Group and sort imports by [custom rules](https://github.com/daidodo/format-imports/blob/main/README.md#grouping-rules), including [sort by paths or names](https://github.com/daidodo/format-imports/blob/main/docs/interfaces/configuration.md#sortimportsby).
- Remove duplicated and unused names with [configurable exceptions](https://github.com/daidodo/format-imports/blob/main/README.md#unused-imports-removal).
- [Ignore files or declarations](https://github.com/daidodo/format-imports/blob/main/README.md#ignoring-files-or-declarations) by config or inline comments.
- Respect [ESLint](https://eslint.org) and [eslint-plugin-import](https://github.com/benmosher/eslint-plugin-import) rules.
- Respect configs from [Prettier](https://prettier.io), [EditorConfig](https://editorconfig.org) and VS Code editor settings.
- Preserve `'use strict'`, `///` directives, shebang (`#!`) and comments.
- Support [Type-Only imports/exports](https://devblogs.microsoft.com/typescript/announcing-typescript-3-8/#type-only-imports-exports).
- Support multi-root projects.

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for
  "format-imports-intellij"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/daidodo/format-imports-intellij/releases/latest) and install it
  manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
