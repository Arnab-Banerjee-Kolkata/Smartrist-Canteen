package cardproject.android.arnab.canteen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PendingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdap adapter;
    private RecyclerView.LayoutManager layoutManager;
    RelativeLayout pendingWait;
    ArrayList<Item> itemArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);


        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rec);
        pendingWait=findViewById(R.id.pendingWait);
        ImageView imageView1 = findViewById(R.id.imageView2);


        toolbar.setTitle("Pending Orders");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int resid = R.drawable.canbg;

        Glide
                .with(this)
                .load(resid).into(imageView1);

        pendingWait.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        pendingWait.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();
        getTotalPending(getApplicationContext());

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void makeScreenUnresponsive()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void makeWindowResponsive()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    void getTotalPending(final Context mContext)
    {
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                //erMsg.setText(response);
                itemArrayList = new ArrayList<>();
                pendingWait.setVisibility(View.GONE);
                makeWindowResponsive();
                try
                {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    //Toast.makeText(mContext,success+"",Toast.LENGTH_SHORT).show();
                    if(success)
                    {
                        String msg="";
                        for (String key : iterate(jsonResponse.keys()))
                        {
                            if(!key.equalsIgnoreCase("success"))
                            {
                                itemArrayList.add(new Item(key, jsonResponse.optInt(key)+""));

                            }
                        }
                        recyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(mContext);
                        adapter = new ItemAdap(itemArrayList);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new ItemAdap.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Intent intent = new Intent(getBaseContext(), IndivPending.class);
                                intent.putExtra("id", itemArrayList.get(position).getId());
                                intent.putExtra("totalItems", itemArrayList.get(position).getTotalItems());
                                startActivity(intent);
                            }
                        });

                    }
                    else
                        Toast.makeText(mContext,"false",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String TOTAL_PENDING_URL= String.format("http://arnabbanerjee.dx.am/RequestGetFullPending.php");
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(TOTAL_PENDING_URL,classesResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }

    private <T> Iterable<T> iterate(final Iterator<T> i){
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return i;
            }
        };
    }
}
