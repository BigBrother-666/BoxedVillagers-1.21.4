# Boxed Villagers

[![Discord](https://img.shields.io/discord/364107873267089409.svg?logo=discord)](https://discord.gg/fr5H9dS)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=eggmc_boxedvillagers&metric=ncloc)](https://sonarcloud.io/dashboard?id=eggmc_boxedvillagers)

Boxed Villagers is a villager capturing and trading plugin for the server EggMC.

Boxed Villagers is built against the latest Spigot API.
Currently: 1.16.5

## Features
### Capturing Villagers
Villagers can be captured using Unbound Scrolls, capturing a villager removes the entity and stores all relevant data in the item.
This eleminates the strain that villagers usually cause on servers (especially if multiple people build large trading halls).
These captured villager "restock" once a day like their counterparts.

### The Witch Doctor
The Witch Doctor is a custom GUI used to manipulate captured villagers. Possible operations:
1. Purchasing additional Unbound Scrolls.
2. Curing the villager, reducing prices.
3. Reordering trades.
4. Purging trades.
5. Extracting trades, which can be transferred to other captured villagers.
6. Purchasing additional trade slots (up to 27 total).
7. An Admin Mode where costs are disregarded.

### Configurability
The costs for various operations are fully configurable, supporting basic resource prices as well as integration for economy.
Arbitrary help pages can be added via the config.
