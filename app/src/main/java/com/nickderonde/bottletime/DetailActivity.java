package com.nickderonde.bottletime;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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

    }

    public void ShowProduct() {

        final String name = (product.containsKey("name") && (product.get("name") instanceof String)) ? (String)product.get("name") : "Onbekend";
        final String producer = (product.containsKey("producer_name") && (product.get("producer_name") instanceof String)) ? (String)product.get("producer_name") : "Onbekend";
        final String imageURL = (product.containsKey("image_url") && (product.get("image_url") instanceof String)) ? (String)product.get("image_url") : null;


        mProductTitleTV.setText(name);
        mProductProducerTV.setText(String.format("Producer: %s", producer));

    }
}
