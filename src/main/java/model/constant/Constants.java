package model.constant;


public interface Constants {

    // parameters for tms urls
    String TMS_BASE_URL = "http://data.tmsapi.com";
    String TMS_API_VERSION = "1.1";
    String TMS_API_KEY_VAL = "4rda8nw3z793asgkzwx7xz6q";
    String TMS_API_KEY = "api_key";

    // parameters for rotten tomatoes urls
    String TOMATO_BASE_URL = "http://api.rottentomatoes.com/api/public";
    String TOMATO_API_VERSION = "1.0";
    String TOMATO_API_KEY_VAL = "vhw2a426waqthh33cvxk3y4b";
    String TOMATO_API_KEY = "apikey";

    // operations for urls
    String LOCAL_THEATERS_OP = "theaters";
    String THEATER_SHOWING_OP = "movies/showings";
    String TOMATOES_ALL_MOVIES_OP = "lists/movies/in_theaters.json";
    String TOMATOES_ONE_MOVIE_OP = "movies";
    String JSON_TYPE = ".json";
    String CRITIC_REVIEWS_OP = "reviews";
    String FULL_CAST_OP = "cast";

    int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    String DEFAULT_RADIUS = "5";
    String SETTER = "set";

    String WIKI_URL = "http://en.wikipedia.org/wiki/";
    String FANDANGO_URL = "http://www.fandango.com";

    //film rating constants
    String G_FILM_RATING	= "General Audiences. All ages admitted";
    String PG_FILM_RATING	= "Parental Guidance Suggested. Some material may not be suitable for children";
    String PG13_FILM_RATING = "Parents Strongly Cautioned. Some material may be inappropriate for children under 13";
    String R_FILM_RATING = "Restricted. Under 17 requires accompanying parent or adult guardian";

    //days of week
    String TODAY = "Today";
    String MONDAY = "Monday";
    String TUESDAY = "Tuesday";
    String WEDNESDAY = "Wednesday";
    String THURSDAY = "Thursday";
    String FRIDAY = "Friday";
    String SATURDAY = "Saturday";
    String SUNDAY = "Sunday";

    // VO Types
    String MOVIE_VO_TYPE = "movie";
    String SHOWTIME_VO_TYPE = "showtime";
    String RATING_VO_TYPE = "rating";
    String CRITIC_VO_TYPE = "critic";
    String CAST_VO_TYPE = "cast";

}
