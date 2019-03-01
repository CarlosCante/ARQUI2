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

//variables para mostrar identificacion
int uv_btn = 1;
int co_btn = 0;
int coo_btn = 0;

//textos de uv, e imagenes uv
String texto1_uv = "No se necesita proteccion";
String texto2_uv = "Se necesita proteccion";
String texto3_uv = "Se necesita proteccion extra";
String texto4_uv = "Alerta PELIGRO!!!!!!";
PImage img1_uv;
PImage img2_uv;
PImage img3_uv;
PImage peligro;

//textos co, e imagenes co
String texto1_co = "Todo bien";
String texto2_co = "Posibles mareos";
String texto3_co = "Moriras amigo";
PImage img1_co;
PImage img2_co;
PImage img3_co;

//textos co2, imagenes co2
String texto1_coo = "Aire limpio";
String texto2_coo = "En el borde de seguridad";
String texto3_coo = "Estas a punto de morir!";
PImage img1_coo;
PImage img2_coo;
PImage img3_coo;


String titulo1 = "Rayos uv";
String titulo2 = "Cantidad de co2";
String titulo3 = "Cantidad de monoxido de carbono";
 
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
   
   //imagenes
   img1_uv = loadImage("sol1.png");
   img2_uv = loadImage("sol2.png");
   img3_uv = loadImage("sol3.png");
   
   img1_co = loadImage("mono1.jpg");
   img2_co = loadImage("mono2.jpeg");
   img3_co = loadImage("mono3.jpg");
   
   img1_coo = loadImage("aire1.png");
   img2_coo = loadImage("aire2.jpg");
   img3_coo = loadImage("aire3.jpg");
   
   peligro = loadImage("toxico.jpg");
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
     if(banderaBoton && Dato_Co2 != "0" && Dato_Monoxido != "0" && Dato_LuzUV != "0")
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
    
    //uv
    else if(mouseX >= 50 && mouseX <= 300 && mouseY >= 1000 && mouseY <= 1250){
      uv_btn = 1;
      co_btn = 0;
      coo_btn = 0;
    }
    
    //co2
    else if(mouseX >= 350 && mouseX <= 600 && mouseY >= 1000 && mouseY <= 1250){
      uv_btn = 0;
      co_btn = 0;
      coo_btn = 1;
    }
    
    //co
    else if(mouseX >= 650 && mouseX <= 900 && mouseY >= 1000 && mouseY <= 1250){
      uv_btn = 0;
      co_btn = 1;
      coo_btn = 0;
    }
  }
}

 
void MandarDatosAlServidor()
{
  if(contador >= 300)
   {
     //Dato_LuzUV = Dato_LuzUV.replaceAll("\n",""); 
     String url = "http://" + "3.90.33.177:8003/resultado?"+Dato_LuzUV+"&"+Dato_Monoxido+"&"+Dato_Co2;
     println(url);
     String[] lines = loadStrings(url);
     //println(lines.length);
     for (int i = 0 ; i < lines.length; i++) {
        println(lines[i]);
     }
     contador = 0;
    }
  
    contador = contador +1;
}
 
void MostrarDatosEnPantalla()
{
  background(#001f3f);
  
  
  //aqui voy a decidir que dibujar
  if(uv_btn == 1){
    int uv = int(Dato_LuzUV);
    
    text(titulo1, 500,100);
    
    if(uv >=1 && uv <= 2){
        image(img1_uv, 600, 330);
        text(texto1_uv,400,830);
    }
    else if(uv >=3 && uv <= 7){
       image(img2_uv, 600, 330);
        text(texto2_uv,400,830);
    }
    else if(uv >=8 && uv <= 11){
       image(img3_uv, 600, 330);
        text(texto3_uv,400,830);
    }
    else if(uv >=12){
       image(peligro, 600, 330);
        text(texto4_uv,400,830);
    }
  }
  
  else if(co_btn == 1){
    int co = int(Dato_Monoxido);
    text(titulo3, 500,100);
    if(co >= 30 && co <= 400){
        image(img1_co, 600, 330);
        text(texto1_co,400,830);
    }
    
    else if(co >= 401 && co <= 3200){
        image(img2_co, 600, 330);
        text(texto2_co,400,830);
    }
    else if(co >= 3201){
        image(img3_co, 600, 330);
        text(texto3_co,400,830);
    }
  }
  else if(coo_btn == 1){
    int co2 = int(Dato_Co2);
    text(titulo2, 500,100);
    if(co2 >= 0 && co2 <= 1000){
        image(img1_coo, 600, 330);
        text(texto1_coo,400,830);
    }
    
    else if(co2 >= 1001 && co2 <= 5000){
        image(img2_coo, 600, 330);
        text(texto2_coo,400,830);
    }
    else if(co2 >= 5000){
        image(img3_coo, 600, 330);
        text(texto3_coo,400,830);
    }
  }
  /*textSize (70);
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
  line(0, 735, displayWidth, 735);*/
  
  //dibujamos el boton de para ver el uv
  fill(#2ECC40);
  rect(50, 1000 , 250, 250);
  textSize (45); 
  fill(255, 255, 0);
  text("Rayos Uv",75, 1125);
  
  //dibujamos el boton de para ver el co2
  fill(#2ECC40);
  rect(350, 1000 , 250, 250);
  textSize (45); 
  fill(255, 255, 0);
  text("Nivel Co2",375, 1125);
  
  //dibujamos el boton de para ver el co
  fill(#2ECC40);
  rect(650, 1000 , 250, 250);
  textSize (45); 
  fill(255, 255, 0);
  text("Nivel Co",675, 1125);
  
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
      Dato_Co2 = tmp[2];
      Dato_Monoxido = tmp[1];
      Dato_LuzUV = tmp[0];
      
      //Se limpia la variable que actua como buffer para que este lista para recibir nuevos datos;
      mensaje="";
    }
  }
}
