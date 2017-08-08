/*
 *                                                            _...---.._
 *                                                        _.'`       -_ ``.
 *                                                    .-'`                 `.
 *                                                 .-`                     q ;
 *                                              _-`                       __  \
 *                                          .-'`                  . ' .   \ `;/
 *                                      _.-`                    /.      `._`/
 *                              _...--'`                        \_`..._
 *                           .'`                         -         `'--:._
 *                        .-`                           \                  `-.
 *                       .                               `-..__.....----...., `.
 *                      '                   `'''---..-''`'              : :  : :
 *                    .` -                '``                           `'   `'
 *                 .-` .` '             .``
 *             _.-` .-`   '            .
 *         _.-` _.-`    .' '         .`
 * (`''--'' _.-`      .'  '        .'
 *  `'----''        .'  .`       .`
 *                .'  .'     .-'`    _____               _    _
 *              .'   :    .-`       |  __ \             | |  | |
 *              `. .`   ,`          | |__) |__ _  _ __  | |_ | |__    ___  _ __
 *               .'   .'            |  ___// _` || '_ \ | __|| '_ \  / _ \| '__|
 *              '   .`              | |   | (_| || | | || |_ | | | ||  __/| |
 *             '  .`                |_|    \__,_||_| |_| \__||_| |_| \___||_|
 *             `  '.
 *             `.___;
 */
package com.ayanix.panther.impl.utils.item;

import com.ayanix.panther.utils.item.IItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

/**
 * Panther - Developed by Lewes D. B.
 * All rights reserved 2017.
 */
public class ItemUtils implements IItemUtils
{

	@Override
	public String itemToString(@Nullable ItemStack item)
	{
		if (item == null)
		{
			return "";
		}

		String                    mat                   = item.getType().toString();
		short                     durability            = item.getDurability();
		String                    amount                = Integer.toString(item.getAmount());
		Map<Enchantment, Integer> enchants              = item.getEnchantments();
		StringBuilder             fullEnchantmentString = new StringBuilder();
		StringBuilder             itemString            = new StringBuilder(mat).append(' ').append(amount);

		if (durability != 0)
		{
			itemString = new StringBuilder(mat + ":" + durability + " " + amount);
		}

		if (item.hasItemMeta())
		{
			try
			{
				String displayName = item.getItemMeta().getDisplayName();
				displayName = displayName.replace(" ", "_");

				itemString.append(' ').append(displayName);
			} catch (NullPointerException ignored)
			{
			}

			try
			{
				List<String> lore = item.getItemMeta().getLore();

				for (String message : lore)
				{
					itemString.append(" lore:").append(message);
				}
			} catch (NullPointerException ignored)
			{
			}
		}

		Set<Map.Entry<Enchantment, Integer>> enchantSet = enchants.entrySet();

		for (Map.Entry<Enchantment, Integer> e : enchantSet)
		{
			Enchantment ench     = e.getKey();
			int         level    = e.getValue();
			String      enchName = ench.getName();

			fullEnchantmentString.append(' ').append(enchName).append(":").append(level);
		}

		if (fullEnchantmentString.length() != 0)
		{
			itemString.append(fullEnchantmentString);
		}

		return itemString.toString();
	}

