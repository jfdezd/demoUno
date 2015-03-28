/**
 * This file Copyright (c) 2014 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is licensed under the MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package info.magnolia.filesystembrowser.app.contentconnector;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.AbstractContentConnector;
import info.magnolia.ui.vaadin.integration.contentconnector.SupportsCreation;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * {@link info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector} implementation
 * which works over file system folder.
 */
public class FileSystemContentConnector extends AbstractContentConnector implements SupportsCreation {

    private Logger log = LoggerFactory.getLogger(FileSystemContentConnector.class);

    private FSContentConnectorDefinition definition;

    @Inject
    public FileSystemContentConnector(FSContentConnectorDefinition definition, ComponentProvider componentProvider) {
        super(definition, componentProvider);
        this.definition = definition;
    }

    public String getRootFolder() {
        return definition.getRootFolder();
    }

    @Override
    public String getItemUrlFragment(Object itemId) {
        if (itemId instanceof File) {
            String rootPath = ((File) getDefaultItemId()).getAbsolutePath();
            File file = (File)itemId;
            return file.getAbsolutePath().replace(rootPath, "");
        }
        return null;
    }

    @Override
    public Object getItemIdByUrlFragment(String urlFragment) {
        return new File(getRootFolder() + urlFragment);
    }

    @Override
    public Item getItem(Object itemId) {
        return new FileItem((File) itemId);
    }

    @Override
    public Object getItemId(Item item) {
        if (item instanceof FileItem) {
            return ((FileItem)item).getFile();
        }
        return null;
    }

    @Override
    public Object getDefaultItemId() {
        return new File(definition.getRootFolder());
    }

    @Override
    public boolean canHandleItem(Object itemId) {
        return (itemId instanceof File);
    }

    @Override
    public Object getNewItemId(Object parentId, Object typeDefinition) {
        File newFile = new File(definition.getRootFolder() + parentId);
        try {
            return newFile.createNewFile();
        } catch (IOException e) {
            log.error("Failed to create a new file.", e);
            return null;
        }
    }
}
