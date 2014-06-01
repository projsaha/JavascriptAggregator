/*
 * (C) Copyright 2012, IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.jaggr.web.impl.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ibm.jaggr.core.resource.IResource;
import com.ibm.jaggr.core.resource.IResourceVisitor;

/**
 * An implementation of {@link IResource} for File resources that does not rely
 * on java.nio
 */
public class JarResource implements IResource {
	static final Logger log = Logger.getLogger(JarResource.class.getName());

	final String zipEntryUri;
	final ArrayList<String> zipFileEntries;
	URL jarUrl;
	URI jarUri;
	String jarName;
	String zipFileEntry;
	ZipEntry zipEntry;
	ZipFile zipFile;

	/**
	 * Public constructor used by factory
	 * 
	 * @param uri
	 *            the resource URI
	 */
	public JarResource(String entry, List<String> entries) {
		zipEntryUri = entry;
		zipFileEntries = (ArrayList<String>) entries;
		try {
			if (entry.startsWith("file:") && entry.contains("!")) {
				String[] split = entry.split("!");
				jarUrl = new URL(split[0]);
				jarUri = new URI(split[0]);
				jarName = split[0].substring(split[0].lastIndexOf("/") + 1);
				zipFileEntry = split[1].substring(1); // need to remove the
														// forward slash

				zipFile = new ZipFile(new File(jarUri));
				zipEntry = zipFile.getEntry(zipFileEntry);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.jaggr.service.modules.Resource#getURI()
	 */
	@Override
	public URI getURI() {
		URI uri = null;
		try {
			uri = new URI("jar:" + zipEntryUri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	public String getPath() {
		return "jar:///" + zipFileEntry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.jaggr.service.modules.Resource#exists()
	 */
	@Override
	public boolean exists() {
		return lastModified() != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.jaggr.service.modules.Resource#lastModified()
	 */
	@Override
	public long lastModified() {
		long lastmod = 0L;
		try {
			lastmod = getURI().toURL().openConnection().getLastModified();
		} catch (IOException e) {
		}
		return lastmod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.jaggr.service.resource.IResource#resolve(java.net.URI)
	 */
	@Override
	public IResource resolve(String relative) {
		String jarUriString, finaljarString = null;
		URI fileUri, finalFileUri = null;
		JarResource jarResource = null;
		try {
			jarUriString = getURI().toString();
			if (jarUriString.startsWith("jar:")) {
				jarUriString = jarUriString.substring(4);
			}

			fileUri = new URI(jarUriString).resolve(relative);
			finaljarString = "jar:/" + fileUri;
			finalFileUri = new URI(finaljarString);

		} catch (Exception e) {

		}
		if (finalFileUri == null)
			jarResource = newInstance(getURI().resolve(relative));
		else
			jarResource = newInstance(finalFileUri);
		return jarResource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.jaggr.service.modules.Resource#getReader()
	 */
	@Override
	public Reader getReader() throws IOException {
		return new InputStreamReader(getURI().toURL().openConnection()
				.getInputStream(), "UTF-8"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.jaggr.service.resource.IResource#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return getURI().toURL().openConnection().getInputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.jaggr.service.modules.Resource#walkTree(com.ibm.jaggr.modules
	 * .ResourceVisitor, boolean)
	 */
	@Override
	public void walkTree(IResourceVisitor visitor) throws IOException {
		if (!exists()) {
			throw new FileNotFoundException(zipEntry.getName());
		}
		if (!zipEntry.isDirectory()) {
			return;
		}
		recurse(zipEntry, visitor, ""); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.jaggr.service.resource.IResource#asVisitorResource()
	 */
	@Override
	public VisitorResource asVisitorResource() throws IOException {
		if (!exists()) {
			throw new IOException(zipEntry.getName());
		}
		return new VisitorResource(zipEntry, zipEntryUri);
	}

	/**
	 * Internal method for recursing sub directories.
	 * 
	 * @param file
	 *            The file object
	 * @param visitor
	 *            The {@link IResourceVisitor} to call for non-folder resources
	 * @throws IOException
	 */
	private void recurse(ZipEntry file, IResourceVisitor visitor, String path)
			throws IOException {
		ArrayList<ZipEntry> files = getChildZipEntries(file);

		if (files == null) {
			return;
		}
		for (ZipEntry child : files) {
			String childName = child.getName().substring(
					file.getName().length());
			String childEntryUri = zipEntryUri + childName;
			System.out.println("zipFile " + zipFile + " child " + child);

			visitor.visitResource(new VisitorResource(child, childEntryUri),
					childName);
		}
	}

	protected JarResource newInstance(URI uri) {
		return (JarResource) new JarResourceFactory().newResource(uri);
	}

	private ArrayList<ZipEntry> getChildZipEntries(ZipEntry entry) {

		String entryName = entry.getName();

		ArrayList<ZipEntry> files = new ArrayList<ZipEntry>();
		for (int i = 0; i < zipFileEntries.size(); i++) {
			if (zipFileEntries.get(i).startsWith(entryName)
					&& !(zipFileEntries.get(i).equalsIgnoreCase(entryName))
					&& zipFileEntries.get(i).endsWith(".js")) {
				files.add(zipFile.getEntry(zipFileEntries.get(i)));
			}
		}

		return files;
	}

	/**
	 * Implementation of {@link IResourceVisitor.Resource} for files.
	 */
	private static class VisitorResource implements IResourceVisitor.Resource {

		ZipEntry entry;
		String ZipFileUri;

		private VisitorResource(ZipEntry entry, String uri) {
			this.entry = entry;
			this.ZipFileUri = uri;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.jaggr.service.resource.IResourceVisitor.Resource
		 * #isFolder()
		 */
		@Override
		public boolean isFolder() {
			if (entry == null) {
				return false;
			}
			return entry.isDirectory();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.jaggr.service.modules.ResourceVisitor.Resource# getURI()
		 */
		@Override
		public URI getURI() {
			URI uri = null;
			try {
				uri = new URI("jar:" + ZipFileUri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.jaggr.service.modules.ResourceVisitor.Resource#
		 * getPath()
		 */
		@Override
		public String getPath() {
			return ZipFileUri;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.jaggr.service.modules.ResourceVisitor.Resource#
		 * lastModified()
		 */
		@Override
		public long lastModified() {
			long lastmodified = 0;
			try {
				lastmodified = getURI().toURL().openConnection()
						.getLastModified();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return lastmodified;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.jaggr.service.modules.ResourceVisitor.Resource#
		 * getReader()
		 */
		@Override
		public Reader getReader() throws IOException {
			return new InputStreamReader(getURI().toURL().openConnection()
					.getInputStream(), "UTF-8");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.ibm.jaggr.service.resource.IResourceVisitor.Resource#getInputStream
		 * ()
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			return getURI().toURL().openConnection().getInputStream();
		}
	}

}
