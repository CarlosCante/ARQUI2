package com.example.practica2_2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import android.bluetooth.BluetoothDevice;

public class Configuracion extends AppCompatActivity {

    String address;
    //monitoreo,carga,automatico,tiempo
    //m,c,a,t
    String[] cadenaEnviar = {"#","#","#","#"};
    Switch swMonitoreo,swPeso,swTiempo,swAutomatico;
    Button btnIniciar;
    TextView txtSegundos;
    TextView txtDistancia;

    //============================ Configuracion para el envio de datos ======================================
    String[] cadenaEnvio;
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
    //=================== para la coneccion del servidord
    String dirIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        swMonitoreo = (Switch)findViewById(R.id.swMonitoreo);
        swPeso =  (Switch)findViewById(R.id.swCarga);
        swTiempo = (Switch)findViewById(R.id.swTiempo);
        swAutomatico = (Switch)findViewById(R.id.swAutomatico);
        btnIniciar = (Button)findViewById(R.id.btnIniciar);
        txtSegundos = (TextView)findViewById(R.id.txtSegundos);
        txtDistancia = (TextView)findViewById(R.id.txtDistancia);
        //obteniendo la direccion
        address = getIntent().getExtras().getString("address");
        //obteniendo la direccion IP del servidor
        dirIp = getIntent().getExtras().getString("ip");

        ConfiguracionInicial(); //configuracion inicial para cuando se inicie el activity
    /* ============================ PARTE DONDE RECIBE DATOS EL BLUETOOTH =======================================*/
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
                        dataInPrint += "&" + contadorSegundos + "&" + txtDistancia.getText();
                        resetear();

                       // Toast.makeText(getBaseContext(), "dato" + dataInPrint, Toast.LENGTH_LONG).show();

                        //=========== envio de datos al servidor ========================

                        try {
                            ConeccionWS r = new ConeccionWS();
                            String tmp = "http://" + dirIp + ":8003/"+ dataInPrint;
                            String envio =  r.execute(tmp).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                        }

                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter

        //eventos del switch de tiempo y automatico
        swTiempo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtSegundos.setEnabled(true);
                    txtSegundos.setText("15");
                    swAutomatico.setChecked(false);
                }else{
                    txtSegundos.setText("");
                    txtSegundos.setEnabled(false);
                }
            }
        });

        swAutomatico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swTiempo.setChecked(false);
                    txtSegundos.setText("");
                    txtSegundos.setEnabled(false);
                }
            }
        });


        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(swMonitoreo.isChecked()){
//                    cadenaEnviar[0] = "m";
//                }else{
//                    cadenaEnviar[0] = "#";
//                }
//                if(swPeso.isChecked()){
//                    cadenaEnviar[1] = "c";
//                }else{
//                    cadenaEnviar[1] = "#";
//                }
//                if(swAutomatico.isChecked()){
//                    cadenaEnviar[2] = "a";
//                }else{
//                    cadenaEnviar[2]= "#";
//                }
//                if(swTiempo.isChecked()){
//                    cadenaEnviar[3] = txtSegundos.getText().toString();
//                }else{
//                    cadenaEnviar[3] = "#";
//                }
//
//                if(btnIniciar.getText().toString().equals("Iniciar")){
//                    String nuevaCadena = ""; //Cadena que sera enviada al arduino
//                    for(int i = 0; i < 4 ; i++){
//
//                        if( i + 1 == 4){
//                            nuevaCadena += cadenaEnviar[3];
//                            break;
//                        }
//
//                        nuevaCadena += cadenaEnviar[i] + ",";
//                    }
//                    //========================================================ENVIO DE LA CADENA POR BLUETOOTH
//                    MyConexionBT.write(nuevaCadena+"\n");
//
//                    contadorSegundos = 0;
//                    flag_conteo = true;
//                    startCountDownTimer(); //Empezamos el contador el cual inicia el movimeinto del prototipo
//                    btnIniciar.setBackgroundResource(R.drawable.circle_buttom_stop);
//                    btnIniciar.setText("Detener");
//                } else { //========================================================ENVIO DE PARAR EL PROTOTIPO POR BLUETOOTH
//                    resetear();
//                    MyConexionBT.write("s\n");
//                }


                //======================================== informacion temporal solo para provar el servidor ==========================
                try {
                    ConeccionWS r = new ConeccionWS();
                    //peso,obstaculos,tiempo,distancia,
                    String tmp = "http://" + dirIp + ":8003/datos?35&6&35&100";
                    String envio =  r.execute(tmp).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server Not Response", Toast.LENGTH_LONG).show();
                }
            }
        });
    }//=============================== fin oncreate

    public void ConfiguracionInicial(){
        swMonitoreo.setChecked(true);
        swPeso.setChecked(false);
        swTiempo.setChecked(true);
        txtSegundos.setText("15");
        swAutomatico.setChecked(false);
        txtDistancia.setText("50");
    }

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
                }
            }

            @Override
            public void onFinish() {

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
