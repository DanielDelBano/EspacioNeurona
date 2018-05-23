package com.example.danieldelbano.espacioneurona;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class SalasActivity extends AppCompatActivity implements View.OnClickListener, TabHost.OnTabChangeListener,DatePickerDialog.OnDateSetListener {

    private EditText etPlannedDate;
    private TextView txtPrecio,txtReservandoEn;
    private boolean cw89bt,cw910bt,cw1011bt,cw1112bt,cw1213bt,cw1314bt,cw1415bt,cw1516bt,cw1617bt,cw1718bt,cw1819bt,cw1920bt,esAula1,esAula2;
    private String nameUser,emailUser,fechaBBDD;
    private Button btnFinalizarReserva;
    private int precio,precioTotal,diaR,mesR,anyoR,precioSin1,precioSin2;
    private static final int []arrCardsId={R.id.card89,R.id.card910h,R.id.card1011h,R.id.card1112h,R.id.card1213h,R.id.card1314h,R.id.card1415h,R.id.card1516h,R.id.card1617h,R.id.card1718h,R.id.card1819h,R.id.card1920h};
    private static final int []arrTxtId={R.id.txt89h,R.id.txt910h,R.id.txt1011h,R.id.txt1112h,R.id.txt1213h,R.id.txt1314h,R.id.txt1415h,R.id.txt1516h,R.id.txt1617h,R.id.txt1718h,R.id.txt1819h,R.id.txt1920h};
    private static final String [] arrHoras={"08:00-09:00","09:00-10:00","10:00-11:00","11:00-12:00","12:00-13:00","13:00-14:00","14:00-15:00","15:00-16:00","16:00-17:00","17:00-18:00","18:00-19:00","19:00-20:00"};
    private ArrayList <Integer> horas_seleccionadas;
    private String refBBDD,lugar,complementos,horasUser,date,amount,idCard;
    private TabHost tabhost;
    //firebase
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference bbddSala;
    //PAYPAL
    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)  //usamos sandbox porque estamos en test
            .clientId(Config.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salas);
        //iniciamos variables
        initControl();
        //iniciamos Paypal Service
        IniciarServicioPaypal();
        //seleccionar bbdd segun idBoton
        IniciarServicios();
        //leer datos usuario
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

        etPlannedDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ComprobarDisponibilidad(fechaBBDD);
                Toast.makeText(SalasActivity.this, fechaBBDD, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void IniciarServicioPaypal(){
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);
    }

    private void initControl(){
        fechaBBDD="";
        nameUser="";
        emailUser="";
        amount="";
        lugar="";
        complementos="";
        //cardview
       for (int i:arrCardsId){
           CardView cardView=(CardView)findViewById(i);
           cardView.setOnClickListener(this);
       }
        //fecha
        etPlannedDate = (EditText)findViewById(R.id.etPlannedDate);
        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });


        //precio

        txtPrecio=(TextView)findViewById(R.id.txtPrecioSala1);
        txtReservandoEn=(TextView)findViewById(R.id.txtReservando);
        btnFinalizarReserva=(Button)findViewById(R.id.btnResSala1);
        btnFinalizarReserva.setOnClickListener(this);

        tabhost=(TabHost)findViewById(R.id.tabHostAulas);
        tabhost.setup();
        //tab sin pc
        TabHost.TabSpec tab1=tabhost.newTabSpec("tab1t");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("Sin ordenadores");
        //tab con pc
        TabHost.TabSpec tab2=tabhost.newTabSpec("tab2t");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("Con ordenadores");

        tabhost.addTab(tab1);
        tabhost.addTab(tab2);
        tabhost.setOnTabChangedListener(this);

    }
    private void Prueba(){
        System.out.println("jhfkjh");
    }

    private void initBoolean(){
        cw89bt=false;cw910bt=false;cw1011bt=false;cw1112bt=false;cw1213bt=false;cw1314bt=false;
        cw1415bt=false;cw1516bt=false;cw1617bt=false;cw1718bt=false;cw1819bt=false;cw1920bt=false;
    }

    public void IniciarServicios(){
        int caller=getIntent().getIntExtra("idsActivity",0);
        switch (caller){
            case R.id.btnReservaAula1:
                esAula1=true;
                precio=30;
                bbddSala=database.getReference(FirebaseReferences.AULA1_REFERENCE);
                refBBDD="aula1";
                lugar="Aula 01";
                txtReservandoEn.setText("Reservando Aula 01 en Neurona");
                break;
            case R.id.btnReservaAula2:
                esAula2=true;
                precio=40;
                bbddSala=database.getReference(FirebaseReferences.AULA2_REFERENCE);
                refBBDD="aula2";
                lugar="Aula 02";
                txtReservandoEn.setText("Reservando Aula 02 en Neurona");
                break;
            case R.id.btnReservaSala1:
                tabhost.setVisibility( View.GONE );
                precio=25;
                bbddSala=database.getReference(FirebaseReferences.SALA1_REFERENCE);
                refBBDD="sala1";
                lugar="Sala de reuniones 01";
                txtReservandoEn.setText("Reservando Sala de Reuniones 01 en Neurona");
                break;
            case R.id.btnReservaSala2:
                tabhost.setVisibility( View.GONE );
                precio=25;
                bbddSala=database.getReference(FirebaseReferences.SALA2_REFERENCE);
                refBBDD="sala2";
                lugar="Sala de reuniones 02";
                txtReservandoEn.setText("Reservando Sala de Reuniones 02 en Neurona");
                break;

        }
    }


    @Override
    public void onClick(View view) {
        if (fechaBBDD==""){
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
        }else{
            switch (view.getId()){
                case R.id.btnResSala1:
                    if (horas_seleccionadas.isEmpty()){
                        Toast.makeText(this, "Debes seleccionar una hora", Toast.LENGTH_SHORT).show();
                    }else{
                        ProcesarPagoPaypal();
                    }
                    break;

                case R.id.card89:
                    if (!cw89bt){
                        cw89bt=true;
                        Card_On(R.id.card89,R.id.txt89h);
                    }else if(cw89bt) {
                        cw89bt = false;
                        Card_Off(R.id.card89,R.id.txt89h);
                    }
                    break;
                case R.id.card910h:
                    if (!cw910bt){
                        cw910bt=true;
                        Card_On(R.id.card910h,R.id.txt910h);
                    }else if(cw910bt){
                        cw910bt=false;
                        Card_Off(R.id.card910h,R.id.txt910h);
                    }
                    break;
                case R.id.card1011h:
                    if (!cw1011bt){
                        cw1011bt=true;
                        Card_On(R.id.card1011h,R.id.txt1011h);
                    }else if(cw1011bt){
                        cw1011bt=false;
                        Card_Off(R.id.card1011h,R.id.txt1011h);
                    }
                    break;
                case R.id.card1112h:
                    if (!cw1112bt){
                        cw1112bt=true;
                        Card_On(R.id.card1112h,R.id.txt1112h);
                    }else if(cw1112bt){
                        cw1011bt=false;
                        Card_Off(R.id.card1112h,R.id.txt1112h);
                    }
                    break;
                case R.id.card1213h:
                    if (!cw1213bt){
                        cw1213bt=true;
                        Card_On(R.id.card1213h,R.id.txt1213h);
                    }else if(cw1213bt){
                        cw1213bt=false;
                        Card_Off(R.id.card1213h,R.id.txt1213h);
                    }
                    break;
                case R.id.card1314h:
                    if (!cw1314bt){
                        cw1314bt=true;
                        Card_On(R.id.card1314h,R.id.txt1314h);
                    }else if(cw1314bt){
                        cw1314bt=false;
                        Card_Off(R.id.card1314h,R.id.txt1314h);
                    }
                    break;
                case R.id.card1415h:
                    if (!cw1415bt){
                        cw1415bt=true;
                        Card_On(R.id.card1415h,R.id.txt1415h);
                    }else if(cw1415bt){
                        cw1415bt=false;
                        Card_Off(R.id.card1415h,R.id.txt1415h);
                    }
                    break;
                case R.id.card1516h:
                    if (!cw1516bt){
                        cw1516bt=true;
                        Card_On(R.id.card1516h,R.id.txt1516h);
                    }else if(cw1516bt){
                        cw1516bt=false;
                        Card_Off(R.id.card1516h,R.id.txt1516h);
                    }
                    break;
                case R.id.card1617h:
                    if (!cw1617bt){
                        cw1617bt=true;
                        Card_On(R.id.card1617h,R.id.txt1617h);
                    }else if(cw1617bt){
                        cw1617bt=false;
                        Card_Off(R.id.card1617h,R.id.txt1617h);
                    }
                    break;
                case R.id.card1718h:
                    if (!cw1718bt){
                        cw1718bt=true;
                        Card_On(R.id.card1718h,R.id.txt1718h);
                    }else if(cw1718bt){
                        cw1718bt=false;
                        Card_Off(R.id.card1718h,R.id.txt1718h);
                    }
                    break;
                case R.id.card1819h:
                    if (!cw1819bt){
                        cw1819bt=true;
                        Card_On(R.id.card1819h,R.id.txt1819h);
                    }else if(cw1819bt){
                        cw1819bt=false;
                        Card_Off(R.id.card1819h,R.id.txt1819h);
                    }
                    break;
                case R.id.card1920h:
                    if (!cw1920bt){
                        cw1920bt=true;
                        Card_On(R.id.card1920h,R.id.txt1920h);
                    }else if(cw1920bt){
                        cw1920bt=false;
                        Card_Off(R.id.card1920h,R.id.txt1920h);
                    }
                    break;

            }
        }

    }

    private void ProcesarPagoPaypal(){
        amount=txtPrecio.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(precioTotal),"EUR","Donate for Dev",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent=new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null){
                    try{

                        for(Integer s:horas_seleccionadas){
                            Reserva reserva = new Reserva(nameUser,emailUser,fechaBBDD,lugar,complementos);
                            bbddSala.child(fechaBBDD).child(s.toString()).push().setValue(reserva);

                            int idReservado=s;
                            int pos=0;
                            //escribo las horas seleccionadas
                            for(Integer o:arrCardsId){
                                if (o==idReservado){
                                    horasUser+=arrHoras[pos]+" / ";
                                }
                                pos++;
                            }

                        }
                        Intent intent=new Intent(getApplicationContext(),PaymentDetails.class);
                        Reserva reserva = new Reserva(nameUser,emailUser,date,lugar,complementos);
                        reserva.setHorario(horasUser);
                        EnviarCorreo(reserva);
                        //paso datos al PaymentActivity para usarlos en calendario
                        Bundle bundle=new Bundle();
                        bundle.putInt("dia",diaR);
                        bundle.putInt("mes",mesR);
                        bundle.putInt("anyo",anyoR);
                        bundle.putSerializable("reserva",reserva);
                        intent.putExtras(bundle);
                        startActivity(intent);

                       // String paymentDetails= confirmation.toJSONObject().toString(4);

                    }catch(Exception e){
                        //e.printStackTrace();
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

    private void LimpiarCards(){

        for(int i:arrCardsId){

            CardView cardView=(CardView)findViewById(i);
            cardView.setBackgroundColor(Color.parseColor("#f3f3f3"));
            cardView.setCardElevation(6);
            cardView.setEnabled(true);
        }
    }

    private void LimpiarTxt(){
        for(int i:arrTxtId){
            TextView textView=(TextView)findViewById(i);
            textView.setTextColor(Color.parseColor("#059695"));
            textView.setText("Disponible");
            txtPrecio.setText("");
        }
    }

    //seleccionar hora
    private void Card_On(int id,int txt){
        CardView cardView=(CardView)findViewById(id);
        cardView.setBackgroundColor(Color.parseColor("#059695"));
        cardView.setCardElevation(1);
        TextView textView=(TextView)findViewById(txt);
        textView.setTextColor(Color.WHITE);
        precioTotal=precioTotal+precio;
        txtPrecio.setText(String.valueOf(precioTotal) + "€");
        horas_seleccionadas.add(id);
    }
    //deseleccionar hora
    private void Card_Off(int id,int txt){
        CardView cardView=(CardView)findViewById(id);
        cardView.setBackgroundColor(Color.parseColor("#f3f3f3"));
        cardView.setCardElevation(6);
        TextView textView=(TextView)findViewById(txt);
        textView.setTextColor(Color.parseColor("#059695"));
        precioTotal=precioTotal-precio;
        txtPrecio.setText(String.valueOf(precioTotal) + "€");
        for(Integer o:arrCardsId){
            if (o==id){
                horas_seleccionadas.remove(o);
            }
        }
    }

    private void EnviarCorreo(final Reserva reserva){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("daniel86zgz@gmail.com",
                            "password");
                    sender.sendMail("Reserva en Espacio Neurona", reserva.toString(),
                            "daniel86zgz@gmail.com", "daniel86zgz@gmail.com");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }

    private void ComprobarDisponibilidad(String fechaRes){
        LimpiarCards();
        LimpiarTxt();
        initBoolean();
        precioTotal=0;
        horas_seleccionadas=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child(refBBDD).child(fechaRes);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    //recorro array con datos de la bbdd y disabled los ya reservados
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        int idReservado=Integer.parseInt(dataSnapshot1.getKey());
                        CardView cardView=(CardView)findViewById(idReservado);
                        cardView.setBackgroundColor(Color.parseColor("#D8D8D8"));
                        cardView.setEnabled(false);
                        int pos=0;

                        for(Integer o:arrCardsId){
                            if (o==idReservado){
                                TextView textView=(TextView)findViewById(arrTxtId[pos]);
                                textView.setText("No disponible");
                            }
                            pos++;
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /*private void showDatePickerDialog(final EditText editText) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                date=twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                diaR=day;
                mesR=month;
                anyoR=year;
                fechaBBDD=String.valueOf(day).concat(String.valueOf((month+1))).concat(String.valueOf(year));
                //Toast.makeText(SalasActivity.this, fechaBBDD, Toast.LENGTH_SHORT).show();
                editText.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");


    }*/

   private void showDatePicker() {

       Calendar calendar = Calendar.getInstance();
       DatePickerDialog dpd = DatePickerDialog.newInstance(
               this,
               calendar.get(Calendar.YEAR),
               calendar.get(Calendar.MONTH),
               calendar.get(Calendar.DAY_OF_MONTH)
       );
       dpd.show(getFragmentManager(), "DatePickerDialog");

       GregorianCalendar g1=new GregorianCalendar();
       g1.add(Calendar.DATE, 1);
       GregorianCalendar gc = new GregorianCalendar();
       gc.add(Calendar.YEAR, 1);
       List<Calendar> dayslist= new LinkedList<Calendar>();
       Calendar[] daysArray;
       Calendar cAux = Calendar.getInstance();
       //disabled sabados y domingos
       while ( cAux.getTimeInMillis() <= gc.getTimeInMillis()) {
           if (cAux.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cAux.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
               Calendar c = Calendar.getInstance();
               c.setTimeInMillis(cAux.getTimeInMillis());
               dayslist.add(c);
           }

           cAux.setTimeInMillis(cAux.getTimeInMillis() + (24*60*60*1000));
       }

       daysArray = new Calendar[dayslist.size()];
       for (int i = 0; i<daysArray.length;i++)
       {
           daysArray[i]=dayslist.get(i);
       }
       dpd.setSelectableDays(daysArray);

       /* //disabled dias festivos
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String a = "26-03-2018"; // example

        java.util.Date date = null;

        try {
            try {
                date = sdf.parse(a);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar = dateToCalendar(date);
        System.out.println(calendar.getTime());

        List<Calendar> dates = new ArrayList<>();
        dates.add(calendar);
        Calendar[] disabledDays1 = dates.toArray(new Calendar[dates.size()]);
        dpd.setDisabledDays(disabledDays1);*/

    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    private void setUserData(FirebaseUser user) {
        nameUser=user.getDisplayName();
        emailUser=user.getEmail();
        //Glide.with(this).load(user.getPhotoUrl()).into(photoImageView);  inserta foto usuario
    }

    @Override
    public void onTabChanged(String tabId) {
        if (esAula1){
            precio=30;
        }else if(esAula2){
            precio=40;
        }
        if (tabId.equalsIgnoreCase("tab1t")){
            complementos="Sin ordenadores";
            if (fechaBBDD!=""){
                ComprobarDisponibilidad(fechaBBDD);
            }
        }else if(tabId.equalsIgnoreCase("tab2t")){
            precio+=10;
            complementos="Con ordenadores";
            if (fechaBBDD!=""){
                ComprobarDisponibilidad(fechaBBDD);
            }

        }

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        etPlannedDate.setText(date);
    }
}
