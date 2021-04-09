<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# format-imports-intellij Changelog

## [Unreleased]
## [1.1.1]

### Added

- Support ESLint [indent](https://eslint.org/docs/rules/indent) and
  [@typescript-eslint/indent](https://github.com/typescript-eslint/typescript-eslint/blob/master/packages/eslint-plugin/docs/rules/indent.md)
  rules.

### Changed

- `tabWidth` from ESLint [max-len](https://eslint.org/docs/rules/max-len) doesn't impact `tabSize` config.

## [1.1.0]

### Added

- Force format when from context menu and short-cut command.
- Add `wrappingStyle.ignoreComments` config to skip trailing comments when counting line length.
- Support ESLint [max-len](https://eslint.org/docs/rules/max-len) rule.

## [1.0.2]

### Added

- Add project settings.
- Format on reformatting code.

## [1.0.0]

### Added

- Format on save, or manually from context menu or shortcut.

## [0.0.1]

### Added

- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
