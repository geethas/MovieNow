package model.vo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by geethasrini on 3/18/15.
 */
public class MovieVO {

    private String title;
    private String releaseDate;
    private String titleLang;
    private String shortDescription;
    private String topCast;
    private String directors;
    private String runTime;
    private String code; //rating
    private String genres;
    private String rootId;

    // Showtime Lists
    private HashMap<String, List<ShowtimeVO>> showtimeVOMap; //list of showtimes for that theater
    private HashMap<String, List<ShowtimeVO>> showtimeVO3DMap;
    private HashMap<String, List<ShowtimeVO>> showtimeVOIMAX3DMap;
    private HashMap<String, List<String>> allShowtimeMap;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitleLang() {
        return titleLang;
    }

    public void setTitleLang(String titleLang) {
        this.titleLang = titleLang;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getTopCast() {
        return topCast;
    }

    public void setTopCast(String topCast) {
        this.topCast = topCast;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public HashMap<String, List<ShowtimeVO>> getShowtimeVOMap() {
        return showtimeVOMap;
    }

    public void setShowtimeVOMap(HashMap<String, List<ShowtimeVO>> showtimeVOMap) {
        this.showtimeVOMap = showtimeVOMap;
    }

    public HashMap<String, List<ShowtimeVO>> getShowtimeVO3DMap() {
        return showtimeVO3DMap;
    }

    public void setShowtimeVO3DMap(HashMap<String, List<ShowtimeVO>> showtimeVO3DMap) {
        this.showtimeVO3DMap = showtimeVO3DMap;
    }

    public HashMap<String, List<ShowtimeVO>> getShowtimeVOIMAX3DMap() {
        return showtimeVOIMAX3DMap;
    }

    public void setShowtimeVOIMAX3DMap(HashMap<String, List<ShowtimeVO>> showtimeVOIMAX3DMap) {
        this.showtimeVOIMAX3DMap = showtimeVOIMAX3DMap;
    }

    public HashMap<String, List<String>> getAllShowtimeMap() {
        return allShowtimeMap;
    }

    public void setAllShowtimeMap(HashMap<String, List<String>> allShowtimeMap) {
        this.allShowtimeMap = allShowtimeMap;
    }
}
