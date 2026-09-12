# Changelog

## 1.3.5

### Fixes
- **Modded enchantments now show up in the infuser for items that support them.** Enchanting Infuser only offered enchantments from its own candidate tag, so enchantments that other mods keep out of `#minecraft:in_enchanting_table` (e.g. Corail Tombstone / Ars Elemental Soulbound, Ars Nouveau Reactive) were never shown — even on items that support them. The infuser now scans the whole enchantment registry (like it did before 1.21) and offers everything the item actually supports (`isPrimaryItemFor`), still gating treasure enchantments behind a treasure shelf and curses behind the config toggle.
- **Fixed the infuser menu failing to open for items with a single applicable enchantment.** Enchanting Infuser's power calculation divides by `maxPower - minPower`, which is `0` when only one enchantment (or several of identical power) applies, throwing `ArithmeticException` and aborting menu setup. That degenerate case is now guarded.

## 1.3.4

### Fixes
- **Removed a redundant enchanting-behavior override.** Depending on mod load order it could replace Enchanting Infuser's built-in Apotheosis integration with one that used the wrong mod namespace (`apotheosis` instead of `apothic_enchanting`), leading to incorrect enchantment cost scaling. Enchanting Infuser's own, correct integration is now used.
