package com.example.practica2_2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ManipularPrototipo extends AppCompatActivity {

    String address;
    String[] cadenaEnvio;
    Button btnIniciar;
    Handler bluetoothIn;
    final int handlerState = 0;
    public BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //para el contador
    private CountDownTimer countDownTimer;
//    private long timeCountInMilliSeconds = 1 * 60000;
long timeCountInMilliSeconds = 1 * 1000000;
boolean flag_conteo = false;

    long contadorSegundos = 0;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    //configuarando  timer
    private TimerStatus timerStatus = TimerStatus.STOPPED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manipular_prototipo);
        btnIniciar = (Button)findViewById(R.id.btnIniciar);
        cadenaEnvio = getIntent().getExtras().getStringArray("envio");
        address = getIntent().getExtras().getString("address");



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
                        dataInPrint += "&" + contadorSegundos;
                        resetear();
                        Toast.makeText(getBaseContext(), "Dato" + dataInPrint, Toast.LENGTH_LONG).show();

                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),cadenaEnvio[0],Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                String t = btnIniciar.getText().toString();
                if(btnIniciar.getText().toString().equals("Iniciar")){
                    String nuevaCadena = ""; //Cadena que sera enviada al arduino
                    for(int i = 0; i < 4 ; i++){

                        if( i + 1 == 4){
                            nuevaCadena += cadenaEnvio[3];
                            break;
                        }

                        nuevaCadena += cadenaEnvio[i] + ",";
                    }
                    MyConexionBT.write(nuevaCadena+"\n");

                    contadorSegundos = 0;
                    flag_conteo = true;
                    startCountDownTimer(); //Empezamos el contador el cual inicia el movimeinto del prototipo
                    btnIniciar.setBackgroundResource(R.drawable.circle_buttom_stop);
                    btnIniciar.setText("Detener");
                } else {
                    resetear();
                    //MyConexionBT.write(Double.toString(contadorSegundos));
                    MyConexionBT.write("s\n");
                }

            }
        });




    } //======================================= FIN DE ONCREATE

    public void resetear(){
        flag_conteo = false;
        stopCountDownTimer();
        btnIniciar.setBackgroundResource(R.drawable.circle_buttom);
        btnIniciar.setText("Iniciar");
    }

    /*========================================== para configurar el timer ===================================*/
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(flag_conteo == true){
                    contadorSegundos = 1000 - TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);// - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                    //Toast.makeText(getApplicationContext(),Double.toString(contadorSegundos),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {

                //textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
               // setProgressBarValues();
                // hiding the reset icon
                //imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                //imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                //editTextMinute.setEnabled(true);
                // changing the timer status to stopped
               // timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }
    /*============================================= fin timer ============================================*/
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
        //Consigue la direccion MAC desde DeviceListActivity via intent
        //Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        //address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->

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
//            swMonitoreo.setEnabled(false);
//            swTiempo.setEnabled(false);
//            swDistancia.setEnabled(false);
//            swCarga.setEnabled(false);
//            txtSegundos.setEnabled(false);
        }


    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
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
