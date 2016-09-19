package com.kamesuta.mc.signpic.entry;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.kamesuta.mc.signpic.util.Downloader;

import net.minecraft.client.resources.I18n;

public class EntryDownloader implements ILoadEntry {
	protected final EntryLocation location;
	protected final EntryPath path;
	protected final EntryState state;

	public EntryDownloader(final EntryLocation location, final EntryPath path, final EntryState state) {
		this.location = location;
		this.path = path;
		this.state = state;
	}

	@Override
	public void onProcess() {
		InputStream input = null;
		CountingOutputStream countoutput = null;
		this.state.setType(EntryStateType.LOADING);
		try {
			final File local = this.location.localLocation(this.path);
			if (!local.exists()) {
				final HttpUriRequest req = new HttpGet(this.location.remoteLocation(this.path));
				final HttpResponse response = Downloader.downloader.client.execute(req);
				final HttpEntity entity = response.getEntity();

				this.state.progress.overall = entity.getContentLength();
				input = entity.getContent();
				countoutput = new CountingOutputStream(new BufferedOutputStream(new FileOutputStream(local))) {
					@Override
					protected void afterWrite(final int n) throws IOException {
						EntryDownloader.this.state.progress.done = getByteCount();
					}
				};
				IOUtils.copy(input, countoutput);
			}
			this.state.setType(EntryStateType.LOADED);
		} catch (final URISyntaxException e) {
			this.state.setType(EntryStateType.ERROR);
			this.state.setMessage(I18n.format("signpic.advmsg.invaildurl"));
		} catch (final Exception e) {
			this.state.setType(EntryStateType.ERROR);
			this.state.setMessage(I18n.format("signpic.advmsg.dlerror", e));
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(countoutput);
		}
	}
}
