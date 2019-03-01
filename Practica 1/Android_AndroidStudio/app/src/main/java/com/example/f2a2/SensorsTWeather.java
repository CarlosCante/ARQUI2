package com.example.f2a2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SensorsTWeather extends AppCompatActivity {

    Button btnFiltraciones;

    String dirIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_tweather);
        btnFiltraciones = (Button)findViewById(R.id.btnFiltraciones);

        dirIp = getIntent().getExtras().getString("ip");

        btnFiltraciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SensorsTWeather.this,ValPerFiltracion.class);
                i.putExtra("ip",dirIp);
                startActivity(i);
            }
        });
    }
}
