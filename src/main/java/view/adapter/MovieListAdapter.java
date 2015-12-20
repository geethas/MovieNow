package view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gsrini.movienow.view.R;
import model.vo.MovieListVO;
import view.activity.MovieDetailView;
import view.helper.CalendarDataHelper;

/**
 * Created by geethasrini on 4/7/15.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    // creates an instance of a row and saves all necessary info for it
    private final Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView audienceRating;
        TextView criticRating;
        TextView typeAndRunTime;
        TextView cast;
        //TextView copyright;

        //ImageView image;
        //ImageView audienceImage;
        //ImageView criticImage;

        public ViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.title);
            this.audienceRating = (TextView) view.findViewById(R.id.audience_rating);
            this.criticRating = (TextView) view.findViewById(R.id.critic_rating);
            this.typeAndRunTime = (TextView) view.findViewById(R.id.typeAndRunTime);
            this.cast = (TextView) view.findViewById(R.id.cast);
            //this.copyright = (TextView) view.findViewById(R.id.copyright);

           // this.image = (ImageView) view.findViewById(R.id.thumbnail);
            //this.audienceImage = (ImageView) view.findViewById(R.id.audience_image);
            //this.criticImage = (ImageView) view.findViewById(R.id.critic_image);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnMovieClicked(title);
        }

        private void OnMovieClicked(TextView title) {

            Intent intent = new Intent(context, MovieDetailView.class);
            intent.putExtra("title", title.getText());

            Calendar cal = Calendar.getInstance();
            Date date = new Date();
            cal.setTime(date);

            CalendarDataHelper calendarData = new CalendarDataHelper();

            if(calendarData.saveDay == null){
                calendarData.saveDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            }

            intent.putExtra("day", CalendarDataHelper.saveDay);
            context.startActivity(intent);
        }

    }

    private List<MovieListVO> movieListVOList = Collections.emptyList();
    MovieListVO movieListVO;

    // the context is needed to inflate views in getView()
    public MovieListAdapter(Context context, List<MovieListVO> movieListVOList) {
        this.context = context;
        this.movieListVOList = movieListVOList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        movieListVO = movieListVOList.get(position);

        //reset all the values
        holder.title.setText("");
        holder.typeAndRunTime.setText("");
        holder.cast.setText("");
        holder.audienceRating.setText("");
        holder.criticRating.setText("");
       // holder.copyright.setText("");
      //  holder.image.setImageResource(R.drawable.no_image);

        if(movieListVO != null) {

            if (movieListVO.getTitle() != null)
                holder.title.setText(movieListVO.getTitle());

            if (movieListVO.getTypeAndRuntime() != null && !movieListVO.getTypeAndRuntime().isEmpty())
                holder.typeAndRunTime.setText(movieListVO.getTypeAndRuntime());

            if (movieListVO.getCast() != null && !movieListVO.getCast().isEmpty())
                holder.cast.setText(movieListVO.getCast());

            // ------ set proper image for the rating based on good or bad score
            if (movieListVO.getAudienceRating() != null) {

                int audienceScore = Integer.parseInt(movieListVO.getAudienceRating());

                if(audienceScore == 0) {
                    holder.audienceRating.setText("N/A");
                }
                else {
                    holder.audienceRating.setText(movieListVO.getAudienceRating() + "%");
                }
//                if (audienceScore < 70) {
//                    // audience score considered bad
//                    Picasso.with(context)
//                            .load(R.drawable.rotten_audience)
//                            .into(holder.audienceImage);
//                } else {
//                    // audience score considered good
//                    Picasso.with(context)
//                            .load(R.drawable.fresh_audience)
//                            .into(holder.audienceImage);
//                }

            }
            if (movieListVO.getCriticRating() != null) {

                int criticsScore = Integer.parseInt(movieListVO.getCriticRating());
                String criticsScoreStr = movieListVO.getCriticRatingStr();

                if(criticsScore == 0 || criticsScore == -1){
                    holder.criticRating.setText("N/A");
                }
                else {
                    holder.criticRating.setText(movieListVO.getCriticRating() + "%");
                }

//                if (criticsScore < 59) {
//                    // critic score considered bad
//                    Picasso.with(context)
//                            .load(R.drawable.rotten)
//                            .into(holder.criticImage);
//                } else if (criticsScoreStr != null && criticsScoreStr.equalsIgnoreCase("Certified Fresh")){
//                    // critic score considered good
//                    Picasso.with(context)
//                            .load(R.drawable.fresh_critic)
//                            .into(holder.criticImage);
//
//                } else {
//                    // critic score considered good
//                    Picasso.with(context)
//                            .load(R.drawable.fresh)
//                            .into(holder.criticImage);
//
//                }
            }

            //------ resize drawable image to fit the row
//            if(movieListVO.getImageLink() != null) {
//                Picasso.with(context)
//                        .load(movieListVO.getImageLink())
//                        .error(R.drawable.no_image)
//                        .into(holder.image);
//
////                Log.d("MovieListAdapter: ", movieListVO.getImageLink());
////                holder.copyright.setText("© Gracenote OnConnect API: " +  movieListVO.getImageLink());
//            }
//            else {
//                Picasso.with(context)
//                        .load(R.drawable.no_image)
//                        .into(holder.image);
           // }
       }
   }


    @Override
    public int getItemCount() {
        return movieListVOList.size();
    }

    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);

    }
}


