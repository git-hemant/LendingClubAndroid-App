package com.ravneetg.lcautomatique.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.utils.Util;

import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;

/**
 * Created by khenush on 8/12/2015.
 */
public class ChangesViewAdapter extends ArrayAdapter<ChangesViewAdapter.ChangeUIItem> {

    public ChangesViewAdapter(Context context, ArrayList<ChangeUIItem> changeItems) {
        super(context, 0, changeItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChangeUIItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_changes_row, parent, false);
        }
        // Lookup view for data population
        TextView tvLbl = (TextView) convertView.findViewById(R.id.changeLbl);
        TextView tvValue = (TextView) convertView.findViewById(R.id.changeTxt);
        // Populate the data into the template view using the data object
        tvLbl.setText(item.getLabel());

        String value = item.getValue();
        if (item.isAmount()) {
            value = "$" + value;
        }
        if (item.isShowHyperLink()) {
            tvValue.setText(Html.fromHtml(Util.hyperlinkHtml(value)));
        } else {
            tvValue.setText(value);
        }
        // Store the noteIds in the tag - as we will use
        // them in onClick in ChangesActivity, null will be set
        // for TextView which we don't have any noteids.
        tvValue.setTag(item.getNoteIds());
        // Return the completed view to render on screen
        return convertView;
    }

    public static class ChangeUIItem {
        private String label;
        private String value;
        // Flag which represent whether value is amount
        private boolean isAmount;
        // Flag which indicate whether we need to show value as hyperlink.
        private boolean showHyperLink;
        // Data which will be used when the hyperlink is used.
        private List<String> noteIds;

        public boolean isAmount() {
            return isAmount;
        }

        public void setIsAmount(boolean isAmount) {
            this.isAmount = isAmount;
        }

        public boolean isShowHyperLink() {
            return showHyperLink;
        }

        public void setShowHyperLink(boolean showHyperLink) {
            this.showHyperLink = showHyperLink;
        }

        public List<String> getNoteIds() {
            return noteIds;
        }

        public void setNoteIds(List<String> noteIds) {
            this.noteIds = noteIds;
        }

        public ChangeUIItem(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }
}