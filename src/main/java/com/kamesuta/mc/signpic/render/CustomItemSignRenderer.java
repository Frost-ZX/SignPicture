package com.kamesuta.mc.signpic.render;

import static org.lwjgl.opengl.GL11.*;

import com.kamesuta.mc.signpic.attr.CompoundAttr;
import com.kamesuta.mc.signpic.attr.prop.SizeData;
import com.kamesuta.mc.signpic.attr.prop.RotationData.RotationGL;
import com.kamesuta.mc.signpic.attr.prop.SizeData.ImageSizes;
import com.kamesuta.mc.signpic.entry.Entry;
import com.kamesuta.mc.signpic.entry.EntryId;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class CustomItemSignRenderer implements IItemRenderer {
	@Override
	public boolean handleRenderType(final ItemStack item, final ItemRenderType type) {
		if (item.getItem()!=Items.sign)
			return false;
		return EntryId.fromItemStack(item).entry().isValid();
	}

	@Override
	public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item, final ItemRendererHelper helper) {
		return type==ItemRenderType.ENTITY;
	}

	@Override
	public void renderItem(final ItemRenderType type, final ItemStack item, final Object... data) {
		OpenGL.glPushMatrix();
		OpenGL.glPushAttrib();
		OpenGL.glDisable(GL_CULL_FACE);
		final Entry entry = EntryId.fromItemStack(item).entry();
		final CompoundAttr meta = entry.getMeta();
		// Size
		final SizeData size = meta.sizes.getMovie().get().aspectSize(entry.content().image.getSize());
		if (type==ItemRenderType.INVENTORY) {
			final float slot = 16f;
			final SizeData size2 = ImageSizes.INNER.defineSize(size, slot, slot);
			OpenGL.glTranslatef((slot-size2.getWidth())/2f, (slot-size2.getHeight())/2f, 0f);
			OpenGL.glScalef(slot, slot, 1f);
			entry.gui.drawScreen(0, 0, 0f, 1f, size2.getWidth()/slot, size2.getHeight()/slot);
		} else {
			if (type==ItemRenderType.ENTITY) {
				if (RenderItem.renderInFrame) {
					OpenGL.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					OpenGL.glTranslatef(0f, 0.025f, 0f);
					OpenGL.glScalef(1.6F, -1.6F, 1f);
					final float f = 0.0078125F; // vanilla map offset
					OpenGL.glTranslatef(-size.getWidth()/2f, -.5f, f*4);
				} else {
					OpenGL.glRotatef(180f, 1f, 0f, 0f);
					OpenGL.glScalef(2f, 2f, 1f);
					OpenGL.glTranslatef(.5f, -1f, 0f);
					OpenGL.glScalef(-1f, 1f, 1f);
					OpenGL.glTranslatef(-(size.getWidth()-1f)/2f, .125f, 0f);
				}
			} else {
				OpenGL.glScalef(2f, 2f, 1f);
				OpenGL.glTranslatef(.5f, 1f, 0f);
				OpenGL.glScalef(-1f, -1f, 1f);
			}
			OpenGL.glTranslatef(0f, 1f-size.getHeight(), 0f);
			OpenGL.glTranslatef(meta.xoffsets.getMovie().get().offset, -meta.yoffsets.getMovie().get().offset, meta.zoffsets.getMovie().get().offset);
			RotationGL.glRotate(meta.rotations.getMovie().get().getRotate());
			entry.gui.drawScreen(0, 0, 0f, 1f, size.getWidth(), size.getHeight());
		}
		OpenGL.glPopAttrib();
		OpenGL.glPopMatrix();
	}
}