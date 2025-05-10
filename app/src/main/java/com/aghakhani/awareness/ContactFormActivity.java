package com.aghakhani.awareness;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ContactFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        EditText enterName = findViewById(R.id.enterName);
        EditText enterEmail = findViewById(R.id.enterEmail);
        EditText enterMessage = findViewById(R.id.enterMessage);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String name = enterName.getText().toString().trim();
            String email = enterEmail.getText().toString().trim();
            String message = enterMessage.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "لطفاً تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            sendEmail(name, email, message);
        });
    }

    private void sendEmail(String name, String email, String message) {
        String[] recipients = new String[]{"kiarash1988@gmail.com", "aghakhaniedu@gmail.com"};
        String subject = "پیام جدید از " + name;
        String body = "نام: " + name + "\n" +
                "ایمیل: " + email + "\n" +
                "پیام: " + message;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, "ارسال ایمیل..."));
        } catch (Exception e) {
            Toast.makeText(this, "خطا در ارسال ایمیل: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}