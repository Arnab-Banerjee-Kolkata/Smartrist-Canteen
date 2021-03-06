package cardproject.android.arnab.canteen;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestPostOrder extends StringRequest
{
    private static String REGISTER_REQUEST_URL= "http://arnabbanerjee.dx.am/RequestPostPendingOrder.php";
    private Map<String, String> params;
    int MY_SOCKET_TIMEOUT_MS=60000;

    public RequestPostOrder(long id, ArrayList<String> selectedNames, ArrayList<Integer> selectedQuantities,
                            Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        JSONArray itemNamesJSON = new JSONArray(Arrays.asList(selectedNames));
        JSONArray quantitiesJSON = new JSONArray(Arrays.asList(selectedQuantities));


        params = new HashMap<>();
        params.put("candidateId", id + "");
        params.put("itemName",itemNamesJSON.toString());
        params.put("quantity",quantitiesJSON.toString());

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
