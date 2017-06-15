package com.nickderonde.bottletime;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.nickderonde.bottletime.utilities.NetworkUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements NetworkUtils.NetworkDelegate {

    public final static String LOG_TAG = "Bottle Time Main";

    private TextView mSearchResultTitleTV;
    private TextView mSearchResultProducerTV;
    private ImageButton mSearchResultImageUrlIB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchResultTitleTV = (TextView) findViewById(R.id.tv_random_bottle_title);
        mSearchResultProducerTV = (TextView) findViewById(R.id.tv_random_bottle_producer);
        mSearchResultImageUrlIB = (ImageButton) findViewById(R.id.ib_random_bottle_image);

        // Setup universal image loader
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(displayImageOptions)
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }

    public void makeLcboQuery(View v) {
        
        mSearchResultTitleTV.setText(R.string.tv_searching);
        mSearchResultProducerTV.setText(R.string.tv_searching);

        NetworkUtils.delegate = this;
        NetworkUtils.makeLcboQuery();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void randomProduct(Map<String, Object> product) {

        String name = (product.containsKey("name") && (product.get("name") instanceof String)) ? (String)product.get("name") : "Onbekend";
        String producer = (product.containsKey("producer_name") && (product.get("producer_name") instanceof String)) ? (String)product.get("producer_name") : "Onbekend";
        String imageURL = (product.containsKey("image_url") && (product.get("image_url") instanceof String)) ? (String)product.get("image_url") : null;

        mSearchResultTitleTV.setText(name);
        mSearchResultProducerTV.setText(String.format("Producer: %s", producer));

        Log.i(LOG_TAG, "13");

        if (imageURL != null) {
            ImageLoader.getInstance().loadImage(imageURL, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    // TODO: 6/15/17 loader show code here
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    mSearchResultImageUrlIB.setImageDrawable();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // TODO: 6/15/17 remove leader show code
                    Log.i(LOG_TAG, "14");
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), loadedImage);
                    mSearchResultImageUrlIB.setImageDrawable(bitmapDrawable);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }

        Log.i(LOG_TAG, "15");
        Toast.makeText(MainActivity.this , "Served", Toast.LENGTH_LONG).show();
        Log.i(LOG_TAG, "16");
    }
}
