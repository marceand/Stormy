package marcel.com.stormy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import marcel.com.stormy.R;
import marcel.com.stormy.adapters.DayAdapter;
import marcel.com.stormy.weather.Day;

//Check these two recommended Adapter websites
//http://developer.android.com/reference/android/R.layout.html
//http://arteksoftware.com/androids-built-in-list-item-layouts/

public class DailyForecastActivity extends Activity {
    private Day[] mDays;

    @InjectView(android.R.id.list) ListView mListView;
    @InjectView(android.R.id.empty) TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        ButterKnife.inject(this);

        Intent intent=getIntent();
        Parcelable[] parcelables=intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays= Arrays.copyOf(parcelables,parcelables.length,Day[].class);
        DayAdapter adapter=new DayAdapter(this,mDays); //this how you get an array of items from a Parcelable extra from the intent


        mListView.setAdapter(adapter);//Using regular activity
        mListView.setEmptyView(mEmptyTextView);

        //Using a regular activity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String dayOfTheWeek=mDays[position].getDayOfTheWeek();
                String conditions=mDays[position].getSummary();
                String highTemp=mDays[position].getTemperatureMax()+"";
                String message=String.format("On %s the high will be %s and it will be %s",
                        dayOfTheWeek,
                        highTemp,
                        conditions
                );
                Toast.makeText(DailyForecastActivity.this,message,Toast.LENGTH_LONG).show();


            }
        });

    }
}
