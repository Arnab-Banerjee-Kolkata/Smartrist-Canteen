package cardproject.android.arnab.canteen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tempos21.t21crypt.exception.CrypterException;
import com.tempos21.t21crypt.exception.DecrypterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrderScanner extends AppCompatActivity
{
    String information="";
    RelativeLayout orderScanWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_scanner);

        orderScanWait=findViewById(R.id.orderScanWait);
        orderScanWait.setVisibility(View.GONE);

        startScanner();
    }

    private void startScanner()
    {
        IntentIntegrator integrator=new IntentIntegrator(OrderScanner.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setCameraId(0);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scanning");
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null && result.getContents()!=null)
        {
            orderScanWait.setVisibility(View.VISIBLE);
            makeScreenUnresponsive();

            String encodedInfo=result.getContents();
            QR qr=new QR(getApplicationContext(),this.getResources().getString(R.string.KEY_TOKEN));
            try {
                information=qr.getDecryptedString(encodedInfo);
                checkOtpValidity(getApplicationContext(),information);

            } catch (CrypterException e) {
                e.printStackTrace();
            } catch (DecrypterException e) {
                e.printStackTrace();
            }

        }
        if(resultCode==0)
        {
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void checkOtpValidity(final Context mContext, final String information)
    {
        final String fields[]=information.split("@");
        final long id=Long.parseLong(fields[0]);
        Response.Listener<String> detailsResponseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonResponse = null;

                orderScanWait.setVisibility(View.GONE);
                makeWindowResponsive();
                if (response != null)
                {
                    try
                    {
                        jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success)
                        {
                            int activeState = jsonResponse.getInt("activeState");
                            if (activeState == 1) {
                                //$Id, $personName, $grade, $department, $orgWalletVal, $activeState, $OTP


                                if (Integer.parseInt(fields[6]) == jsonResponse.getInt("OTP"))
                                {
                                   // Toast.makeText(getApplicationContext(),information,Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(getApplicationContext(),OrderDetails.class);
                                    intent.putExtra("info",information);
                                    startActivity(intent);
                                    finish();

                                }
                                else
                                {
                                    Toast.makeText(mContext, "Invalid card", Toast.LENGTH_SHORT).show();
                                    finish();
                                }


                            } else {
                                if (activeState == 0) {
                                    Toast.makeText(mContext, "Card not active. Contact office", Toast.LENGTH_SHORT).show();
                                    params.put("message", "Failed");
                                    finish();

                                }
                            }
                        } else {
                            Toast.makeText(mContext, "Invalid Id. Contact office", Toast.LENGTH_SHORT).show();
                            params.put("message", "Failed");
                            finish();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Some error occured", Toast.LENGTH_SHORT).show();
                        params.put("message", "Failed");
                        finish();
                    }

                }
            }
        };



        String CANDIDATE_DETAILS_URL= String.format("http://arnabbanerjee.dx.am/requestCandidateDetails.php?id=%1$d",id);

        VolleyGetRequest requestDetails=new VolleyGetRequest(CANDIDATE_DETAILS_URL,detailsResponseListener);
        RequestQueue queue=Volley.newRequestQueue(mContext);
        queue.add(requestDetails);
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

    public void makeScreenUnresponsive()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void makeWindowResponsive()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
