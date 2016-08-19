package com.kamesuta.mc.signpic.image;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ResourceImage extends Image {
	protected ResourceLocation location;

	protected ImageTextures texture;
	protected ImageState state = ImageState.INIT;

	public ResourceImage(final ResourceLocation location) {
		super(location.toString());
		this.location = location;
	}

	@Override
	public void init() {
		this.state = ImageState.LOADING;
	}

	@Override
	public void preload() {
		try {
			final IResourceManager manager = FMLClientHandler.instance().getClient().getResourceManager();
			this.texture = new ImageTextures(manager, this.location);
			this.state = ImageState.AVAILABLE;
		} catch (final Exception e) {
			this.state = ImageState.ERROR;
		}
	}

	@Override
	public float getProgress() {
		switch(this.state) {
		case AVAILABLE:
			return 1f;
		default:
			return 0;
		}
	}
	@Override
	public String getStatusMessage() {
		return I18n.format(this.state.msg, (int)getProgress()*100);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RemoteImage))
			return false;
		final RemoteImage other = (RemoteImage) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Image[%s]", this.id);
	}

	@Override
	public ImageState getState() {
		return this.state;
	}

	@Override
	public ImageTextures getTexture() {
		return this.texture;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getLocal() {
		return "Resource:"+this.location;
	}

	@Override
	public String advMessage() {
		return null;
	}

	@Override
	public void load() {
	}
}