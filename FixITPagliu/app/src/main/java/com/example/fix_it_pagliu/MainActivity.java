package com.example.fix_it_pagliu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity{
    private FirebaseAuth auth;
    TextView verifyMsg;
    Button resendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.emailNotVerified);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        if(!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);
            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "È stata inviata una e-Mail di verifica.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Errore nell'invio della e-Mail.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1 :
                Toast.makeText(this, "item 1 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2 :
                Toast.makeText(this, "item 2 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3 :
                Toast.makeText(this, "item 3 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem1 :
                Toast.makeText(this, "sub item 1 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem2 :
                Toast.makeText(this, "sub item 2 selected", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}