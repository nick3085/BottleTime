package com.nickderonde.bottletime;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private TextView mProductTitleTV;
    private TextView mProductProducerTV;
    private Image mProductImageUrlIm;
    private Button mProductLoactionBtn;

    static public Map<String, Object> product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mProductTitleTV = (TextView) findViewById(R.id.tv_product_title);
        mProductProducerTV = (TextView) findViewById(R.id.tv_product_producer);
        mProductImageUrlIm = (Image) findViewById(R.id.im_product_image);
        mProductLoactionBtn = (Button) findViewById(R.id.btn_product_location;

        showProduct(name, producer);

    }

    public void showProduct(Map<String, Object> product) {

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
                    mProductImageUrlIm.setImageDrawable(bitmapDrawable);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    complete(name, producer);
                }
            });
        } else {
            complete(name, producer);
        }

        mProductTitleTV.setText(name);
        mProductProducerTV.setText(String.format("Producer: %s", producer));

    }
}
