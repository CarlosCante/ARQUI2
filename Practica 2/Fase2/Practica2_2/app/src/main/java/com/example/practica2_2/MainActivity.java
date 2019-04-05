package com.example.practica2_2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnBluetooth,btnConfiguracion,btnEstadisticas,btnAcercaDe;
    //================================================
    Handler bluetoothIn;
    final int handlerState = 0;
    public BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    //private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    private String[] cadenaEnvio = null;

    //=========================== PARA EL ENVIO Y CONSUMO DEL SERVIDOR = ================================
    String dirIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnBluetooth = (Button)findViewById(R.id.btnBluetooth);
        btnConfiguracion = (Button)findViewById(R.id.btnConfiguracion);
        btnAcercaDe = (Button)findViewById(R.id.btnInformacion);
        btnEstadisticas = (Button)findViewById(R.id.btnEstadisticas);
        dirIP = "3.90.33.177";
        //METODO QUE VERIFICA QUE ESTE ACTIVADO EL BLUETOOTH
        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        try {
            cadenaEnvio = getIntent().getExtras().getStringArray("envio");
            address = getIntent().getExtras().getString("address");

        }catch (Exception e){

        }


        if(address == null){ //SINO ES PORQUE TRAE CON SIGO LA DIRECCION DEL DISPOSITIVO
            if(btAdapter != null){
                //Consigue la direccion MAC desde DeviceListActivity via intent
                Intent intent = getIntent();
                //Consigue la direccion MAC desde DeviceListActivity via EXTRA

                address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
                if(address == null || address.equals("")){
//                    btnManipular.setEnabled(false);
                    btnConfiguracion.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"Debe contectar su dispositivo con el carrito",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Hay problemas con el bluetooth de su dispositivo",Toast.LENGTH_LONG).show();
            }
        }


        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,DispositivosBT.class);
                startActivity(i);
            }
        });

        btnConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Configuracion.class);
                i.putExtra("address",address); //envio de la direccion del bluetooth
                i.putExtra("ip",dirIP); //envio de la direcicon  IP (Este va quemado)
                startActivity(i);
            }
        });

        btnEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Estadisticas.class);
                i.putExtra("ip",dirIP); //envio de la direcicon  IP (Este va quemado)
                startActivity(i);
            }
        });

        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i = new Intent(MainActivity.this,AcercaDe.class);
                    startActivity(i);

            }
        });

    }


    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

}
