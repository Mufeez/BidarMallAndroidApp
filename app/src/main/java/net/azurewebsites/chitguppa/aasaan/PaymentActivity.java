package net.azurewebsites.chitguppa.aasaan;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.util.HashMap;


public class PaymentActivity extends Activity implements PaymentResultWithDataListener, ExternalWalletListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private AlertDialog.Builder alertDialogBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(getApplicationContext());
        startPayment();
        // Payment button created by you in XML layout
      //  Button button = (Button) findViewById(R.id.btn_pay);

        alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            //do nothing
        });



    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();





        try {
                JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("options"));
                int amount= jsonObj.getInt("amount");
                String razorPayOrderId= jsonObj.getString("order_id");
                String token = jsonObj.getString("token");
                System.out.println(getIntent().getStringExtra("options"));
                JSONObject options = new JSONObject();
                options.put("key","rzp_live_rzbaCTlI14GAkM");
                options.put("name", "Ratail Center");
                options.put("description", "Total Amount");
                options.put("order_id",razorPayOrderId);
                options.put("send_sms_hash",true);
                options.put("allow_rotation", true);
               // options.put("callback_url","https://www.mufeed.co.in/api/payment/pay-order?razorPayOrderId="+razorPayOrderId);
               // options.put("redirect","true");
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                options.put("currency", "INR");
                options.put("amount", 500);

                JSONObject preFill = new JSONObject();
                preFill.put("email", "mufeez.ahmed22@gmail.com");
                preFill.put("contact", "8861710458");

                options.put("prefill", preFill);

                co.open(activity, options);
            } catch (Exception e) {
                Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
        }




    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */


    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("External Wallet Selected:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try{
            JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("options"));
            int amount= jsonObj.getInt("amount");
            String razorPayOrderId= jsonObj.getString("order_id");
            String token = jsonObj.getString("token");

          /*  HashMap<String, String> headers = new HashMap<String, String>();
            String authValue = "Bearer " + token;
            headers.put("Authorization", authValue);
            headers.put("Accept", "application/json; charset=UTF-8");
            headers.put("Content-Type", "application/json; charset=UTF-8");*/



            String url = "https://www.mufeed.co.in/api/payment/pay-order";

// Request a string response from the provided URL.

// Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("amount", String.valueOf(amount));
            params.put("razorpayOrderId", paymentData.getData().getString("razorpay_order_id"));
            params.put("razorpayPaymentId", paymentData.getData().getString("razorpay_payment_id"));
            params.put("razorpaySignature", paymentData.getData().getString("razorpay_signature"));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            alertDialogBuilder.setMessage("Response: " + response.toString());
                            finish();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            finish();
                        }

                    });


            MySingleTon.getInstance(this).addToRequestQue(jsonObjectRequest);

           // alertDialogBuilder.setMessage("Payment Successful :\nPayment ID: "+s+"\nPayment Data: "+paymentData.getData());
          //  alertDialogBuilder.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


