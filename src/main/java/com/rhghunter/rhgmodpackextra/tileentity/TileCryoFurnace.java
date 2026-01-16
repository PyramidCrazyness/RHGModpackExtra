package com.rhghunter.rhgmodpackextra.tileentity;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import com.rhghunter.rhgmodpackextra.recipes.CryoRecipes;

public class TileCryoFurnace extends TileEntity implements IEnergyReceiver {

    // Inventory Slots: 0 = Input, 1 = Output
    private ItemStack[] inventory = new ItemStack[2];

    // Machine State
    private int energyStored = 0;
    private int maxEnergy = 10000;
    public int progress = 0;
    public static final int PROCESS_TIME = 100; // 5 Seconds
    public static final int RF_PER_TICK = 20;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return; // Server Side Only

        ItemStack input = inventory[0];
        ItemStack output = inventory[1];

        // Can we process? (Has input, has power, output not full)
        ItemStack result = CryoRecipes.getResult(input);
        boolean canProcess = (result != null) &&
                                (energyStored >= RF_PER_TICK) &&
                                (output == null || (output.isItemEqual(result) && output.stackSize + result.stackSize <= output.getMaxStackSize()));

        // Process Logic
        if (canProcess) {
            energyStorage -= RF_PER_TICK;
            progress++;

            if (progress >= PROCESS_TIME) {
                smeltItem(result);
                progress = 0;
            }
        } else {
            progress = 0;
        }

        // Sync Triggers
        markDirty();
    }

    private void smeltItem(ItemStack result) {
        // Decrease Input
        if (this.inventory[0] != null) {
            this.inventory[0].stackSize--;
            if (this.inventory[0].stackSize <= 0) {
                this.inventory[0] = null;
            }
        }

        // Increase Output
        if (this.inventory[1] == null) {
            this.inventory[1] = result.copy();
        } else {
            this.inventory[1].stackSize += result.stackSize;
        }
    }

    // --- RF Implementation ---
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        int energyReceived = Math.min(maxEnergy - energyStorage, maxReceive);
        if (!simulate) {
            energyStorage += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) { return energyStorage; }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) { return maxEnergy; }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) { return true; }

    // --- NBT Saving (Crucial for saving data on restart) ---
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Energy", energyStorage);
        nbt.setInteger("Progress", progress);
        // Save inventory... (boilerplate omitted, use NBTTagList)
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energyStorage = nbt.getInteger("Energy");
        progress = nbt.getInteger("Progress");
        // Load inventory...
    }
}
