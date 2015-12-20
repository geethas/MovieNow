package control.asyncresponse;

import java.util.List;
import java.util.Map;

import model.vo.CastVO;
import model.vo.CriticVO;
import model.vo.RatingVO;


public interface MovieAsyncResponse {

    void movieProcessFinished( Map<String, RatingVO> ratingsMap, List<CriticVO> criticVOList, List<CastVO> castVOList);
}
