package view.helper;

import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gsrini.movienow.view.R;
import model.constant.Constants;

/**
 * Created by geethasrini on 5/25/15.
 */
public class CalendarDataHelper implements Constants {


    public static class Holder {

        MenuItem day1;
        MenuItem day2;
        MenuItem day3;
        MenuItem day4;
        MenuItem day5;
        MenuItem day6;
        MenuItem day7;
    }

    Holder days;

    // save the day user wants to view movies from
    public static String saveDay = null;

    /**
     * set the calendar day as checked or unchecked based on user input
     *
     * this gets called every time a click happens in a calendar
     */
    public void checkCalendarDays(MenuItem item){

        // set the old item with the un-checkbox item
        if(days.day1.isChecked()){
            days.day1.setIcon(R.drawable.checkbox_unchecked);
            days.day1.setChecked(false);
        }
        if(days.day2.isChecked()){
            days.day2.setIcon(R.drawable.checkbox_unchecked);
            days.day2.setChecked(false);
        }
        if(days.day3.isChecked()){
            days.day3.setIcon(R.drawable.checkbox_unchecked);
            days.day3.setChecked(false);
        }
        if(days.day4.isChecked()){
            days.day4.setIcon(R.drawable.checkbox_unchecked);
            days.day4.setChecked(false);
        }
        if(days.day5.isChecked()){
            days.day5.setIcon(R.drawable.checkbox_unchecked);
            days.day5.setChecked(false);
        }
        if(days.day6.isChecked()){
            days.day6.setIcon(R.drawable.checkbox_unchecked);
            days.day6.setChecked(false);
        }
        if(days.day7.isChecked()){
            days.day7.setIcon(R.drawable.checkbox_unchecked);
            days.day7.setChecked(false);
        }

        // set the new clicked item with the checkbox item
        if(R.id.day1 == item.getItemId()) {
            days.day1.setIcon(R.drawable.checkbox_checked);
            days.day1.setChecked(true);
            saveDay = "1";
        }
        else if(R.id.day2 == item.getItemId()) {
            days.day2.setIcon(R.drawable.checkbox_checked);
            days.day2.setChecked(true);
            saveDay = "2";
        }
        else if(R.id.day3 == item.getItemId()) {
            days.day3.setIcon(R.drawable.checkbox_checked);
            days.day3.setChecked(true);
            saveDay = "3";
        }
        else if(R.id.day4 == item.getItemId()) {
            days.day4.setIcon(R.drawable.checkbox_checked);
            days.day4.setChecked(true);
            saveDay = "4";
        }
        else if(R.id.day5 == item.getItemId()) {
            days.day5.setIcon(R.drawable.checkbox_checked);
            days.day5.setChecked(true);
            saveDay = "5";
        }
        else if(R.id.day6 == item.getItemId()) {
            days.day5.setIcon(R.drawable.checkbox_checked);
            days.day6.setChecked(true);
            saveDay = "6";
        }
        else if(R.id.day7 == item.getItemId()) {
            days.day7.setIcon(R.drawable.checkbox_checked);
            days.day7.setChecked(true);
            saveDay = "7";
        }
    }
    /**
     * sets calendar dates for popup menu when calendar button is clicked using today as a starting point
     *
     * this gets called only when calendar is first created
     */
    public void setCalendarDates(Menu popupMenu) {

        days = new Holder();
        days.day1 = popupMenu.findItem(R.id.day1);
        days.day2 = popupMenu.findItem(R.id.day2);
        days.day3 = popupMenu.findItem(R.id.day3);
        days.day4 = popupMenu.findItem(R.id.day4);
        days.day5 = popupMenu.findItem(R.id.day5);
        days.day6 = popupMenu.findItem(R.id.day6);
        days.day7 = popupMenu.findItem(R.id.day7);

        // use the previous state of day and set box as checked
        // even though this gets called once when activity is started,
        // for every movie we want to keep the day consistent
        if(saveDay == null || saveDay.equals("1")) {
            days.day1.setIcon(R.drawable.checkbox_checked);
            days.day1.setChecked(true);
        }
        else if(saveDay != null && saveDay.equals("2")) {
            days.day2.setIcon(R.drawable.checkbox_checked);
            days.day2.setChecked(true);
        }
        else if(saveDay != null && saveDay.equals("3")) {
            days.day3.setIcon(R.drawable.checkbox_checked);
            days.day3.setChecked(true);
        }
        else if(saveDay != null && saveDay.equals("4")) {
            days.day4.setIcon(R.drawable.checkbox_checked);
            days.day4.setChecked(true);
        }
        else if(saveDay != null && saveDay.equals("5")) {
            days.day5.setIcon(R.drawable.checkbox_checked);
            days.day5.setChecked(true);
        }
        else if(saveDay != null && saveDay.equals("6")) {
            days.day6.setIcon(R.drawable.checkbox_checked);
            days.day6.setChecked(true);
        }
        else if(saveDay != null && saveDay.equals("7")) {
            days.day7.setIcon(R.drawable.checkbox_checked);
            days.day7.setChecked(true);
        }

        // set proper dates during the week
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);

