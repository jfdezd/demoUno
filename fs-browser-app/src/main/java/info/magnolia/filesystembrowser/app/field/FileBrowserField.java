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
package info.magnolia.filesystembrowser.app.field;

import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.overlay.OverlayCloser;
import info.magnolia.ui.mediaeditor.MediaEditorPresenter;
import info.magnolia.ui.mediaeditor.MediaEditorPresenterFactory;
import info.magnolia.ui.mediaeditor.event.MediaEditorCompletedEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

/**
 * A {@link CustomField}.
 */
public class FileBrowserField extends CustomField<byte[]> {

    private Logger log = LoggerFactory.getLogger(FileBrowserField.class);

    private Image image = new Image();

    private VerticalLayout layout = new VerticalLayout();
    private UiContext uiContext;
    private MediaEditorPresenterFactory mediaEditorFactory;

    public FileBrowserField(UiContext uiContext, MediaEditorPresenterFactory mediaEditorFactory) {
        this.uiContext = uiContext;
        this.mediaEditorFactory = mediaEditorFactory;
    }

    @Override
    protected Component initContent() {
        layout.setWidth("100%");
        layout.addComponent(image);
        layout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
        Button editButton = new Button("Edit", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                openMediaEditor();
            }
        });
        editButton.setWidth("100%");
        layout.addComponent(editButton);
        layout.setExpandRatio(image, 1f);
        return layout;
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        if (newDataSource instanceof Transactional) {
            ((Transactional) newDataSource).startTransaction();
        }
    }

    private void openMediaEditor() {
        MediaEditorPresenter mediaEditorPresenter = mediaEditorFactory.getPresenterById("ui-mediaeditor:image");
        final InputStream inputStream = new ByteArrayInputStream(getValue());
        final OverlayCloser overlayCloser = uiContext.openOverlay(mediaEditorPresenter.start(inputStream));
        mediaEditorPresenter.addCompletionHandler(new MediaEditorCompletedEvent.Handler() {
            @Override
            public void onSubmit(MediaEditorCompletedEvent event) {
                final InputStream is = event.getStream();
                try {
                    setValue(IOUtils.toByteArray(is));
                } catch (IOException e) {
                    log.error("Failed to submit changes done with mediaEditor", e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
                overlayCloser.close();
            }

            @Override
            public void onCancel(MediaEditorCompletedEvent event) {
                overlayCloser.close();
                IOUtils.closeQuietly(event.getStream());
            }
        });
    }

    @Override
    protected void setInternalValue(final byte[] newValue) {
        super.setInternalValue(newValue);
        image.setSource(new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                return new ByteArrayInputStream(newValue);
            }
        }, "filefile" + System.nanoTime() + ".png"));
    }

    @Override
    public Class<? extends byte[]> getType() {
        return byte[].class;
    }
}
