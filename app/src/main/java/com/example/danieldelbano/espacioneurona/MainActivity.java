package com.example.danieldelbano.espacioneurona;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Typeface typeface1,typeface2;
    private CardView cardWorking,cardAula1,cardAula2,cardSala1,cardSala2;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init_Navigation();

        String font_path= "fuentes/beigetype.ttf"; // string para elegir tipo de fuente
        this.typeface1 = Typeface.createFromAsset(getAssets(),font_path);
        String font_path2= "fuentes/montserrat.ttf"; // string para elegir tipo de fuente
        this.typeface2 = Typeface.createFromAsset(getAssets(),font_path2);

        cardWorking=(CardView)findViewById(R.id.cardCoworking);
        cardWorking.setOnClickListener(this);
        cardAula1=(CardView)findViewById(R.id.cardAula1);
        cardAula1.setOnClickListener(this);
        cardAula2=(CardView)findViewById(R.id.cardAula2);
        cardAula2.setOnClickListener(this);
        cardSala1=(CardView)findViewById(R.id.cardSala1);
        cardSala1.setOnClickListener(this);
        cardSala2=(CardView)findViewById(R.id.cardSala2);
        cardSala2.setOnClickListener(this);
        firebaseAuth=FirebaseAuth.getInstance();

        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.cardCoworking:
                i=new Intent(this,CoworkingReservaActivity.class);
                startActivity(i);
                break;
            case R.id.cardAula1:
                i=new Intent(this,Aula1Activity.class);
                startActivity(i);
                break;
            case R.id.cardAula2:
                i=new Intent(this,Aula2Activity.class);
                startActivity(i);
                break;
            case R.id.cardSala1:
                i=new Intent(this,Sala1Activity.class);
                startActivity(i);
                break;
            case R.id.cardSala2:
                i=new Intent(this,Sala2Activity.class);
                startActivity(i);
                break;
        }
    }

    private void Init_Navigation(){

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_main);
        navView = (NavigationView)findViewById(R.id.navview);

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;

                        switch (menuItem.getItemId()) {
                            case R.id.menu_seccion_1://reservas
                                fragment = new PerfilFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_seccion_2://localizanos
                                //fragment = new Fragment2();
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_seccion_3://opciones
                                //fragment = new Fragment3();
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_opcion_1://acerca de
                                //Log.i("NavigationView", "Pulsada opción 1");
                                break;
                            case R.id.menu_opcion_2://cerrar sesion
                                //Log.i("NavigationView", "Pulsada opción 2");
                                CerrarSesion();
                                break;
                        }

                        if(fragmentTransaction) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }

    private void CerrarSesion() {
        AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void Init_Toolbar(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            //...
        }

        return super.onOptionsItemSelected(item);
    }


    public void revoke(View view){
        firebaseAuth.signOut();
    }
}
