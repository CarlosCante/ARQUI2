package com.example.practica2_2;


import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConeccionWS extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection httpURLConnection = null;
        URL url = null;
        try {
            url = new URL(strings[0]); //hago la utilizacion de la Url que quiero consumir con strings[0]
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            httpURLConnection = (HttpURLConnection)url.openConnection(); //abrimos la conexion
            httpURLConnection.connect();

            int code = httpURLConnection.getResponseCode();

            if(code == HttpURLConnection.HTTP_OK){
                //obtencion del flujo de datos
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                //permite leer el flujo de caracteres que ingresaron
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                StringBuffer buffer = new StringBuffer();
                while((line = reader.readLine())!= null){
                    buffer.append(line);
                }
                return  buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
