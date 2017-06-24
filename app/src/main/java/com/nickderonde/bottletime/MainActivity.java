package com.nickderonde.bottletime;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private MediaPlayer popSoundMP;
    private Button mGetBottleBtn;
    private Map<String, Object> currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchResultTitleTV = (TextView) findViewById(R.id.tv_random_bottle_title);
        mSearchResultProducerTV = (TextView) findViewById(R.id.tv_random_bottle_producer);
        mSearchResultImageUrlIB = (ImageButton) findViewById(R.id.ib_random_bottle_image);
        mGetBottleBtn = (Button) findViewById(R.id.button_get_bottle);

        popSoundMP = MediaPlayer.create(this, R.raw.pop);

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

    /**
     * Onclicklistener for shuffle button
     *
     * @param v
     */
    public void makeLcboQuery(View v) {

        mGetBottleBtn.setEnabled(false);
        Toast.makeText(MainActivity.this , "Serving...", Toast.LENGTH_SHORT).show();

        NetworkUtils.delegate = this;
        NetworkUtils.makeLcboQuery();
    }

    public void detailButtonHandler(View v) {

        //get product when no product is available
        if(currentProduct == null) {
            makeLcboQuery(v);
            return;
        }

        DetailActivity.product = currentProduct;
        Intent detailActivity = new Intent(this, DetailActivity.class);
        startActivity(detailActivity);
    }


    @Override
    public void randomProduct(Map<String, Object> product) {

        currentProduct = product;

        final String name = (product.containsKey("name") && (product.get("name") instanceof String)) ? (String)product.get("name") : "Onbekend";
        final String producer = (product.containsKey("producer_name") && (product.get("producer_name") instanceof String)) ? (String)product.get("producer_name") : "Onbekend";
        final String imageURL = (product.containsKey("image_url") && (product.get("image_url") instanceof String)) ? (String)product.get("image_url") : null;

        if (imageURL != null) {
            ImageLoader.getInstance().loadImage(imageURL, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    // TODO: 6/15/17 loader show code here
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    complete(name, producer);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    complete(name, producer);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), loadedImage);
                    mSearchResultImageUrlIB.setImageDrawable(bitmapDrawable);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    complete(name, producer);
                }
            });
        } else {
            complete(name, producer);
        }
    }

    private void complete(String name, String producer) {
        mSearchResultTitleTV.setText(name);
        mSearchResultProducerTV.setText(String.format("Producer: %s", producer));
        mGetBottleBtn.setEnabled(true);
        popSoundMP.start();
        Toast.makeText(MainActivity.this , "Served", Toast.LENGTH_SHORT).show();
    }

    //show options menu in actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // start settings activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
        }

        return super.onOptionsItemSelected(item);
    }
}
