package com.aghakhani.awareness;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check internet connection
        if (!isInternetConnected()) {
            showNoInternetDialog();
            return;
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(this, audioList);
        recyclerView.setAdapter(audioAdapter);

        // Fetch data from API
        fetchAudioList();
    }

    // Check internet connection
    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Show no internet dialog
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اتصال به دنیای آگاهی!")
                .setMessage("به نظر می‌رسد به اینترنت وصل نیستید. لطفاً اینترنت را فعال کنید تا از محتوای ارزشمند ما لذت ببرید. ما اینجا منتظر شما هستیم!")
                .setPositiveButton("تلاش مجدد", (dialog, which) -> {
                    if (isInternetConnected()) {
                        dialog.dismiss();
                        recreate(); // Restart activity to check again
                    } else {
                        Toast.makeText(this, "هنوز به اینترنت وصل نیستید!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("خروج", (dialog, which) -> finish())
                .setCancelable(false); // Prevent closing with back button
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // Prevent closing by touching outside
        dialog.show();
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

        // Add request to Volley queue
        VolleySingleton.getInstance(this).getRequestQueue().add(jsonArrayRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioAdapter != null) {
            audioAdapter.releaseMediaPlayer();
        }
    }
}