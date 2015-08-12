package marcel.com.stormy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import marcel.com.stormy.R;
import marcel.com.stormy.weather.Current;
import marcel.com.stormy.weather.Day;
import marcel.com.stormy.weather.Forecast;
import marcel.com.stormy.weather.Hour;


//TODO: get your API KEY from forecast.io and assign it to the API_KEY variable below
//TODO: add also your Latitude and Longitude (line 73 and 74) unless you implement google map

public class MainActivity extends Activity {

    private static final String API_KEY=""; //Add your api key as string

    public static final String TAG=MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST="DAILY_FORECAST";
    public static final String HOURLY_FORECAST="HOURLY_FORECAST";
    private boolean goToNextActivity;
    private Forecast mForecast;

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.locationLabel) TextView mLocationLabel;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        goToNextActivity=false;

        //Add also your Latitude and Longitude
        final double latitude=40.8333;
        final double longitude=-73.9166;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude,longitude);
            }
        });

        getForecast(latitude,longitude);

    }

    private void getForecast(double latitude, double longitude) {

        String forecastUrl="https://api.forecast.io/forecast/"+API_KEY+
                "/"+latitude+","+longitude;


        //Check network connection

        if(isNetworkAvailable()) {

            toggleRefresh();

            OkHttpClient client = new OkHttpClient(); //client the android

            //Build a request that the client sends to the server(forecast)
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            //Put the request inside a Call object
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                   alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {

                        //After get a response, check if the request was successful
                        String jsonData=response.body().string();

                        if (response.isSuccessful()) {

                            mForecast =parseForecastDetails(jsonData);
                            goToNextActivity=true;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "exception caught: ", e);
                    }

                }
            });
        }else{
            //Let user know no network connection
            noConnectivityUser();
            goToNextActivity=false;
        }
    }

    private void toggleRefresh() {

        if(mProgressBar.getVisibility()==View.INVISIBLE) {

            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);

        }else{

            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);

        }
    }

    private void updateDisplay() {

        Current current=mForecast.getCurrent();

        mLocationLabel.setText("New York, NY");
        mTemperatureLabel.setText(current.getTemperature()+" ");
        mTimeLabel.setText("At "+ current.getFormattedTime()+" it will be");
        mHumidityValue.setText(current.getHumidity()+"");
        mPrecipValue.setText(current.getPrecipChance()+"%");
        mSummaryLabel.setText(current.getSummary());

        Drawable drawable=getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails(String jsonData)throws JSONException{

        Forecast forecast=new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;

    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{

        //This gives the forecast for the next 7 days.
        JSONObject forecast=new JSONObject(jsonData);
        String timezone=forecast.getString("timezone");
        JSONObject daily=forecast.getJSONObject("daily");
        JSONArray data=daily.getJSONArray("data");

        Day[] days=new Day[data.length()];

        for(int i=0; i<data.length(); i++){

            JSONObject jsonDay=data.getJSONObject(i);
            Day day=new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i]=day;

        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException{

        //This gives the forecast for the next 48 days.

        JSONObject forecast=new JSONObject(jsonData);
        String timezone=forecast.getString("timezone");
        JSONObject hourly=forecast.getJSONObject("hourly");

        JSONArray data=hourly.getJSONArray("data");

        Hour[] hours=new Hour[data.length()];

        for(int i=0; i<data.length(); i++){

            JSONObject jsonHour=data.getJSONObject(i);
            Hour hour=new Hour();
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setHumidity(jsonHour.getDouble("humidity"));
            hour.setPrecipChance(jsonHour.getDouble("precipProbability"));
            hour.setWindSpeed(jsonHour.getDouble("windSpeed"));
            hour.setTimezone(timezone);

            hours[i]=hour;

        }

        return hours;
    }


    private Current getCurrentDetails(String jsonData)throws JSONException{

        JSONObject forecast=new JSONObject(jsonData);

        String timezone=forecast.getString("timezone");

        JSONObject currently=forecast.getJSONObject("currently");

        Current current =new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);

        return current;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager= (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        boolean isAvailable=false;

        if(networkInfo!=null && networkInfo.isConnected()) {
           isAvailable = true;
        }
        return isAvailable;

    }

    private void alertUserAboutError() {

        //Use Dialog or Fragment dialog
        AlertDialogFragment dialog=new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");

    }

    @OnClick(R.id.dailyButton)
    public void startDailyActivity(View view){

        if(goToNextActivity) {
            //Check if you have the data for the next activity
            //else don't create the next activity
            Intent intent = new Intent(this, DailyForecastActivity.class);
            intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
            startActivity(intent);
        }else{
            noConnectivityUser();
        }
    }

    @OnClick(R.id.hourlyButton)
    public void startHourlyActivity(View view){

        if(goToNextActivity) {
            //Check if you have the data for the next activity
            //else don't create the next activity
            Intent intent = new Intent(this, HourlyForecastActivity.class);
            intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
            startActivity(intent);
        }else{

            noConnectivityUser();
        }
    }

    //Notify user that network is unavailable
    private void noConnectivityUser(){
        Toast.makeText(this, getString(R.string.network_unavailable_message),
                Toast.LENGTH_LONG).show();
    }

}
