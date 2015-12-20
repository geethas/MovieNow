package model.vo;

/**
 * Created by geethasrini on 3/28/15.
 */
public class ShowtimeVO {

    private String name; //theater name
    private String dateTime;
    private String ticketURI;
    private String code; //rating

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTicketURI() {
        return ticketURI;
    }

    public void setTicketURI(String ticketURI) {
        this.ticketURI = ticketURI;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
