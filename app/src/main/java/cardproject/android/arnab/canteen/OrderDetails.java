package cardproject.android.arnab.canteen;

import android.app.Activity;
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
import android.widget.CompoundButton;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener
{
    ImageView detailsBg;
    Button acceptBtn, cancelBtn;
    RelativeLayout orderWait,orderMsg;
    TextView name, quantity;
    LinearLayout listView;
    int len=0,totalCost=0,walletBalance=0;
    String information;
    long id;
    ArrayList<String> itemNames, selectedNames;
    ArrayList<Integer> quantities,prices, selectedQuantities, selectedPrices;
    int[] total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Order Summary");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        information=intent.getStringExtra("info");
        String str[]=information.split("@");
        id=Long.parseLong(str[0]);
        walletBalance=Integer.parseInt(str[4]);

        detailsBg=findViewById(R.id.detailsBg);
        acceptBtn=findViewById(R.id.accept2);
        cancelBtn=findViewById(R.id.cancel);
        orderWait=findViewById(R.id.orderWait);
        listView = findViewById(R.id.list);
        orderMsg=findViewById(R.id.orderMsg);


        orderMsg.setVisibility(View.GONE);
        orderWait.setVisibility(View.VISIBLE);
        makeScreenUnresponsive();

        int resid = R.drawable.canbg;

        Glide
                .with(this)
                .load(resid).into(detailsBg);

        getOrderDetails(getApplicationContext(),id);

        TextView temp = findViewById(R.id.by);
        temp.setText("Order by: " + id);


        acceptBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);




    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(acceptBtn))
        {
            int count = 0;
            for (int i = 0; i < len; i++) {
                CheckBox c = findViewById(i * 10 + 2);
                if (c.isChecked()) {
                    count++;
                }
            }
            if (count != 0)
            {
                selectedNames=new ArrayList<String>();
                selectedQuantities=new ArrayList<Integer>();
                selectedPrices=new ArrayList<Integer>();

                orderWait.setVisibility(View.VISIBLE);
                makeScreenUnresponsive();

                count = 0;
                for (int i = 0; i <len; i++) {
                    CheckBox c = findViewById(i * 10 + 2);
                    if (c.isChecked())
                    {
                        selectedNames.add(itemNames.get(i));
                        selectedQuantities.add(quantities.get(i));
                        selectedPrices.add(prices.get(i));

                        //Toast.makeText(getApplicationContext(),"qty="+quantities.get(i),Toast.LENGTH_LONG).show();

                        totalCost+=prices.get(i)*quantities.get(i);
                        count++;
                    }
                }

                if(walletBalance>=totalCost)
                {
                    walletBalance=walletBalance-totalCost;
                    updateWalletBalance(getApplicationContext(), id, walletBalance);
                    updateTransaction(getApplicationContext(),id,totalCost,walletBalance);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Insufficient Balance",Toast.LENGTH_SHORT).show();
                    orderWait.setVisibility(View.GONE);
                    makeWindowResponsive();
                }
            } else {
                Toast.makeText(getBaseContext(), "Select an item !", Toast.LENGTH_SHORT).show();
                orderWait.setVisibility(View.GONE);
                makeWindowResponsive();
            }

        }
        else if(v.equals(cancelBtn))
        {
            removeOrders(getApplicationContext(),id);
            finish();
        }

    }

    void updateTransaction(final Context mContext,final long id, final int transAmt, final int balance)
    {
        Response.Listener<String> postTransResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                //Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        //Toast.makeText(mContext,"Transaction Succes",Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(mContext,"OTP not changed",Toast.LENGTH_SHORT).show();
                }


            }
        };

        Date cDate = new Date();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        String type="debit";
        String details="Canteen Food";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());

        RequestPostTransaction requestPostTransaction=new RequestPostTransaction(id,type,details,date,time,balance,
                transAmt,postTransResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestPostTransaction);
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

    void getOrderDetails(final Context mContext, final long canId)
    {
        Response.Listener<String> orderResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                orderWait.setVisibility(View.GONE);
                makeWindowResponsive();
                //erMsg.setText(response);
                try
                {
                    JSONArray array = new JSONArray(response);
                    //Toast.makeText(mContext,"len="+response+"",Toast.LENGTH_SHORT).show();
                    itemNames=new ArrayList<String>();
                    quantities=new ArrayList<Integer>();
                    prices=new ArrayList<Integer>();
                    total=new int[array.length()];
                    len=array.length();
                    TextView temp = findViewById(R.id.number);
                    temp.setText("Items in this order : " + Integer.toString(len));
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject order = array.getJSONObject(i);

                        itemNames.add(order.getString("itemName"));
                        quantities.add(order.getInt("quantity"));
                        prices.add(order.getInt("price"));


                        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
                        listView.addView(v);
                        name = findViewById(R.id.name);
                        quantity = findViewById(R.id.quantity);
                        name.setText(order.getString("itemName"));
                        name.setId(i * 10);
                        quantity.setText("Quantity : " + Integer.toString(order.getInt("quantity")));
                        quantity.setId(i * 10 + 1);
                        CheckBox c = findViewById(R.id.checkbox);
                        c.setId(i * 10 + 2);
                        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    TextView t = findViewById(buttonView.getId() - 1);
                                    String c = "" + t.getText().toString().charAt(t.getText().length() - 1);
                                    total[buttonView.getId() / 10] = Integer.parseInt(c) * prices.get(buttonView.getId() / 10);
                                } else {
                                    total[buttonView.getId() / 10] = 0;
                                }
                                TextView temp2 = findViewById(R.id.total);
                                int sum = 0;
                                for (int i = 0; i < total.length; i++)
                                    sum += total[i];
                                temp2.setText("Order total : Rs. " + Integer.toString(sum));
                            }
                        });


                    }
                    if(len==0)
                    {
                        orderMsg.setVisibility(View.VISIBLE);
                        acceptBtn.setEnabled(false);
                        cancelBtn.setEnabled(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        };
        String ORDER_URL= String.format("http://arnabbanerjee.dx.am/RequestGetOrder.php?id=%1$d",canId);
        VolleyGetRequest volleyGetRequest=new VolleyGetRequest(ORDER_URL,orderResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(volleyGetRequest);
    }

    private void updateWalletBalance(final Context mContext,final long id, int orgBalance)
    {
        Response.Listener<String> updateBalanceResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Transaction Successful",Toast.LENGTH_SHORT).show();

                        addPendingOrders(mContext,id, selectedNames, selectedQuantities);
                        removeOrders(mContext,id);

                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    //Toast.makeText(mContext,"OTP not changed",Toast.LENGTH_SHORT).show();
                }


            }
        };
        RequestUpdateBalance requestUpdateBalance=new RequestUpdateBalance(id,orgBalance,updateBalanceResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestUpdateBalance);
    }

    void addPendingOrders(final Context mContext, final long id, final ArrayList<String> selectedNames,
                          final ArrayList<Integer> selectedQuantities)
    {
        Response.Listener<String> pendingResponseListener=new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                //Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        Toast.makeText(mContext,"Posted",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Something went wrong",Toast.LENGTH_SHORT).show();
                    finish();
//                    err.setText(e.toString());
                }


            }
        };
        RequestPostOrder requestPostOrder=new RequestPostOrder(id, selectedNames, selectedQuantities,
                pendingResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestPostOrder);
    }

    void removeOrders(final Context mContext, final long id)
    {
        Response.Listener<String> removeOrderResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject jsonResponse= null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean success=jsonResponse.getBoolean("success");
                    if(success)
                    {
                        //Toast.makeText(mContext,"OTP changed",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Some error occured",Toast.LENGTH_SHORT).show();
                }


            }
        };
        RequestRemoveOrder requestRemoveOrder=new RequestRemoveOrder(id, removeOrderResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestRemoveOrder);
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
