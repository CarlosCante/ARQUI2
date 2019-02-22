import android.content.Intent;
import android.os.Bundle;
import ketai.net.bluetooth.*;
import ketai.ui.*;
import ketai.net.*;
 
PFont fontMy; 
boolean bReleased = true; //Variable que indica si se esta conectado a un dispositivo
KetaiBluetooth bt;
boolean isConfiguring = true;
boolean banderaBoton = false;
 
//Variables para el reconocimiento de los datos
static final byte TERMINADOR_DATOS=10;
String Dato_Co2 = "0";
String Dato_Monoxido = "0";
String Dato_LuzUV = "0";
String MAC="";
String mensaje="";
int contador = 0;
 
 
KetaiList klist;
ArrayList devicesDiscovered = new ArrayList();
 
/***************************************************************************************
------------------Metodos nesearios para la conexion con el bluetooth-------------------
------------------------------------(NO TOCAR)------------------------------------------
****************************************************************************************/
void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
   bt = new KetaiBluetooth(this);
}
 
void onActivityResult(int requestCode, int resultCode, Intent data) {
   bt.onActivityResult(requestCode, resultCode, data);
}
 
//*************************************************************************************** 

void setup() {
   size(displayWidth, displayHeight);    //Se define la resolucion de pantalla de la aplicacion
   frameRate(10);                        //Se define la tasa de refresco de la pantalla
   orientation(PORTRAIT);                //Se define la orientacion de la pantalla(Que no pueda voltearse)
   background(0);                        //Color blnaco de fondo
 
   //Se inicia la coexion bluetooth
   bt.start();
 
   //La aplicacion inicia en la seleccion del dispositivo
   isConfiguring = true;
 
   //font size
   fontMy = createFont("SansSerif", 40);
   textFont(fontMy);
}
 
 
void draw() {
   //La aplicacion inicia en la seleccion de dispositivo a conectar
   if (isConfiguring)
   {
    background(78, 93, 75);
  
    //Se crea una lista con los dispositivos emparejados al telefono
    klist = new KetaiList(this, bt.getPairedDeviceNames());
  
    isConfiguring = false;
   }
   else
   {
   
     MostrarDatosEnPantalla();
     if(banderaBoton && Dato_Co2 != "0" && Dato_LuzUV != "0" && Dato_LuzUV != "0")
     {
       MandarDatosAlServidor();
     }
     delay(500);

 }
}

void mouseReleased()
{
  if(banderaBoton)
  {
    if(mouseX >= 50 && mouseX <= 1050 && mouseY >= 1450 && mouseY <= 1700) //Verifica que se pulso la pantalla en el area del boton DESCONECTAR
    {
      isConfiguring =  true; //Devuelve la variable a true para indicar que se volvera a configurar una conexion
      bt.stop();             //Se da por terminada la conexion con el dispositivo actual
      banderaBoton = false;  //Se bloque la captura de pantalla para los botones
    }
    else  if(mouseX >= 50 && mouseX <= 1050 && mouseY >= 1750 && mouseY <= 2000)//Verifica que se pulso la pantalla en el area del boton CERRAR
    {   
      banderaBoton = false;
      bt.stop(); //Se da por terminada la conexion con el dispositivo actual
      super.exit(); //Se cierra la aplicacion
    }
  }
}

 
void MandarDatosAlServidor()
{
  if(contador >= 20)
   {
     String url = "http://" + "54.209.43.106:8003/resultado?"+Dato_LuzUV+"&"+Dato_Monoxido+"&"+Dato_Co2;
     String[] lines = loadStrings(url);
     println(lines[0]);
     contador = 0;
    }
  
    contador = contador +1;
}
 
void MostrarDatosEnPantalla()
{
  background(#001f3f);
  
  textSize (70);
  fill(#FF851B);
  text("CO EN EL AIRE (PPM)",200,200);
  textSize (120);
  text(Dato_Monoxido,displayWidth/2 - 90,330);
  stroke(255);
  line(0, 225, displayWidth, 225);
  line(0, 235, displayWidth, 235);
  
  textSize (70);
  fill(#DDDDDD);
  text("CO2 EN EL AIRE (PPM)",180,450);
  textSize (120);
  text(Dato_Co2,displayWidth/2 - 120,580);
  stroke(255);
  line(0, 475, displayWidth, 475);
  line(0, 485, displayWidth, 485);
  
  textSize (70);
  fill(#FFDC00);
  text("INDICE UV",360,700);
  textSize (120);
  text(Dato_LuzUV,displayWidth/2 - 60,830);
  stroke(255);
  line(0, 725, displayWidth, 725);
  line(0, 735, displayWidth, 735);
  
  
  //Dibujado del boton para desconectar
  fill(#2ECC40);
  rect(50, 1400 , 1000, 250);
  textSize (70); 
  fill(255, 255, 0);
  text(" DESCONECTAR", 280, 1550);
  
  //Dibujado del boton para desconectar
  fill(255, 0, 0);
  rect(50, 1750 , 1000, 250);
  textSize (70); 
  fill(255, 255, 0);
  text("      CERRAR   ", 280, 1900);
  
  
}


//Metodo que capta la seleccion dentro de la lista de dispositivos
void onKetaiListSelection(KetaiList klist) {
   String selection = klist.getSelection();
   bt.connectToDeviceByName(selection);
 
   //Se guarda la direccion MAC del dispositivo seleccionado para filtrar que la informacion leida sea solo de este
   MAC = bt.lookupAddressByName(selection);
   banderaBoton = true;
 
   klist = null; 
}
 
 
//Metodo que se activa cuando el telefono recibe datos por medio del bluetooth
 
void onBluetoothDataEvent(String origen, byte[] datos)
{
  if(origen==MAC)//Se verifica que los datos recibidos sean del dispositivo conectado por la aplicacion
  {  
    mensaje+=new String(datos);//Si si provienen del dispositivo deseado se ban almacenando en la variable mensaje 
    if(datos[datos.length-1]==TERMINADOR_DATOS)//se verifica que el mensaje recibido hasta ahora contenga el caracter de terminacion
    {
      //Se separan los datos obtenidos por com ya que asi se definio que se recibirian
      String [] tmp = mensaje.split(",");
      
      //Se guardan los datos separados para mostrarlos en pantalla y enviarlos al servidor
      Dato_Co2 = tmp[0];
      Dato_Monoxido = tmp[1];
      Dato_LuzUV = tmp[2];
      
      //Se limpia la variable que actua como buffer para que este lista para recibir nuevos datos;
      mensaje="";
    }
  }
}
