package com.ravneetg.lcautomatique.controls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.controls.listener.DialogListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khenush on 10/31/2015.
 */
public class DialogUtil {
/*
    public static Dialog dialogWithSelectionList(Activity activity, final List<String> listItems, boolean singleSelection,
                                                 final List<String> selectedItems, final DialogListener dialogListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (singleSelection) {
            builder.setSingleChoiceItems()
        }
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                builder.setMultiChoiceItems(listItems.toArray(new String[listItems.size()]), null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                String selectedItem = listItems.get(which);
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedItems.add(selectedItem);
                                } else if (selectedItems.contains(selectedItem)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedItems.remove(selectedItem);
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        dialogListener.okPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.cancelPressed();
                    }
                });

        return builder.create();
    }
    */
}
