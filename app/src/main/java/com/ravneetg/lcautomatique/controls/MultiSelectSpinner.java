package com.ravneetg.lcautomatique.controls;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.ravneetg.lcautomatique.utils.DropdownValue;

/**
 * Created by Ravneet on 1/21/2015.
 */
public class MultiSelectSpinner extends Spinner implements
        OnMultiChoiceClickListener
{
    private String _propertyName = null;
    private DropdownValue[] _items = null;
    private boolean[] mSelection = null;
    private boolean [] mTempSelection = null;

    ArrayAdapter<DropdownValue> simple_adapter;

    public MultiSelectSpinner(Context context) {
        super(context);
        simple_adapter = new ArrayAdapter<DropdownValue>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<DropdownValue>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mTempSelection != null && which < mTempSelection.length) {
            mTempSelection[which] = isChecked;
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mTempSelection = Arrays.copyOf(mSelection, mSelection.length);
        builder.setMultiChoiceItems(DropdownValue.getDisplayValues(_items), mTempSelection, this);
        builder.setTitle(_propertyName);

        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelection = Arrays.copyOf(mTempSelection, mTempSelection.length);
                simple_adapter.clear();
                simple_adapter.addAll(getSelectedItems());

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simple_adapter.clear();
                simple_adapter.addAll(getSelectedItems());
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setPropertyName(String name){
        _propertyName = name;

    }

    public void setItems(DropdownValue[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        mTempSelection = new boolean[_items.length];
        simple_adapter.clear();
        //simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        Arrays.fill(mTempSelection, false);
    }

    public void setItems(List<DropdownValue> items) {
        setItems(items.toArray(new DropdownValue[items.size()]));
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                }
            }
        }
    }

    // TODO - Since we are storing different value
    // so we need to see who invokes this method.
    public void setSelection(List<DropdownValue> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (DropdownValue sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.addAll(getSelectedItems());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.addAll(getSelectedItems());
    }

    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.addAll(getSelectedItems());
    }

    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    public List<DropdownValue> getSelectedItems() {
        List<DropdownValue> selectedItems = new ArrayList<DropdownValue>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selectedItems.add(_items[i]);
            }
        }
        return selectedItems;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    private String buildTempSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mTempSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }
}
