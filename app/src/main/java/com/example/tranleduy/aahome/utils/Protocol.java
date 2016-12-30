package com.example.tranleduy.aahome.utils;

/**
 * Created by tranleduy on 22-May-16.
 */
public class Protocol {
    public static final String POST = "P "; //post
    public static final String GET = "G "; //get
    public static final String COMMAND_AT_ESP = "C ";
    public static final String COMMAND_AT_SIM = "C ";

    //P <cmd>
    // P D <value> P D 200
    public static final String SET_AUTO_LIGHT_DIGITAL = "A ";
    public static final String SET_VALUE_AUTO_LIGHT_ANALOG = "B ";
    public static final String SET_AUTO_LIGHT_PIR = "C ";
    public static final String SET_VALUE_AUTO_LIGHT = "D ";
    public static final String SET_FIRE_ALRAM_SYSTEM = "E ";
    //P O <Pin>  P O 22
    //G O <pin> G O 22  result: on ? off
    public static final String SET_OFF = "F ";//off IO
    public static final String SET_ON = "O "; //on IO
    public static final String SET_AUTO_DOOR = "G ";
    public static final String CALL_PHONE_ARDUINO = "H ";
    public static final String OPEN_DOOR = "I ";
    public static final String SET_AUTO_LIGHT_ANALOG = "K ";

    public static final String CLOSE_DOOR = "M ";
    public static final String SET_SECURITY = "S ";
    //G <cmd>

    public static final String GET_AUTO_LIGHT_DIGITAL = "A ";
    public static final String GET_VAULE_LIGHT_SENSOR = "B ";
    public static final String GET_AUTO_LIGHT_PIR = "C ";
    public static final String GET_FIRE_ALRAM_SYSTEM = "D ";
    //G E <pin> G E 21 result high ? low;
    public static final String GET_VALUE_PIR = "E ";
    //G L <pin>  result: on ? off
    public static final String GET_AUTO_DOOR = "G ";
    public static final String GET_AUTO_LIGHT_ANALOG = "K ";
    public static final String GET_STATUS_PIN = "L ";
    public static final String GET_TEMP_HUMI = "T ";
    public static final String GET_SECURITY = "S ";


    //FOR JSON READER
    public static final String TEMPERATURE = "TEMPERATURE";
    public static final String HUMIDITY = "HUMIDITY";
    public static final String VALUE_LIGHT_SENSOR = "LIGHT_SENSOR";
    public static final String AUTO_LIGHT_PIR = "AUTO_LIGHT_PIR";
    public static final String AUTO_LIGHT_ANALOG = "AUTO_LIGHT_ANALOG";
    public static final String AUTO_LIGHT_DIGITAL = "AUTO_LIGHT_DIGITAL";
    public static final String FIRE_ALRAM_SYSTEM = "FIRE_ALRAM_SYSTEM";
    public static final String AUTO_DOOR = "AUTO_DOOR";
    public static final String VALUE_PIR = "PIR";
    public static final String PIN = "PIN";
    public static final String VALUE_PIN = "VALUE";
    public static final String SECURITY = "SECURITY";
    public static final String RESULT = "RESULT";

    //camera
    public static final String RECORDING = "RECORDING";
    public static final String STOP_RECORDING = "STOP_RECORING";
    //telephone
    public static final String CALL_PHONE_ANDROID = "CALL_PHONE";
    public static final String SEND_SMS_ANDROID = "SEND_SMS";

    //music
    public static final String PLAY_MUSIC = "PLAY_MUSIC";
    public static final String PAUSE_MUSIC = "PAUSE_MUSIC";
    public static final String STOP_MUSIC = "STOP_MUSIC";
}
