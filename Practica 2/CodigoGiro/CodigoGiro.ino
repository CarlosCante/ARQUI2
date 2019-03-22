#include <AFMotor.h>
#include <Servo.h>

// Librerias I2C para controlar el mpu6050
// la libreria MPU6050.h necesita I2Cdev.h, I2Cdev.h necesita Wire.h
#include "I2Cdev.h"
#include "MPU6050.h"
#include "Wire.h"

///Definision de servi
Servo servo1;
Servo servo2;
int posicionServo1 = 90;
int posicionServo2 = 90;

AF_DCMotor Motor1(2);
AF_DCMotor Motor2(3);



// La dirección del MPU6050 puede ser 0x68 o 0x69, dependiendo 
// del estado de AD0. Si no se especifica, 0x68 estará implicito
MPU6050 sensor;

// Valores RAW (sin procesar) del acelerometro  en los ejes x,y,z
int ax, ay, az;

// ================================================================
// ===               INTERRUPT DETECTION ROUTINE                ===
// ================================================================

volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
    mpuInterrupt = true;
}


void setup() {
  Serial.begin(57600);    //Iniciando puerto serial
  Wire.begin();           //Iniciando I2C  
  sensor.initialize();    //Iniciando el sensor
  //para los pines del servo
  servo1.attach(10);
  servo2.attach(9);


  servo1.write(90);
  servo2.write(90);

  Motor1.setSpeed(255);
  Motor2.setSpeed(255);
  /*
  attachInterrupt(19, dmpDataReady, RISING);

  if (sensor.testConnection()) Serial.println("Sensor iniciado correctamente");
  else Serial.println("Error al iniciar el sensor");

  // wait for ready
    Serial.println(F("\nSend any character to begin DMP programming and demo: "));
    while (Serial.available() && Serial.read()); // empty buffer
    while (!Serial.available());                 // wait for data
    while (Serial.available() && Serial.read()); // empty buffer again    

   */
}

void loop() {
  Motor1.run(BACKWARD);
  Motor2.run(BACKWARD);
  
  // Leer las aceleraciones 
  sensor.getAcceleration(&ax, &ay, &az);
  //Calcular los angulos de inclinacion:
  float accel_ang_x=atan(ax/sqrt(pow(ay,2) + pow(az,2)))*(180.0/3.14);
  float accel_ang_y=atan(ay/sqrt(pow(ax,2) + pow(az,2)))*(180.0/3.14);

  
  if(accel_ang_x > 10 && posicionServo1 < 180 && posicionServo2 > 0){
    posicionServo1 = posicionServo1 + 2;
    posicionServo2 = posicionServo2 - 2;
    servo1.write(posicionServo1);
    servo2.write(posicionServo2);
  }
  
  if(accel_ang_x < -10 && posicionServo1 > 0 && posicionServo2 < 180){
    posicionServo1 = posicionServo1 - 2;
    posicionServo2 = posicionServo2 + 2;
    servo1.write(posicionServo1);
    servo2.write(posicionServo2);
  }

  Serial.print("Angulo: ");
  Serial.print(accel_ang_x);
  Serial.print("\t Servo1: ");
  Serial.print(posicionServo1); 
  Serial.print("\t servo2:");
  Serial.println(posicionServo2);
    
  delay(5);
}
