package fryzjer;
import java.time.LocalTime;

public class Visit {
    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    private int hour;
    private String username;
}
