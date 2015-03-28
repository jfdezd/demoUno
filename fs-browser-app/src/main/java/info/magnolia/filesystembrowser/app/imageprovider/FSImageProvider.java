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
package info.magnolia.filesystembrowser.app.imageprovider;

import info.magnolia.ui.imageprovider.ImageProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhlabs.image.CropFilter;
import com.jhlabs.image.ScaleFilter;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.util.FileTypeResolver;

/**
 * {@link ImageProvider} implementation which works over file system folder.
 */
public class FSImageProvider implements ImageProvider {

    private Logger log = LoggerFactory.getLogger(FSImageProvider.class);

    @Override
    public String getPortraitPath(Object itemId) {
        return null;
    }

    @Override
    public String getThumbnailPath(Object itemId) {
        return null;
    }

    @Override
    public String resolveIconClassName(String mimeType) {
        return null;
    }

    @Override
    public Object getThumbnailResource(Object itemId, String generator) {
        if (itemId instanceof File) {
            File file = (File)itemId;
            if (file.exists()) {
                String mimeType = FileTypeResolver.getMIMEType(file);
                if (mimeType != null && mimeType.matches("image.*")) {
                    Resource imageResource = scaleImage(file);
                    return imageResource;
                } else {
                    Resource resolvedThumbnail = FileTypeResolver.getIcon(file);
                    return resolvedThumbnail == null ? FileTypeResolver.DEFAULT_ICON : resolvedThumbnail;
                }
            }
        }
        return null;
    }

    private Resource scaleImage(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            final BufferedImage img = ImageIO.read(fis);
            if (img != null) {
                int width = img.getWidth();
                int height = img.getHeight();
                BufferedImage result = img;
                if (width > 200) {
                    double ratio = (double) height / width;
                    int newWidth = 200;
                    int newHeight = (int) (newWidth * ratio);
                    result = new ScaleFilter(newWidth, newHeight).filter(img, null);
                    result = new CropFilter(0, 0, newWidth, newHeight).filter(result, null);
                }
                final InputStream is = createStreamSource(result, "png");
                StreamResource.StreamSource streamSource = new StreamResource.StreamSource() {

                    @Override
                    public InputStream getStream() {
                        return is;
                    }
                };
                return new StreamResource(streamSource, "image" + System.currentTimeMillis() + ".png");
            }
        } catch (IOException e) {
            log.error("Failed to scale a resource {}.", file, e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    private InputStream createStreamSource(final BufferedImage img, final String formatName) {
        ByteArrayOutputStream out2 = null;
        try {
            if (img == null) {
                return null;
            }
            out2 = new ByteArrayOutputStream();
            ImageIO.write(img, formatName, out2);
            return new ByteArrayInputStream(out2.toByteArray());
        } catch (IOException e) {

        } finally {
            IOUtils.closeQuietly(out2);
        }
        return null;
    }
}
