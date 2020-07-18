package cardproject.android.arnab.canteen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IndivPending extends AppCompatActivity implements View.OnClickListener
{
    long id;
    int totalItems;
    RelativeLayout indivWait;
    LinearLayout listView;
    TextView foodname, quantity;
    Button serveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indiv_pending);



        Intent intent=getIntent();
        totalItems=Integer.parseInt(intent.getStringExtra("totalItems"));
        id=Long.parseLong(intent.getStringExtra("id"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView imageView1 = findViewById(R.id.imageView2);
        indivWait=findViewById(R.id.indivWait);
        listView = findViewById(R.id.list2);
        serveBtn=findViewById(R.id.serveBtn);


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

        indivWait.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();

        getIndivPending(getApplicationContext(), id);

        serveBtn.setOnClickListener(this);
    }

    void getIndivPending(final Context mContext, final long canId)
    {
        Response.Listener<String> classesResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                indivWait.setVisibility(View.GONE);
                makeWindowResponsive();
                //erMsg.setText(response);
                //Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                try
                {
                    JSONArray array = new JSONArray(response);
                    //Toast.makeText(mContext,"len="+response+"",Toast.LENGTH_SHORT).show();
                    int total=0;
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject pending = array.getJSONObject(i);

                        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
                        listView.addView(v);
                        foodname = findViewById(R.id.name);
                        quantity = findViewById(R.id.quantity);
                        foodname.setText(pending.getString("itemName"));
                        foodname.setId(i * 10);
                        quantity.setText("Quantity : " + Integer.toString(pending.getInt("quantity")));
                        quantity.setId(i * 10 + 1);
                        CheckBox c = findViewById(R.id.checkbox);
                        c.setId(i * 10 + 2);
                        c.setVisibility(View.INVISIBLE);
                        total+=pending.getInt("quantity");
                    }
                    TextView temp = findViewById(R.id.by2);
                    temp.setText("Order by: " + id);
                    temp = findViewById(R.id.number2);
                    temp.setText("Items in this order : " + total);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String INDIV_PENDING_URL= String.format("http://arnabbanerjee.dx.am/RequestGetIndivPending.php?id=%1$d",canId);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(INDIV_PENDING_URL,classesResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
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

    @Override
    public void onClick(View v)
    {
        if(v.equals(serveBtn))
        {
            indivWait.setVisibility(View.VISIBLE);
            makeScreenUnresponsive();
            removePendingOrder(getApplicationContext(),id);
        }

    }

    void removePendingOrder(final Context mContext, final long id)
    {
        indivWait.setVisibility(View.VISIBLE);
        makeWindowResponsive();
        Response.Listener<String> removeResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Served!",Toast.LENGTH_SHORT).show();
                        finish();

                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        };
        RequestDeleteOrder requestDeleteOrder=new RequestDeleteOrder(id,removeResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestDeleteOrder);
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
}
