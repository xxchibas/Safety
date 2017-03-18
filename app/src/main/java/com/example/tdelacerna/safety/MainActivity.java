package com.example.tdelacerna.safety;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button AlertButton;
    Button sosButton;
    Button socialButton;
    Button sign_out;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertButton = (Button) findViewById(R.id.AlertButton);
        sosButton = (Button) findViewById(R.id.sosButton);
        socialButton = (Button) findViewById(R.id.socialButton);
        sign_out = (Button) findViewById(R.id.sign_out);

        AlertButton.setOnClickListener(this);
        sosButton.setOnClickListener(this);
        socialButton.setOnClickListener(this);
        sign_out.setOnClickListener(this);

        FirebaseAuth.getInstance().signOut();

    }



    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.AlertButton:
                startActivity(new Intent(this, alertActivity.class));
                break;

            // Reserve this for compiling the whole code.


            case R.id.sosButton:
                startActivity(new Intent(this, sosActivity.class));
                break;


            case R.id.socialButton:
            startActivity(new Intent(this, Chat.class));
                break;

            case R.id.sign_out:
                startActivity(new Intent(this, LoginActivity.class));

        }

    }




}
