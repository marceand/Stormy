package marcel.com.stormy.customChart;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Marcel on 7/23/2015.
 */
public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;
    String mUnits;
    public MyValueFormatter(String units) {
        mFormat = new DecimalFormat("###"); // turn float into integer, 90.0=>90
        mUnits=units;
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + mUnits; // append any unit-sign
        //return mFormat.format(value) + "\u00B0"; // append a degree-sign
    }
}
