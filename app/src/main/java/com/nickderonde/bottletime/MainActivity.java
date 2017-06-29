package com.nickderonde.bottletime;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class MainActivity extends AppCompatActivity implements NetworkUtils.NetworkDelegate, SharedPreferences.OnSharedPreferenceChangeListener {

    public final static String LOG_TAG = "Bottle main";

    private TextView mSearchResultTitleTV;
    private TextView mSearchResultProducerTV;
    private ImageButton mSearchResultImageUrlIB;
    private MediaPlayer popSoundMP;
    private Button mGetBottleBtn;
    public Map<String, Object> currentProduct;

    private boolean playSound;
    private boolean showToasts;

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

        setupSharedPreferences();
    }

    /**
     * Sets the playSound setting for playing the pop sound or not
     *
     * @param playSound
     */
    public void setPlaySound(boolean playSound) {this.playSound = playSound; }

    /**
     * Sets the showToasts for the debug mode setting
     *
     * @param showToasts
     */
    public void setShowToasts(boolean showToasts) {this.showToasts = showToasts; }

    /**
     * Onclicklistener for shuffle button
     *
     * @param v
     */
    public void makeLcboQuery(View v) {

        mGetBottleBtn.setEnabled(false);
        if (showToasts) {
            Toast.makeText(MainActivity.this , "Serving...", Toast.LENGTH_SHORT).show();
        }

        NetworkUtils.delegate = this;
        Log.i(LOG_TAG, "makeLcboQuery");
        NetworkUtils.makeLcboQuery();
    }


    /**
     * Go to detail activity when button is clicked
     * and pass the product to DetailActivity
     *
     * @param v
     */
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


    /**
     * Array of the prodyct
     * filter out the name, producer name and the imageurl
     * Also do some checks and load all data after the image is loaded
     * with the onconplete method
     * @param product
     */
    @Override
    public void randomProduct(Map<String, Object> product) {

        currentProduct = product;

        final String name = (product.containsKey("name") && (product.get("name") instanceof String)) ? (String)product.get("name") : "Unknown";
        final String producer = (product.containsKey("producer_name") && (product.get("producer_name") instanceof String)) ? (String)product.get("producer_name") : "Unknown";
        final String imageURL = (product.containsKey("image_url") && (product.get("image_url") instanceof String)) ? (String)product.get("image_url") : null;

        if (imageURL != null) {
            ImageLoader.getInstance().loadImage(imageURL, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    Log.i(LOG_TAG, "onLoadingStarted: randomProduct");
                    mSearchResultImageUrlIB.setImageResource(R.drawable.loading_icon);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    Log.i(LOG_TAG, "onLoadingFailed: randomProduct");
                    mSearchResultImageUrlIB.setImageResource(R.drawable.none_bottle);
                    complete(name, producer);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Log.i(LOG_TAG, "onLoadingComplete: randomProduct");
                    complete(name, producer);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), loadedImage);
                    mSearchResultImageUrlIB.setImageDrawable(bitmapDrawable);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    Log.i(LOG_TAG, "onLoadingCancelled: randomProduct");
                    mSearchResultImageUrlIB.setImageResource(R.drawable.none_bottle);
                    complete(name, producer);
                }
            });
        } else {
            mSearchResultImageUrlIB.setImageResource(R.drawable.none_bottle);
            complete(name, producer);
        }
    }

    /**
     * Complete method
     *
     * @param name
     * @param producer
     */
    private void complete(String name, String producer) {
        mSearchResultTitleTV.setText(name);
        mSearchResultProducerTV.setText(String.format("Producer: %s", producer));
        mGetBottleBtn.setEnabled(true);
        playPop();
        if (showToasts) {
            Toast.makeText(MainActivity.this , "Served", Toast.LENGTH_SHORT).show();
        }
        Log.i(LOG_TAG, "complete: name + producer");
    }

    /**
     * Play sound Method
     */
    private void playPop(){
        if (playSound) {
            popSoundMP.start();
        }
    }

    /**
     * put settings menu in actionbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * start settings activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent SettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(SettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * get all values from the shared preferences and set it up and register the listener
     *
     */
    public void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setPlaySound(sharedPreferences.getBoolean(getString(R.string.pref_play_sound), getResources().getBoolean(R.bool.pref_play_sound_default)));
        setShowToasts(sharedPreferences.getBoolean(getString(R.string.pref_show_toasts), getResources().getBoolean(R.bool.pref_show_toasts_default)));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * get all values from the shared preferences and set it up and register the listener
     *
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_play_sound))) {
            setPlaySound(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_play_sound_default)));
        } else if (key.equals(getString(R.string.pref_show_toasts))) {
            setShowToasts(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_toasts_default)));
        }
    }

    /**
     * Unregister Mainctivity as an OnPreferenceChangedListener to avoid any memory leaks.
     *
     */
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
