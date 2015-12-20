package model.vo;

/**
 * Created by geethasrini on 4/11/15.
 */
public class MovieListVO {

    private String title;
    private String imageLink;
    private String typeAndRuntime;
    private String audienceRating;
    private String criticRating;
    private String cast;
    private String theaterId;
    private String theaterName;
    private String selectedDate;
    private String criticRatingStr;

    public MovieListVO(String name, String criticRating, String audienceRating, String typeAndRuntime, String cast, String imageLink, String criticRatingStr) {
        this.title = name;
        this.criticRating = criticRating;
        this.audienceRating = audienceRating;
        this.typeAndRuntime = typeAndRuntime;
        this.cast = cast;
        this.imageLink = imageLink;
        this.criticRatingStr = criticRatingStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeAndRuntime() {
        return typeAndRuntime;
    }

    public void setTypeAndRuntime(String typeAndRuntime) {
        this.typeAndRuntime = typeAndRuntime;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getAudienceRating() {
        return audienceRating;
    }

    public void setAudienceRating(String audienceRating) {
        this.audienceRating = audienceRating;
    }

    public String getCriticRating() {
        return criticRating;
    }

    public void setCriticRating(String criticRating) {
        this.criticRating = criticRating;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getCriticRatingStr() {
        return criticRatingStr;
    }

    public void setCriticRatingStr(String criticRatingStr) {
        this.criticRatingStr = criticRatingStr;
    }
}
