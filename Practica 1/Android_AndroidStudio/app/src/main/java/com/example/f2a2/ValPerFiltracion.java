package com.example.f2a2;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ValPerFiltracion extends AppCompatActivity {

    //componentes de la activty
    Switch swFecha;
    Switch swHora;
    Switch swSensor;
    Switch swUbicacion;
    Spinner spFecha;
    Spinner spHora;
    Spinner spSensor;
    Spinner spUbicacion;
    Activity actual;
    Button btnFiltar;

    //variables globales qeu serviran para la adquisicion de datos
    String dirIp;
    String fechaNoSplit;

    int tipoSensor = 0;

    //banderas para los spinners
    boolean flagSpFecha = true;
    boolean flagSpHora = true;
    boolean flagSPSensor = true;
    boolean flagSPUbicacion = true;

    //informacion de  todos los sensorea
    String infoNoSplit = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_val_per_filtracion);
        dirIp = getIntent().getExtras().getString("ip");
        actual = this;

        swFecha = (Switch)findViewById(R.id.swFecha);
        swHora = (Switch)findViewById(R.id.swHora);
        swSensor = (Switch)findViewById(R.id.swSensor);
        swUbicacion = (Switch)findViewById(R.id.swUbicacion);

        spFecha = (Spinner)findViewById(R.id.spnFecha);
        spHora = (Spinner)findViewById(R.id.spnHora);
        spSensor = (Spinner)findViewById(R.id.spnSensor);
        spUbicacion = (Spinner)findViewById(R.id.spnUbicacion);

        btnFiltar = (Button)findViewById(R.id.btnFiltrar);
        //logica de los switch


        swFecha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ //sino esta checkeado
                    swHora.setChecked(false);
                    swUbicacion.setChecked(false);
                    swSensor.setChecked(false);

                    spFecha.setEnabled(true);
                    spHora.setEnabled(false);
                    spSensor.setEnabled(false);
                    spUbicacion.setEnabled(false);

                    flagSpFecha = true;
                    //aca hacemos las peticiones para obtener la fecha
                    setValueSpinnerFecha();
                    tipoSensor = 0;
                }
                else
                    spFecha.setAdapter(null);
            }
        });

        swHora.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { //sino esta checkeado
                    swFecha.setChecked(false);
                    swUbicacion.setChecked(false);
                    swSensor.setChecked(false);

                    spFecha.setEnabled(true);
                    spHora.setEnabled(true);
                    spSensor.setEnabled(false);
                    spUbicacion.setEnabled(false);

                    flagSpFecha = true;
                    flagSpHora = true;
                    //aca hacemos las peticiones para obtener la hora
                    if(setValueSpinnerFecha()){
                        String[] tmp ={"Escoja una fecha"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(actual,android.R.layout.simple_spinner_item,tmp);
                        spHora.setAdapter(adapter2);
                    }
                    tipoSensor = 0;
                }
                else{
                    spFecha.setAdapter(null);
                    spHora.setAdapter(null);
                }
            }
        });

        swSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { //sino esta checkeado
                    swHora.setChecked(false);
                    swUbicacion.setChecked(false);
                    swFecha.setChecked(false);

                    spFecha.setEnabled(true);
                    spHora.setEnabled(false);
                    spSensor.setEnabled(true);
                    spUbicacion.setEnabled(true);

                    if(setValueSpinnerFecha() && setValueSpinnerUbicacion()){
                        flagSPSensor = true;
                        String[] tmp ={"Escoja un Sensor","CO2","CO","UV"};
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(actual,android.R.layout.simple_spinner_item,tmp);
                        spSensor.setAdapter(adapter2);
                    }


                }
            }
        });

        swUbicacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { //sino esta checkeado
                    swHora.setChecked(false);
                    swFecha.setChecked(false);
                    swSensor.setChecked(false);

                    spFecha.setEnabled(false);
                    spHora.setEnabled(false);
                    spSensor.setEnabled(false);
                    spUbicacion.setEnabled(true);

                    setValueSpinnerUbicacion();
                    tipoSensor = 0;
                }
            }
        });

        spFecha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(flagSpFecha){
                    flagSpFecha = false;  //para que no escoge pro primera vez
                } else {
                    if(spHora.isEnabled()){ //queremos hacer una request hora
                        setValueSpinnerHora();
                    } else if(spSensor.isEnabled()){

                    } else{ //hago una peticion de los valores por fecha
                        try {
                            infoNoSplit = null;
                            ConeccionWS r = new ConeccionWS();
                            infoNoSplit = r.execute("http://"+dirIp+":8003/s_fecha?"+spFecha.getSelectedItem().toString()).get();
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
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spHora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(flagSpHora){
                    flagSpHora = false;  //para que no escoger por primera vez
                } else {
                    infoNoSplit =null;
                    try {
                        if(spHora.getSelectedItem().toString() != "Escoja una hora"){
                            String consulta = spFecha.getSelectedItem().toString() + "&" + spHora.getSelectedItem().toString();
                            ConeccionWS r = new ConeccionWS();
                            String tmp = "http://" + dirIp + ":8003/s_hora?" + consulta;
                            infoNoSplit = r.execute("http://" + dirIp + ":8003/s_hora?" + consulta).get();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spUbicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(flagSPUbicacion){
                    flagSPUbicacion = false;  //para que no escoger por primera vez
                } else {
                    if(spSensor.isEnabled()){


                    } else{
                        infoNoSplit =null;
                        try {
                            if(spUbicacion.getSelectedItem().toString() != "Escoja una ubicacion"){
                                String consulta = "sensores?" + spUbicacion.getSelectedItem().toString();
                                ConeccionWS r = new ConeccionWS();
                                String tmp = "http://" + dirIp + ":8003/" + consulta;
                                tipoSensor = 0;
                                infoNoSplit = r.execute("http://" + dirIp + ":8003/" + consulta).get();
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(flagSPSensor){
                    flagSPSensor = false;  //para que no escoger por primera vez
                } else {
                    String fecha = spFecha.getSelectedItem().toString();
                    String ubicacion = spUbicacion.getSelectedItem().toString();
                    String tSensor = spSensor.getSelectedItem().toString();

                    if(tSensor =="CO2"){
                        tipoSensor = 1;
                    } else if(tSensor == "CO"){
                        tipoSensor = 2;
                    } else if(tSensor == "UV"){
                        tipoSensor = 3;
                    } else
                        tipoSensor = 0;

                    boolean hayFecha = false;
                    boolean hayUbicacion = false;
                    infoNoSplit = null;
                    if(tipoSensor != 0){
                        if(!fecha.equals("Escoja una fecha")){
                           hayFecha = true;
                        }
                        if(!ubicacion.equals("Escoja una ubicacion") && !ubicacion.equals("None"))
                            hayUbicacion = true;

                        ConeccionWS r = new ConeccionWS();

                        if(hayFecha && hayUbicacion){
                            try {
                                infoNoSplit = r.execute("http://" + dirIp + ":8003/s_fecha?" + fecha).get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else if(hayFecha){

                            try {
                                infoNoSplit = r.execute("http://" + dirIp + ":8003/s_fecha?" + fecha).get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else if(hayUbicacion){
                            try {
                                infoNoSplit = r.execute("http://" + dirIp + ":8003/sensores?" + ubicacion).get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else{
                            Toast.makeText(getApplicationContext(), "No se puede consultar su peticion", Toast.LENGTH_LONG).show();
                        }
                    }

                    if(spSensor.isEnabled()){


                    } else{
                        infoNoSplit =null;
                        try {
                            if(spUbicacion.getSelectedItem().toString() != "Escoja una ubicacion"){
                                String consulta = "sensores?" + spUbicacion.getSelectedItem().toString();
                                ConeccionWS r = new ConeccionWS();
                                String tmp = "http://" + dirIp + ":8003/" + consulta;
                                tipoSensor = 0;
                                infoNoSplit = r.execute("http://" + dirIp + ":8003/" + consulta).get();
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnFiltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),infoNoSplit,Toast.LENGTH_LONG).show();
                if(infoNoSplit != null && infoNoSplit != ""){
                    Intent i = new Intent(ValPerFiltracion.this,Graficos.class);
                    if(tipoSensor == 0)
                        i.putExtra("tSensor","all");
                    else if(tipoSensor == 1)
                        i.putExtra("tSensor","co2");
                    else if(tipoSensor == 2)
                        i.putExtra("tSensor","co");
                    else if(tipoSensor == 3)
                        i.putExtra("tSensor","uv");

                    i.putExtra("vals",infoNoSplit);
                    startActivity(i);
                } else{
                    Toast.makeText(getApplicationContext(),"no hay datos filtrados para mostrar",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public boolean setValueSpinnerFecha(){
        fechaNoSplit = null;
        try {
            ConeccionWS request = new ConeccionWS();
            fechaNoSplit = request.execute("http://"+dirIp+":8003/fechas").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
        }
        if(fechaNoSplit != null && fechaNoSplit != ""){
            flagSpFecha = true;
            spFecha.setAdapter(null);
            String[] fechas = fechaNoSplit.split(";"); //valores de las fechas espliteadas
            List<String> lstFechas = new ArrayList<>();
            lstFechas.add("Escoja una fecha");
            for(String f: fechas)
                lstFechas.add(f);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(actual,android.R.layout.simple_spinner_item,lstFechas);
            spFecha.setAdapter(adapter2);
            return true;
        }
        return false;
    }

    public boolean setValueSpinnerHora(){
        String horaNoSpilt = null;
        try {
            String fechaSelected = spFecha.getSelectedItem().toString();
            if(fechaSelected != "Escoja una fecha"){
                ConeccionWS r = new ConeccionWS();
                horaNoSpilt = r.execute("http://"+dirIp+":8003/horas?"+fechaSelected).get();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
        }

        if(horaNoSpilt != null && horaNoSpilt != ""){
            flagSpHora = true;
            spHora.setAdapter(null);
            String[] horas = horaNoSpilt.split(";");
            List<String> lstHoras = new ArrayList<>();
            lstHoras.add("Escoja una hora");
            for(String f: horas)
                lstHoras.add(f);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(actual,android.R.layout.simple_spinner_item,lstHoras);
            spHora.setAdapter(adapter);
            return true;
        }
        return false;
    }

    public boolean setValueSpinnerUbicacion(){
        String UbicacionNoSpilt = null;
        try {
                ConeccionWS r = new ConeccionWS();
                UbicacionNoSpilt = r.execute("http://"+dirIp+":8003/ubicaciones").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Not Response",Toast.LENGTH_LONG).show();
        }
        if(UbicacionNoSpilt != null  && UbicacionNoSpilt != ""){
            flagSPUbicacion = true;
            spUbicacion.setAdapter(null);
            String[] ubicaciones = UbicacionNoSpilt.split(";");
            List<String> lstUbicaciones = new ArrayList<>();
            lstUbicaciones.add("Escoja una ubicacion");
            for(String f: ubicaciones)
                lstUbicaciones.add(f);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(actual,android.R.layout.simple_spinner_item,lstUbicaciones);
            spUbicacion.setAdapter(adapter);
            return true;
        }
        return false;
    }
}
