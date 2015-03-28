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

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * An {@link com.vaadin.data.Item} for filesystem "objects".
 */
public class FileItem extends PropertysetItem {

    public static String PROPERTY_NAME = "name";

    public static String PROPERTY_SIZE = "size";

    public static String PROPERTY_LASTMODIFIED = "lastModified";

    public static Collection<String> FILE_PROPERTIES;

    private final static Method FILEITEM_LASTMODIFIED;

    private final static Method FILEITEM_SIZE;

    static {

        FILE_PROPERTIES = new ArrayList<String>();
        FILE_PROPERTIES.add(PROPERTY_NAME);
        FILE_PROPERTIES.add(PROPERTY_SIZE);
        FILE_PROPERTIES.add(PROPERTY_LASTMODIFIED);
        FILE_PROPERTIES = Collections.unmodifiableCollection(FILE_PROPERTIES);
        try {
            FILEITEM_LASTMODIFIED = FileItem.class.getMethod("getLastModified", new Class[] {});
            FILEITEM_SIZE = FileItem.class.getMethod("getSize", new Class[] {});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in FilesystemContainer");
        }
    }

    private FileContentProperty dataProperty;

    private ObjectProperty<String> nameProperty;

    private File file;

    private boolean isRenamed = false;

    public FileItem(File file) {
        this.file = file;
        this.dataProperty = new FileContentProperty(file);
        this.nameProperty = new ObjectProperty(file.getName());
        addItemProperty("data", dataProperty);

        addItemProperty(PROPERTY_LASTMODIFIED, new MethodProperty(Date.class, this, FILEITEM_LASTMODIFIED, null));
        addItemProperty(PROPERTY_SIZE, new MethodProperty(String.class, this, FILEITEM_SIZE, null));
        addItemProperty(PROPERTY_NAME, nameProperty);
        dataProperty.startTransaction();
    }

    public void save() {
        dataProperty.commit();
        if (!file.getName().equalsIgnoreCase(nameProperty.getValue())) {
            File newFile = new File(file.getParent(), nameProperty.getValue());
            isRenamed = file.renameTo(newFile);
            if (isRenamed) {
                file = newFile;
            }
        }
    }

    public void cancel() {
        dataProperty.rollback();
    }

    public File getFile() {
        return file;
    }

    public Date getLastModified() {
        return new Date(file.lastModified());
    }

    public String getSize() {
        return readableFileSize(file.length());
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public boolean isRenamed() {
        return isRenamed;
    }
}
