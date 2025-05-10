package com.aghakhani.awareness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Website Link
        LinearLayout websiteLink = findViewById(R.id.websiteLink);
        websiteLink.setOnClickListener(v -> openLink("https://mazdafar.com"));

        // Instagram Link
        LinearLayout instagramLink = findViewById(R.id.instagramLink);
        instagramLink.setOnClickListener(v -> openLink("https://instagram.com/mazdafar.momeni"));

        // Telegram Link
        LinearLayout telegramLink = findViewById(R.id.telegramLink);
        telegramLink.setOnClickListener(v -> openLink("https://t.me/mazdafarcom"));

        // Facebook Link
        LinearLayout facebookLink = findViewById(R.id.facebookLink);
        facebookLink.setOnClickListener(v -> openLink("https://facebook.com/Mazdafar-Momeni-107369438230653"));

        // YouTube Link
        LinearLayout youtubeLink = findViewById(R.id.youtubeLink);
        youtubeLink.setOnClickListener(v -> openLink("https://www.youtube.com/@Mazdafar"));
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}