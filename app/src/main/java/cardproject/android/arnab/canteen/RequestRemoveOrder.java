package cardproject.android.arnab.canteen;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestRemoveOrder extends StringRequest
{
    private static String REGISTER_REQUEST_URL= "http://arnabbanerjee.dx.am/RequestPostRemoveOrder.php";
    private Map<String, String> params;
    int MY_SOCKET_TIMEOUT_MS=60000;

    public RequestRemoveOrder(long id, Response.Listener<String> listener)
    {
        super(Method.POST,REGISTER_REQUEST_URL,listener,null);
        params=new HashMap<>();
        params.put("candidateId",id+"");

        this.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
