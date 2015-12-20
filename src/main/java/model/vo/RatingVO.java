package model.vo;

/**
 * Created by geethasrini on 4/3/15.
 */
public class RatingVO {

    private String id;
    private String title;
    private String criticsRating;
    private String criticsScore;
    private String audienceRating;
    private String audienceScore;
    private String synopsis;
    private String thumbnail;

    public String getCriticsRating() {
        return criticsRating;
    }

    public void setCriticsRating(String criticsRating) {
        this.criticsRating = criticsRating;
    }

    public String getCriticsScore() {
        return criticsScore;
    }

    public void setCriticsScore(String criticsScore) {
        this.criticsScore = criticsScore;
    }

    public String getAudienceRating() {
        return audienceRating;
    }

    public void setAudienceRating(String audienceRating) {
        this.audienceRating = audienceRating;
    }

    public String getAudienceScore() {
        return audienceScore;
    }

    public void setAudienceScore(String audienceScore) {
        this.audienceScore = audienceScore;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
