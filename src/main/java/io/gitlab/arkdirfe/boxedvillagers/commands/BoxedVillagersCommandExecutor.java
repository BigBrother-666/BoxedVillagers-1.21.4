package io.gitlab.arkdirfe.boxedvillagers.commands;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BoxedVillagersCommandExecutor implements TabExecutor
{
    private final BoxedVillagers plugin;

    public  BoxedVillagersCommandExecutor(BoxedVillagers plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
    {
        if(command.getName().equalsIgnoreCase("boxedvillagers"))
        {
            if(args.length > 0)
            {
                String subCmd = args[0];

                if(subCmd.equalsIgnoreCase("give") && sender.hasPermission(Strings.PERM_ADMIN))
                {
                    Player player = getPlayer((args.length == 2 || args.length == 3 || (args.length >= 2 && args[1].equalsIgnoreCase("trade"))), sender, args, 2);
                    if(player == null)
                    {
                        return true;
                    }

                    int slot = player.getInventory().firstEmpty();
                    if(slot < 0)
                    {
                        sender.sendMessage("Inventory Full!");
                    }
                    else
                    {
                        if(args[1].equalsIgnoreCase("unbound"))
                        {
                            player.getInventory().addItem(ItemUtil.getUnboundScroll(false));
                        }
                        else if (args[1].equalsIgnoreCase("unbound-nonlethal"))
                        {
                            player.getInventory().addItem(ItemUtil.getUnboundScroll(true));
                        }
                        else if(args[1].equalsIgnoreCase("trade"))
                        {
                            if(args.length == 7)
                            {
                                int slot1, slot2, slot3, uses, reduction;

                                try
                                {
                                    slot1 = Integer.parseInt(args[2]);
                                    slot2 = Integer.parseInt(args[3]);
                                    slot3 = Integer.parseInt(args[4]);
                                    uses = Integer.parseInt(args[5]);
                                    reduction = Integer.parseInt(args[6]);
                                }
                                catch (NumberFormatException e)
                                {
                                    player.sendMessage(Strings.ERROR_GIVE_TRADE_INVALID_SLOT);
                                    return true;
                                }

                                if(!(slot1 >= 0 && slot1 <= 8 && slot2 >= -1 && slot2 <= 8 && slot3 >= 0 && slot3 <= 8 && uses > 0 && reduction >= 0))
                                {
                                    player.sendMessage(Strings.ERROR_GIVE_TRADE_INVALID_SLOT);
                                }

                                ItemStack input1 = player.getInventory().getItem(slot1);
                                ItemStack input2 = slot2 == -1 ? new ItemStack(Material.AIR) : player.getInventory().getItem(slot2);
                                ItemStack output = player.getInventory().getItem(slot3);

                                MerchantRecipe trade = new MerchantRecipe(output, uses);
                                trade.addIngredient(input1);
                                if(slot2 != -1)
                                {
                                    trade.addIngredient(input2);
                                }

                                TradeData data = new TradeData(reduction, input1.getAmount(), trade);

                                player.getInventory().addItem(ItemUtil.convertExtractedToFree(ItemUtil.convertTradeToExtracted(ItemUtil.getTradeItem(data, true)))); // Yup
                            }
                            else
                            {
                                player.sendMessage("Usage: /bv give trade <hotbar slot of input 1> <hotbar slot of input 2 (or -1 for no input)> <hotbar slot of output> <uses> <reduction per cure>");
                            }
                        }
                        else
                        {
                            player.sendMessage("Invalid Item!");
                        }
                        return true;
                    }
                }
                else if (subCmd.equalsIgnoreCase("cure") && !sender.hasPermission(Strings.PERM_ADMIN))
                {
                    int numCures = 1;

                    Player player = getPlayer(args.length < 3, sender, args, 2);

                    if(player == null)
                    {
                        return true;
                    }

                    NBTItem nbtItem = Util.validateBoundItem(player.getInventory().getItemInMainHand());

                    if(nbtItem != null)
                    {
                        VillagerData data = new VillagerData(nbtItem);
                        if(data.getCures() == 7)
                        {
                            player.sendMessage("Already at max cures!");
                        }
                        else
                        {
                            if(args.length > 1)
                            {
                                numCures = Integer.parseInt(args[1]);
                            }

                            data.cure(nbtItem, numCures);
                            ItemStack item = data.writeToItem(nbtItem);
                            Util.updateBoundScrollTooltip(item, data);
                            player.getInventory().setItemInMainHand(item);
                            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.5f, 1);
                            player.sendMessage("Villager Cured!");
                        }
                    }
                    else
                    {
                        player.sendMessage("Invalid Item!");
                    }
                    return true;
                }
                else if(args[0].equalsIgnoreCase("help"))
                {
                    sender.sendMessage("Help pages are being worked on!");
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if(command.getName().equalsIgnoreCase("boxedvillagers"))
        {
            if(sender instanceof Player)
            {
                if(sender.hasPermission(Strings.PERM_ADMIN))
                {
                    if((args.length == 1 || args.length == 2))
                    {
                        if(args[0].equalsIgnoreCase("give"))
                        {
                            return Arrays.asList("unbound", "unbound-nonlethal", "trade");
                        }
                        else if (args[0].equalsIgnoreCase("cure"))
                        {
                            return Arrays.asList("1", "2", "3", "4", "5", "6", "7");
                        }
                    }

                    if (args.length >= 2 && args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("trade"))
                    {
                        return new ArrayList<>();
                    }
                }

                if(args.length > 0 && args[0].equalsIgnoreCase("help"))
                {
                    return new ArrayList<>(); // Expand once proper help pages are done.
                }

                if((args.length == 0 || args.length == 1))
                {
                    return Arrays.asList("give", "cure", "help");
                }
            }
        }

        if(args.length == 3 && !args[0].equalsIgnoreCase("help"))
        {
            return null;
        }

        return new ArrayList<>();
    }

    private Player getPlayer(boolean condition, CommandSender sender, String[] args, int playerIndex)
    {
        if(condition)
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage("Use /bv [arg1] [arg2] [player] to run as non-player!");
                return null;
            }

            return (Player)sender;
        }
        else
        {
            Player player = Bukkit.getServer().getPlayer(args[playerIndex]);
            if(player == null)
            {
                sender.sendMessage("Player offline!");
            }

            return player;
        }
    }
}
