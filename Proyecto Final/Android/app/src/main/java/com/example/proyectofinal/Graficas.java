package com.example.proyectofinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class Graficas extends AppCompatActivity {

    Switch swDia;
    Switch swSemana;
    Spinner spEstadistica;
    Button btnMostrar;
    boolean flagSpinner = true;
    String dirIp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);
        swDia = (Switch)findViewById(R.id.swDiario);
        swSemana = (Switch)findViewById(R.id.swSemanal);
        spEstadistica = (Spinner)findViewById(R.id.spTipoEstadistica);
        btnMostrar = (Button)findViewById(R.id.btnMostrar);

        try {
            dirIp = getIntent().getExtras().getString("dirIp");
        }catch (Exception e){

        }
        String[] datos = new String[] {"ducha"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, datos);
        spEstadistica.setAdapter(adapter);

//        spEstadistica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(flagSpinner)
//                    flagSpinner = false;
//                else{
//                    if(spEstadistica.getSelectedItem().toString().equals("ducha")){
//
//                    }
//                }
//            }
//
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spEstadistica.getSelectedItem().toString().equals("ducha")){
                    if(swDia.isChecked()){
                        //hacemos la peticion y mostramos la grafica
                        String infoNoSplitTiempo = "";
                        String infoNoSplitDia = "";
                        try {
                            ConeccionWS r = new ConeccionWS();
                            infoNoSplitTiempo = r.execute("http://"+dirIp+":8003/tiempo").get();
                            infoNoSplitDia = r.execute("http://"+dirIp+":8003/dia").get();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
                        }

                    }else if(swSemana.isChecked()){
                        //hacemos la peticion y hacemos la grafica

                        String infoNoSplitTiempo = "";
                        String infoNoSplitDias = "";
                        try {
                            ConeccionWS r = new ConeccionWS();
                            infoNoSplitTiempo = r.execute("http://"+dirIp+":8003/tiempo").get();
                            infoNoSplitDias = r.execute("http://"+dirIp+":8003/dia").get();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
}
