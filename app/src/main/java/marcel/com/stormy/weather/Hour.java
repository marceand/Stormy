package marcel.com.stormy.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Marcel on 6/19/2015.
 */
public class Hour implements Parcelable{

    private long mTime;
    private String mSummary;
    private double mTemperature;
    private String mIcon;
    private String mTimezone;
    private double mHumidity;
    private double mPrecipChance;
    private double mWindSpeed;

    public int getWindSpeed() {
        return (int)Math.round(mWindSpeed);
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }


    public int getHumidity() {
        double humidityPercentage= mHumidity*100;
        return (int)Math.round(humidityPercentage);
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }
    public int getPrecipChance() {

        double precipPercentage=mPrecipChance*100;
        return (int)Math.round(precipPercentage);
    }

    public Hour(){}

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperature() {
        return (int)Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getIconId(){

        return Forecast.getIconId(mIcon);
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getHour(){

        SimpleDateFormat formatter=new SimpleDateFormat("h a");
        Date date=new Date(mTime*1000);
        return formatter.format(date);
    }

    public String getDayOfTheWeek(){
        //Check the link: http://developer.android.com/reference/java/text/SimpleDateFormat.html
        SimpleDateFormat formatter=new SimpleDateFormat("EEEE");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimezone));
        Date dateTime=new Date(mTime*1000);
        return formatter.format(dateTime).substring(0,3);
    }

    @Override
    public int describeContents() {
        return 0; //ignore
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        //Write the information for this class to the destination (dest parameter)
        dest.writeLong(mTime);
        dest.writeDouble(mTemperature);
        dest.writeString(mSummary);
        dest.writeString(mIcon);
        dest.writeString(mTimezone);
        dest.writeDouble(mHumidity);
        dest.writeDouble(mPrecipChance);
        dest.writeDouble(mWindSpeed);
    }

    //write the private constructor that we will use to read in
    //from a parcelable.
    private Hour(Parcel in){

        //mTemperature=in.readLong();
        mTime=in.readLong();
        mTemperature=in.readDouble();
        mSummary=in.readString();
        mIcon=in.readString();
        mTimezone=in.readString();
        mHumidity=in.readDouble();
        mPrecipChance=in.readDouble();
        mWindSpeed=in.readDouble();

    }

    public static final Creator<Hour> CREATOR=new Creator<Hour>() {
        @Override
        public Hour createFromParcel(Parcel source) {
            return new Hour(source); //call that private Hour constructor
        }

        @Override
        public Hour[] newArray(int size) {
            return new Hour[size];
        }
    };

}
