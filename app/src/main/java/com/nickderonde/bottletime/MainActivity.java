package com.nickderonde.bottletime;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.nickderonde.bottletime.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements NetworkUtils.NetworkDelegate {

    public final static String LOG_TAG = "Bottle Time Main";

    private TextView mSearchResultTitleTV;
    private TextView mSearchResultProducerTV;
    private ImageButton mSearchResultImageUrlIB;

    String uri = "@drawable/none_bottle";
    int imageResource = getResources().getIdentifier(uri, null, getPackageName());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchResultTitleTV = (TextView) findViewById(R.id.tv_random_bottle_title);
        mSearchResultProducerTV = (TextView) findViewById(R.id.tv_random_bottle_producer);
        mSearchResultImageUrlIB = (ImageButton) findViewById(R.id.ib_random_bottle_image);
    }

    public void makeLcboQuery(View v) {
        mSearchResultTitleTV.setText(R.string.tv_searching);
        mSearchResultProducerTV.setText(R.string.tv_searching);
//        mSearchResultImageUrlIB.setImageDrawable(R.drawable.none_bottle);
        NetworkUtils.delegate = this;
        NetworkUtils.makeLcboQuery();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void randomProduct(Map<String, Object> product) {

        mSearchResultTitleTV.setText((String)product.get("name"));
        mSearchResultProducerTV.setText(String.format("Producer: %s", (String)product.get("producer_name")));

//        Drawable res = getResources();
//        mSearchResultImageUrlIB.setImageDrawable(res);

//        mSearchResultImageUrlIB.setImageURI(Uri.parse((String)product.get("image_thumb_url")));

        Toast.makeText(MainActivity.this , "Served", Toast.LENGTH_LONG).show();
    }
}
