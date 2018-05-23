package com.example.danieldelbano.espacioneurona;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Aula1Activity extends AppCompatActivity {
    Button btnReserva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aula1);
        btnReserva=(Button)findViewById(R.id.btnReservaAula1);
        btnReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),SalasActivity.class);
                i.putExtra("idsActivity",view.getId());
                startActivity(i);
            }
        });
    }
}
