#include <SoftwareSerial.h>   // Incluimos la librería  SoftwareSerial  
#include <String.h>
#include <Separador.h> //libreria que nos ayudara para la separacion de cadenas

//  datos generales
int peticion_movil = 0;
int contador_obstaculos = 0;

//  datos bluetooth
Separador separador;
int flag_monitoreo = 0;
int flag_soltarCarga = 0;
int flag_stopTiempo = 0;
int no_segundos = 0;
int flag_obstaculosAutomaticos = 0;
String monitoreo = "#";
String soltarCarga = "#";
String stopDistancia = "#";
String segundos_cad = "#";
String obstaculosAutomaticos = "#";
String cadena = "";
int llegoFinal  = 0; //bandera que avisa si llego al final prototipo o que el usuario lo descidio desde la app

//  sensor ultrasonico
long duration, cm;
int tiempo_sonico = 0;
const int trigPin = 8;
const int echoPin = 7;

//  sensor CNY70
int tiempo_cny = 0;
int valorcny = 0;
int valorcny2 = 0;
int valorcny_aceptable = 0;

//  motores DC
int EnA = 5;    // motor one
int In1 = 42;
int In2 = 44;
int EnB = 10;   // motor two
int In3 = 46;
int In4 = 48;

//  tiempos milis
unsigned long tiempo_sonico_inicio = 0;
unsigned long tiempo_sonico_final = 0;
unsigned long tiempo_cny_inicio = 0;
unsigned long tiempo_cny_final = 0;
unsigned long lapso_tiempo = 0;

void setup() {
  Serial.begin(9600);
  Serial3.begin(9600); //Serial para el hc05
  pinMode(52, OUTPUT);
  digitalWrite(52, HIGH); //pin vcc
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  digitalWrite(trigPin, LOW);

  tiempo_sonico = 200;
  tiempo_cny = 200;
  tiempo_sonico_inicio = millis();
  tiempo_cny_inicio = millis();
  valorcny_aceptable = 110;
}

void loop() {
  if (Serial3.available()) {
    inicializarControlCadenaEntrada();
    //obtenemos la cadana entrante
    cadena = Serial3.readStringUntil('\n'); //recibimos los datos del serial

    peticion_movil = 1;

    if (cadena.length() == 1) { //significa que trae mas datos de configuracion
      if (cadena == "s") { //s de stop
        Serial.println("cadena tiene 1 dato");
        llegoFinal = 1;
      }
    } else {
      /*---------------- PARTE DONDE SPLITEAMOS  LOS VALORES*/
      Serial.println("  cadena tiene mas de 1 dato");
      monitoreo = separador.separa(cadena, ',', 0);
      soltarCarga = separador.separa(cadena, ',', 1);
      obstaculosAutomaticos = separador.separa(cadena, ',', 2);
      segundos_cad = separador.separa(cadena, ',', 3);

      if (monitoreo == "m") {
        flag_monitoreo = 1;
        //Serial.println("  estoy en monitoreo");
      }
      if (soltarCarga == "c")
        flag_soltarCarga = 1;
      if (obstaculosAutomaticos == "a")
        flag_obstaculosAutomaticos = 1;
      if (segundos_cad != "#") {
        flag_stopTiempo = 1;
        no_segundos = segundos_cad.toInt();
      }
    }
  }

  if (peticion_movil == 1) {
    if (llegoFinal == 0) {
      
      tiempo_cny_final = millis();        // tiempo inicial cny
      valorcny = (int) analogRead(A0);    // valor cny
      valorcny2 = (int) analogRead(A1);    // valor cny
      lapso_tiempo = (tiempo_cny_final - tiempo_cny_inicio);

      if (lapso_tiempo > tiempo_cny) {
        //Serial.println(valorcny);
        //Serial.print( / );
        //Serial.print(valorcny2);

        if (valorcny > valorcny_aceptable && valorcny2 > valorcny_aceptable) {
          tomar_valor_sonico();
          if (cm > 16) {
            avanzarLlantas();
          } else {
            contador_obstaculos += 1;
            if (flag_obstaculosAutomaticos == 1) {
              evacionAutomatica();
            } else {
              detenerLlantas();
              delay(10000);     //insertar tiempo del usuario
            }
          }

        } else {
          rectificar_llantas();
        }
        //pendiente de validar rango
        tiempo_cny_inicio = millis(); //Actualiza el tiempo actual
      }
    } else {
      detenerLlantas();
      /*peso,obstaculos,
        Serial3.write("/datos?35&6#");
        Serial.println("llego al final");
        llegoFinal = 0;*/
    }
  }
}
void rectificar_llantas(){
  if(valorcny < valorcny_aceptable){
    detenerLlantas();
    delay(200);
    
    moverLlantaUnoAdelante();
    delay(400);

    detenerLlantas();
    delay(200);
  }

  if(valorcny2 < valorcny_aceptable){
    detenerLlantas();
    delay(200);
    
    moverLlantaDosAdelante();
    delay(400);

    detenerLlantas();
    delay(200);   
  }
}
  
