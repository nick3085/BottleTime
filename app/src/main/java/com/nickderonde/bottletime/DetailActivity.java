package com.nickderonde.bottletime;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.DecimalFormat;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private TextView mProductTitleTV;
    private TextView mProductProducerTV;
    private ImageView mProductImageUrlIm;
    private TextView mPriceInCentsTV;
    private TextView mAlcoholContentTV;
    private TextView mPrimaryCategoryTV;
    private TextView mSecondaryCategoryTV;
    private TextView mServingSuggestionTV;
    private TextView mTastingNoteTV;
    private TextView mOriginTV;
    private TextView mReleasedOnTV;

    private ImageButton mProductLoactionBtn;

    static public Map<String, Object> product;
    private Bitmap loadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mProductTitleTV = (TextView) findViewById(R.id.tv_product_title);
        mProductProducerTV = (TextView) findViewById(R.id.tv_product_producer);
        mPriceInCentsTV = (TextView) findViewById(R.id.tv_price_in_cents);
        mAlcoholContentTV = (TextView) findViewById(R.id.tv_alcohol_content);
        mPrimaryCategoryTV = (TextView) findViewById(R.id.tv_primary_category);
        mSecondaryCategoryTV = (TextView) findViewById(R.id.tv_secondary_category);
        mServingSuggestionTV = (TextView) findViewById(R.id.tv_serving_suggestion);
        mTastingNoteTV = (TextView) findViewById(R.id.tv_tasting_note);
        mOriginTV = (TextView) findViewById(R.id.tv_origin);
        mReleasedOnTV = (TextView) findViewById(R.id.tv_released_on);

        mProductImageUrlIm = (ImageView) findViewById(R.id.im_product_image);
        mProductLoactionBtn = (ImageButton) findViewById(R.id.btn_product_location);

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

        showProduct(product);
    }

    public void goToLocation(View v) {
        setContentView(R.layout.activity_maps);
    }

    public void showProduct(Map<String, Object> product) {

        final String name               = (product.containsKey("name") && (product.get("name") instanceof String)) ? (String)product.get("name") : "Unknown";
        final String primaryCategory    = (product.containsKey("primary_category") && (product.get("primary_category") instanceof String)) ? (String)product.get("primary_category") : "Unknown";
        final String secondaryCategory  = (product.containsKey("secondary_category") && (product.get("secondary_category") instanceof String)) ? (String)product.get("secondary_category") : "Unknown";
        final String servingSuggestion  = (product.containsKey("serving_suggestion") && (product.get("serving_suggestion") instanceof String)) ? (String)product.get("serving_suggestion") : "Unknown";
        final String tastingNote        = (product.containsKey("tasting_note") && (product.get("tasting_note") instanceof String)) ? (String)product.get("tasting_note") : "Unknown";
        final String origin             = (product.containsKey("origin") && (product.get("origin") instanceof String)) ? (String)product.get("origin") : "Unknown";
        final String releasedOn         = (product.containsKey("released_on") && (product.get("released_on") instanceof String)) ? (String)product.get("released_on") : "Unknown";
        final String producer           = (product.containsKey("producer_name") && (product.get("producer_name") instanceof String)) ? (String)product.get("producer_name") : "Unknown";
        final Integer priceInCents      = (product.containsKey("price_in_cents") && (product.get("price_in_cents") instanceof Integer)) ? (Integer) product.get("price_in_cents") : null;
        if (priceInCents == null) {
            mPriceInCentsTV.setText("Unknown");
        } else {
            double d = priceInCents/100;
            DecimalFormat df = new DecimalFormat(",##");
            mPriceInCentsTV.setText("$" + df.format(d));
        }

        final Integer alcoholContent = (product.containsKey("alcohol_content") && (product.get("alcohol_content") instanceof Integer)) ? (Integer) product.get("alcohol_content") : null;
        if(alcoholContent == null) {
            mAlcoholContentTV.setText( "Unknown");
        } else {
            double d = alcoholContent/100;
            DecimalFormat df = new DecimalFormat(",##");
            mAlcoholContentTV.setText(df.format(d) + "%");
        }

        final String imageURL = (product.containsKey("image_url") && (product.get("image_url") instanceof String)) ? (String)product.get("image_url") : null;
        if (imageURL != null) {
            ImageLoader.getInstance().loadImage(imageURL, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {}

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), loadedImage);
                    mProductImageUrlIm.setImageDrawable(bitmapDrawable);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {}
            });
        } else {
            mProductImageUrlIm.setImageResource(R.drawable.none_bottle);
        }

        mProductTitleTV.setText(name);
        mProductProducerTV.setText(producer);
        mPrimaryCategoryTV.setText(primaryCategory);
        mSecondaryCategoryTV.setText(secondaryCategory);
        mServingSuggestionTV.setText(servingSuggestion);
        mTastingNoteTV.setText(tastingNote);
        mOriginTV.setText(origin);
        mReleasedOnTV.setText(releasedOn);
    }
}
