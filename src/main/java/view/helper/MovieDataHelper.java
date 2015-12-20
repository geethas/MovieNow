package view.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import model.vo.RatingVO;
import model.vo.ShowtimeVO;

/**
 * Created by geethasrini on 4/17/15.
 */
public class MovieDataHelper {

    String TAG = this.getClass().getSimpleName();

    public String getCast(String topCast, boolean fullCast) {

        String cast = "";
        String delims = "[\",]";

        Log.d(TAG, "Cast (before format): " + topCast);

        if (topCast != null) {
            StringTokenizer st = new StringTokenizer(topCast, delims);
            // concatenate all cast names
            if (fullCast) {
                while (st.hasMoreTokens()) {
                    cast += st.nextToken();
                    cast += ", ";
                }
                // remove last semi colon
                cast = cast.substring(0, cast.length() - 2);
            }
            // only concatenate first 2 cast names
            else {
                int firstToken = 0;
                while (st.hasMoreTokens()) {
                    if (firstToken == 1)
                        cast += ", ";
                    else if (firstToken == 2)
                        // already got 2 cast names, don't need any more
                        break;

                    cast += st.nextToken();
                    firstToken++;
                }
            }
        }

        return cast;
    }

    public List<String> getCast(String topCast) {

        List<String> cast = new ArrayList<>();
        String delims = "[\",]";

        if (topCast != null) {
            StringTokenizer st = new StringTokenizer(topCast, delims);
            while (st.hasMoreTokens()) {
                cast.add(st.nextToken());
            }
        }

        return cast;
    }

    public List<String> getDirectors(String directors) {

        List<String> directorList = new ArrayList<>();
        String delims = "[\",]";

        Log.d(TAG, "Director (before format): " + directors);

        if (directors != null) {
            StringTokenizer st = new StringTokenizer(directors, delims);
            while (st.hasMoreTokens()) {
                directorList.add(st.nextToken());
            }
        }

        return directorList;
    }


    public String getGenre(String genre) {

        String movieGenre = "";
        String delims = "[\",]";

        Log.d(TAG, "Before genre is formatted: " + genre);
        if(genre != null) {
            StringTokenizer st = new StringTokenizer(genre, delims);
            // concatenate all genre types
            while (st.hasMoreTokens()) {
                movieGenre += st.nextToken();
                movieGenre += ", ";
            }
            // remove last semi colon
            movieGenre = movieGenre.substring(0, movieGenre.length() - 2);
            Log.d(TAG, "After genre is formatted: " + movieGenre);
        }
        return movieGenre;
    }

    public String getMovieType (String filmRating, String unformattedRunTime){
        // unformattedRunTime format: PT15H30M
        String movieType;
        String runtime = "";
        Log.d(TAG, "run time: (before format): " + unformattedRunTime);
        Log.d(TAG, "film rating: " + filmRating);

        if(unformattedRunTime != null) {
            String hour = unformattedRunTime.substring(unformattedRunTime.indexOf("T") + 1, unformattedRunTime.indexOf("H"));
            String min = unformattedRunTime.substring(unformattedRunTime.indexOf("H") + 1, unformattedRunTime.indexOf("M"));
            // hour format: 05, remove leading 0
            if (hour.contains("0")) {
                hour = hour.substring(1);
            }
             // add hour and min for legibility
             if(min.equals("00"))
                runtime = hour + " hr ";
             else
                 runtime = hour + " hr " + min + " min ";
        }

        if(filmRating != null)
            movieType = filmRating + ", " + runtime;
        else
            movieType = runtime;

        return movieType;
    }

