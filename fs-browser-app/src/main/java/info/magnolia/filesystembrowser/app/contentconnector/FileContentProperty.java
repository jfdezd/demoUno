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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;

/**
 * Special property which allows for accessing and modifying file contents.
 * The access is lazy and modifications are done in transaction, so they can be buffered and/or discarded.
 */
public class FileContentProperty implements Property.Transactional<byte[]> {

    private static final Logger log = LoggerFactory.getLogger(FileContentProperty.class);

    private File file;

    private boolean transactionInitialized = false;

    private byte[] bufferedValue;

    private byte[] currentValue;

    public FileContentProperty(File file) {
        this.file = file;
    }

    private byte[] getBytes(File file) {
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                return IOUtils.toByteArray(fis);
            } catch (FileNotFoundException e) {
                log.error("File not {1} found: ", file.getPath());
            } catch (IOException e) {
                log.error("I/O issue {1} encountered: ", e.getMessage());
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }
        return new byte[0];
    }

    @Override
    public byte[] getValue() {
        if (currentValue == null) {
            currentValue = getBytes(file);
        }
        return currentValue;
    }

    @Override
    public void setValue(byte[] newValue) throws ReadOnlyException {
        if (!transactionInitialized) {
            bufferedValue = getValue();
            transactionInitialized = true;
        }
        this.currentValue = newValue;
    }

    @Override
    public Class<? extends byte[]> getType() {
        return byte[].class;
    }

    @Override
    public void startTransaction() {
        transactionInitialized = false;
    }

    @Override
    public void commit() {
        if (transactionInitialized) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(getValue());
            } catch (FileNotFoundException e) {
                log.error("File not {1} found: ", file.getPath());
            } catch (IOException e) {
                log.error("I/O issue {1} encountered: ", e.getMessage());
            } finally {
                IOUtils.closeQuietly(fos);
                startTransaction();
            }
        }
    }

    @Override
    public void rollback() {
        FileOutputStream fos = null;
        if (transactionInitialized) {
            try {
                fos = new FileOutputStream(file);
                fos.write(bufferedValue);
            } catch (FileNotFoundException e) {
                log.error("File not {1} found: ", file.getPath());
            } catch (IOException e) {
                log.error("I/O issue {1} encountered: ", e.getMessage());
            } finally {
                IOUtils.closeQuietly(fos);
                startTransaction();
            }
        }
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
