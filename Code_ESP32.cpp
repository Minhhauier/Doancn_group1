#include <Arduino.h>
#include <WiFi.h>
#include <TinyGPS++.h>
#include <HardwareSerial.h>
#include <SPI.h>
#include <MFRC522.h>
#include <ESP32Servo.h>
#include <Firebase_ESP_Client.h>

#define RST_PIN 32
#define SS_PIN 5
#define servo 13
#define Coi 33 

unsigned long lupdate=0;

//pass wifi
//char ssid[] = "AndroidAP_6995";
//char pass[] = "88888888";
char ssid[] = "PING⁹⁹⁹⁺";
char pass[] = "123456789@";
int value=0;
int dem=0;
//Cấu hình firebase
#define API_KEY "AIzaSyCtP_O2pUarcmhyNNBHHQvRgBFyMNcyN14"
#define DATABASE_URL "https://gpstracker-group1-default-rtdb.firebaseio.com/"
#define USER_EMAIL "lequangminh936@gmail.com"
#define USER_PASSWORD "gpstrackergroup1"

FirebaseData fbdo;
FirebaseData streamData;
FirebaseAuth auth;
FirebaseConfig config;

void streamCallback(FirebaseStream data);
void streamTimeoutCallback(bool timeout);

MFRC522 mfrc522(SS_PIN, RST_PIN);

TinyGPSPlus gps;
HardwareSerial mySerial(1);

boolean check=false;
boolean cb=false;
boolean check1=false;
int speed=0;

ESP32PWM pwm;

void thietlap()
{
  if (mfrc522.PICC_IsNewCardPresent())
  {
   if(mfrc522.PICC_ReadCardSerial())
   {
     dem++;
     if (dem>3) dem=0;
     mfrc522.PICC_HaltA(); 
   }
  }
}
void setup() 
{
  Serial.begin(9600);
  pinMode(33,OUTPUT);
  //RIFD
  SPI.begin();
  mfrc522.PCD_Init();
  // Ket noi Wifi
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED);
 //GPS
  mySerial.begin(9600, SERIAL_8N1, 16, 17); 
  //Servo
  ESP32PWM::allocateTimer(0);
  ESP32PWM::allocateTimer(1);
	ESP32PWM::allocateTimer(2);
	ESP32PWM::allocateTimer(3); 
  pwm.attachPin(servo,50,10);
  // cấu hình firebase
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  // Lắng nghe sự thay đổi của firebase
  if (!Firebase.RTDB.beginStream(&streamData, "/xe")) {
    Serial.println("Không thể bắt đầu stream:");
    Serial.println(streamData.errorReason());
  }
  else 
  {
    Firebase.RTDB.setStreamCallback(&streamData, streamCallback, streamTimeoutCallback);
    Serial.println("Đang lắng nghe thay đổi tại /xe...");
  }
}

void loop() 
{
  unsigned long now = millis();
while (mySerial.available()) {
 gps.encode(mySerial.read());  // Giải mã dữ liệu GPS
}
if (gps.location.isUpdated()) {
  if(gps.location.isValid())
  {
      // Gửi lên Firebase
  if(now - lupdate>1000)
  {
  lupdate=now;
  Firebase.RTDB.setFloat(&fbdo, "/xe/lat", gps.location.lat());
  Firebase.RTDB.setFloat(&fbdo, "/xe/lng", gps.location.lng());
  Firebase.RTDB.setInt(&fbdo, "/xe/vetinh", gps.satellites.value());
  Firebase.RTDB.setFloat(&fbdo, "/xe/speed", gps.speed.kmph());
  }
  }
}
thietlap();
if(dem%2==0) 
{
  pwm.writeScaled(0.05);
  check=false;
  if(check1==false)
  {
    Firebase.RTDB.setBool(&fbdo, "/xe/khoa", false);
    check1=true;
  }
}
else
 {
  pwm.writeScaled(0.1);
  if(check==false)
  {
    Firebase.RTDB.setBool(&fbdo, "/xe/khoa", true);
    check=true;
  }
  check1=false;
}
}
void streamCallback(FirebaseStream data) {
  String path = data.dataPath();
  Serial.print("Thay đổi tại: ");
  Serial.println(path);

  if (path == "/khoa") {
    bool khoa = data.boolData();
    if(khoa) dem=1;
    else dem=0;
  }

  else if (path == "/coi") {
    bool coi = data.boolData();
    if(coi) digitalWrite(Coi,1);
    else digitalWrite(Coi,0);
  }
  else {
    Serial.println("Dữ liệu khác không xử lý.");
  }
}
void streamTimeoutCallback(bool timeout) {
  if (timeout) {
    Serial.println("⚠️ Stream timeout.");
  }
}