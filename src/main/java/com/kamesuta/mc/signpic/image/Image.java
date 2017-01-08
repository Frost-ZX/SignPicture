package com.kamesuta.mc.signpic.image;

import static org.lwjgl.opengl.GL11.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kamesuta.mc.bnnwidget.render.OpenGL;
import com.kamesuta.mc.bnnwidget.render.WRenderer;
import com.kamesuta.mc.bnnwidget.render.WRenderer.BlendType;
import com.kamesuta.mc.signpic.Config;
import com.kamesuta.mc.signpic.ILoadCancelable;
import com.kamesuta.mc.signpic.attr.prop.SizeData;
import com.kamesuta.mc.signpic.entry.IAsyncProcessable;
import com.kamesuta.mc.signpic.entry.ICollectable;
import com.kamesuta.mc.signpic.entry.IDivisionProcessable;
import com.kamesuta.mc.signpic.entry.IInitable;
import com.kamesuta.mc.signpic.entry.content.Content;
import com.kamesuta.mc.signpic.state.StateType;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public abstract class Image implements IInitable, IAsyncProcessable, IDivisionProcessable, ICollectable, ILoadCancelable {
	protected final @Nonnull Content content;

	public Image(final @Nonnull Content content) {
		this.content = content;
	}

	public abstract @Nonnull ImageTexture getTexture() throws IllegalStateException;

	public abstract @Nonnull String getLocal();

	public @Nonnull SizeData getSize() {
		if (this.content.state.getType()==StateType.AVAILABLE)
			return getTexture().getSize();
		else
			return SizeData.DefaultSize;
	}

	public void draw(final float u, final float v, final float w, final float h, final float c, final float s, final @Nullable BlendType b, final @Nullable BlendType d, final boolean r, final boolean m) {
		if (this.content.state.getType()==StateType.AVAILABLE) {
			WRenderer.startTexture(b, d);
			final ImageTexture image = getTexture();
			image.bind();

			final int wraps = OpenGL.glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S);
			final int wrapt = OpenGL.glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T);
			final int mag = OpenGL.glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER);
			final int min = OpenGL.glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER);
			if (r) {
				OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
				OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			} else {
				OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
				OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
			}
			if (image.hasMipmap())
				if (m&&OpenGL.openGl30()&&Config.getConfig().renderUseMipmap.get()) {
					OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, Config.getConfig().renderMipmapTypeNearest.get() ? GL_NEAREST : GL_LINEAR);
					OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, Config.getConfig().renderMipmapTypeNearest.get() ? GL_NEAREST_MIPMAP_LINEAR : GL_LINEAR_MIPMAP_LINEAR);
				} else {
					OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
					OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				}
			WRenderer.w.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			WRenderer.w.pos(0, 0, 0).tex(u, v).endVertex();
			WRenderer.w.pos(0, 1, 0).tex(u, v+h/s).endVertex();
			WRenderer.w.pos(1, 1, 0).tex(u+w/c, v+h/s).endVertex();
			WRenderer.w.pos(1, 0, 0).tex(u+w/c, v).endVertex();
			WRenderer.t.draw();
			OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wraps);
			OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapt);
			if (image.hasMipmap()) {
				OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag);
				OpenGL.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min);
			}
			WRenderer.startTexture();
		}
	}
}