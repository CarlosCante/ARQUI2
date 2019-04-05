package com.example.practica2_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Estadisticas extends AppCompatActivity {

    //direccion del servidor
    String dirIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);
        dirIp = getIntent().getExtras().getString("ip");
        Toast.makeText(getApplicationContext(),dirIp,Toast.LENGTH_LONG).show();
    }
}