void inicializarControlCadenaEntrada() {
  //inicializando las banderas
  flag_monitoreo = 0;
  flag_soltarCarga = 0;
  flag_stopTiempo = 0;
  no_segundos = 0;
  flag_obstaculosAutomaticos = 0;
  peticion_movil = 0;
  llegoFinal = 0;

  //inicializando las variables
  monitoreo = "#";
  soltarCarga = "#";
  stopDistancia = "#";
  segundos_cad = "#";
  obstaculosAutomaticos = "#";
}

long microsecondsToCentimeters(long microseconds) {
  return microseconds / 58;
}

void evacionAutomatica() {
  int tiempomovevacion = 700;
  delay(2000);
  moverLlantaDosAtras();
  delay(tiempomovevacion);
  detenerLlantas();
  delay(500);

  avanzarLlantas();
  delay(tiempomovevacion);
  detenerLlantas();
  delay(500);

  moverLlantaDosAdelante();
  delay(tiempomovevacion);
  detenerLlantas();
  delay(500);

  avanzarLlantas();
  delay(tiempomovevacion);
  detenerLlantas();
  delay(500);

  moverLlantaDosAdelante();
  delay(tiempomovevacion);
  detenerLlantas();
  delay(500);

  avanzarLlantas();
  delay(tiempomovevacion);
  detenerLlantas();
  delay(500);

  moverLlantaUnoAdelante();
  delay(tiempomovevacion);
  detenerLlantas();
  delay(500);
}

void tomar_valor_sonico() {
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);        //Devuelve la longitud del pulso del pin Echo en us
  cm = microsecondsToCentimeters(duration); //conversion del tiempo en distancia
}

void avanzarLlantas() {
  moverLlantaUnoAdelante();
  moverLlantaDosAdelante();
}

void detenerLlantas() {
  // turn on motor B
  digitalWrite(In1, LOW);
  digitalWrite(In2, LOW);
  digitalWrite(In3, LOW);
  digitalWrite(In4, LOW);
}

void moverLlantaUnoAdelante() {
  // turn on motor A
  digitalWrite(In1, HIGH);
  digitalWrite(In2, LOW);
  // set speed to 150 out 255
  analogWrite(EnA, 200);
}

void moverLlantaUnoAtras() {
  // turn on motor A
  digitalWrite(In1, LOW);
  digitalWrite(In2, HIGH);
  // set speed to 150 out 255
  analogWrite(EnA, 200);
}

void moverLlantaDosAdelante() {
  // turn on motor B
  digitalWrite(In3, HIGH);
  digitalWrite(In4, LOW);
  // set speed to 150 out 255
  analogWrite(EnB, 200);
}

void moverLlantaDosAtras() {
  // turn on motor B
  digitalWrite(In3, LOW);
  digitalWrite(In4, HIGH);
  // set speed to 150 out 255
  analogWrite(EnB, 200);
}
