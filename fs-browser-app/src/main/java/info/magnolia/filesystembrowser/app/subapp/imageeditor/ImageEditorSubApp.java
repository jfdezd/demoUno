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
package info.magnolia.filesystembrowser.app.subapp.imageeditor;

import info.magnolia.filesystembrowser.app.subapp.imageeditor.view.ImageEditorSubAppView;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.contentapp.detail.DetailLocation;
import info.magnolia.ui.framework.app.BaseSubApp;
import info.magnolia.ui.mediaeditor.MediaEditorPresenter;
import info.magnolia.ui.mediaeditor.MediaEditorPresenterFactory;
import info.magnolia.ui.mediaeditor.event.MediaEditorCompletedEvent;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Media Editor component within a sub-app.
 */
public class ImageEditorSubApp extends BaseSubApp<ImageEditorSubAppView> {

    private static final Logger log = LoggerFactory.getLogger(ImageEditorSubApp.class);

    private MediaEditorPresenterFactory mediaEditorFactory;

    private MediaEditorPresenter mediaEditorPresenter;

    private ContentConnector contentConnector;

    private ImageEditorSubAppView view;

    private InputStream inputStream;

    private File file;

    private MediaEditorCompletedEvent.Handler handler = new MediaEditorCompletedEvent.Handler() {
        @Override
        public void onSubmit(MediaEditorCompletedEvent event) {
            final InputStream is = event.getStream();
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
                IOUtils.copy(is, os);
            } catch (IOException e) {
                log.error("Failed to save file update: " + e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(os);
                getSubAppContext().close();
            }
        }

        @Override
        public void onCancel(MediaEditorCompletedEvent event) {
            IOUtils.closeQuietly(event.getStream());
            getSubAppContext().close();
        }
    };

    @Inject
    public ImageEditorSubApp(SubAppContext subAppContext, ImageEditorSubAppView view, MediaEditorPresenterFactory mediaEditorFactory, ContentConnector contentConnector) {
        super(subAppContext, view);
        this.view = view;
        this.mediaEditorFactory = mediaEditorFactory;
        this.contentConnector = contentConnector;
    }

    @Override
    public ImageEditorSubAppView start(Location location) {
        super.start(location);
        DetailLocation detailLocation = DetailLocation.wrap(location);

        this.file = (File) contentConnector.getItemIdByUrlFragment(detailLocation.getNodePath());
        this.mediaEditorPresenter = mediaEditorFactory.getPresenterById("ui-mediaeditor:image");

        try {
            this.inputStream = new FileInputStream(file);
            this.view.setMediaEditor(mediaEditorPresenter.start(inputStream));
            this.mediaEditorPresenter.addCompletionHandler(handler);
        } catch (FileNotFoundException e) {
            log.warn("File not found: " + e.getMessage(), e);
            getSubAppContext().close();
        }
        return view;
    }

    @Override
    protected void onSubAppStop() {
        super.onSubAppStop();
        IOUtils.closeQuietly(inputStream);
    }

    @Override
    public String getCaption() {
        if (file != null) {
            return file.getName();
        }
        return "Image Editor";
    }
}
