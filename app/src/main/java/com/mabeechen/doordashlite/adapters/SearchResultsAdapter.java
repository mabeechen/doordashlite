package com.mabeechen.doordashlite.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mabeechen.doordashlite.R;
import com.mabeechen.doordashlite.database.*;
import com.mabeechen.doordashlite.dbhelpers.RestaurantsDBHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import static com.mabeechen.doordashlite.database.DoorDashDatabase.*;

/**
 * Adapter for restaurant search results
 *
 * @author mabeechen
 * @since 7/20/17
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private OnFavoriteButtonClickListener mFavoriteClickListener = new OnFavoriteButtonClickListener();

    public SearchResultsAdapter (Context context) {
        mContext = context;
    }

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
            String imageUrl = mCursor.getString(mCursor.getColumnIndex(RestaurantTableColumns.IMAGE_URL));
            if(!imageUrl.isEmpty()) {
                ViewLoader loader = new ViewLoader(holder.vImage, imageUrl);
                new Thread(loader).start();
            }
            String description = mCursor.getString(mCursor.getColumnIndex(RestaurantTableColumns.DESCRIPTION));
            holder.vDescription.setText(description);
            int time = mCursor.getInt(mCursor.getColumnIndex(SearchResultsTableColumns.ASAP_TIME));
            String timeString = Integer.toString(time);
            holder.vDeliveryTime.setText(timeString);
            boolean isFavorite = mCursor.getInt(mCursor.getColumnIndex(RestaurantTableColumns.IS_FAVORITE)) > 0? true: false;
            if(isFavorite) {
                holder.vFavoriteImage.setImageResource(R.mipmap.ic_star_black_24dp);
            } else {
                holder.vFavoriteImage.setImageResource(R.mipmap.ic_star_border_black_24dp);
            }
            String businessId = mCursor.getString(mCursor.getColumnIndex(RestaurantTableColumns.BUSINESS_ID));
            holder.vFavoriteImage.setTag(businessId);
            holder.vFavoriteImage.setOnClickListener(mFavoriteClickListener);
        }
    }

    @Override
    public long getItemId(int position) {
        int actualPosition = position;
        mCursor.moveToPosition(actualPosition);
        return mCursor.getLong(mCursor.getColumnIndex(RestaurantTableColumns._ID));
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    /**
     * Switches the current cursor to the passed in cursor.
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected ImageView vImage;
        protected TextView vDescription;
        protected TextView vDeliveryTime;
        protected ImageView vFavoriteImage;

        public SearchResultViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.restaurant_name_text);
            vImage = (ImageView) v.findViewById(R.id.restaurant_image);
            vDescription = (TextView) v.findViewById(R.id.description_text);
            vDeliveryTime = (TextView) v.findViewById(R.id.delivery_time_text);
            vFavoriteImage = (ImageView) v.findViewById(R.id.favorite_image);
        }
    }

    /**
     * Ghetto image downloader.
     * Stole most of this from here: https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
     * Was considering doing something more elaborate here.
     */
    private class ViewLoader implements Runnable {
        private final ImageView mImage;
        private String mUrl;
        public ViewLoader(ImageView image, String Url) {
            mImage = image;
            mUrl = Url;
        }

        @Override
        public void run() {
            LoadImage();
        }

        private void LoadImage()
        {
            Bitmap bitmap = null;
            InputStream in = null;
            try {
                in = OpenHttpConnection(mUrl);
                bitmap = BitmapFactory.decodeStream(in, null, new BitmapFactory.Options());
                in.close();
            } catch (IOException e1) {
                Log.d("Stream download Failed", "Painful");
            }
            final Bitmap finalMap = bitmap;

            mImage.post(new Runnable() {
                @Override
                public void run() {
                    mImage.setImageBitmap(finalMap);
                }
            });
        }

        private InputStream OpenHttpConnection(String strURL) throws IOException{
            InputStream inputStream = null;
            URL url = new URL(strURL);
            URLConnection conn = url.openConnection();

            try{
                HttpsURLConnection httpConn = (HttpsURLConnection)conn;
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = httpConn.getInputStream();
                }
            }
            catch (Exception ex)
            {
                Log.d("HTTP Connection Failed", "Painful");
            }
            return inputStream;
        }
    }

    private class OnFavoriteButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String businessID = (String)v.getTag();
            DoorDashDatabase data = new DoorDashDatabase(mContext);
            ContentValues values = new ContentValues();
            values.put(RestaurantTableColumns.IS_FAVORITE, true);

            RestaurantsDBHelper.updateRestaurantOnBusinessId(data.getWritableDatabase(), businessID, values);
        }
    }
}
