package model.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.constant.Constants;
import model.vo.CastVO;
import model.vo.CriticVO;
import model.vo.MovieVO;
import model.vo.RatingVO;
import model.vo.ShowtimeVO;


public class GrabData implements Constants {

    private String TAG = this.getClass().getSimpleName();

   // private TheaterVO theaterVO = new TheaterVO();
    private MovieVO movieVO = new MovieVO();
    private ShowtimeVO showtimeVO = new ShowtimeVO();
    private RatingVO ratingVO = new RatingVO();
    private CriticVO criticVO = new CriticVO();
    private CastVO castVO = new CastVO();

    /**
     * get movie information from json array and populate in MovieVO object. Place all MovieVOs in map for that theater
     *
     * @param entireMovieArray json array of movie information
     * @return movieMap
     */
    public  Map<String, MovieVO> populateMovieInfo(JSONArray entireMovieArray) {

        Log.d(TAG, "In populateMovieInfo");

        Map<String, MovieVO> movieMap = new HashMap<>();
        String voType = MOVIE_VO_TYPE;

        if(entireMovieArray == null) {
            Log.d(TAG, "Movie array is null");
            return null;
        }

        for (int movieCount = 0; movieCount < entireMovieArray.length(); movieCount++) {
            try {
                movieVO = new MovieVO(); //reinitialize movieVO so data gets cleared for old theater
                // go through array of all movies for a specific theater
                if(entireMovieArray.get(movieCount) instanceof  JSONObject) {
                    // this map contains info for one movie
                    Map<String, Object> oneMovieMap = toMap(voType, entireMovieArray.getJSONObject(movieCount)); //each jsonObject: one movie

                        String showtimeVOType = SHOWTIME_VO_TYPE;
                        // go through map of one movie to see if there are more objects to loop through
                        for (Map.Entry<String, Object> entry : oneMovieMap.entrySet()) {

                            if (entry.getValue() instanceof JSONArray) {

                                JSONArray showTimeJsonArray = (JSONArray) entry.getValue();
                                // go through list of show times for each movie
                                for (int showtimeCount = 0; showtimeCount < showTimeJsonArray.length(); showtimeCount++) {
                                    showtimeVO = new ShowtimeVO();
                                    if (showTimeJsonArray.get(showtimeCount) instanceof JSONObject) {
                                        Map<String, Object> showtimeMap = toMap(showtimeVOType, showTimeJsonArray.getJSONObject(showtimeCount));
                                        setValueInVO(showtimeVOType, showtimeMap);
                                    }
                                    // add showtime vo to the list
                                    if (showtimeVO.getCode() != null) {
                                        movieVO.setCode(showtimeVO.getCode());
                                    }
                                    if (showtimeVO.getName() != null) {
                                        if(movieVO.getShowtimeVOMap() == null) {
                                           //showtime map is null for movie, create it
                                            HashMap<String, List<ShowtimeVO>> showtimeVOMap = new HashMap<>();
                                            movieVO.setShowtimeVOMap(showtimeVOMap);
                                        }
                                        if(movieVO.getShowtimeVOMap() != null && movieVO.getShowtimeVOMap().get(showtimeVO.getName()) != null) {
                                            //showtime map has list of showtimes for that theater already
                                            movieVO.getShowtimeVOMap().get(showtimeVO.getName()).add(showtimeVO);
                                        }
                                        else if(movieVO.getShowtimeVOMap() != null) {
                                            //showtime map has no showtimes for that theater
                                            List <ShowtimeVO> showtimeList = new ArrayList<>();
                                            showtimeList.add(showtimeVO);
                                            movieVO.getShowtimeVOMap().put(showtimeVO.getName(), showtimeList);
                                        }
                                    }
                                }
                            }

                        // result: movieVO is now a combination of all the hash map values that came from recursive function
                        setValueInVO(voType, oneMovieMap);
                        //movieVO.setShowtimeVOList(showTimeList);

                        String movieTitle = movieVO.getTitle();
                        if (movieTitle != null) {
                            movieMap.put(movieTitle, movieVO);
                        } else {
                            Log.d(TAG, "Cannot set in Map");
                        }
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, "Could not get JSON Object! " + e.getMessage());
            }
        }

        StaticDataHolder.movieCache.put(StaticDataHolder.postalCode, movieMap); // set movie map in cache
        Log.d(TAG, "Leaving populateMovieInfo");
        return movieMap;

    }

    /**
     * get rating information from json object and populate in RatingVO object. Place all RatingVOs in map for that movie
     *
     * @param jsonObject (result from url)
     */
    public  Map<String, RatingVO> populateRatingInfo(JSONObject jsonObject) {

        Log.d(TAG, "In populateRatingInfo");
        String ratingVOType = RATING_VO_TYPE;
        Map <String, RatingVO> ratingMap = new HashMap<>();

        if(jsonObject == null) {
            return null;
        }

        try {

            Map<String, Object> tempMap = toMap(ratingVOType, jsonObject);

            for (Map.Entry<String, Object> entry : tempMap.entrySet()) {

                if (entry.getValue() instanceof JSONArray) {

                    JSONArray tomatoJsonArray = (JSONArray) entry.getValue();
                    for (int i = 0; i < tomatoJsonArray.length(); i++) {
                        ratingVO = new RatingVO(); // reinitialize ratingVO so data gets cleared for old rating
                        if(tomatoJsonArray.get(i) instanceof JSONObject) {
                            Map<String, Object> tempRatingMap = toMap(ratingVOType, tomatoJsonArray.getJSONObject(i));
                            setValueInVO(ratingVOType, tempRatingMap);
                        }
                        String title = ratingVO.getTitle();
                        if(title != null && (!ratingMap.containsKey(title))) {
                            ratingMap.put(title, ratingVO);
                            StaticDataHolder.ratingCache.put(title, ratingVO);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Could not get JSON Object! " + e.getMessage());
        }

        Log.d(TAG, "Leaving populateRatingInfo");
        return ratingMap;
    }

    /**
     * get critic information from json object and populate in criticVO object.
     *
     * @param jsonObject (result from url)
     */
    public  List<CriticVO> populateCriticInfo(JSONObject jsonObject) {

        Log.d(TAG, "In populateCriticInfo");
        String criticVOType = CRITIC_VO_TYPE;
        List <CriticVO> criticList = new ArrayList<>();

        if(jsonObject == null) {
            return null;
        }

        try {

            Map<String, Object> tempMap = toMap(criticVOType, jsonObject);

            for (Map.Entry<String, Object> entry : tempMap.entrySet()) {

                if (entry.getValue() instanceof JSONArray) {

                    JSONArray tomatoJsonArray = (JSONArray) entry.getValue();
                    for (int i = 0; i < tomatoJsonArray.length(); i++) {
                        criticVO = new CriticVO(); // reinitialize critic so data gets cleared for old rating
                        if(tomatoJsonArray.get(i) instanceof JSONObject) {
                            Map<String, Object> tempRatingMap = toMap(criticVOType, tomatoJsonArray.getJSONObject(i));
                            setValueInVO(criticVOType, tempRatingMap);
                        }

                        criticList.add(criticVO);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Could not get JSON Object! " + e.getMessage());
        }

        Log.d(TAG, "Leaving populateCriticInfo");
        return criticList;
    }

    /**
     * get cast information from json object and populate in castVO object.
     *
     * @param jsonObject (result from url)
     */
    public  List<CastVO> populateCastInfo(JSONObject jsonObject) {

        Log.d(TAG, "In populateCastInfo");
        String castVOType = CAST_VO_TYPE;
        List <CastVO> castList = new ArrayList<>();

        if(jsonObject == null) {
            return null;
        }

        try {

            Map<String, Object> tempMap = toMap(castVOType, jsonObject);

            for (Map.Entry<String, Object> entry : tempMap.entrySet()) {

                if (entry.getValue() instanceof JSONArray) {

                    JSONArray tomatoJsonArray = (JSONArray) entry.getValue();
                    for (int i = 0; i < tomatoJsonArray.length(); i++) {
                        castVO = new CastVO(); // reinitialize critic so data gets cleared for old rating
                        if(tomatoJsonArray.get(i) instanceof JSONObject) {
                            Map<String, Object> tempRatingMap = toMap(castVOType, tomatoJsonArray.getJSONObject(i));
                            setValueInVO(castVOType, tempRatingMap);
                        }

                        castList.add(castVO);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Could not get JSON Object! " + e.getMessage());
        }

        Log.d(TAG, "Leaving populateCriticInfo");
        return castList;
    }


    /**
     * @param jsonObject read contents of the json object and place it in a map. If jsonObject has a value that is a jsonObject,
     *                   recursively call this method to read those contents
     * @return hashMap end result should be combination of all hash map in jsonObject
     * @throws JSONException
     */
    private Map<String, Object> toMap(String voType, JSONObject jsonObject) throws JSONException {

        HashMap<String, Object> hashMap = new HashMap<>();

        if(jsonObject != null) {
            Iterator keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object value = jsonObject.get(key);

                if (value instanceof JSONObject) {
                    value = toMap(voType, (JSONObject) value);
                    // populate theaterVO using the value (hash map)
                    setValueInVO(voType, (Map<String, Object>) value);
                }
                hashMap.put(key, value);
            }
        }
        return hashMap;
    }

    /**
     * Find method in VO and sets the values
     *
     * @param voType tells us which vo to search method in
     * @param map the values needed to place in vo
     */

    public void setValueInVO(String voType, Map<String, Object> map) {

        // use reflection to set each value in theaterVO
        java.lang.reflect.Method method;

        try {

            for (Map.Entry<String, Object> e : map.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                String insertValue;

                String methodName = SETTER + key.substring(0, 1).toUpperCase() + key.substring(1);

                if(!(value instanceof String)) {
                    insertValue = String.valueOf(value);
                } else {
                    insertValue = (String) value;
                }

                if(voType.equals(MOVIE_VO_TYPE)) {

                    method = getMethod(MovieVO.class.getName(), methodName, String.class);
                    if (method == null) {
                        continue;
                    }
                    method.invoke(movieVO, insertValue);

                } else if (voType.equals(SHOWTIME_VO_TYPE)) {

                    method = getMethod(ShowtimeVO.class.getName(), methodName, String.class);
                    if (method == null) { continue; }
                    method.invoke(showtimeVO, insertValue);

                } else if (voType.equals(RATING_VO_TYPE)) {
                    if(key.contains("_")) {
                        int underScore = key.indexOf("_");
                        methodName = SETTER + key.substring(0, 1).toUpperCase() + key.substring(1, underScore) +
                                key.substring(underScore+1, underScore+2).toUpperCase() + key.substring(underScore+2);
                    }
                    method = getMethod(RatingVO.class.getName(), methodName, String.class);
                    if (method == null) { continue; }
                    method.invoke(ratingVO, insertValue);

                } else if (voType.equals(CRITIC_VO_TYPE)) {
                    method = getMethod(CriticVO.class.getName(), methodName, String.class);
                    if (method == null) { continue; }
                    method.invoke(criticVO, insertValue);

                } else if (voType.equals(CAST_VO_TYPE)) {
                    method = getMethod(CastVO.class.getName(), methodName, String.class);
                    if (method == null) { continue; }
                    method.invoke(castVO, insertValue);

                }

            }
         } catch (IllegalAccessException e) {
            Log.e(TAG, "Illegal Access Exception, " + e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Invocation Target Exception, " + e.getMessage());
        }
    }

    /**
     * Find method name using reflection
     *
     * @param className name of vo class
     * @param methodName what method to search for in class
     * @param param the parameter type for that method
     */
    private Method getMethod(String className, String methodName, Class<?> param) {

        java.lang.reflect.Method method = null;
        try {
            method = Class.forName(className).getMethod(methodName, param);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class Not Found Exception, " + e.getMessage());
        } catch (NoSuchMethodException e) {
            // expected in some scenarios, not all methods are in VO
        }

        return method;
    }

}
