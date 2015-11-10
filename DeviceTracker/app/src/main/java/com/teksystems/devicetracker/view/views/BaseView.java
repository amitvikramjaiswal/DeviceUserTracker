package com.teksystems.devicetracker.view.views;

import android.text.Spanned;

import com.teksystems.devicetracker.service.handlers.AlertDialogHandler;

/**
 * Base class for all views.
 */
public interface BaseView {

    /**
     * Shows a toast message.
     *
     * @param message
     *            The message to show.
     */
    void showToast(String message);

    /**
     * Shows or hides the indefinite progress bar.
     *
     * @param shown
     *            Whether the progress bar should be shown.
     */
    void setProgressBarIndeterminateVisibility(boolean shown);

    /**
     * Sets the app title.
     *
     * @param title
     *            The title.
     */
    void setTitle(int title);

    /**
     * Sets the app title.
     *
     * @param title
     *            The title.
     */
    void setTitle(CharSequence title);

    /**
     * Gets a string from app resources.
     *
     * @param stringResourceId
     *            The string resource id.
     * @return The string.
     */
    String getString(int stringResourceId);

    /**
     * Gets a string array from app resources.
     *
     * @param arrayResourceId
     *            The array resource id.
     * @return The string array.
     */
    String[] getStringArray(int arrayResourceId);

    /**
     * Closes the view.
     */
    void finish();

    /**
     * Checks if Google Play Services is available.
     */
    boolean checkGooglePlayServices();

    /**
     * Checks if the view is currently finishing.
     */
    boolean isFinishing();

    /**
     * Shows a progress dialog in the center of the screen.
     *
     * @param status
     *            The status message to show.
     */
    void showProgressDialog(String status);

    /**
     * Hides the progress dialog from the center of the screen.
     */
    void hideProgressDialog();

    /**
     * Shows an alert dialog to prompt the user.
     *
     * @param title
     *            The title of the dialog, optional.
     * @param message
     *            The message for the dialog.
     * @param itemlabels
     *            If multiple choice, the item labels.
     * @param checkItemLabelsChecked
     *            If multiple choice, the checked state of each item.
     * @param positiveButton
     *            The positive button (optional).
     * @param negativeButton
     *            The negative button (optional).
     * @param alertDialogHandler
     *            The dialog handler for button clicks.
     */
    void showAlertDialog(String title, String message, Spanned[] itemlabels, boolean[] checkItemLabelsChecked, String positiveButton,
                         String negativeButton, AlertDialogHandler alertDialogHandler);

    void hideKeyBoard();
}
