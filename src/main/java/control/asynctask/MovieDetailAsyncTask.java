package control.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import control.asyncresponse.MovieAsyncResponse;
import control.helper.ExecuteUrl;
import model.constant.Constants;
import model.data.GrabData;
import model.vo.CastVO;
import model.vo.CriticVO;
import model.vo.RatingVO;

public class MovieDetailAsyncTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> implements Constants {

        String TAG = this.getClass().getSimpleName();

        public MovieAsyncResponse delegate = null;

        private Map<String, RatingVO> ratingsMap = new HashMap<>();

        private List<CriticVO> criticsList = new ArrayList<>();
        private List<CastVO> castList = new ArrayList<>();

        /**
         * Calls rotten tomatoes API to get information for movies that didn't have any
         */

        @SafeVarargs
        @Override
        protected final ArrayList<String> doInBackground(ArrayList<String>... arg0) {


            ExecuteUrl executeUrl = new ExecuteUrl();
            GrabData data = new GrabData();

            String operation = null;
            String movieId = null;

            if(arg0[0] != null && arg0[0].size() > 0) {
                operation = arg0[0].get(0);
                Log.d(TAG, "operation: " + operation);
            }

            if(arg0.length > 1 && arg0[1] != null && arg0[1].size() > 0) {
                movieId = arg0[1].get(0);
                Log.d(TAG, "movie id: " + movieId);
            }

            if(movieId != null) {

                assert operation != null;
                if(operation.equals(CRITIC_REVIEWS_OP)) {
                    JSONObject criticJsonObj = (JSONObject) executeUrl.callMovieAPI(operation, movieId);
                    criticsList = data.populateCriticInfo(criticJsonObj);
                }
                else if(operation.equals(FULL_CAST_OP)){
                    JSONObject castJsonObj = (JSONObject) executeUrl.callMovieAPI(operation, movieId);
                    castList = data.populateCastInfo(castJsonObj);
                }
            }

            return null;
        }

        /**
         * Async Task has finished, return to the view
         *
         * @param result from task
         */
        @Override
        protected void onPostExecute(ArrayList <String> result) {

            Log.d(TAG, "Movie Async Task Finished!");
            // send results from async task to activity
            if (delegate != null) {
                delegate.movieProcessFinished(ratingsMap, criticsList, castList);
            } else {
                Log.d(TAG, "No Data Returned!");
            }
        }
    }


