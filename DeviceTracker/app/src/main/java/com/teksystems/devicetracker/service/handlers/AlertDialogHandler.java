package com.teksystems.devicetracker.service.handlers;

public interface AlertDialogHandler {
    
    void onPositiveButtonClicked();
    
    void onNegativeButtonClicked();

    void onMultiChoiceClicked(int position, boolean isChecked);

}
