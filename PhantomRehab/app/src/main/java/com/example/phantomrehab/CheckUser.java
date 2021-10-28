package com.example.phantomrehab;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CheckUser extends AppCompatActivity {

    private String name;
    private String email;
    private String uid;
    private boolean emailVerified;
    private TextView Name, Email, EmailVerified, Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user);

        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        EmailVerified = findViewById(R.id.email_verified);
        Uid = findViewById(R.id.uid);

        //Firebase code

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //profile update
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        //access the user
        if (user != null) {

            //profile update
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User profile updated.");
                            }
                        }
                    });

            // Name, email address, and profile photo Url
            name = user.getDisplayName();
            email = user.getEmail();
            emailVerified = user.isEmailVerified();
            uid = user.getUid();

            Name.setText(name);
            Email.setText(email);
            Uid.setText(uid);

            if (emailVerified) {
                EmailVerified.setVisibility(View.VISIBLE);
            }
        }
    }

}
