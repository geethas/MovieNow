package view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import control.asyncresponse.MovieAsyncResponse;
import control.asynctask.MovieDetailAsyncTask;
import gsrini.movienow.view.R;
import model.constant.Constants;
import model.vo.CastVO;
import model.vo.CriticVO;
import model.vo.ListVO;
import model.vo.RatingVO;
import view.adapter.ListAdapter;

public class MoreInfoView extends ActionBarActivity implements Constants, MovieAsyncResponse {

    public MovieDetailAsyncTask asyncTask;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id. custom_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the listview
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mRecyclerView.setLayoutParams(mParam);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // get critic review or cast list
        if(getIntent().getExtras() != null) {
            String movieId = (String) getIntent().getExtras().get("movieId");
            String operation = (String) getIntent().getExtras().get("operation");
            String title = (String) getIntent().getExtras().get("title");

            if(operation.equals(CRITIC_REVIEWS_OP))
                getSupportActionBar().setTitle("Critic Reviews for " + title);
            else if(operation.equals(FULL_CAST_OP))
                getSupportActionBar().setTitle("Full Cast for " + title);

            asyncTask = new MovieDetailAsyncTask();
            asyncTask.delegate = MoreInfoView.this;

            ArrayList<String> movieIdList = new ArrayList<>();
            movieIdList.add(movieId);

            ArrayList<String> operationList = new ArrayList<>();
            operationList.add(operation);

            asyncTask.execute(operationList, movieIdList);
        }
    }

    @Override
    public void movieProcessFinished(Map<String, RatingVO> ratingsMap, List<CriticVO> criticsList, List<CastVO> castList) {

        if(criticsList != null && !criticsList.isEmpty()) {
            Log.d(TAG, "Got Critics Reviews");
            List<ListVO> criticReviews = new ArrayList<>();

            if (criticsList.size() > 0) {
                for (CriticVO criticVO : criticsList) {
                    Log.d(TAG, "critic: " + criticVO.getCritic());
                    ListVO listVO = new ListVO(criticVO.getFreshness(), criticVO.getCritic(), criticVO.getQuote());
                    criticReviews.add(listVO);
                }
            }

            listAdapter = new ListAdapter(this, criticReviews);
            // setting list adapter
            mRecyclerView.setAdapter(listAdapter);

        }

        else if(castList != null && !castList.isEmpty()) {
            Log.d(TAG, "Got Full Cast List");
            List<ListVO> fullCastList = new ArrayList<>();

            if (castList.size() > 0) {
                for (CastVO castVO : castList) {
                    Log.d(TAG, "cast: " + castVO.getName());

                    // properly format characters
                    String characters = castVO.getCharacters();
                    Log.d(TAG, "Old Character List: " + characters);

                    String formattedCharacters = "";

                    if(characters != null && characters.length() > 2) {
                        for (int i=2; i < characters.length()-2; i++) {

                            char c = characters.charAt(i);

                            if (Character.isLetter(c) || Character.isDigit(c)) {
                                formattedCharacters += c;
                            }
                            else if (' ' == c) {
                                formattedCharacters += c;
                            }
                            else if('.' == c || '\'' == c) {
                                formattedCharacters += characters.charAt(i);
                            }
                            else {
                                formattedCharacters += "  ";
                            }
                        }
                    }

                    Log.d(TAG, "New Character List: " + formattedCharacters);

                    if(formattedCharacters.isEmpty()){
                        continue;
                    }

                    ListVO listVO = new ListVO(null,  castVO.getName(), formattedCharacters);
                    fullCastList.add(listVO);
                }
            }

            listAdapter = new ListAdapter(this, fullCastList);
            // setting list adapter
            mRecyclerView.setAdapter(listAdapter);

        }
    }
}
