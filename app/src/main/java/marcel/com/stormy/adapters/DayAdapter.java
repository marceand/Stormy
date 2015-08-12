package marcel.com.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import marcel.com.stormy.R;
import marcel.com.stormy.weather.Day;

/**
 * Created by Marcel on 6/22/2015.
 */
public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
        //we aren't going to use this. Tag items for easy reference
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView==null){

            convertView= LayoutInflater.from(mContext).inflate(R.layout.daily_list_item,null);
            holder=new ViewHolder();
            holder.iconImageView=(ImageView)convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel=(TextView)convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel=(TextView)convertView.findViewById(R.id.dayNameLabel);

            convertView.setTag(holder); //use the holder as a tag

        }
        else{

            holder=(ViewHolder)convertView.getTag();

        }

        //Set the data for our list
        Day day=mDays[position];

        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax()+"");

        //Change Today name to "Today"
        if(position==0){

            holder.dayLabel.setText("Today");

        }else {
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }
        return convertView;
    }

    //Helper class, best practice in Android
    private static class  ViewHolder{
        //public
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;

    }
}
