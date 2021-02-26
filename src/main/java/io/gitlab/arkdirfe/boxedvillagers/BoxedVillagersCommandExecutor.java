package io.gitlab.arkdirfe.boxedvillagers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class BoxedVillagersCommandExecutor implements CommandExecutor
{
    private final BoxedVillagers plugin;

    public  BoxedVillagersCommandExecutor(BoxedVillagers plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("boxedvillagers") || command.getName().equalsIgnoreCase("bv"))
        {
            if(args.length == 2)
            {
                if(!(sender instanceof Player)) // TODO: Add argument to be able to target player
                {
                    sender.sendMessage("Command only executable for players!");
                    return false;
                }

                Player player = (Player)sender;

                switch (args[0])
                {
                    case "give":
                    {
                        switch (args[1])
                        {
                            case "unbound":
                            {
                                int slot = player.getInventory().firstEmpty();
                                if(slot < 0)
                                {
                                    sender.sendMessage("Inventory Full!");
                                }
                                else
                                {
                                    giveUnboundScroll(player);
                                }
                                break;
                            }
                            default:
                            {
                                sender.sendMessage("Invalid Item!");
                                break;
                            }
                        }
                        return true;
                    }
                    case "cure":
                    {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        NBTItem nbtItem = new NBTItem(item);

                        if(nbtItem.getUUID(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM) != null && nbtItem.getBoolean(BoxedVillagers.TAG_IS_BOUND))
                        {
                            VillagerData data = new VillagerData(nbtItem);
                            if(data.cures == 7)
                            {
                                player.sendMessage("Already at max cures!");
                            }
                            else
                            {
                                data.cure(nbtItem, Integer.parseInt(args[1]));
                                data.writeToItem(nbtItem, false);
                                item = nbtItem.getItem();

                                Util.updateBoundScrollTooltip(item, data);

                                player.getInventory().setItemInMainHand(item);
                                player.sendMessage("Villager Cured!");
                            }
                        }
                        else
                        {
                            player.sendMessage("Invalid Item!");
                        }
                        return true;
                    }
                    default:
                    {
                        sender.sendMessage("Unknown Sub-Command!");
                        break;
                    }
                }
            }
        }

        return false;
    }

    private void giveUnboundScroll(Player player)
    {
        ItemStack scroll = new ItemStack(Material.PAPER);
        ItemMeta meta = scroll.getItemMeta();
        meta.setDisplayName("§aUnbound Villager Scroll");
        meta.setLore(Arrays.asList("§r§fRight click on a villager to §4§mensnare its mortal soul§r§f capture it.",
                "§r§fCaptured villagers do not benefit from previous cures or",
                "§r§fHero of the Village and can not unlock additional trades."));
        scroll.setItemMeta(meta);

        NBTItem nbtscoll = new NBTItem(scroll);
        nbtscoll.setUUID(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM, UUID.randomUUID());
        nbtscoll.setBoolean(BoxedVillagers.TAG_IS_BOUND, false);
        scroll = nbtscoll.getItem();

        player.getInventory().addItem(scroll);
    }
}
