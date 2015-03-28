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
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.api.event.ContentChangedEvent;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Item;

/**
 * Duplicates file in file system.
 * @param <D> the definition.
 */
public class DuplicateFileAction<D extends DuplicateFileActionDefinition> extends AbstractAction<D> {

    private final ContentConnector contentConnector;

    private SimpleTranslator translator;

    private final EventBus eventBus;

    private UiContext uiContext;

    private Item item;

    @Inject
    public DuplicateFileAction(UiContext uiContext, D definition, Item item, ContentConnector contentConnector, @Named(AdmincentralEventBus.NAME)EventBus eventBus, SimpleTranslator translator) {
        super(definition);
        this.uiContext = uiContext;
        this.item = item;
        this.contentConnector = contentConnector;
        this.eventBus = eventBus;
        this.translator = translator;
    }

    @Override
    public void execute() throws ActionExecutionException {
        Object itemId = contentConnector.getItemId(item);
        if (itemId instanceof File) {
            File file = (File)itemId;
            File copy = createFileCopy(file);
            try {
                if (file.isFile()) {
                    FileUtils.copyFile(file, copy);
                } else {
                    FileUtils.copyDirectory(file, copy);
                }

                eventBus.fireEvent(new ContentChangedEvent(file));
            } catch (IOException e) {
                String message = translator.translate("ui-framework.abstractcommand.executionfailure");
                uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, message);
            }
        }
    }

    private File createFileCopy(File file) {
        String extension = StringUtils.substringAfterLast(file.getPath(), ".");
        extension = StringUtils.isEmpty(extension) ? "" : "." + extension;
        String newName = file.getPath().replace(extension, "") + " copy";
        File copyFile = new File(newName + extension);
        int idx = 0;
        while (copyFile.exists()) {
            copyFile = new File(newName + ++idx + extension );
        }
        return copyFile;
    }
}
