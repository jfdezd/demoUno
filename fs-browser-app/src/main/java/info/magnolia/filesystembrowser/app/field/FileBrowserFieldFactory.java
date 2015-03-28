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
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.mediaeditor.MediaEditorPresenterFactory;

import javax.inject.Inject;

import com.vaadin.data.Item;
import com.vaadin.ui.Field;

/**
 * Creates {@link FileBrowserField}.
 * @param <D> definition type.
 */
public class FileBrowserFieldFactory<D extends FileBrowserFieldDefinition> extends AbstractFieldFactory<D, byte[]> {

    private UiContext uiContext;

    private MediaEditorPresenterFactory mediaEditorPresenterFactory;

    @Inject
    public FileBrowserFieldFactory(D definition, Item relatedFieldItem, UiContext uiContext, MediaEditorPresenterFactory mediaEditorPresenterFactory) {
        super(definition, relatedFieldItem);
        this.uiContext = uiContext;
        this.mediaEditorPresenterFactory = mediaEditorPresenterFactory;
    }

    @Override
    protected Field<byte[]> createFieldComponent() {
        return new FileBrowserField(uiContext, mediaEditorPresenterFactory);
    }

    protected Class<?> getFieldType() {
        return byte[].class;
    }
}
