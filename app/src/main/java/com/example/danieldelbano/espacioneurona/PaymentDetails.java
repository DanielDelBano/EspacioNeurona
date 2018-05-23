package com.example.danieldelbano.espacioneurona;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danieldelbano.espacioneurona.Objetos.Reserva;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class PaymentDetails extends AppCompatActivity {

    TextView txtStatus,txtFecha,txtLugar,txtHora;
    private ArrayList<String>arrHorasReserva;
    //EditText año = null;
    //Spinner mes = null;
    //EditText dia = null;                    //
    //CheckBox duracion = null;              // Declaracion de Recursos
    //Spinner hora = null;                  //
    //EditText minuto = null;              //
    //ButtonBarLayout agregar = null;     //
    //EditText titulo = null;            //
    //EditText descripcion = null;      //
    //EditText localizacion = null;
    private int dia,mes,anyo;
    private String lugar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        //Get intent
        Intent intent =getIntent();
        Bundle bundle=getIntent().getExtras();
        Reserva reserva=null;
        if (bundle!=null){
            dia=bundle.getInt("dia");
            mes=bundle.getInt("mes");
            anyo=bundle.getInt("anyo");
            reserva=(Reserva) bundle.getSerializable("reserva");
            lugar=reserva.getLugar();
            arrHorasReserva=reserva.getHora();
        }
        /*try{
            JSONObject jsonObject=new JSONObject(getIntent().getStringExtra("PaymentDetails"));
            //showDetails(jsonObject.getJSONObject("response"),intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.layoutPayments);
        Snackbar snackbar=Snackbar.make(constraintLayout,"Añadir recordatorio a mi calendario",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Aceptar", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarEvento();
            }
        });
        snackbar.show();
    }

   /* private void showDetails(JSONObject response, String paymentAmount) {

        try{
            //txtStatus.setText(response.getString("state"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    public void AgregarEvento() {

        //for(String s:arrHorasReserva){
            Calendar cal = Calendar.getInstance();


            boolean val = false; //Controlador del ciclo while
            Intent intent = null;
            while (val == false) {

                try {
                    cal.set(Calendar.YEAR, anyo);                 //
                    cal.set(Calendar.MONTH, mes);   // Set de AÑO MES y Dia
                    cal.set(Calendar.DAY_OF_MONTH, dia);       //


                    //cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((hora.getSelectedItem().toString())));// Set de HORA y MINUTO
                    //cal.set(Calendar.MINUTE, Integer.parseInt(minuto.getText().toString()));            //

                    intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");

                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis() + 60 * 60 * 1000);
                    if (lugar.equalsIgnoreCase("Espacio Coworking")){
                        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
                    }

                    intent.putExtra(CalendarContract.Events.TITLE, "Reserva en Espacio Neurona");
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, lugar);
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Espacio Neurona");

                    startActivity(intent);
                    val = true;
                } catch (Exception e) {
                    //año.setText("");
                    //dia.setText("");
                    Toast.makeText(getApplicationContext(), "Fecha Inválida", Toast.LENGTH_LONG).show();
                }
            }
        }


   // }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}
