package com.teksystems.devicetracker.presenter;

import java.util.List;

/**
 * Interface for presenters that need to show a list to the user.
 *
 * @param <T>
 *            Parameter identifying the type of the list item.
 */
public interface ListPresenter<T> {

    /**
     * Gets the number of items in the list.
     *
     * @return Number of items in the list.
     */
    int getListItemCount();

    /**
     * Gets the item at a position.
     *
     * @param position
     *            The position.
     * @return The item, or null if not found.
     */
    T getListItem(int position);

    /**
     * Gets a list of all the items.
     *
     * @return The list of items.
     */
    List<T> getListItems();

    /**
     * Triggers when a list item is clicked.
     *
     * @param position
     *            The list item.
     */
    void onListItemClicked(int position);

    /**
     * Triggers when the list is scrolled.
     *
     * @param firstVisibleItem
     *            The first visible item in the list.
     * @param visibleItemCount
     *            The number of visible items.
     * @param totalItemCount
     *            The total number of items in the list.
     */
    void onListScrolled(int firstVisibleItem, int visibleItemCount, int totalItemCount);

    /**
     * Fires when an item in the list is long clicked.
     *
     * @param position
     *            The position.
     * @return True if the long click was consumed, false otherwise.
     */
    boolean onItemLongClick(int position);
}
