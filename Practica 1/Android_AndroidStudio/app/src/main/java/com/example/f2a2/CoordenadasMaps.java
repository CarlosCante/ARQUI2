package com.example.f2a2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CoordenadasMaps extends AppCompatActivity {

    // ========== Objetos que me van a servir del  activity
    Spinner spCoordenadas;
    TableLayout tabla;
    Button btnGMaps;
    // ========== varaibles para el manejo de informacion
    String coorNoSplit;
    String[] coordenadas;
    boolean iniSpin = true;
    String lstValSensores;
    String[] valorSensores;
    String coordenada;
    Activity tmp;
    String dirIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordenadas_maps);
        spCoordenadas = (Spinner)findViewById(R.id.spCoordenadas);
        tabla = (TableLayout)findViewById(R.id.tabla);
        btnGMaps = (Button) findViewById(R.id.btnShowMap);
        tmp = this;
        //Obtengo la ip
        dirIp = getIntent().getExtras().getString("ip");
        btnGMaps.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(coordenada != null){
                    Intent intent = new Intent(CoordenadasMaps.this,MapsActivity.class);
                    intent.putExtra("coord",coordenada);
                    startActivity(intent);
                }
            }
        });
        coorNoSplit = getIntent().getExtras().getString("lstCoord");
        if(coorNoSplit != null){
            coordenadas = coorNoSplit.split(";");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,coordenadas);
            spCoordenadas.setAdapter(adapter);
        }

        spCoordenadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(iniSpin){
                    iniSpin = false;
                } else {
                   // Toast.makeText(getApplicationContext(),coordenadas[position],Toast.LENGTH_LONG).show();
                    if(coordenadas[position] != "None"){
//                        Intent intent = new Intent(CoordenadasMaps.this,MapsActivity.class);
//                        intent.putExtra("coord",coordenadas[position]);
//                        startActivity(intent);
                        coordenada = coordenadas[position];
                        ConeccionWS request = new ConeccionWS();
                        try {
                            lstValSensores = request.execute("http://"+dirIp+":8003/sensores?"+coordenadas[position]).get();
                            if(lstValSensores != null){
                                 TablaSensores t = new TablaSensores(tmp,tabla);
                                t.agregarCabecera(R.array.valSnr);
                                valorSensores = lstValSensores.split(";");
                                String[] valor;
                                for(int i = 0; i < valorSensores.length; i++)
                                {
                                    valor = valorSensores[i].split(",");
                                    ArrayList<String> elementos = new ArrayList<String>();
                                    elementos.add(Integer.toString(i));
                                    elementos.add(valor[0]);
                                    elementos.add(valor[1]);
                                    elementos.add(valor[2]);
                                    t.agregarFilaTabla(elementos);
                                }
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
