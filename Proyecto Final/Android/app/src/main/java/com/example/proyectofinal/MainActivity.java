package com.example.proyectofinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnDucha, btnEstadisticas,  btnIniciar;
    //================================================
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
    private static String segundosDucha = null;      //IMPORTANTE PARA LOS SEGUNDOS DEL CRONOMETRO DE LA DUCHA
    private String[] cadenaEnvio = null;

     Context context = this;


     boolean flag_iniciar_ducha = false;

    //=========================== PARA EL ENVIO Y CONSUMO DEL SERVIDOR = ================================
    String dirIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDucha = (Button)findViewById(R.id.btnDucha);
        btnEstadisticas = (Button)findViewById(R.id.btnEstadisticas);
        btnIniciar = (Button)findViewById(R.id.btnInicio);

        //=========================== PARA MODIFICAR LA DIRECCION IP DEL SERVIDOR =============================
        dirIP = "52.87.166.247";
        // ====================================================================================================

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
                        if (dataInPrint.equals("ok")) {
                            Intent i = new Intent(MainActivity.this,Cronometro.class);
                            i.putExtra("segundosDucha",segundosDucha);
                            i.putExtra("dirIp",dirIP);
                            i.putExtra("dirBlue",address);
                            startActivity(i);
                        }else if(dataInPrint.equals("no")){
                            Toast.makeText(getBaseContext(), "No hay toalla ve por una ", Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(getBaseContext(), dataInPrint, Toast.LENGTH_LONG).show();
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };
        // ====================================================================================================

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter

        VerificarEstadoBT();


        btnDucha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ConfDucha.class);
                startActivity(i);
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(address == null){
                    Toast.makeText(getBaseContext(), "Necesita configurar primero el prototipo con el bluetooth", Toast.LENGTH_LONG).show();
                }else{
                    MyConexionBT.write("empezar\n"); //envio la senial al arduino
//                     Intent i = new Intent(MainActivity.this,Cronometro.class);
//                      i.putExtra("segundosDucha",segundosDucha);
//                      i.putExtra("dirIp",dirIP);
//                      startActivity(i);
                }
            }
        });

        btnEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Graficas.class);
//                i.putExtra();
                startActivity(i);
//                Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
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

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        try{
//            SharedPreferences sp = getPreferences(context.MODE_WORLD_READABLE);
//            String valor = sp.getString("confDucha","No");
            String valor = getIntent().getExtras().getString("configDucha");
            String[] datos  = valor.split(",");
            if(datos[0].equals("")){ //sino se configuro el bluethoot de la ducha
                Toast.makeText(getApplicationContext(),"aun no hay direccion de bluetooth",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),datos[0],Toast.LENGTH_LONG).show();
                address = datos[0].toString();
                segundosDucha = datos[1].toString();
            }
        }catch (Exception e){

        }

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
