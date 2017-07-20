package com.mabeechen.doordashlite.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mabeechen.doordashlite.R;
import com.mabeechen.doordashlite.database.DoorDashDatabase;
import java.text.DateFormat;
import java.util.Date;
import static com.mabeechen.doordashlite.database.DoorDashDatabase.*;
/**
 * Created by marbe on 7/19/2017.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> {

    private Cursor mCursor;
    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_item, parent, false);
        return new SearchResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        if(mCursor != null) {
            int actualPosition = position;
            mCursor.moveToPosition(actualPosition);
            String name = mCursor.getString(mCursor.getColumnIndex(RestaurantTableColumns.NAME));
            holder.vName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    /**
     * Switches the current cursor to the passed in cursor.  Yay!!!
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        public SearchResultViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.restaurant_name_text);
        }
    }
}
