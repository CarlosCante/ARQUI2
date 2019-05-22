#include "HX711.h"
#include "string.h"
#include <SoftwareSerial.h>

//PEso
#define DOUT  A1
#define CLK  A0
#define booz 27

//Sensor de peso
HX711 balanza;
double peso;

//  sensor ultrasonico
#define MEDIA_VELOCIDAD_SONIDO 0.017175 // Mitad de la velocidad del sonido a 20 °C expresada en cm/µs
#define PIN_TRIGGER 7
#define PIN_ECHO 8
#define ESPERA_ENTRE_LECTURAS 1000 // tiempo entre lecturas consecutivas en milisegundos
#define TIMEOUT_PULSO 25000 // la espera máxima de es 30 ms o 30000 µs
float distancia;
unsigned long tiempo;
unsigned long cronometro;
unsigned long reloj=0;

//Bomba de agua
int EnA = 5;    // motor one
int In1 = 42;
int In2 = 44;
int EnB = 10;   // motor two
int In3 = 46;
int In4 = 48;

//bluetooth
String mensaje;
int empezar = 0;

//setup
void setup() {
  
Serial.begin(9600);
Serial3.begin(9600);
  //area de calibracion de la balanza
  balanza.begin(DOUT, CLK);
  Serial.print("Lectura del valor del ADC:  ");
  Serial.println(balanza.read());
  Serial.println("No ponga ningun  objeto sobre la balanza");
  Serial.println("Destarando...");
  Serial.println("...");
  balanza.set_scale(707815); // Establecemos la escala
  balanza.tare(20);  //El peso actual es considerado Tara.

  //area de sensor ultrasonico
  pinMode(PIN_ECHO,INPUT);
  pinMode(PIN_TRIGGER,OUTPUT);
  pinMode(booz, OUTPUT);
  pinMode(34, OUTPUT);
  digitalWrite(PIN_TRIGGER,LOW); // Para «limpiar» el pulso del pin trigger del módulo
  delayMicroseconds(2);

  Serial.println("Listo para pesar");  
}

//Loop
void loop() {
  if(Serial3.available()){
    mensaje = Serial3.readStringUntil('\n');
    Serial.println(mensaje);

    //aqui avizo que empecemos la ducha
    if(mensaje == "empezar"){
      if(peso > 0 ){
        //si hay toalla entonces empezamos
        Serial3.write("ok#");
        empezar = 1;
      }
      else{
        //si no hay toalla entonces avizo
        Serial3.write("no#");
      }
    }

    if(mensaje=="alarma"){
      digitalWrite(booz, HIGH);
      digitalWrite(34,LOW);
    }
  }
  
  Serial.print("Sonica: ");
  tomar_valor_sonico();
  Serial.println(distancia);
  if(distancia < 10 && empezar == 1){
    encenderBomba();
  }
  else{
    apagarBomba();
  }
  //Serial.print("Peso: ");
  obtenerPeso();
  //Serial.println(peso);
}

//metodos para el sonico
void obtenerPeso(){
  peso = balanza.get_units(20);
  delay(500);
}

//metodos para valor sonico
void tomar_valor_sonico() {
  cronometro=millis()-reloj;
  if(cronometro>ESPERA_ENTRE_LECTURAS)
  {
    digitalWrite(PIN_TRIGGER,HIGH); // Un pulso a nivel alto…
    delayMicroseconds(10); // …durante 10 µs y
    digitalWrite(PIN_TRIGGER,LOW); // …volver al nivel bajo
    tiempo=pulseIn(PIN_ECHO,HIGH,TIMEOUT_PULSO); // Medir el tiempo que tarda en llegar un pulso
    distancia=MEDIA_VELOCIDAD_SONIDO*tiempo;
    reloj=millis();
  }
}

long microsecondsToCentimeters(long microseconds) {
  return microseconds / 58;
}

//metodos para la bomba de agua
void encenderBomba() {
  // turn on motor B
  digitalWrite(In3, HIGH);
  digitalWrite(In4, LOW);
  digitalWrite(In1, HIGH);
  digitalWrite(In2, LOW);
  // set speed to 150 out 255
  analogWrite(EnA, 200);
  // set speed to 150 out 255
  analogWrite(EnB, 200);
}

void apagarBomba(){
  digitalWrite(In1, LOW);
  digitalWrite(In2, LOW);
  digitalWrite(In3, LOW);
  digitalWrite(In4, LOW);
}
