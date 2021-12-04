# JS/TS Import/Export Sorter

![Build](https://github.com/daidodo/format-imports-intellij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/16195.svg)](https://plugins.jetbrains.com/plugin/16195)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16195.svg)](https://plugins.jetbrains.com/plugin/16195)

<!-- Plugin description -->

Automatically format **imports** and **exports** for **JavaScript** and **TypeScript** in IntelliJ IDEs.

## Features

- Auto format imports and exports on saving file; Or manually from reformatting code, short-cut or context menu.
- Group and sort imports by [custom rules](https://github.com/daidodo/format-imports/blob/main/README.md#grouping-rules), including [sort by paths or names](https://github.com/daidodo/format-imports/blob/main/docs/interfaces/configuration.md#sortimportsby).
- Remove duplicated and unused names with [configurable exceptions](https://github.com/daidodo/format-imports/blob/main/README.md#unused-imports-removal).
- [Ignore files or declarations](https://github.com/daidodo/format-imports/blob/main/README.md#ignoring-files-or-declarations) by config or inline comments.
- Respect [ESLint](https://eslint.org) and [eslint-plugin-import](https://github.com/benmosher/eslint-plugin-import) rules ([Wiki](https://github.com/daidodo/format-imports/wiki/ESLint-Compatibility)).
- Respect configs from [Prettier](https://prettier.io) and [EditorConfig](https://editorconfig.org).
- Preserve `'use strict'`, `///` directives, shebang (`#!`) and comments.
- Support [Type-Only imports/exports](https://devblogs.microsoft.com/typescript/announcing-typescript-3-8/#type-only-imports-exports) and [Type Modifier on names](https://devblogs.microsoft.com/typescript/announcing-typescript-4-5/#type-on-import-names).

## How to use

- <kbd>Settings/Preferences</kbd> > <kbd>Languages & Frameworks</kbd> > <kbd>JavaScript</kbd> > <kbd>JS/TS Import/Export Sorter</kbd>
  - Enable auto format on `Save` (`Ctrl+S` or `⌘S`)
  - Enable auto format on `Reformat Code` (`Ctrl+Alt+L` or `⌘⌥L`)

  <img width="475" alt="1" src="https://user-images.githubusercontent.com/8170176/112557394-470be380-8dc4-11eb-944e-96b98824f85f.png">

- Press short-cut keys, default to `Alt+Shift+S` or `⌥⇧S`.
- Right click on editor content and select `Sort Imports/Exports`.

  <img width="421" alt="2" src="https://user-images.githubusercontent.com/8170176/112558168-04e3a180-8dc6-11eb-997c-3b23bd379672.png">

## Configuration

The extension reads configurations from the following sources (in precedence from high to low):

- [ESLint configuration](https://eslint.org/docs/user-guide/configuring) if installed.
- `"importSorter"` section in `package.json`
- `import-sorter.json`
- [Prettier configuration](https://github.com/prettier/prettier-vscode#configuration) if installed
- `.editorconfig`

## Config Files

All options in [Configuration](https://github.com/daidodo/format-imports/blob/main/docs/interfaces/configuration.md) are available in `import-sorter.json` or `package.json` under `"importSorter` section.

For examples:

_import-sorter.json:_

```json
{
  "quoteMark": "single",
  "wrappingStyle": {
    "maxBindingNamesPerLine": 2
  }
}
```

Or

_package.json:_

```json
{
  "importSorter": {
    "quoteMark": "single",
    "wrappingStyle": {
      "maxBindingNamesPerLine": 2
    }
  }
}
```

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