	@Override
	public ItemStack stringToItem(@Nullable String item)
	{
		if (item == null ||
				item.isEmpty())
		{
			return new ItemStack(Material.AIR, 1);
		}

		String[]     itemSplit    = item.split(" ");
		List<String> itemWordList = Arrays.asList(itemSplit);

		String materialName = itemWordList.get(0);
		int    durability   = 0;

		if (materialName.contains(":"))
		{
			String[] materialData = materialName.split(":");
			materialName = materialData[0];
			durability = Integer.parseInt(materialData[1]);
		} else if (materialName.equalsIgnoreCase("POTION"))
		{
			durability = (short) 24576;
		}

		if (!isMaterial(materialName))
		{
			return new ItemStack(Material.AIR, 1);
		}

		Material mat = Material.valueOf(materialName.toUpperCase());

		int amount = 1;

		try
		{
			amount = Integer.parseInt(itemWordList.get(1));
		} catch (Exception ignored)
		{
		}

		String       name = null;
		List<String> lore = new ArrayList<>();

		// Potion
		PotionEffectType type      = null;
		boolean          splash    = false;
		boolean          extended  = false;
		int              duration  = 10;
		int              amplifier = 0;

		Map<Enchantment, Integer> enchantments = new HashMap<>();

		for (int x = 1; x < itemWordList.size(); x++)
		{
			String[] parts = itemWordList.get(x).split(":");

			switch (parts[0])
			{
				case "name":
					name = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', parts[1]);
					name = name.replaceAll("_", " ");

					continue;

				case "lore":
					String loreString = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', parts[1]);
					loreString = loreString.replaceAll("_", " ");
					lore.add(loreString);

					continue;

					// Potion
				case "effect":
					for (PotionEffectType pType : PotionEffectType.values())
					{
						if (pType.getName().equalsIgnoreCase(parts[1]))
						{
							type = pType;
							break;
						}
					}

					continue;

				case "power":
					try
					{
						amplifier = Integer.parseInt(parts[1]) - 1;
					} catch (Exception ignored)
					{
					}

					continue;

				case "duration":
					try
					{
						duration = Integer.parseInt(parts[1]);
					} catch (Exception ignored)
					{
					}

					continue;

				case "splash":
					try
					{
						splash = Boolean.valueOf(parts[1]);
					} catch (Exception ignored)
					{
					}

					continue;

				case "extended":
					try
					{
						extended = Boolean.valueOf(parts[1]);
					} catch (Exception ignored)
					{
					}

					continue;
				default:
					continue;
			}

			try
			{
				Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());

				if (enchantment == null)
				{
					continue;
				}

				int level = Integer.parseInt(parts[1]);

				enchantments.put(enchantment, level);
			} catch (Exception ignored)
			{
			}
		}

		ItemStack itemStack = new ItemStack(mat, amount, (short) durability);
		ItemMeta  itemMeta  = Bukkit.getItemFactory().getItemMeta(mat);

		if (name != null)
		{
			itemMeta.setDisplayName(name);
		}

		if (!lore.isEmpty())
		{
			itemMeta.setLore(lore);
		}

		itemStack.setItemMeta(itemMeta);

		if (mat == Material.POTION && type != null)
		{
			PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

			potionMeta.addCustomEffect(new PotionEffect(type, duration * 20, amplifier), extended);

			itemStack.setItemMeta(potionMeta);

			Potion potion = Potion.fromItemStack(itemStack);
			potion.setSplash(splash);
			potion.apply(itemStack);
		}

		itemStack.addUnsafeEnchantments(enchantments);

		return itemStack;
	}

	@Override
	public boolean isMaterial(@Nullable String materialName)
	{
		if (materialName == null)
		{
			return false;
		}

		try
		{
			Material.valueOf(materialName);
		} catch (IllegalArgumentException | NullPointerException exception)
		{
			return false;
		}

		return true;
	}

	@Override
	public boolean areItemsEqual(@Nullable ItemStack itemA, @Nullable ItemStack itemB)
	{
		if (itemA == null ||
				itemB == null)
		{
			return false;
		}

		if (itemA.getType() != itemB.getType())
		{
			return false;
		}

		if (itemA.getDurability() != itemB.getDurability())
		{
			return false;
		}

		ItemMeta metaA = itemA.hasItemMeta() ? itemA.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemA.getType());
		ItemMeta metaB = itemB.hasItemMeta() ? itemB.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemB.getType());

		if (metaA.getLore().size() != metaB.getLore().size())
		{
			return false;
		}

		for (int x = 0; x < Math.max(metaA.getLore().size(), metaB.getLore().size()); x++)
		{
			if (!metaA.getLore().get(x).equals(metaB.getLore().get(x)))
			{
				return false;
			}
		}

		return metaA.getDisplayName().equalsIgnoreCase(metaB.getDisplayName());
	}

	@Override
	public List<Material> getMaterialsContaining(String... str)
	{
		List<Material> matching = new ArrayList<>();

		for (String string : str)
		{
			for (Material material : Material.values())
			{
				if (material.name().toLowerCase().contains(string.toLowerCase()))
				{
					matching.add(material);
				}
			}
		}

		return matching;
	}

}