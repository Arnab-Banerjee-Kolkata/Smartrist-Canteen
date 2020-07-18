package cardproject.android.arnab.canteen;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

public class CanteenHome extends AppCompatActivity implements View.OnClickListener
{
    ImageView imageView1,foodImg;
    Button acceptBtn, pendingBtn;

    ArrayList<Item> itemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("App Name");

        imageView1 = findViewById(R.id.homeBg);
        foodImg = findViewById(R.id.foodImg);
        acceptBtn = findViewById(R.id.accept);
        pendingBtn = findViewById(R.id.pending);


        int resid = R.drawable.canbg;

        Glide
                .with(this)
                .load(resid).into(imageView1);

        Glide
                .with(this)
                .load(R.drawable.food).into(foodImg);

        Button b = findViewById(R.id.accept);
        Button b2 = findViewById(R.id.pending);
        String dummyitems[] = {"sadrgh", "wasrgw", "erg"};
        final int dummyquantity[] = {1, 2, 4}, price[] = {54, 14, 10};
        itemArrayList.add(new Item("Customer name", dummyitems, dummyquantity, price));
        itemArrayList.add(new Item("Customer name", dummyitems, dummyquantity, price));
        itemArrayList.add(new Item("Customer name", dummyitems, dummyquantity, price));

        acceptBtn.setOnClickListener(this);
        pendingBtn.setOnClickListener(this);
}

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(acceptBtn))
        {
            Intent intent = new Intent(getBaseContext(), OrderScanner.class);
            startActivity(intent);
        }
        else if(v.equals(pendingBtn))
        {
            Intent intent=new Intent(getApplicationContext(),PendingActivity.class);
            startActivity(intent);
        }

    }
}
