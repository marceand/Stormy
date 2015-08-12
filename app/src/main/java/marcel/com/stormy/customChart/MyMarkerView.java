package marcel.com.stormy.customChart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;


import marcel.com.stormy.R;
import marcel.com.stormy.weather.Hour;

/**
 * Created by Marcel on 7/20/2015.
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private Hour[] mHours;
    private int mIndexOfX;
    final static int NUMBER_OF_INDEX=24;

    public MyMarkerView(Context context, int layoutResource,Hour[] hours) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
        mHours=hours;
    }

    @Override
    public void refreshContent(Entry entry, int i) {
        mIndexOfX =entry.getXIndex();
        tvContent.setText(mHours[mIndexOfX].getDayOfTheWeek()+" "+mHours[mIndexOfX].getHour()+" :"+
                "\nTemp: "+mHours[mIndexOfX].getTemperature()+" \u2109"+"\n"
                +"Humidity: "+mHours[mIndexOfX].getHumidity()+"%"+"\n"
                +"Rain/Snow: "+mHours[mIndexOfX].getPrecipChance()+"%"+"\n"
                +"Wind: "+mHours[mIndexOfX].getWindSpeed()+"MPH");
    }

    @Override
    public int getXOffset() {
        // this will center the marker-view horizontally
        if(mIndexOfX ==0){
            return (-getWidth()/6);

        }else if(mIndexOfX ==NUMBER_OF_INDEX){
            return -(getWidth());
        }

        return -(getWidth() / 2);

    }

    @Override
    public int getYOffset() {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
