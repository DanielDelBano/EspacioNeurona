package com.example.danieldelbano.espacioneurona;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Sala2Activity extends AppCompatActivity {

    Button btnReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala2);
        btnReserva=(Button)findViewById(R.id.btnReservaSala2);
        btnReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SalasActivity.class);
                intent.putExtra("idsActivity",view.getId());
                startActivity(intent);
            }
        });
    }
}
