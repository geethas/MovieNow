package model.data;

import java.util.HashMap;
import java.util.Map;

import model.vo.MovieVO;
import model.vo.RatingVO;


public class StaticDataHolder {

    // saves movie information based on movie title
    // key: postal code, value: Map (key: movie title, value: Map <title, MovieVO> )
    // since each movie has its own movie information, we need another map for it
    public static Map<String, Map<String, MovieVO>> movieCache = new HashMap<String, Map<String, MovieVO>>();

    // saves rating information based on movie title
    // Map (key: movie title, value: RatingVO)
    public static Map<String, RatingVO> ratingCache = new HashMap<String, RatingVO>();

    //saves images of the each movie
    public static Map<String, String> imageMapCache = new HashMap <String, String>();

    // current postal code or the one that user enters
    public static String postalCode;

    // current location or the one that user enters
    public static double latitude = 0;
    public static double longitude = 0;

    // used for the radius bar that user uses to change radius
    public static int radiusNum = 0;
    public static String radiusStr;
    public static String date;

}
