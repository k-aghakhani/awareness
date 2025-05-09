package com.aghakhani.awareness;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;
    private List<Audio> audioList;
    private static final String API_URL = "https://rasfam.ir/upload/get_audio_list.php";
    private AlertDialog noInternetDialog;
    private Handler handler;
    private Runnable checkInternetRunnable;
    private Typeface vazirRegular;
    private Typeface vazirBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load fonts
        vazirRegular = Typeface.createFromAsset(getAssets(), "fonts/Vazir-Regular.ttf");
        vazirBold = Typeface.createFromAsset(getAssets(), "fonts/Vazir-Bold.ttf");

        // Apply font to app title
        TextView tvAppTitle = findViewById(R.id.tv_app_title);
        tvAppTitle.setTypeface(vazirBold);

        handler = new Handler(Looper.getMainLooper());

        // Check internet connection
        if (!isInternetConnected()) {
            showNoInternetDialog();
            return;
        }

        // Initialize RecyclerView
        initializeRecyclerView();
        fetchAudioList();
    }

    // Check internet connection
    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Initialize RecyclerView
    private void initializeRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(this, audioList);
        recyclerView.setAdapter(audioAdapter);
    }

    // Show no internet dialog
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);

        // Custom title with font
        TextView titleView = new TextView(this);
        titleView.setText("اتصال به دنیای آگاهی!");
        titleView.setTextSize(20);
        titleView.setPadding(40, 40, 40, 20);
        titleView.setTextColor(getResources().getColor(android.R.color.black));
        titleView.setTypeface(vazirBold);
        builder.setCustomTitle(titleView);

        // Custom message with font
        TextView messageView = new TextView(this);
        messageView.setText("به نظر می‌رسد به اینترنت وصل نیستید. لطفاً اینترنت را فعال کنید تا از محتوای ارزشمند ما لذت ببرید. ما اینجا منتظر شما هستیم!");
        messageView.setTextSize(16);
        messageView.setPadding(40, 20, 40, 20);
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setTypeface(vazirRegular);
        builder.setView(messageView);

        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("تلاش مجدد", (dialog, which) -> startInternetCheck())
                .setNegativeButton("خروج", (dialog, which) -> finish())
                .setCancelable(false);

        noInternetDialog = builder.create();
        noInternetDialog.setCanceledOnTouchOutside(false);
        noInternetDialog.show();

        // Apply font to dialog buttons
        Button positiveButton = noInternetDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = noInternetDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (positiveButton != null) {
            positiveButton.setTypeface(vazirRegular);
        }
        if (negativeButton != null) {
            negativeButton.setTypeface(vazirRegular);
        }

        startInternetCheck();
    }

    // Start periodic internet check
    private void startInternetCheck() {
        if (checkInternetRunnable != null) {
            handler.removeCallbacks(checkInternetRunnable);
        }

        checkInternetRunnable = new Runnable() {
            @Override
            public void run() {
                if (isInternetConnected()) {
                    noInternetDialog.dismiss();
                    initializeRecyclerView();
                    fetchAudioList();
                } else {
                    Toast.makeText(MainActivity.this, "هنوز به اینترنت وصل نیستید!", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(this, 3000);
                }
            }
        };
        handler.post(checkInternetRunnable);
    }

    // Fetch audio list from API
    private void fetchAudioList() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            audioList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Audio audio = new Audio(
                                        jsonObject.getInt("id"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("audio_url"),
                                        jsonObject.getString("thumbnail_url")
                                );
                                audioList.add(audio);
                            }
                            audioAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioAdapter != null) {
            audioAdapter.releaseMediaPlayer();
        }
        if (handler != null && checkInternetRunnable != null) {
            handler.removeCallbacks(checkInternetRunnable);
        }
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }
}