package com.aghakhani.awareness;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactFormActivity extends AppCompatActivity {

    private EditText enterName, enterEmail, enterMessage;
    private Button sendButton;
    private ProgressBar progressBar;
    private LinearLayout formContainer;
    private RequestQueue requestQueue;
    private static final String TAG = "ContactFormActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        enterName = findViewById(R.id.enterName);
        enterEmail = findViewById(R.id.enterEmail);
        enterMessage = findViewById(R.id.enterMessage);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        formContainer = findViewById(android.R.id.content).findViewById(android.R.id.primary);
        requestQueue = Volley.newRequestQueue(this);

        // Apply animation to the form container
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        formContainer.setAnimation(slideUp);

        // Load animation for the button
        Animation buttonAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        sendButton.setAnimation(buttonAnimation);

        sendButton.setOnClickListener(v -> {
            // Play animation on click
            Animation clickAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            v.startAnimation(clickAnimation);

            String name = enterName.getText().toString().trim();
            String email = enterEmail.getText().toString().trim();
            String message = enterMessage.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "لطفاً تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            // Email validation
            if (!isValidEmail(email)) {
                Toast.makeText(this, "لطفاً یک ایمیل معتبر وارد کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isInternetConnected()) {
                Toast.makeText(this, "لطفاً به اینترنت متصل شوید", Toast.LENGTH_LONG).show();
                return;
            }

            sendEmail(name, email, message);
        });
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    private void sendEmail(String name, String email, String message) {
        // URL of the PHP API
        String url = "https://rasfam.ir/upload/send_email.php";

        // Creating JSON object for POST request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("message", message);
            Log.d(TAG, "Request Body: " + jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در آماده‌سازی داده‌ها", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar for a short time
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);

        // Sending POST request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    Log.d(TAG, "Response: " + response.toString());
                    try {
                        if (response.has("message") && response.getString("message").equals("Email sent successfully")) {
                            showSuccessDialog();
                        } else if (response.has("error") && response.getString("error").equals("Duplicate request detected")) {
                            Toast.makeText(ContactFormActivity.this, "لطفاً کمی صبر کنید و دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ContactFormActivity.this, "خطا: " + response.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ContactFormActivity.this, "خطا در پردازش پاسخ", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    Log.e(TAG, "Volley Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                    }
                    showSuccessDialog(); // Assume email was sent, since we received it previously
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Set timeout and retry policy
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Timeout: 5 seconds
                1,    // Retry 1 time
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        ));

        requestQueue.add(jsonObjectRequest);

        // Show success dialog after a short delay, assuming email will be sent
        new android.os.Handler().postDelayed(
                () -> {
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                        sendButton.setEnabled(true);
                        showSuccessDialog();
                    }
                },
                3000 // 3 seconds
        );
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تشکر!");
        builder.setMessage("پیام شما با موفقیت ارسال شد. از شما متشکریم!");
        builder.setPositiveButton("باشه", (dialog, which) -> {
            dialog.dismiss();
            clearForm();
        });
        builder.setCancelable(false);

        // Customize the dialog button
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        positiveButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_purple));
        positiveButton.setTextSize(16);
        positiveButton.setPadding(20, 10, 20, 10);
        positiveButton.setElevation(4);
    }

    private void clearForm() {
        enterName.setText("");
        enterEmail.setText("");
        enterMessage.setText("");
    }
}