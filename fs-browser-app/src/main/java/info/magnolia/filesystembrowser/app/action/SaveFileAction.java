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
package info.magnolia.filesystembrowser.app.action;

import info.magnolia.event.EventBus;
import info.magnolia.filesystembrowser.app.contentconnector.FileItem;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.form.EditorCallback;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

/**
 * Saves file modifications on disk.
 * @param <T> definition type.
 */
public class SaveFileAction<T extends SaveFileActionDefinition> extends AbstractAction<T> {

    private FileItem item;

    private EditorCallback callback;

    private EventBus eventBus;

    public SaveFileAction(T definition, FileItem item, EditorCallback callback, @Named(SubAppEventBus.NAME) EventBus eventBus) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.eventBus = eventBus;
    }

    @Override
    public void execute() throws ActionExecutionException {
        item.save();
        callback.onSuccess("commit");
        if (item.isRenamed()) {
            Set<Object> ids = new HashSet<Object>();
            ids.add(item.getFile());
           // eventBus.fireEvent(new SelectionChangedEvent(ids));
        }
    }
}
