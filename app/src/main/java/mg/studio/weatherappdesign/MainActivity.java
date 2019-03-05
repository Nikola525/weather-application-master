package mg.studio.weatherappdesign;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();
        btnClick(null);
    }

    public void btnClick(View view) {
        new CurrentWeather().execute();
        new Forecast().execute();
    }

    private boolean isTheWeatherServiceAvailable(Context context){
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private class CurrentWeather extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            if(isTheWeatherServiceAvailable(getApplicationContext())){
                String stringUrl = "https://api.openweathermap.org/data/2.5/weather?q=ChongQing,cn&appid=87900c8dbea46b9af4ee9fb6a6aa3b6d";
                HttpURLConnection urlConnection = null;
                BufferedReader reader;

                try {
                    URL url = new URL(stringUrl);
                    // Create the request to get the information from the server, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Mainly needed for debugging
                        Log.d("TAG", line);
                        buffer.append(line);
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    //The content
                    return buffer.toString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }else{
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String content) {
            if(content != null){
                try{
                    JSONObject root = new JSONObject(content);
                    JSONObject weather = root.getJSONObject("main");
                    String temp = weather.getString("temp");
Log.e("XXX", temp);
                    ((TextView) findViewById(R.id.temperature_of_the_day)).setText(String.valueOf(Float.valueOf(temp).intValue() - 273));


                    Toast showToast=Toast.makeText(getApplicationContext(), "The weather data have updated.", Toast.LENGTH_LONG);
                    showToast.setGravity(Gravity.TOP, 0, 50);
                    showToast.show();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                Toast toast=Toast.makeText(getApplicationContext(), "The weather service is unavailable.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 50);
                toast.show();
            }

        }
    }
    private class Forecast extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            if(isTheWeatherServiceAvailable(getApplicationContext())){

                String stringUrl = "http://t.weather.sojson.com/api/weather/city/101040100";
                HttpURLConnection urlConnection = null;
                BufferedReader reader;

                try {
                    URL url = new URL(stringUrl);

                    // Create the request to get the information from the server, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        // Mainly needed for debugging
                        Log.e("TAG", line);
                        buffer.append(line);
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    //The content

                    return buffer.toString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else {
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String content) {
            if(content != null) {
                try {
                    JSONObject root = new JSONObject(content);
                    JSONObject data = root.getJSONObject("data");
                    JSONArray array = data.getJSONArray("forecast");
                    JSONObject theCurrentDay = array.getJSONObject(0);
                    JSONObject theFirstDay = array.getJSONObject(1);
                    JSONObject theSecondDay = array.getJSONObject(2);
                    JSONObject theThirdDay = array.getJSONObject(3);
                    JSONObject theForthDay = array.getJSONObject(4);

                    ((TextView) findViewById(R.id.tv_location)).setText(root.getJSONObject("cityInfo").getString("city"));
                    ((TextView) findViewById(R.id.tv_date)).setText(root.getString("time").substring(0,10));
                    ((TextView) findViewById(R.id.tv_today)).setText(theCurrentDay.getString("week"));

                    if(theCurrentDay.getString("type").contains("雨")){
                        ((ImageView) findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.rainy_small);
                        ((LinearLayout) findViewById(R.id.linearLayout)).setBackgroundColor(Color.parseColor("#00EE00"));
                        ((ImageView) findViewById(R.id.img_zigzag)).setImageResource(R.drawable.design);
                    }else if(theFirstDay.getString("type").contains("云")){
                        ((ImageView) findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.partly_sunny_small);
                        ((ImageView) findViewById(R.id.img_zigzag)).setImageResource(R.drawable.design);
                    }else if(theCurrentDay.getString("type").contains("晴")){
                        ((ImageView) findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.sunny_small);
                        ((LinearLayout) findViewById(R.id.linearLayout)).setBackgroundColor(Color.parseColor("#EEB422"));
                        ((ImageView) findViewById(R.id.img_zigzag)).setImageResource(R.drawable.design);
                    }else if(theCurrentDay.getString("type").contains("阴")){
                        ((ImageView) findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.partly_sunny_small);
                        ((LinearLayout) findViewById(R.id.linearLayout)).setBackgroundColor(Color.parseColor("#66CD00"));
                        ((ImageView) findViewById(R.id.img_zigzag)).setImageResource(R.drawable.design);
                    }else{
                        ((ImageView) findViewById(R.id.img_lst)).setImageResource(R.drawable.sunny_small);
                        ((LinearLayout) findViewById(R.id.linearLayout)).setBackgroundColor(Color.parseColor("#6495ED"));
                        ((ImageView) findViewById(R.id.img_zigzag)).setImageResource(R.drawable.design);
                    }
                    ((TextView) findViewById(R.id.tv_lst)).setText(theFirstDay.getString("week"));

                    if(theFirstDay.getString("type").contains("雨")){
                        ((ImageView) findViewById(R.id.img_lst)).setImageResource(R.drawable.rainy_small);
                    }else if(theFirstDay.getString("type").contains("云")){
                        ((ImageView) findViewById(R.id.img_lst)).setImageResource(R.drawable.partly_sunny_small);
                    }else if(theFirstDay.getString("type").contains("晴")){
                        ((ImageView) findViewById(R.id.img_lst)).setImageResource(R.drawable.sunny_small);
                    }else if(theFirstDay.getString("type").contains("阴")){
                        ((ImageView) findViewById(R.id.img_lst)).setImageResource(R.drawable.partly_sunny_small);
                    }else{
                        ((ImageView) findViewById(R.id.img_lst)).setImageResource(R.drawable.sunny_small);
                    }
                    ((TextView) findViewById(R.id.tv_lst)).setText(theFirstDay.getString("week"));


                    if(theSecondDay.getString("type").contains("雨")){
                        ((ImageView) findViewById(R.id.img_2nd)).setImageResource(R.drawable.rainy_small);
                    }else if(theSecondDay.getString("type").contains("云")){
                        ((ImageView) findViewById(R.id.img_2nd)).setImageResource(R.drawable.partly_sunny_small);
                    }else if(theSecondDay.getString("type").contains("晴")){
                        ((ImageView) findViewById(R.id.img_2nd)).setImageResource(R.drawable.sunny_small);
                    }else if(theSecondDay.getString("type").contains("阴")){
                        ((ImageView) findViewById(R.id.img_2nd)).setImageResource(R.drawable.partly_sunny_small);
                    }else{
                        ((ImageView) findViewById(R.id.img_2nd)).setImageResource(R.drawable.partly_sunny_small);
                    }
                    ((TextView) findViewById(R.id.tv_2nd)).setText(theSecondDay.getString("week"));


                    if(theThirdDay.getString("type").contains("雨")){
                        ((ImageView) findViewById(R.id.img_3rd)).setImageResource(R.drawable.rainy_small);
                    }else if(theThirdDay.getString("type").contains("云")){
                        ((ImageView) findViewById(R.id.img_3rd)).setImageResource(R.drawable.partly_sunny_small);
                    }else if(theThirdDay.getString("type").contains("晴")){
                        ((ImageView) findViewById(R.id.img_3rd)).setImageResource(R.drawable.sunny_small);
                    }else if(theThirdDay.getString("type").contains("阴")){
                        ((ImageView) findViewById(R.id.img_3rd)).setImageResource(R.drawable.partly_sunny_small);
                    }else{
                        ((ImageView) findViewById(R.id.img_3rd)).setImageResource(R.drawable.partly_sunny_small);
                    }
                    ((TextView) findViewById(R.id.tv_3rd)).setText(theThirdDay.getString("week"));


                    if(theForthDay.getString("type").contains("雨")){
                        ((ImageView) findViewById(R.id.img_4th)).setImageResource(R.drawable.rainy_up);
                    }else if(theForthDay.getString("type").contains("云")){
                        ((ImageView) findViewById(R.id.img_4th)).setImageResource(R.drawable.partly_sunny_small);
                    }else if(theForthDay.getString("type").contains("晴")){
                        ((ImageView) findViewById(R.id.img_4th)).setImageResource(R.drawable.sunny_small);
                    }else if(theForthDay.getString("type").contains("阴")){
                        ((ImageView) findViewById(R.id.img_4th)).setImageResource(R.drawable.partly_sunny_small);
                    }else{
                        ((ImageView) findViewById(R.id.img_4th)).setImageResource(R.drawable.partly_sunny_small);
                    }
                    ((TextView) findViewById(R.id.tv_4th)).setText(theForthDay.getString("week"));

                    Toast showToast = Toast.makeText(getApplicationContext(), "The forecast has updated.", Toast.LENGTH_LONG);
                    showToast.setGravity(Gravity.TOP, 0, 50);
                    showToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{
                Toast toast=Toast.makeText(getApplicationContext(), "The forecast is unavailable.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 50);
                toast.show();
            }
        }
    }
}
