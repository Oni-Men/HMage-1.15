package onim.en.hmage.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants.NBT;

public class NBTUtils {

  public static List<String> getLore(ItemStack stack) {
    if (stack == null)
      return null;

    CompoundNBT root = stack.getTag();

    if (root == null)
      return null;

    CompoundNBT display = root.getCompound("Display");

    if (display == null)
      return null;

    ListNBT lore = display.getList("Lore", NBT.TAG_STRING);

    if (lore == null)
      return null;

    ArrayList<String> list = new ArrayList<String>();

    for (Iterator<INBT> itr = lore.iterator(); itr.hasNext();) {
      INBT tag = itr.next();

      list.add(tag.toString());
    }

    return list;
  }

}
