package src.solrUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.stereotype.Component;
 
/**
* @author shizifan
* @date 16/9/9
*/
@Component
public class TimeUtil {
    public  String formatUnixtime(long unixSeconds){
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
 
    public  long timeConversion(String time)
    {
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dfm.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//Specify your timezone
        long unixtime = 0;
        try
        {
            unixtime = dfm.parse(time).getTime();
            unixtime=unixtime/1000;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return unixtime;
    }
 
    public  String formatUnixtime2(long unixSeconds){
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
   
    public  long timeConversion2(String time)
    {
        DateFormat dfm = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.US);
        dfm.setTimeZone(TimeZone.getTimeZone("GMT+8"));//Specify your timezone
        long unixtime = 0;
        try
        {
            unixtime = dfm.parse(time).getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return unixtime;
    }
}
 