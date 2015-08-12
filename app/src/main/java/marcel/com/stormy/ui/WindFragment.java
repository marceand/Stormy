package marcel.com.stormy.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment; //supporting old devices
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.Arrays;

import marcel.com.stormy.R;
import marcel.com.stormy.customChart.MyMarkerView;
import marcel.com.stormy.customChart.MyValueFormatter;
import marcel.com.stormy.weather.Hour;

/**
 * A simple {@link Fragment} subclass.
 */
public class WindFragment extends Fragment implements OnChartValueSelectedListener{

    private Hour[] mHours;
    private LineChart mChart;

    private int minWindSpeed;
    private int maxWindSpeed;
    private final static int COMPENSATE_MIN_MAX = 10;
    private final static int SUBSTRACT_FROM_HOURS = 24;

    public WindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);

        //Receive and cast the data sent by the activity HourlyForestActivity.java
        Parcelable[] parcelables = (Parcelable[]) container.getTag();
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        //Get reference to LineChart
        mChart = (LineChart) rootView.findViewById(R.id.chartHour);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Plot Graph
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        mChart.setDrawGridBackground(false); //Allow to customize the background set to true
        mChart.setHighlightEnabled(false);//Allow cursor
        mChart.getAxisRight().setEnabled(false); //No right axis
        //mChart.setMaxVisibleValueCount(25);//Plot 25 values
        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart); //Add animation

        //setOnChartValueSelectedListener
        mChart.setOnChartValueSelectedListener(this);

        //Legend
        Legend legend = mChart.getLegend();
        legend.setEnabled(false); //No Legend

        //Prepare values for X axis and Y axis
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        //mHours contain data of the next 2 days which is equal to 48 hours
        //so only plot the values for the next 24 hours, so substract 24 of
        //the 48 data in mHours array.

        //X-Axis values have to be string
        for (int i = 0; i < mHours.length - SUBSTRACT_FROM_HOURS; i++) {
            xVals.add(mHours[i].getHour());
        }

        //Y-Axis values have to be float
        minWindSpeed = mHours[0].getWindSpeed();
        maxWindSpeed = mHours[0].getWindSpeed();
        for (int i = 0; i < mHours.length - SUBSTRACT_FROM_HOURS; i++) {
            yVals.add(new Entry(mHours[i].getWindSpeed(), i));

            //Find the Minimum value
            if (mHours[i].getWindSpeed() < minWindSpeed) {
                minWindSpeed = mHours[i].getWindSpeed();
            }

            //Find Maximum value
            if (mHours[i].getWindSpeed() > maxWindSpeed) {
                maxWindSpeed = mHours[i].getWindSpeed();
            }
        }

        //Initialize for the custom circular indicator
        //If user click on the line, a circle is shown as indicator
        ArrayList<Entry> indicatorCircle = new ArrayList<Entry>();
        indicatorCircle.add(new Entry(mHours[0].getTemperature(), 0));

        //Customize the X-label
        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false); //Not draw the vertical grid lines
        xl.setEnabled(true);
        xl.setTextColor(Color.WHITE);
        xl.setAxisLineColor(Color.WHITE);

        //Customize the Y-label
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setStartAtZero(false);//Axis minimum is not zero
        leftAxis.setValueFormatter(new MyValueFormatter("MPH")); //Set Y label to miles per hour (MPH)
        leftAxis.setAxisMaxValue(maxWindSpeed + COMPENSATE_MIN_MAX);//Set maximum value for Y axis
        leftAxis.setAxisMinValue(minWindSpeed - COMPENSATE_MIN_MAX); //Set minimum value for Y axis
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setGridColor(Color.WHITE);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view, mHours);
        mChart.setMarkerView(mv); // set the marker to the chart


        // create datasets and give them a type
        LineDataSet set1 = new LineDataSet(yVals, "Wind");
        LineDataSet set2=new LineDataSet(indicatorCircle, ""); //Circle indicator

        set1.setLineWidth(2f); //Line width
        set1.setDrawValues(false); //This eliminate the value at each point
        set1.setDrawCircles(false); //This eliminate the circle
        set1.setColor(Color.WHITE);
        //set1.setDrawFilled(true);

        //Customize the circular indicator
        set2.setDrawValues(false);
        set2.setDrawCircles(false);
        set2.setCircleSize(4f);
        set2.setDrawCircleHole(false);
        set2.setCircleColor(Color.WHITE);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set and plot the data
        mChart.setData(data);

        // don't forget to refresh the drawing
        mChart.invalidate();

    }


    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {

        //Reference
        //http://stackoverflow.com/questions/30011615/mpandroidchart-drawing-a-circle-in-the-last-entry-only
        //http://stackoverflow.com/questions/30223591/how-to-setdrawcircles-for-only-one-maximum-value-in-the-chart

        //Allows you to redraw only one circle indicator at a time on the graph
        //Get the datasets
        ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
                .getDataSets();

        //Get second set which is the circle indicator
        LineDataSet set= sets.get(1);
        //Clear the set, add a new one, and display it
        set.clear();
        set.addEntry(entry);
        set.setDrawCircles(true);
        mChart.invalidate();

    }

    @Override
    public void onNothingSelected() {

    }

}
