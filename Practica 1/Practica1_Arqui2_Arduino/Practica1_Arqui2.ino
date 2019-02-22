#include <SoftwareSerial.h>

#define anInput     A0                        //analog feed from MQ135
#define digTrigger   2                        //digital feed from MQ135
#define co2Zero     55                        //calibrated CO2 0 level

#define RxD 10
#define TxD 11

SoftwareSerial BTSerial(RxD, TxD);

//************************************VARIABLES DE CO2********************
int co2raw = 0;                               //int for raw value of co2
int co2comp = 0;                              //int for compensated co2 
int co2ppm = 0;                               //int for calculated ppm
int zzz = 0;

//***********************************VARIABLES DE MONOXIDO****************
//constantes
const int AOUTpin = A2;
const int DOUTpin = 50;
const int ledPin = 40;

//**********************************VARIABLES UV**************************
float sensorVoltage; 
float sensorValue;
int Indice;


void setup() {
  //BLUETOOTH
  BTSerial.begin(9600);
  BTSerial.print("AT\r\n");
  
  //CO2
  pinMode(anInput,INPUT);                     //MQ135 analog feed set for input
  pinMode(digTrigger,INPUT);                  //MQ135 digital feed set for input
  //MONOXIDO
  pinMode(DOUTpin, INPUT);
  pinMode(ledPin,OUTPUT);
  
  Serial.begin(9600);                         //serial comms for debuging
}

void loop() {

//********************************CODIGO SENSOR DE CO2************************************************  
  int co2now[10]; 
  co2raw = 0;                               //int for raw value of co2
  co2comp = 0;                              //int for compensated co2 
  co2ppm = 0;                               //int for calculated ppm
  zzz = 0;
  
  for (int x = 0; x<10 ; x++){                   //samplpe co2 10x over 2 seconds
    co2now[x]=analogRead(A0);
  }

  for (int x = 0 ; x < 10 ; x++){                     //add samples together
    zzz=zzz + co2now[x];
  }
  
  co2raw = zzz/10;                            //divide samples by 10
  co2comp = co2raw - co2Zero;                 //get compensated value
  co2ppm = map(co2comp,0,1023,400,5000);      //map value for atmospheric levels


//====================================================================================================
//********************************************CODIGO MONOXIDO*****************************************
 
  int valormonoxido = analogRead(AOUTpin); //leemos el valor analogo del sensor analogico
  
//====================================================================================================
//********************************************CODIGO UV***********************************************

  sensorValue = analogRead(A1);
  sensorVoltage = sensorValue/1024*3.3;
  Indice = IndiceUV(sensorValue);

//====================================================================================================
//*********************************************BLUETOOTH**********************************************
  if (BTSerial.available())
    Serial.write(BTSerial.read());

  if (Serial.available())
    BTSerial.write(Serial.read());

  Serial.print("Medida de Co2: ");
  Serial.println(co2ppm);
  Serial.print("Medida de Monoxido: ");
  Serial.println(valormonoxido);  
  Serial.print("Indice UV = ");
  Serial.println(Indice);
  
  BTSerial.print(co2ppm);
  BTSerial.print(",");
  BTSerial.print(valormonoxido);
  BTSerial.print(",");
  BTSerial.print(Indice);
  BTSerial.print("\n");
  
  delay(1000);

}

int IndiceUV(float ValorSensor)
{
    if(ValorSensor <= 10)
    {
      return 0;
    }
    else if(ValorSensor > 10 && ValorSensor <= 46)
    {
      return 1;
    }
    else if(ValorSensor > 46 && ValorSensor <= 65)
    {
      return 2;
    }
    else if(ValorSensor > 65 && ValorSensor <= 83)
    {
      return 3;
    }
    else if(ValorSensor > 83 && ValorSensor <= 103)
    {
      return 4;
    }
    else if(ValorSensor > 103 && ValorSensor <= 124)
    {
      return 5;
    }
    else if(ValorSensor > 124 && ValorSensor <= 142)
    {
      return 6;
    }
    else if(ValorSensor > 142 && ValorSensor <= 162)
    {
      return 7;
    }
    else if(ValorSensor > 162 && ValorSensor <= 180)
    {
      return 8;
    }
    else if(ValorSensor > 180 && ValorSensor <= 200)
    {
      return 9;
    }
    else if(ValorSensor > 200 && ValorSensor <= 221)
    {
      return 10;
    }
    else if(ValorSensor > 221 && ValorSensor <= 240)
    {
      return 11;
    }
    else
    {
      return 12;  
    }
}
