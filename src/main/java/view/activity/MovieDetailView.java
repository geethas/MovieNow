package view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gsrini.movienow.view.R;
import model.constant.Constants;
import model.data.StaticDataHolder;
import model.vo.MovieVO;
import model.vo.RatingVO;
import view.helper.CalendarDataHelper;
import view.helper.MovieDataHelper;



public class MovieDetailView extends ActionBarActivity implements Constants {

    public static class Holder {

        ImageView image;
        //ImageView audienceImage;
        //ImageView criticImage;

        TextView title;
        TextView audienceRating;
        TextView criticRating;
        TextView movieType;
        TextView genre;
        TextView movieDate;
        //TextView copyright;

        LinearLayout castLayout;
        LinearLayout directorsLayout;
        LinearLayout infoLayout;

        Button criticReview;
        Button fullCast;
    }

    private Holder holder = null;

    private String TAG = this.getClass().getSimpleName();

    MovieVO movieVO = null;
    RatingVO ratingVO = null;
    String selectedShowtime = null;
    String selectedTheater = null;
    String rowTitle;
    String dateStr;
    String dayOfWeek;
    Spinner movieSpinner;
    Spinner theaterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_list_row);

        Toolbar toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (holder == null) {
            holder = new Holder();
            //holder.image = (ImageView) findViewById(R.id.thumbnail);
            holder.title = (TextView) findViewById(R.id.title);
           // holder.audienceImage = (ImageView) findViewById(R.id.audience_image);
            holder.audienceRating = (TextView) findViewById(R.id.audience_rating);
            //holder.criticImage = (ImageView) findViewById(R.id.critic_image);
            holder.criticRating = (TextView) findViewById(R.id.critic_rating);
            holder.movieType = (TextView) findViewById(R.id.typeAndRunTime);
            holder.genre = (TextView) findViewById(R.id.genre);
            holder.castLayout = (LinearLayout) findViewById(R.id.cast_layout);
            holder.directorsLayout = (LinearLayout) findViewById(R.id.director_layout);
            holder.infoLayout = (LinearLayout) findViewById(R.id.info_layout);
            holder.movieDate = (TextView) findViewById(R.id.movie_date);
            holder.criticReview = (Button) findViewById(R.id.critic_button);
            holder.fullCast = (Button) findViewById(R.id.full_cast_button);
           // holder.copyright = (TextView) findViewById(R.id.copyright);
        } else {
            // clear previous data
            holder.image.setImageResource(android.R.color.transparent);
            holder.title.setText("");
           // holder.audienceImage.setImageResource(android.R.color.transparent);
            holder.audienceRating.setText("");
           // holder.criticImage.setImageResource(android.R.color.transparent);
            holder.criticRating.setText("");
            holder.movieType.setText("");
            holder.genre.setText("");
            holder.movieDate.setText("");
           // holder.copyright.setText("");
        }

        if (getIntent() != null && getIntent().getExtras() != null) {

            rowTitle = (String) getIntent().getExtras().get("title");

            getSupportActionBar().setTitle(rowTitle);

            String day = (String) getIntent().getExtras().get("day");
            Log.d(TAG, "day: " + day);

            CalendarDataHelper calendarDataHelper = new CalendarDataHelper();
            Calendar cal = Calendar.getInstance();

            // set proper dates during the week
            if (day != null && Character.isDigit(day.charAt(0))) {

                Date date = new Date();
                cal.setTime(date);

                String[] daysOfWeek = calendarDataHelper.getDaysOfWeek(cal);
                dayOfWeek = daysOfWeek[Integer.parseInt(day)-1];

                cal.add(Calendar.DATE, Integer.parseInt(day)-1);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
                dateStr = dateFormat.format(cal.getTime());

                holder.movieDate.setText(dayOfWeek + ", " + dateStr);
                holder.movieDate.setTextColor(Color.parseColor("#d80731"));

            } else {
                dayOfWeek = day;

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
                dateStr = dateFormat.format(cal.getTime());

                holder.movieDate.setText(dayOfWeek + ", " + dateStr);
                holder.movieDate.setTextColor(Color.parseColor("#d80731"));
            }

            if (StaticDataHolder.movieCache != null && StaticDataHolder.movieCache.get(StaticDataHolder.postalCode) != null)
                movieVO = StaticDataHolder.movieCache.get(StaticDataHolder.postalCode).get(rowTitle);

            if (movieVO != null) {
                ratingVO = StaticDataHolder.ratingCache.get(movieVO.getTitle());
            }

            Map<String, String> imageMap = StaticDataHolder.imageMapCache;

            holder.title.setText(rowTitle);

            //------ set proper image for movie
//            if (imageMap.get(rowTitle) != null) {
//                Picasso.with(this)
//                        .load(imageMap.get(rowTitle))
//                        .resize(110, 100)
//                        .into(holder.image);
//
//                //holder.copyright.setText("© Gracenote OnConnect API: " + imageMap.get(rowTitle));
//
//            } else {
//                Picasso.with(this)
//                        .load(R.drawable.no_image)
//                        .resize(110, 100)
//                        .into(holder.image);
           // }

            //set showtime list
            movieSpinner = (Spinner) findViewById(R.id.movie_spinner);
            theaterSpinner = (Spinner) findViewById(R.id.theater_spinner);

            setMovieDetails();

            //===================================
            holder.criticReview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieDetailView.this, MoreInfoView.class);
                    if (ratingVO != null) {
                        String movieId = ratingVO.getId();
                        intent.putExtra("operation", CRITIC_REVIEWS_OP);
                        intent.putExtra("movieId", movieId);
                        intent.putExtra("title", rowTitle);
                    }
                    startActivity(intent);

                }
            });

            holder.fullCast.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MovieDetailView.this, MoreInfoView.class);
                    if (ratingVO != null) {
                        String movieId = ratingVO.getId();
                        intent.putExtra("operation", FULL_CAST_OP);
                        intent.putExtra("movieId", movieId);
                        intent.putExtra("title", rowTitle);
                    }
                    startActivity(intent);

                }
            });
        } else {
            holder.title.setText("Could not get movie details. Please try again!");
        }
    }

    public void setMovieDetails() {

         final MovieDataHelper movieHelper = new MovieDataHelper();
        // ------ set proper image for the rating based on good or bad score

        Map<String, RatingVO> ratingMap = new HashMap<>();
        ratingMap.put(rowTitle, ratingVO);

        String audienceScore = movieHelper.getAudienceScore(rowTitle, ratingMap);

        if (audienceScore != null) {

            int audienceScoreInt = Integer.parseInt(audienceScore);

            if (audienceScoreInt == 0) {
                holder.audienceRating.setText("N/A");
            } else {
                holder.audienceRating.setText(audienceScore + "%");
            }
//            if (audienceScoreInt < 70) {
//                // audience score considered bad
//                Picasso.with(this)
//                        .load(R.drawable.rotten_audience)
//                        .into(holder.audienceImage);
//
//            } else {
//                // audience score considered good
//                Picasso.with(this)
//                        .load(R.drawable.fresh_audience)
//                        .into(holder.audienceImage);
//
//            }
        }

        String criticScore = movieHelper.getCriticScore(rowTitle, ratingMap);
        String criticRating = movieHelper.getCriticRating(rowTitle, ratingMap);
        if (criticScore != null) {

            int criticScoreInt = Integer.parseInt(criticScore);

            if (criticScoreInt == 0 || criticScoreInt == -1) {
                holder.criticRating.setText("N/A");
            } else {
                holder.criticRating.setText(criticScore + "%");
            }

//            if (criticScoreInt < 59) {
//                // critic score considered bad
//                Picasso.with(this)
//                        .load(R.drawable.rotten)
//                        .into(holder.criticImage);
//
//            } else if (criticRating != null && criticRating.equalsIgnoreCase("Certified Fresh")){
//                // critic score considered good
//                Picasso.with(this)
//                        .load(R.drawable.fresh_critic)
//                        .into(holder.criticImage);
//
//            } else {
//                // critic score considered good
//                Picasso.with(this)
//                        .load(R.drawable.fresh)
//                        .into(holder.criticImage);
//            }
        }

        if (movieVO != null) {
            //set runtime and film rating
            String type = movieHelper.getMovieType(movieVO.getCode(), movieVO.getRunTime());
            if (type != null && !type.isEmpty())
                holder.movieType.setText(type);
            else
                holder.movieType.setVisibility(View.GONE);

            //set genre
            String genre = movieHelper.getGenre(movieVO.getGenres());
            if (genre != null && !genre.isEmpty())
                holder.genre.setText(genre);
            else
                holder.genre.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            //set movie information (synopsis, runtime, language etc)
            List<String> movieInfoList = new ArrayList<>();
            if (ratingMap.get(rowTitle) != null) {

                String synopsis = ratingMap.get(rowTitle).getSynopsis();

                //set synopsis of movie
                if (synopsis.isEmpty()) {
                    synopsis = movieVO.getShortDescription();
                }

                movieInfoList.add(synopsis);
            }

            final String runtime = movieHelper.getMovieType(null, movieVO.getRunTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String releaseDate = movieVO.getReleaseDate();
            if(releaseDate != null) {
                try {
                    //format month to return the name instead of number
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(dateFormat.parse(movieVO.getReleaseDate()));
                    int monthInt = cal1.get(Calendar.MONTH);
                    String[] monthNames = {"Jan", "Feb", "Mar", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
                    String month = monthNames[monthInt];
                    releaseDate = month + " " + cal1.get(Calendar.DATE) + ", " + cal1.get(Calendar.YEAR);
                } catch (Exception e) {
                    Log.d(TAG, "Parsing Exception: " + e.getMessage());
                }
            }

            if (runtime != null && !runtime.isEmpty())
                movieInfoList.add("Run Time: " + runtime);

            String filmRating = "";
            if (movieVO.getCode() != null) {

                if (movieVO.getCode().equals("G")) {
                    filmRating = G_FILM_RATING;
                }
                if (movieVO.getCode().equals("PG")) {
                    filmRating = PG_FILM_RATING;
                }
                if (movieVO.getCode().equals("PG-13")) {
                    filmRating = PG13_FILM_RATING;
                }
                if (movieVO.getCode().equals("R")) {
                    filmRating = R_FILM_RATING;
                }
            }

            if (movieVO.getCode() != null) {
                movieInfoList.add("Film Rating: " + movieVO.getCode() + "\n(" + filmRating + ")");
            } else if (movieVO.getCode() != null) {
                movieInfoList.add("Film Rating: " + movieVO.getCode());
            }

            Log.d(TAG, "Genre: " + genre);
            if (genre != null && !genre.isEmpty())
                movieInfoList.add("Genre: " + genre);
            if (movieVO.getTitleLang() != null)
                movieInfoList.add("Language: " + movieVO.getTitleLang());
            if (releaseDate != null && !releaseDate.isEmpty())
                movieInfoList.add("Released On: " + releaseDate);

            holder.infoLayout.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < movieInfoList.size(); i++) {
                TextView textView = new TextView(this);
                textView.setText(movieInfoList.get(i));
                textView.setPadding(10, 2, 10, 2);
                textView.setLayoutParams(params);
                holder.infoLayout.addView(textView);

                if (i != movieInfoList.size() - 1) {
                    TextView lineView = new TextView(this);
                    lineView.setBackgroundResource(R.drawable.line);
                    holder.infoLayout.addView(lineView);
                }
            }

            List<String> castList = movieHelper.getCast(movieVO.getTopCast());

            //set cast list
            if(!castList.isEmpty()) {
                holder.castLayout.setOrientation(LinearLayout.VERTICAL);
                Log.d(TAG, "cast list is not empty");
                for (int i = 0; i < castList.size(); i++) {
                    TextView textView = new TextView(this);
                    textView.setText(castList.get(i));
                    if(i == 0) {
                        textView.setPadding(10, 0, 10, 0);
                    }
                    else {
                        textView.setPadding(10, 2, 10, 2);
                    }
                    textView.setLayoutParams(params);
                    holder.castLayout.addView(textView);

                    if (i != castList.size() - 1) {
                        TextView lineView = new TextView(this);
                        lineView.setBackgroundResource(R.drawable.line);
                        holder.castLayout.addView(lineView);
                    }
                }
            } else {
                holder.castLayout.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(this);
                textView.setText("No Cast Information");
                textView.setPadding(10, 0, 10, 0);
                textView.setLayoutParams(params);
                holder.castLayout.addView(textView);
            }

            //set director list
            List<String> directorList = movieHelper.getDirectors(movieVO.getDirectors());

            if(!directorList.isEmpty()) {
                holder.directorsLayout.setOrientation(LinearLayout.VERTICAL);
                for (int i = 0; i < directorList.size(); i++) {
                    TextView textView = new TextView(this);
                    textView.setText(directorList.get(i));
                    if(i == 0) {
                        textView.setPadding(10, 0, 10, 0);
                    }
                    else {
                        textView.setPadding(10, 2, 10, 2);
                    }
                    textView.setLayoutParams(params);
                    holder.directorsLayout.addView(textView);

                    if (i != directorList.size() - 1) {
                        TextView lineView = new TextView(this);
                        lineView.setBackgroundResource(R.drawable.line);
                        holder.directorsLayout.addView(lineView);
                    }
                }
            } else {
                holder.directorsLayout.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(this);
                textView.setText("No Director Information");
                textView.setPadding(10, 0, 10, 0);
                textView.setLayoutParams(params);
                holder.directorsLayout.addView(textView);
            }

            if(movieVO.getAllShowtimeMap() == null) {
                HashMap<String, List<String>> showtimeVOMap = new HashMap<>();
                movieVO.setAllShowtimeMap(showtimeVOMap);
            }

            final Context context = this;

            if (movieVO.getAllShowtimeMap() != null) {
                List<String> theaterList = new ArrayList<>(movieVO.getShowtimeVOMap().keySet());
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, theaterList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                theaterSpinner.setAdapter(dataAdapter);

                theaterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        selectedTheater = parentView.getItemAtPosition(position).toString();
                        movieSpinner.setAdapter(null);

                        Log.d(TAG, "============================");
                        Log.d(TAG, "Getting Showtime for Movie: " + selectedTheater);
                        if(movieVO.getAllShowtimeMap() != null && movieVO.getAllShowtimeMap().get(selectedTheater) == null) {

                            Log.d(TAG, "All Showtime List is null");

                            List<String> allShowtimeList = movieHelper.getShowtimeList(movieVO.getShowtimeVOMap().get(selectedTheater), false, false);

                            // add 3D Showtimes
                            if(movieVO.getShowtimeVO3DMap() != null && movieVO.getShowtimeVO3DMap().size() > 0) {
                                List<String> showtime3DList = movieHelper.getShowtimeList(movieVO.getShowtimeVO3DMap().get(selectedTheater), true, false);
                                allShowtimeList.addAll(showtime3DList);
                            }

                            // add imax showtimes
                            if(movieVO.getShowtimeVOIMAX3DMap() != null && movieVO.getShowtimeVOIMAX3DMap().size() > 0) {
                                List<String> showtimeImaxList = movieHelper.getShowtimeList(movieVO.getShowtimeVOIMAX3DMap().get(selectedTheater), false, true);
                                allShowtimeList.addAll(showtimeImaxList);
                            }

                            // order the showtime list starting from closest time from now
                            orderShowtimes(allShowtimeList);
                            movieVO.getAllShowtimeMap().put(selectedTheater, allShowtimeList);
                        }

                        if(selectedTheater != null && movieVO.getAllShowtimeMap().get(selectedTheater) != null) {
                            List<String> showtimeList = movieVO.getAllShowtimeMap().get(selectedTheater);
                            if (showtimeList == null || showtimeList.size() == 0) {
                                Log.d(TAG, "No Showtimes");
                                List<String> noShowtimeList = new ArrayList<>();
                                noShowtimeList.add("No Showtimes");

                                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, noShowtimeList);
                                dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                movieSpinner.setAdapter(dataAdapter1);

                            } else {
                                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, showtimeList);
                                dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                movieSpinner.setAdapter(dataAdapter1);
                            }

                            movieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    selectedShowtime = parentView.getItemAtPosition(position).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {

                                }

                            });
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {

                    }

                });
            }
        }
    }

    /**
     * order showtime list starting from closest time from now
     * @param showtimeList
     */
    public void orderShowtimes(List <String> showtimeList){

        // reorder list to have highest critic/audience rating on top and lowest critic/audience rating on bottom
        Collections.sort(showtimeList, new Comparator<String>() {
            @Override

            public int compare(String lhs, String rhs) {

                Date showtime1 = null;
                Date showtime2 = null;

                // set all null values to 0 so they go to the bottom of the list
                SimpleDateFormat timeFormat12Hr = new SimpleDateFormat("hh:mm a");
                try {
                    if (lhs.contains(" [")) {
                        lhs = lhs.substring(0, lhs.indexOf(" ["));
                    }
                    showtime1 = timeFormat12Hr.parse(lhs);

                    if (rhs.contains(" [")) {
                        rhs = rhs.substring(0, rhs.indexOf(" ["));
                    }
                    showtime2 = timeFormat12Hr.parse(rhs);

                } catch (ParseException e) {
                    Log.d(TAG, "Cannot parse date during ordering");
                }

                if (showtime1 != null && showtime2 != null) {
                    if (showtime1.after(showtime2)) {
                        return 1;
                    }
                }

                return -1;
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            Intent intent = new Intent(MovieDetailView.this, MovieView.class);
            startActivity(intent);
    }

    private void buyTickets() {

        Intent intent = new Intent(this, view.activity.WebView.class);
        String operation = "movietimes";
        String url = String.format("%s/%s_%s?q=%s", FANDANGO_URL, StaticDataHolder.postalCode, operation, StaticDataHolder.postalCode);

        intent.putExtra("url", url);
        this.startActivity(intent);

    }

    public void addToCalendar() {
        String endTime = "";
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", selectedShowtime);
        Log.d(TAG, "selected showtime for Calendar: " + selectedShowtime);
        intent.putExtra("endTime", endTime);
        intent.putExtra("title", movieVO.getTitle());
        intent.putExtra("eventLocation", selectedTheater);
        startActivity(intent);
    }

    public void sendMessage() {
        Intent intent = new Intent(MovieDetailView.this, MessageView.class);
        //intent.putExtra("theaterId", rowTheaterId);
        intent.putExtra("movieTitle", rowTitle);
        intent.putExtra("theaterName", selectedTheater);
        intent.putExtra("showtime", selectedShowtime);
        intent.putExtra("selectedDate", dateStr);
        if (dayOfWeek != null)
            intent.putExtra("day", dayOfWeek);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.add_to_calendar:
                addToCalendar();
                return true;

            case R.id.send_message:
                sendMessage();
                return true;

            case R.id.ticket:
                // start theater maps view
                Log.d(TAG, "Buy Ticket");
                buyTickets();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