    public String getCriticScore(String title, Map<String, RatingVO> ratingsMap){

        String halfTitle = null;
        String criticScore = null;
        // in case ratings map only has second half of title
        // ex: The Divergent Series: Insurgent, ratings map only has Insurgent
        if(title != null && ratingsMap != null) {

            if (title.contains(":")) {
                halfTitle = title.substring(title.indexOf(":") + 1).trim();
            }

            if (halfTitle != null && ratingsMap.containsKey(halfTitle) && ratingsMap.get(halfTitle) != null) {
                criticScore = ratingsMap.get(halfTitle).getCriticsScore();
            } else if (ratingsMap.containsKey(title) && ratingsMap.get(title) != null) {
                criticScore = ratingsMap.get(title).getCriticsScore();
            }
        }

        return criticScore;
    }

    public String getCriticRating(String title, Map<String, RatingVO> ratingsMap){

        String halfTitle = null;
        String criticScore = null;
        // in case ratings map only has second half of title
        // ex: The Divergent Series: Insurgent, ratings map only has Insurgent
        if(title != null && ratingsMap != null) {

            if (title.contains(":")) {
                halfTitle = title.substring(title.indexOf(":") + 1).trim();
            }

            if (halfTitle != null && ratingsMap.containsKey(halfTitle) && ratingsMap.get(halfTitle) != null) {
                criticScore = ratingsMap.get(halfTitle).getCriticsRating();
            } else if (ratingsMap.containsKey(title) && ratingsMap.get(title) != null) {
                criticScore = ratingsMap.get(title).getCriticsRating();
            }
        }

        return criticScore;
    }

    public String getAudienceScore(String title, Map<String, RatingVO> ratingsMap){

        String halfTitle = null;
        String audienceScore = null;
        // in case ratings map only has second half of title
        // ex: The Divergent Series: Insurgent, ratings map only has Insurgent
        if(title != null && ratingsMap != null) {

            if (title.contains(":")) {
                halfTitle = title.substring(title.indexOf(":") + 1).trim();
            }
            if (halfTitle != null && ratingsMap.containsKey(halfTitle) && ratingsMap.get(halfTitle) != null) {
                audienceScore = ratingsMap.get(halfTitle).getAudienceScore();
            } else if (ratingsMap.containsKey(title) && ratingsMap.get(title) != null) {
                audienceScore = ratingsMap.get(title).getAudienceScore();
            }
        }

        return audienceScore;

    }

    public List<String> getShowtimeList (List<ShowtimeVO> showtimeVOList, boolean is3D, boolean isImax){

        // time values in show time list is in order. Get first 2 show times greater than current time
        SimpleDateFormat dateFormat24Hr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date currDate = get24hrDateFormat(new Date());
        List<String> showtimeList = new ArrayList<>();
        if (currDate != null && (showtimeVOList != null && showtimeVOList.size() > 0)) {
            for (ShowtimeVO showtimeVO : showtimeVOList) {
                try {
                    Date showTimeDate = dateFormat24Hr.parse(showtimeVO.getDateTime());
                    // check if the show time is after the current time
                    if (showTimeDate.after(currDate)) {
                        Log.d(TAG, "Showtime: " + showTimeDate);
                        SimpleDateFormat timeFormat12Hr = new SimpleDateFormat("hh:mm a");
                        String showtime = timeFormat12Hr.format(showTimeDate);

                        if (showtime.charAt(0) == '0') {
                            showtime = showtime.substring(1);
                        }

                        if(is3D){
                            showtimeList.add(showtime + " [3D]");
                        }
                        else if(isImax){
                            showtimeList.add(showtime + " [IMAX 3D]");
                        }
                        else {
                            showtimeList.add(showtime);
                        }
                    }
                 } catch (ParseException e) {
                    Log.d(TAG, "Parsing exception for showtime date: " + e.getMessage());
                }
            }
        }
        return showtimeList;
    }

    public Date get24hrDateFormat(Date date){
        SimpleDateFormat dateFormat24Hr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date newDate = null;
        try {
            newDate = dateFormat24Hr.parse(dateFormat24Hr.format(date));
        } catch (ParseException e) {
            Log.d(TAG, "Parsing exception for current date: " + e.getMessage());
        }

        return newDate;
    }
}
