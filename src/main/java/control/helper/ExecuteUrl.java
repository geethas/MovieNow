package control.helper;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import model.constant.Constants;

public class ExecuteUrl implements Constants {

    String TAG = this.getClass().getSimpleName();

    /**
     * Construct url based on operation
     *
     * @param params specific fields needed for url
     * @param operation type of url
     * @return url
     */
    protected String getUrl(String params, String operation) {

        String url = null;
        // get url based on operation
        if (THEATER_SHOWING_OP.equals(operation)) {
            // ex of url: http://data.tmsapi.com/v1.1/movies/showings?startDate=2015-07-11&zip=78701&radius=15&api_key=4rda8nw3z793asgkzwx7xz6q
            url = String.format("%s/v%s/%s?%s&%s=%s", TMS_BASE_URL, TMS_API_VERSION, operation, params, TMS_API_KEY, TMS_API_KEY_VAL);
        }  else if (TOMATOES_ALL_MOVIES_OP.equals(operation) && params != null) {
            // ex of url: http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=vhw2a426waqthh33cvxk3y4b&page_limit=1
            url = String.format("%s/v%s/%s?%s=%s&%s", TOMATO_BASE_URL, TOMATO_API_VERSION, operation, TOMATO_API_KEY, TOMATO_API_KEY_VAL, params);
        } else if (TOMATOES_ALL_MOVIES_OP.equals(operation)) {
            // ex of url: http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=vhw2a426waqthh33cvxk3y4b
            url = String.format("%s/v%s/%s?%s=%s", TOMATO_BASE_URL, TOMATO_API_VERSION, operation, TOMATO_API_KEY, TOMATO_API_KEY_VAL);
        } else if (TOMATOES_ONE_MOVIE_OP.equals(operation)) {
            // ex of url: http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=vhw2a426waqthh33cvxk3y4b&q=[NAME OF MOVIE}
            url = String.format("%s/v%s/%s%s?%s=%s&%s", TOMATO_BASE_URL, TOMATO_API_VERSION, operation, JSON_TYPE, TOMATO_API_KEY, TOMATO_API_KEY_VAL, params);
        } else if (CRITIC_REVIEWS_OP.equals(operation)) {
            //http://api.rottentomatoes.com/api/public/v1.0/movies/770672122/reviews.json?apikey=vhw2a426waqthh33cvxk3y4b
            url = String.format("%s/v%s/%s/%s%s?%s=%s", TOMATO_BASE_URL, TOMATO_API_VERSION, params, operation, JSON_TYPE, TOMATO_API_KEY, TOMATO_API_KEY_VAL);
        } else if (FULL_CAST_OP.equals(operation)) {
            //http://api.rottentomatoes.com/api/public/v1.0/movies/770 672122/cast.json?apikey=vhw2a426waqthh33cvxk3y4b
            url = String.format("%s/v%s/%s/%s%s?%s=%s", TOMATO_BASE_URL, TOMATO_API_VERSION, params, operation, JSON_TYPE, TOMATO_API_KEY, TOMATO_API_KEY_VAL);
        }

        Log.d(TAG, "Operation: " + operation);
        Log.d(TAG, "URL: " + url);
        return url;
    }

    /**
     * Execute url and return data
     *
     * @param params specific fields needed for url
     * @param operation type of url that is going to get executed
     * @return response from url
     */
    public Object executeUrl(String params, String operation) {

        String url = getUrl(params, operation);

        try {
            Log.d(TAG, "Executing URL: " + url);
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGetUrl = new HttpGet(url);
            HttpParams urlParams = httpGetUrl.getParams();
            HttpClientParams.setRedirecting(urlParams, false);
            HttpResponse response = httpclient.execute(httpGetUrl);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            String result = out.toString();
            Log.d(TAG, "result: " + result);
            // build a JSON Array using result from URL
            if (operation.equals(TOMATOES_ALL_MOVIES_OP) || operation.equals(TOMATOES_ONE_MOVIE_OP)
                    || operation.equals(CRITIC_REVIEWS_OP) || operation.equals(FULL_CAST_OP)) {
                return new JSONObject(result);
            } else {
                return new JSONArray(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "HTTP exception: " + e.getMessage());
        }

        return null;
    }

    /**
     * prepare parameters to call onConnect API and prepares the data from the API to send back to the view
     *
     * @param operation  tells you what api to execute
     * @param postalCode current location
     * @param count number of movies
     * @param radius theaters to get within this distance
     * @param selectedDate date user wants the movie information on
     *
     */
    public Object callTheaterAPI(String operation, String postalCode, String count, String movieName, String radius, String selectedDate) {

        Log.d(TAG, "Call OnConnect API");
        Log.d(TAG, "Operation: " + operation);

        String params = null;
        if(operation.equals(THEATER_SHOWING_OP)){

           // http://data.tmsapi.com/v1.1/movies/showings?startDate=2015-07-11&zip=78701&radius=5&api_key=4rda8nw3z793asgkzwx7xz6q

            // prepare parameters for tms api
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String startDate;

            if(selectedDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH, Integer.parseInt(selectedDate.substring(0, selectedDate.indexOf("-")))-1);
                cal.set(Calendar.DATE, Integer.parseInt(selectedDate.substring(selectedDate.indexOf("-")+1, selectedDate.length())));
                startDate = dateFormat.format(cal.getTime());
            }
            else {
                Calendar cal = Calendar.getInstance();
                startDate = dateFormat.format(cal.getTime());
            }

            if(radius == null){
                radius = DEFAULT_RADIUS;
            }

            Log.d(TAG, "Date used for Movies in Theaters URL: " + startDate);
            params = String.format("startDate=%s&zip=%s&radius=%s", startDate, postalCode, radius);

        } else if (operation.equals(TOMATOES_ALL_MOVIES_OP)) {
            // count is amount of movies that will be returned back. count is movie map size
            if(count != null)
                params = String.format("page_limit=%s", count);

        } else if (operation.equals(TOMATOES_ONE_MOVIE_OP)) {
            // get information for one movie
            Log.d(TAG, movieName);
            if(movieName != null)
                params = String.format("q=%s", movieName);
            else
                return null;
        }

        // execute onConnect API
        Object jsonObj = executeUrl(params, operation);

        // if nothing is returned from rotten tomatoes API, then execute again without params
        if(jsonObj == null){
            jsonObj = executeUrl(null, operation);
        }

        Log.d(TAG, "Got JSON");

        return jsonObj;
    }

    public Object callMovieAPI(String operation, String movieId) {

        Log.d(TAG, "Call Rotten Tomato API");
        Log.d(TAG, "Operation: " + operation);

        String params = null;

        if (movieId != null)
            params = String.format("movies/%s", movieId);

        if(params != null) {
            // execute onConnect API
            Object jsonObj = executeUrl(params, operation);

            // if nothing is returned from rotten tomatoes API, then execute again without params
            if (jsonObj == null) {
                jsonObj = executeUrl(null, operation);
            }

            Log.d(TAG, "Got JSON Object");
            return jsonObj;
        }

        return null;
    }
}
