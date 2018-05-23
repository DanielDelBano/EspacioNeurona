package com.example.danieldelbano.espacioneurona;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TabHost;

import java.math.BigDecimal;
import java.util.Calendar;

import android.widget.TextView;
import android.widget.Toast;

import com.example.danieldelbano.espacioneurona.Config.Config;
import com.example.danieldelbano.espacioneurona.Correo.GMailSender;
import com.example.danieldelbano.espacioneurona.Config.FirebaseReferences;
import com.example.danieldelbano.espacioneurona.Objetos.Reserva;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import javax.mail.Session;

public class FinResCoworkingActivity extends AppCompatActivity implements TabHost.OnTabChangeListener{

    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)  //usamos sandbox porque estamos en test
            .clientId(Config.PAYPAL_CLIENT_ID);

    private Session session;
    /*-----------------*/
     //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Widgets
    EditText etFecha,etFecha2;
    private TextView txtPrecio;
    private Button btnFinRes;
    private int precio;
    private String amount;
    private int cont,diaR,mesR,anyoR;
    //calendar
    private TextView txtCalendar;
    private CalendarView calendarView;
    //firebase
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    final DatabaseReference bbddCoworking=database.getReference(FirebaseReferences.COWORKING_REFERENCE);

    private String usuario,nameUser,emailUser,date,fechaBBDD,lugar,complementos,correo,password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_res_coworking);
        //obtenemos datos usuario
        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user =firebaseAuth.getCurrentUser();
                if (user != null){
                    setUserData(user);
                }


            }
        };

        //Iniciamos Paypal Service
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        fechaBBDD="";
        lugar="Espacio Coworking";
        complementos="";
        //CalendarView
        final Calendar calendar=Calendar.getInstance();
        txtCalendar=(TextView)findViewById(R.id.txtCalendar);
        calendarView=(CalendarView)findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                diaR=dayOfMonth;
                mesR=month;
                anyoR=year;
                date = dayOfMonth + "/" + (month+1) + "/" + year;
                fechaBBDD=String.valueOf(dayOfMonth).concat(String.valueOf((month+1))).concat(String.valueOf(year));
                txtCalendar.setText(date);
                //Toast.makeText(FinResCoworkingActivity.this, fechaBBDD, Toast.LENGTH_SHORT).show();
                ComprobarDisponibilidad(fechaBBDD);

            }

        });

        precio=15;
        amount="";
        nameUser="";
        emailUser="";

        TabHost tabhost=(TabHost)findViewById(R.id.tabHostCoworking);
        tabhost.setup();
        //tab 1 dia
        TabHost.TabSpec tab1=tabhost.newTabSpec("tab1t");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("BONO 1 DIA");
        //tab 1 mes
        TabHost.TabSpec tab2=tabhost.newTabSpec("tab2t");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("BONO 1 MES");

        tabhost.addTab(tab1);
        tabhost.addTab(tab2);

        tabhost.setOnTabChangedListener(this);


        usuario="";

        btnFinRes=(Button)findViewById(R.id.btnResCW);
        btnFinRes.setEnabled(true);
        txtPrecio=(TextView)findViewById(R.id.txtPrecioCoworking);
        txtPrecio.setText(String.valueOf(precio) + "€");


        btnFinRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtCalendar.getText().toString().isEmpty()){
                    Toast.makeText(FinResCoworkingActivity.this, "Debes seleccionar una fecha", Toast.LENGTH_SHORT).show();
                }else{
                    //permitir pago
                    ProcesarPagoPaypal();

                    //EnviarCorreo(reserva);
                }
            }
        });

    }

    private void EnviarCorreo(final Reserva reserva){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("daniel86zgz@gmail.com",
                            "scarhacr");
                    sender.sendMail("Reserva en Espacio Neurona", reserva.toString(),
                            "daniel86zgz@gmail.com", "daniel86zgz@gmail.com");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }


    private void setUserData(FirebaseUser user) {
        nameUser=user.getDisplayName();
        emailUser=user.getEmail();
        //Glide.with(this).load(user.getPhotoUrl()).into(photoImageView);  inserta foto usuario
    }

    private void ProcesarPagoPaypal(){
        amount=txtPrecio.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(precio),"EUR","Reserva en Neurona",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent=new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);

    }

    private void ComprobarDisponibilidad(String fechaRes){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.COWORKING_REFERENCE).child(fechaRes);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cont=0;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    cont++;
                }
                if (cont<8){
                    btnFinRes.setBackgroundColor(Color.parseColor("#30c7bb"));
                    btnFinRes.setEnabled(true);

                }else{
                    //bloqueo boton si esta completo el dia
                    btnFinRes.setBackgroundColor(Color.DKGRAY);
                    btnFinRes.setEnabled(false);
                    Toast.makeText(FinResCoworkingActivity.this, "Día completo, lo sentimos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null){
                    try{
                        String paymentDetails= confirmation.toJSONObject().toString(4);
                        Intent intent=new Intent(this,PaymentDetails.class);
                        Reserva reserva = new Reserva(nameUser,emailUser,date,lugar);
                        bbddCoworking.child(fechaBBDD).push().setValue(reserva);
                        EnviarCorreo(reserva);
                        Bundle bundle=new Bundle();
                        bundle.putInt("dia",diaR);
                        bundle.putInt("mes",mesR);
                        bundle.putInt("anyo",anyoR);
                        bundle.putSerializable("reserva",reserva);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener !=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equalsIgnoreCase("tab1t")){
            precio=15;
            String precioString=String.valueOf(precio);
            txtPrecio.setText(precioString + "€");
        }else if(tabId.equalsIgnoreCase("tab2t")){
            precio=59;
            String precioString=String.valueOf(precio);
            txtPrecio.setText(precioString + "€");
        }
    }
}
