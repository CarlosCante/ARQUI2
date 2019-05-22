package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfDucha extends AppCompatActivity {

    Button btnBluetooth,btnConfigurar;
    Button btnTmp;
    EditText txtTiempo;

    //variables que contienen datos de la aplicacion
    String idBluetooth = null;
    String minutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_ducha);
        btnBluetooth = (Button)findViewById(R.id.btnBluetooth);
        btnConfigurar = (Button)findViewById(R.id.btnConfigurar);
        txtTiempo = (EditText)findViewById(R.id.editMinutos);

        btnTmp = (Button)findViewById(R.id.btnTmp);
        //variables para la escritura del archivo
        final Context context = this;
        SharedPreferences sharPrefs = getSharedPreferences("archivo_bluetooth",context.MODE_PRIVATE);


        //Toma los datos de la variables que utilizaremos
        try {
            idBluetooth =  getIntent().getExtras().getString("device_address");
        }catch (Exception e){

        }
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfDucha.this,DispositivosBT.class);
                startActivity(i);
            }
        });

        btnConfigurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idBluetooth != null){
                    /*Tratamos de guardar la direccion del bluetooth en un archvio txt para posteriores usos de la aplicacion*/
//                    SharedPreferences sharPrefs = getPreferences(context.MODE_WORLD_WRITEABLE);
//                    SharedPreferences.Editor  editor= sharPrefs.edit();
//                    editor.putString("confDucha",idBluetooth + ","+txtTiempo.getText());
//                    editor.commit();
                    Intent i = new Intent(ConfDucha.this,MainActivity.class);
                    i.putExtra("configDucha",idBluetooth+","+txtTiempo.getText());
                    startActivity(i);
                } else{
                    Toast.makeText(getApplicationContext(),"No se configuro el dispositivo Bluetooth correctamente",Toast.LENGTH_LONG).show();
                }

            }
        });



        btnTmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sp = getPreferences(context.MODE_PRIVATE);
//                String valor = sp.getString("confDucha","No hay dato");
//                Toast.makeText(getApplicationContext(),valor,Toast.LENGTH_LONG).show();
                Intent i = new Intent(ConfDucha.this,MainActivity.class);
                startActivity(i);
            }
        });
    }



}
