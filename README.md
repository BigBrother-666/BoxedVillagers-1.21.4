# Boxed Villagers

Boxed Villagers is a villager capturing and trading plugin made for the server EggMC, though it is also available for anyone else.

Boxed Villagers is built against the latest Spigot API.
Currently: 1.17.1

## Features
### Capturing Villagers
Villagers can be captured using Unbound Scrolls, capturing a villager removes the entity and stores all relevant data in the item. This eleminates the strain that villagers usually cause on servers (especially if multiple people build large trading halls).
These captured villager can be traded with and "restock" once a day like their counterparts.

### The Witch Doctor
The Witch Doctor is a custom GUI used to manipulate captured villagers. Possible operations:
1. Purchasing additional Unbound Scrolls.
2. Curing the villager, reducing prices (follows Vanilla rules, up to 7 cures).
3. Reordering trades.
4. Purging trades, which permanently removes them.
5. Extracting trades, which can be transferred to other captured villagers.
6. Purchasing additional trade slots (up to 27 total).
7. An Admin Mode where costs are disregarded.

### Player Commands
- `/boxedvillagers help [page]`: Accesses the plugin's help pages.
- `/boxedvillagers rename [name]`: Renames a held Bound Villager Scroll.
- `/witchdoctor`: Opens the Witch Doctor GUI.

### Admin Commands
- `/boxedvillagers give [item]`: Allows obtaining of Unbound Scrolls and an Admin variant which does not kill the villager.
- `/boxedvillagers give trade [input1] [input2] [output]`: Allows creation of trades, the command uses indices of hotbar slots to determine the components. It can also be used with just an input and an output.
- `/boxedvillagers reload`: Reloads configs and string overrides.
- `/witchdoctor admin`: Admin version of the regular GUI, all costs are free.

### Permissions
- `boxedvillagers.*`: Access to all permissions
- `boxedvillagers.witchdoctor`: `/witchdoctor` permission
- `boxedvillagers.witchdoctor.advanced`: Allows rearranging and purging of trades in the witchdoctor UI
- `boxedvillagers.witchdoctor.extract`: Allows extracting and re-inserting of trades in the witchdoctor UI
- `boxedvillagers.admin`: Allows use of /bv give and /bv cure and allows opening of the witchdoctor ui in admin mode (with costs disabled)

### Configurability
The costs for various operations are fully configurable, supporting basic resource prices as well as integration for economy using Vault. Most player-facing strings can be changed using the `strings.yml` file. For more information on configuration options, read the comments in `config.yml`.
