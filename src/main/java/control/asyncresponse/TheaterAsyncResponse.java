package control.asyncresponse;

import java.util.Map;

import model.vo.MovieVO;
import model.vo.RatingVO;


public interface TheaterAsyncResponse {

    void processFinished(Map<String, MovieVO> movieMap, Map<String, RatingVO> ratingsMap, Map<String, String> imageMap);

}