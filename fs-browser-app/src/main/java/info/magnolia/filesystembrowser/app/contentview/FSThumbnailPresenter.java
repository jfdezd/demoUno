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
package info.magnolia.filesystembrowser.app.contentview;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.workbench.thumbnail.ThumbnailContainer;
import info.magnolia.ui.workbench.thumbnail.ThumbnailPresenter;
import info.magnolia.ui.workbench.thumbnail.ThumbnailView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.vaadin.data.Container;

/**
 * Extension of {@link ThumbnailPresenter} which connects to file system instead of JCR.
 */
public class FSThumbnailPresenter extends ThumbnailPresenter {

    @Inject
    public FSThumbnailPresenter(ThumbnailView view, ImageProvider imageProvider, ComponentProvider componentProvider) {
        super(view, imageProvider, componentProvider);
    }

    @Override
    public Container initializeContainer() {
        ThumbnailContainer c = new ThumbnailContainer(getImageProvider(), new ThumbnailContainer.IdProvider() {
            @Override
            public List<Object> getItemIds() {
                List<Object> ids = new ArrayList<Object>();
                File rootFolder = (File) contentConnector.getDefaultItemId();
                if (rootFolder.isDirectory()) {
                    ids.addAll(populateFilesRecursively(rootFolder));
                }
                return ids;
            }

            private List<Object> populateFilesRecursively(File rootFolder) {
                List<Object> ids = new ArrayList<Object>();
                File[] files = rootFolder.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        ids.addAll(populateFilesRecursively(file));
                    } else {
                        ids.add(file);
                    }
                }
                return ids;
            }
        });
        c.setThumbnailHeight(73);
        c.setThumbnailWidth(73);
        return c;
    }
}