        String [] daysOfWeek = getDaysOfWeek(cal);

        setDays(daysOfWeek, cal);
    }

    public String[] getDaysOfWeek(Calendar cal){

        String day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String [] daysOfWeek = null;

        // check for today's day
        if (day.equalsIgnoreCase("Monday")) {

            daysOfWeek = new String[] {TODAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};

        } else if (day.equalsIgnoreCase("Tuesday")) {

            daysOfWeek = new String[] {TODAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY};

        } else if (day.equalsIgnoreCase("Wednesday")) {

            daysOfWeek = new String[] {TODAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY};

        } else if (day.equalsIgnoreCase("Thursday")) {

            daysOfWeek = new String[] {TODAY, FRIDAY, SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY};

        } else if (day.equalsIgnoreCase("Friday")) {

            daysOfWeek = new String[] {TODAY, SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY};

        } else if (day.equalsIgnoreCase("Saturday")) {

            daysOfWeek = new String[] {TODAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY};

        } else if (day.equalsIgnoreCase("Sunday")) {

            daysOfWeek = new String[] {TODAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};
        }

        return daysOfWeek;
    }

    /**
     * Set the proper day and date for calendar button to keep track of what day the user wants
     * @param daysOfWeek the order of the days of week
     * @param cal current date
     */
    public void setDays(String [] daysOfWeek, Calendar cal){

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        String dateStr;

        if(days != null && daysOfWeek != null) {
            dateStr = dateFormat.format(cal.getTime());
            days.day1.setTitle(daysOfWeek[0] + ", " + dateStr);

            // Just add a day from today's date
            cal.add(Calendar.DATE, 1);
            dateStr = dateFormat.format(cal.getTime());
            days.day2.setTitle(daysOfWeek[1] + ", " + dateStr);

            cal.add(Calendar.DATE, 1);
            dateStr = dateFormat.format(cal.getTime());
            days.day3.setTitle(daysOfWeek[2] + ", " + dateStr);

            cal.add(Calendar.DATE, 1);
            dateStr = dateFormat.format(cal.getTime());
            days.day4.setTitle(daysOfWeek[3]  + ", " + dateStr);

            cal.add(Calendar.DATE, 1);
            dateStr = dateFormat.format(cal.getTime());
            days.day5.setTitle(daysOfWeek[4] + ", " + dateStr);

            cal.add(Calendar.DATE, 1);
            dateStr = dateFormat.format(cal.getTime());
            days.day6.setTitle(daysOfWeek[5] + ", " + dateStr);

            cal.add(Calendar.DATE, 1);
            dateStr = dateFormat.format(cal.getTime());
            days.day7.setTitle(daysOfWeek[6] + ", " + dateStr);
        }

    }
}
