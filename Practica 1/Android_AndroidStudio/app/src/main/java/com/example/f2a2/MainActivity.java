package com.example.f2a2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button btnGMaps;
    String valCoordenadas;
    Button sensorWeather;
    String dirIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGMaps = (Button)findViewById(R.id.btnGMaps);
        sensorWeather = (Button)findViewById(R.id.btnWeather);
        dirIP = "3.90.33.177";
        sensorWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SensorsTWeather.class);
                i.putExtra("ip",dirIP);
                startActivity(i);
            }
        });

        btnGMaps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConeccionWS request = new ConeccionWS();
                try {
                    valCoordenadas = request.execute("http://"+dirIP+":8003/ubicaciones").get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(valCoordenadas != null){
                    Intent intent = new Intent(MainActivity.this,CoordenadasMaps.class);
                    intent.putExtra("lstCoord",valCoordenadas);
                    intent.putExtra("ip",dirIP);
                    startActivity(intent);
                }

            }
        });
    }
}
