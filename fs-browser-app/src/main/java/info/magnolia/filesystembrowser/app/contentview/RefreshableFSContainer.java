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

import info.magnolia.ui.workbench.container.Refreshable;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.FilesystemContainer;

/**
 * An implementation of {@link Container} for the filesystem.
*/
class RefreshableFSContainer implements Container.Hierarchical, Container.ItemSetChangeNotifier, Refreshable {

    private List<ItemSetChangeListener> listeners = new LinkedList<ItemSetChangeListener>();

    private FilesystemContainer delegate;

    private File root;

    public RefreshableFSContainer(File root) {
        this.root = root;
        refreshDelegate();
    }

    private void refreshDelegate() {
        delegate = new FilesystemContainer(root);
    }

    @Override
    public void addItemSetChangeListener(ItemSetChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void addListener(ItemSetChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeItemSetChangeListener(ItemSetChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeListener(ItemSetChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void refresh() {
        refreshDelegate();
        notifyItemSetChange();
    }

    private void notifyItemSetChange() {
        final Object[] l = listeners.toArray();
        BaseItemSetChangeEvent event = new BaseItemSetChangeEvent(this);
        for (int i = 0; i < l.length; i++) {
            ((ItemSetChangeListener) l[i]).containerItemSetChange(event);
        }
    }

    @Override
    public Collection<?> getChildren(Object itemId) {
        return delegate.getChildren(itemId);
    }

    @Override
    public Object getParent(Object itemId) {
        return delegate.getParent(itemId);
    }

    @Override
    public Collection<?> rootItemIds() {
        return delegate.rootItemIds();
    }

    @Override
    public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
        return delegate.setParent(itemId, newParentId);
    }

    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return delegate.areChildrenAllowed(itemId);
    }

    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
        return delegate.setChildrenAllowed(itemId, areChildrenAllowed);
    }

    @Override
    public boolean isRoot(Object itemId) {
        return delegate.isRoot(itemId);
    }

    @Override
    public boolean hasChildren(Object itemId) {
        return delegate.hasChildren(itemId);
    }

    @Override
    public Item getItem(Object itemId) {
        return delegate.getItem(itemId);
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return delegate.getContainerPropertyIds();
    }

    @Override
    public Collection<?> getItemIds() {
        return delegate.getItemIds();
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return delegate.getContainerProperty(itemId, propertyId);
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return delegate.getType(propertyId);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        return delegate.containsId(itemId);
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        return delegate.addItem(itemId);
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        return delegate.addItem();
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
        return delegate.removeItem(itemId);
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
        return delegate.addContainerProperty(propertyId, type, defaultValue);
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
        return delegate.removeContainerProperty(propertyId);
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        return delegate.removeAllItems();
    }

    /**
     * A custom {@link EventObject}.
     */
    protected static class BaseItemSetChangeEvent extends EventObject implements
            Container.ItemSetChangeEvent, Serializable {

        protected BaseItemSetChangeEvent(Container source) {
            super(source);
        }

        @Override
        public Container getContainer() {
            return (Container) getSource();
        }
    }
}
