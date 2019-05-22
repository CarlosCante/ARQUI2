package com.example.proyectofinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Cronometro extends AppCompatActivity {

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

    private String segString = null;
    private int segundosDucha = 0;
    private String dirIp = null;
    /************************************************************** BLUETOOTH* ********************************************* */
    Handler bluetoothIn;
    final int handlerState = 0;
    public BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    //private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;            //IMPORTANTE PARA LA CONECCION CON EL BLUETOOTH
    /***-*********************************************************************************************************/
    boolean flag_1 = false;

    Button btnDetener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cronometro);

        btnDetener = (Button)findViewById(R.id.btnDetener);

        try{
            segString =  getIntent().getExtras().getString("segundosDucha");
            dirIp = getIntent().getExtras().getString("dirIp");
            address = getIntent().getExtras().getString("dirBlue");
            Toast.makeText(Cronometro.this, "adress "+address, Toast.LENGTH_SHORT).show();
            if(segString != null){
                segundosDucha = Integer.parseInt(segString);
                segundosDucha = segundosDucha * 1000; //convietiendo a milisegundos

                if(dirIp == null){
                    Toast.makeText(Cronometro.this, "no hay direccion con el servidor", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(Cronometro.this, "algo salio mal con los segundos", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }


        // ================= HILO QUE ESPERA RESPUESTA DESDE BLUETOOTH DESDE ARDUINO EN ESTE CASO ================
        bluetoothIn = new Handler() {

            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    //
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        //IdBufferIn.setText("Dato: " + dataInPrint);//<-<- PARTE A MODIFICAR >->->
//                        dataInPrint += "&" + contadorSegundos;
//                        resetear();

                        Toast.makeText(getBaseContext(), dataInPrint, Toast.LENGTH_LONG).show();
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter

        VerificarEstadoBT();

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= segundosDucha) {
                    //chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(Cronometro.this, "Bing!", Toast.LENGTH_SHORT).show();
                    flag_1 = true;

                }
            }
        });

        if(flag_1 == false)
        {
            MyConexionBT.write("alarma");
        }

        startChronometer();

        btnDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseChronometer();

//                try {
                    ConeccionWS r = new ConeccionWS();
                    //obteniendo la fecha
                    String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    Long segundos = SystemClock.elapsedRealtime() - chronometer.getBase();
                    segundos = segundos / 1000;
                    Integer seg = segundos.intValue();
                    Toast.makeText(getApplicationContext(), fecha + " " + seg.toString(), Toast.LENGTH_LONG).show();
                    //String tmp = "http://" + dirIp + ":8003/datos?"+fecha+"&"+segundos.toString();
                    
                    //String envio =  r.execute(tmp).get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
//                }
            }
        });


        if(address != null){
            //Setea la direccion MAC
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
            }
            // Establece la conexión con el socket Bluetooth.
            try
            {
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {}
            }
            MyConexionBT = new ConnectedThread(btSocket);
            MyConexionBT.start();
        }else{
            Toast.makeText(getApplicationContext(),"Conecte su Dispositivo a Bluetooth con el Prototipo",Toast.LENGTH_LONG).show();
        }
    }

    public void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
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

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    //@Override
//    public void onResume()
//    {
//        super.onResume();
////
////        try{
//////            SharedPreferences sp = getPreferences(context.MODE_WORLD_READABLE);
//////            String valor = sp.getString("confDucha","No");
////             address = getIntent().getExtras().getString("dirBlue");
////            Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
////            if(address == null){ //sino se configuro el bluethoot de la ducha
////                Toast.makeText(getApplicationContext(),"hay problemas con la coneccion bluetooth",Toast.LENGTH_LONG).show();
////            }
////        }catch (Exception e){
////
////        }
//
//        if(address != null){
//            //Setea la direccion MAC
//            BluetoothDevice device = btAdapter.getRemoteDevice(address);
//            try {
//                btSocket = createBluetoothSocket(device);
//            } catch (IOException e) {
//                Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
//            }
//            // Establece la conexión con el socket Bluetooth.
//            try
//            {
//                btSocket.connect();
//            } catch (IOException e) {
//                try {
//                    btSocket.close();
//                } catch (IOException e2) {}
//            }
//            MyConexionBT = new ConnectedThread(btSocket);
//            MyConexionBT.start();
//        }else{
//            Toast.makeText(getApplicationContext(),"Conecte su Dispositivo a Bluetooth con el Prototipo",Toast.LENGTH_LONG).show();
//        }
//
//
//    }
    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión

                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


}
