package com.osmium.weatherrepoter2017;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.osmium.weatherrepoter2017.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainAcitvity";
    TextView txtCity, txtWeather, txtWeatherInfo, txtTemprature, txtMaxTemp, txtMinTemp, txtHumidity;
    TextView txtSunrise;
    EditText edtSearch;
    Button btnSearch,btnLoad;
    ProgressDialog dialog;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tf.setTimeZone(TimeZone.getTimeZone("UTC"));
        edtSearch = (EditText) findViewById(R.id.edt_search);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnLoad = (Button) findViewById(R.id.btn_load_all);
        txtCity = (TextView) findViewById(R.id.txt_city);
        txtWeather = (TextView) findViewById(R.id.txt_weather);
        txtHumidity = (TextView) findViewById(R.id.txt_humidity);
        txtTemprature = (TextView) findViewById(R.id.txt_temprature);
        txtWeatherInfo = (TextView) findViewById(R.id.txt_weather_info);
        txtMaxTemp = (TextView) findViewById(R.id.txt_max_temp);
        txtMinTemp = (TextView) findViewById(R.id.txt_min_temp);
        txtSunrise = (TextView) findViewById(R.id.txt_sunrise);
        btnSearch.setOnClickListener(this);
        btnLoad.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_search:
                new WeatherGetter().execute(edtSearch.getText().toString());
                break;
            case R.id.btn_load_all:
                ArrayList<Weather>arr= new WeatherDBHelper(this).getAllWeatherInfo();
                Toast.makeText(this," have "+arr.size(),Toast.LENGTH_LONG).show();
        }
    }

    private class WeatherGetter extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... values) {
            //Network Call -start
            String search = values[0];
            String json = new String();
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+search+"&appid=a5af2b26a507452e5bb4573998d01764");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //Read The data
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } catch(IOException ex){
                    Log.e(TAG,ex.getMessage(),ex);
                } finally {
                    in.close();
                }
                json = sb.toString();
                Log.i(TAG,json);
            } catch (MalformedURLException ex) {
                Log.e(TAG,ex.getMessage(),ex);
            } catch (IOException ex) {
                Log.e(TAG,ex.getMessage(),ex);
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
//            Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
            try {
                Weather weather = new Weather();
                JSONObject jsonData = new JSONObject(s);
                weather.setCity(jsonData.getString("name"));
                txtCity.setText(jsonData.getString("name"));
                JSONArray jsonWeathers = jsonData.getJSONArray("weather");
                for (int i = 0 ; i<jsonWeathers.length();i++){
                    JSONObject jsonWeather = jsonWeathers.getJSONObject(i);
                    txtWeather.setText(jsonWeather.getString("main"));
                    weather.setWeather(jsonWeather.getString("main"));
                    txtWeatherInfo.setText(jsonWeather.getString("description"));
                }
                JSONObject jsonMain = jsonData.getJSONObject("main");
                double farenheitTemp = jsonMain.getDouble("temp");
                double maxTemp = jsonMain.getDouble("temp_max");
                double minTemp = jsonMain.getDouble("temp_min");
                int humidity = jsonMain.getInt("humidity");
                txtMaxTemp.setText("Max - Min Temp:"+ convertKelvintoC(maxTemp));
                txtMinTemp.setText(" - "+ convertKelvintoC(minTemp));
                JSONObject jsonSys= jsonData.getJSONObject("sys");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(jsonSys.getLong("sunrise"));
                weather.setTemp(convertKelvintoC(farenheitTemp));
                txtTemprature.setText("Temprature :"+ convertKelvintoC(farenheitTemp)+" Celcius");
                txtHumidity.setText("Humidity :"+humidity);
                txtSunrise.setText("Sunrises at "+tf.format(calendar.getTime()));
                weather.setDate(sdf.format(Calendar.getInstance().getTime()));
                new WeatherDBHelper(MainActivity.this).addRecord(weather);
            } catch (JSONException e) {
                Log.e(TAG,e.getMessage(),e);
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Getting data...");
            dialog.show();
        }

    }

    private String convertKelvintoC(double kelvin){
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(kelvin-273.15);
    }
}
