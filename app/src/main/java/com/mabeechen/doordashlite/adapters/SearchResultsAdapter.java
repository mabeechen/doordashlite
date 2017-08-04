package com.mabeechen.doordashlite.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.mabeechen.doordashlite.RestaurantChosenActivity;
import com.mabeechen.doordashlite.providers.RestaurantsContentProvider;

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
    private OnItemClickListener mItemListener = new OnItemClickListener();

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
                GhettoLoader loader = new GhettoLoader(holder.vImage, imageUrl);
                new Thread(loader).start();
            }
            String description = mCursor.getString(mCursor.getColumnIndex(RestaurantTableColumns.DESCRIPTION));
            holder.vDescription.setText(description);

            StatusType type = StatusType.values()[mCursor.getInt(mCursor.getColumnIndex(SearchResultsTableColumns.STATUS_TYPE))];


            String time = mCursor.getString(mCursor.getColumnIndex(SearchResultsTableColumns.STATUS_TEXT));
            switch(type) {
                case OPEN:
                    holder.vDeliveryTime.setText(time);
                    break;
                case PRE_ORDER:
                    holder.vDeliveryTime.setText(mContext.getResources().getText(R.string.status_pre_order));
                    break;
                case UNAVAILABLE:
                    holder.vDeliveryTime.setText(mContext.getResources().getText(R.string.status_closed));
                    break;
                case UNKNOWN:
                    holder.vDeliveryTime.setText("");
                    break;
            }

            boolean isFavorite = mCursor.getInt(mCursor.getColumnIndex(RestaurantTableColumns.IS_FAVORITE)) > 0? true: false;
            if(isFavorite) {
                holder.vFavoriteImage.setImageResource(R.mipmap.ic_star_black_24dp);
            } else {
                holder.vFavoriteImage.setImageResource(R.mipmap.ic_star_border_black_24dp);
            }
            String businessId = mCursor.getString(mCursor.getColumnIndex(RestaurantTableColumns.BUSINESS_ID));
            holder.vFavoriteImage.setTag(businessId);
            holder.vFavoriteImage.setOnClickListener(mFavoriteClickListener);
            holder.vImage.setOnClickListener(mItemListener);
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

    /**
     * View holder for the adapter
     */
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
     * Ghetto image downloader.  This is pretty ghetto.
     * Stole most of this from here: https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
     * Was considering doing something more elaborate here but was running short on time at that point.
     * If you all decide to proceed I'll likely clean this up before the interview.
     */
    private class GhettoLoader implements Runnable {
        private final ImageView mImage;
        private String mUrl;

        /**
         * Constructor
         * @param image The image view to load an image for
         * @param Url The url of the image to load
         */
        public GhettoLoader(ImageView image, String Url) {
            mImage = image;
            mUrl = Url;
        }

        @Override
        public void run() {
            LoadImage();
        }

        /**
         * Loads an image into the control
         */
        private void LoadImage()
        {
            Bitmap bitmap = null;
            InputStream in = null;
            try {
                in = OpenHttpConnection(mUrl);
                bitmap = BitmapFactory.decodeStream(in, null, new BitmapFactory.Options());
                in.close();
            } catch (IOException e1) {
                Log.d("Stream download Failed", "Sigh!!!");
            }
            final Bitmap finalMap = bitmap;

            mImage.post(new Runnable() {
                @Override
                public void run() {
                    mImage.setImageBitmap(finalMap);
                }
            });
        }

        /**
         * Creates an httpsConnection
         * @param strURL
         * @return
         * @throws IOException
         */
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
                Log.d("HTTP Connection Failed", "Double Sigh!");
            }
            return inputStream;
        }
    }

    /**
     * Handles clicking on the favorite button
     *
     * @author mabeechen
     * @since 8/3/17
     */
    private class OnFavoriteButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String businessID = (String)v.getTag();
            mContext.getContentResolver().call(RestaurantsContentProvider.LIST_URI, "Offline", businessID, null);
        }
    }

    /**
     *
     */
    private class OnItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String businessID = (String)v.getTag();

            try {
                Intent intent = new Intent(mContext, RestaurantChosenActivity.class);
//            intent.putExtra(SimpleDialogActivity.TITLE_KEY, title);
//            intent.putExtra(SimpleDialogActivity.MESSAGE_KEY, message);
//            intent.putExtra(SimpleDialogActivity.POSITIVE_BUTTON_TITLE_KEY, dismissButtonText);

                mContext.startActivity(intent);
            } catch (Exception ex) {
                Log.d("Error", ex.getMessage());
            }
        }
    }
}
