package com.example.danieldelbano.espacioneurona;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class CoworkingReservaActivity extends Activity {

    Button btnReservarCoworking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coworking_reserva);

        btnReservarCoworking=(Button)findViewById(R.id.btnReserva);

        btnReservarCoworking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),FinResCoworkingActivity.class);
                startActivity(intent);
            }
        });
    }
}
