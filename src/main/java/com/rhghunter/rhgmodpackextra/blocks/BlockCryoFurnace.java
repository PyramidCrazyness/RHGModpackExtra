package com.rhghunter.rhgmodpackextra.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.rhghunter.rhgmodpackextra.tileentity.TileCryoFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class BlockCryoFurnace extends BlockContainer {

    // Store the icons
    @SideOnly(side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;

    protected BlockCryoFurnace() {
        super(Material.iron);
        setBlockName("cryo_furnace");
        setCreativeTab(RHGModpackExtra.RHGME); // This is my creative tab name
        setHardness(3.5F);
    }

    // Load the textures
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        // "rhgmodpackextra:filename" looks in assets/rhgmodpackextra/texures/blocks/
        this.iconFront = iconRegister.registerIcon("rhgmodpackextra:cryo_furnace_front");
        this.iconSide = iconRegister.registerIcon("rhgmodpackextra:cryo_furnace_side");
        this.iconTop = iconRegister.registerIcon("rhgmodpackextra:cryo_furnace_top");
    }

    // Define which texture goes where
    // Side: 0=bottom, 1=top, 2=north, 3=south, 4=west, 5=east
    // meta: This direction the block is actually facing (storec in the world)
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        // Render the Top and Bottom
        if (side == 0 || side == 1) {
            return iconTop;
        }

        // If the side we are rendering it the front side, return face.
        // If meta is 0, usually inply side 3 which is the south side and is the front
        if (meta == 0 && side == 3) return iconFront;

        if (side == meta ) {
            return iconFront;
        }

        // Otherwise, return the generic side
        return iconSide;
    }

    // Handle rotation on placement
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        // Math to calculate which way the player is looking
        int l = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        // Convert player rotation to block metadata
        if (l = 0) world.setBlockMetaDataWithNotify(x, y, z, 2, 2); // Face North
        if (l == 1) world.setBlockMetadataWithNotify(x, y, z, 5, 2); // Face East
        if (l == 2) world.setBlockMetadataWithNotify(x, y, z, 3, 2); // Face South
        if (l == 3) world.setBlockMetadataWithNotify(x, y, z, 4, 2); // Face West
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCryoFurnace();
    }

    // Called when the block right-clicked
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RHGModpackExtra.instance, RHGModpackExtra.GUI_CRYO_FURNACE, world, x, y, z);
        }
        return true;
    }
}
