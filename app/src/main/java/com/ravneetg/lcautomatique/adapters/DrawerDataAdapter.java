package com.ravneetg.lcautomatique.adapters;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ravneetg.lcautomatique.R;
import com.ravneetg.lcautomatique.utils.Util;


public class DrawerDataAdapter extends CursorAdapter {


    private final CharArrayBuffer mBuffer = new CharArrayBuffer(128);
    private int[] mCellStates;
    private Typeface tfRobotoLight;
    private Typeface tfHeader;

    public DrawerDataAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mCellStates = cursor == null ? null : new int[cursor.getCount()];
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        mCellStates = cursor == null ? null : new int[cursor.getCount()];
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        int ItemType = cursor.getInt(Util.COL_TYPE);
        if(ItemType == 3) return 1;
        if(ItemType == 1) return 0;

        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final DrawerListViewHolder holder = (DrawerListViewHolder) view.getTag();

        /*
         * Separator
         */
        boolean isHeader = false;

        final int position = cursor.getPosition();

        //get the current Caption
        holder.titleBuffer = new CharArrayBuffer(128);
        cursor.copyStringToBuffer(Util.COL_VALUE , holder.titleBuffer);

        //get current Type
        CharArrayBuffer typeBuffer = new CharArrayBuffer(10);
        int ItemType = cursor.getInt(Util.COL_TYPE);

        switch (ItemType) {
            case Util.TYPE_BIG_HEADER:
                isHeader = true;
                break;

            case Util.TYPE_ITEM:
                isHeader = false;
                break;

            case Util.TYPE_UNKNOWN:
            default:
                //expect a header at pos 0
                if (position == 0) {
                    isHeader = true;
                }
                mCellStates[position] = isHeader ? Util.TYPE_HEADER : Util.TYPE_ITEM;
                break;
        }

        view = holder.view;
        if (isHeader) {
            //holder.separator.setText(holder.titleBuffer.data,0,holder.titleBuffer.data.length);
            //holder.separator.setVisibility(View.VISIBLE);
            //holder.titleView.setVisibility(View.GONE);
//            holder.lvw.setDivider(new ColorDrawable(0x99F10529));
//            holder.lvw.setDividerHeight(2);

        } else {
            //holder.separator.setVisibility(View.GONE);
            if(holder.titleView != null){
                holder.titleView.setVisibility(View.VISIBLE);
                holder.titleView.setText(holder.titleBuffer.data,0,holder.titleBuffer.data.length);
            }
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View v = null;
        final int position = cursor.getPosition();

        //get current Type
        CharArrayBuffer typeBuffer = new CharArrayBuffer(10);
        int ItemType = cursor.getInt(Util.COL_TYPE);

        switch (ItemType) {
            case Util.TYPE_BIG_HEADER:
                v = LayoutInflater.from(context).inflate(R.layout.big_row_header, parent, false);
                break;

            case Util.TYPE_ITEM:
                v = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
                break;

            case Util.TYPE_UNKNOWN:
            default:
                v = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
                break;
        }


//        View mainView = LayoutInflater.from(context).inflate(R.layout.activity_main, parent,false);
//        ListView lvw = (ListView) mainView.findViewById(R.id.lvwDrawer);

        DrawerListViewHolder holder = new DrawerListViewHolder();
        holder.titleView = (TextView) v.findViewById(R.id.textView);
        holder.view = v;
//        holder.lvw = lvw;

        //holder.titleView.setTypeface(tfRobotoLight);
        //holder.separator.setTypeface(tfHeader);

        v.setTag(holder);

        return holder.view;
    }


    /**
     * Holder pattern...for caching views
     * @author Ravneet
     *
     */
    private static class DrawerListViewHolder {
        public TextView separator;
        public TextView titleView;
        public CharArrayBuffer titleBuffer = new CharArrayBuffer(128);
        public StringBuilder subtitleBuffer = new StringBuilder();
        public ListView lvw;
        public View view;
    }

}
