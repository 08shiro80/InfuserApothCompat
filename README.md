# Infuser Apotheosis Compat

Enhanced compatibility between [Enchanting Infuser](https://www.curseforge.com/minecraft/mc-mods/enchanting-infuser-forge) and [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) mods for Minecraft 1.20.1.

## Features

- **Full Bookshelf Support**: Apotheosis bookshelves provide their full Eterna bonus to the Enchanting Infuser
- **Max Eterna Scaling**: Special bookshelves (Dragon's Breath Endshelf, Melonshelf, etc.) increase the maximum enchanting power
- **Higher Enchantment Levels**: Access Apotheosis's enhanced enchantment levels through the Infuser
- **Infusion Recipe Support**: Apotheosis infusion recipes (Hellshelf → Infused Hellshelf, etc.) work in the Enchanting Infuser
- **Proper Treasure Handling**: Correct handling of treasure and discoverable enchantments

## Requirements

- Minecraft 1.20.1
- Forge 47.0.0+
- Apotheosis 7.x
- Enchanting Infuser 8.x
- Placebo (required by Apotheosis)
- PuzzlesLib (required by Enchanting Infuser)

## Installation

1. Install all required mods
2. Download the latest release from the releases page
3. Place the JAR file in your `mods` folder

## How It Works

This mod registers a custom `EnchantStatsProvider` with higher priority than the built-in Apotheosis integration:

1. Uses Apotheosis's `EnchantingStatRegistry.getEterna()` for bookshelf power calculation
2. Uses `EnchantingStatRegistry.getMaxEterna()` for maximum power scaling
3. Uses `EnchModule.getEnchInfo()` for enchantment properties
4. Enables Apotheosis's enhanced enchantment levels in the Infuser

### Infusion Recipes

The mod also adds support for Apotheosis infusion recipes via Mixin:
- Place an infusion recipe input (e.g., Hellshelf) in the Enchanting Infuser
- Meet the Eterna/Quanta/Arcana requirements from surrounding bookshelves
- The item will be infused when you click enchant

## Building from Source

```bash
./gradlew build
```

The built JAR will be in `build/libs/`

## License

MIT License - see [LICENSE](LICENSE)
