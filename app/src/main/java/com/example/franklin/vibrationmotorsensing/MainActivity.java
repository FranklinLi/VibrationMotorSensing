package com.example.franklin.vibrationmotorsensing;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    Vibrator vib;
    String V_period, V_position, V_gap;

    public float accx = 0, accy = 0, accz = 0;
    public static final String file = ("data.txt");
    public static File myData = null;
    public static File myDataCollection = null;
    public SensorEventListener mSensorListener ;
    public SensorManager sensorManager;
    public List<Sensor> listSensor;
    Boolean switchState = false;
    private  Sensor mAccelerometer;

    public static Date currentTime = Calendar.getInstance().getTime();
    public static final String DATA_COLLECTION_FILE = currentTime.toString() + ".csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                V_gap = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> gap_categories = new ArrayList<String>();
        gap_categories.add("Please enter the gap time");
        gap_categories.add("100");
        gap_categories.add("200");
        gap_categories.add("300");
        gap_categories.add("400");
        gap_categories.add("500");
        gap_categories.add("600");
        gap_categories.add("700");
        gap_categories.add("800");
        gap_categories.add("900");
        gap_categories.add("1000");
        gap_categories.add("1500");
        gap_categories.add("2000");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gap_categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(dataAdapter);


        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                V_period = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> period_categories = new ArrayList<String>();
        period_categories.add("Please enter the vibration period");
        period_categories.add("100");
        period_categories.add("200");
        period_categories.add("300");
        period_categories.add("400");
        period_categories.add("500");
        period_categories.add("600");
        period_categories.add("700");
        period_categories.add("800");
        period_categories.add("900");
        period_categories.add("1000");
        period_categories.add("1500");
        period_categories.add("2000");

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, period_categories);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter1);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                V_position = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> position_categories = new ArrayList<String>();
        position_categories.add("Please enter the position of the phone");
        position_categories.add("in hand");
        position_categories.add("in pocket");
        position_categories.add("in bag");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, position_categories);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter2);

        final Switch startrecording = (Switch) findViewById(R.id.switch1);
        //Boolean switchState = startrecording.isChecked();

        startrecording.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchState == false){
                    switchState = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vib.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        long[] pattern = {0, Integer.valueOf(V_period), Integer.valueOf(V_gap)};
                        vib.vibrate(pattern,1);
                    }
                }
                else{
                    vib.cancel();
                    switchState = false;
                }

            }
        });


        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        mAccelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        List<String> listSensorType = new ArrayList<String>();
        for(int i=0; i<listSensor.size(); i++){
            System.out.println("Inside list sensors:::::::");
            listSensorType.add((i+1)+" "+listSensor.get(i).getName());
            String sensorNames = listSensor.get(i).getName();
            System.out.println("frankli" + listSensor.get(i).getType());
            //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(listSensor.get(i).getType()), SensorManager.SENSOR_DELAY_NORMAL);
            //writeToFile(listSensor.get(i).getName().getBytes(),sensorNames );
        }
        String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();

        myData = new File(extStorageDirectory + "/"+ file);
        try{
            if(!myData.exists()){
                myData.createNewFile();
            }
        }catch(IOException ioExp){
            Log.d("AndroidSensorList::", "error in file creation");
        }

        myDataCollection = new File(extStorageDirectory + "/"+ DATA_COLLECTION_FILE);
        try{
            if(!myDataCollection.exists()){
                myDataCollection.createNewFile();
            }
        }catch(IOException ioExp){
            Log.d("AndroidSensorList::", "error in file creation");
        }

        try{
            OutputStream fo = new FileOutputStream(myDataCollection,true);
            String title = "time,accx,accy,accz,LinearAcc,position" + "\n";

            fo.write(title.getBytes());
            fo.close();
        }catch(IOException e){
            Log.e("AndroidSensorList::","File write failed: " + e.toString());
        }

    }


    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,mAccelerometer,  SensorManager.SENSOR_DELAY_NORMAL);
    }



    protected void onPause() {
        sensorManager.unregisterListener(mSensorListener);
        super.onPause();

    }

    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }





    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        System.out.println("++++++++++++++++INSIDE onSensorChanged() ++++++++++++++++++++++");
        //System.out.println("sensorName:"+sensorName);
        System.out.println("event.sensor.getName():"+event.sensor.getName());
        //float x,y,z;
        String sensorlist = "";


        if (event.sensor.getName().equals("LGE Accelerometer Sensor")){
            accx = event.values[0];
            accy = event.values[1];
            accz = event.values[2];
        }
        long time = System.currentTimeMillis();
        Date currentTime = Calendar.getInstance().getTime();

        sensorlist = String.valueOf(time) + "," + String.valueOf(accx) + "," + String.valueOf(accy) + "," + String.valueOf(accz) + "," + String.valueOf(Math.sqrt(accx*accx + accy*accy + accz*accz)) + "," + V_position +"\n";
        byte[] bsensorlist = String.valueOf(sensorlist).getBytes();

        if (switchState == true){
            try{
                //soundmeter.start();
                OutputStream fo = new FileOutputStream(myDataCollection,true);
                fo.write(bsensorlist);
                fo.close();
            }catch(IOException e){
                Log.e("AndroidSensorList::","File write failed: " + e.toString());
            }
        }


    }

}
