package com.aghakhani.awareness;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        // Set up FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showMenu(v));

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

    private void showMenu(View v) {
        ListPopupWindow popup = new ListPopupWindow(this);
        popup.setAnchorView(v); // Anchor to FAB

        // Menu items with icons
        String[][] menuData = {
                {"مالکیت فکری", "ic_info"},
                {"تماس با ما", "ic_phone"},
                {"فرم تماس", "ic_edit"}
        };

        // Custom ArrayAdapter
        ArrayAdapter<String[]> adapter = new ArrayAdapter<String[]>(this, R.layout.menu_item, menuData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_item, parent, false);
                }
                TextView textView = convertView.findViewById(android.R.id.text1);
                ImageView iconView = convertView.findViewById(R.id.icon);

                String[] item = getItem(position);
                textView.setText(item[0]);
                textView.setTextDirection(View.TEXT_DIRECTION_RTL); // Ensure RTL text
                textView.setGravity(Gravity.END); // Align text to the right
                textView.setTypeface(vazirRegular); // Apply custom font
                textView.setTextColor(getResources().getColor(android.R.color.black));

                // Set icon based on resource name
                String iconName = item[1];
                int resId = getResources().getIdentifier(iconName, "drawable", getPackageName());
                if (resId != 0) {
                    iconView.setImageResource(resId);
                } else {
                    iconView.setVisibility(View.GONE); // Hide if icon not found
                }

                return convertView;
            }
        };

        // Set the adapter
        popup.setAdapter(adapter);

        // Set width and height
        popup.setWidth(400); // Increased width for better text and icon display
        popup.setHeight(ListPopupWindow.WRAP_CONTENT);

        // Align to the right (RTL)
        popup.setHorizontalOffset(-v.getWidth()); // Align to the right of FAB
        popup.setVerticalOffset(-v.getHeight() / 2); // Adjust vertical position

        // Set background
        popup.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));

        // Handle item clicks
        popup.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: // Intellectual Property
                    startActivity(new Intent(MainActivity.this, IntellectualPropertyActivity.class));
                    break;
                case 1: // Contact Us
                    startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                    break;
                case 2: // Contact Form
                    startActivity(new Intent(MainActivity.this, ContactFormActivity.class));
                    break;
            }
            popup.dismiss();
        });

        popup.show();
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