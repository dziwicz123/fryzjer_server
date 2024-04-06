package fryzjer;
import java.util.ArrayList;
import java.util.List;
public class Schedule {
    private List<Visit> visits;
    private final int  ERROR_OK = 0;
    private final int  ERROR_WRONG = 1;
    public Schedule() {
        visits = new ArrayList<>();
        for (int hour = 10; hour <= 17; hour++) {
            Visit visit = new Visit();
            visit.setHour(hour);
            visit.setUsername(null);
            visits.add(visit);
        }
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public Visit getVisitByHour(int hour) {
        for (Visit visit : visits) {
            if (visit.getHour() == hour) {
                return visit;
            }
        }
        return null;
    }
    public Visit getVisitByUsername(String username) {
        for (Visit visit : visits) {
            if (visit.getUsername().equals(username)) {
                return visit;
            }
        }
        return null;
    }

    public int setUsernameForVisit(String username, int hour){
        Visit visit = getVisitByHour(hour);
        if(visit.getUsername() == null) {
            visit.setUsername(username);
            return ERROR_OK;
        }
        else {
            return ERROR_WRONG;
        }
    }

    public int cancelVisit(String username, int hour){
        Visit visit = getVisitByHour(hour);
        if (visit.getUsername().equals(username)) {
            visit.setUsername(null);
            return ERROR_OK;
        }
        return ERROR_WRONG;
    }

    public List<Visit> getFreeVisits() {
        List<Visit> freeVisits = new ArrayList<>();
        for (Visit visit : visits) {
            if (visit.getUsername() == null) {
                freeVisits.add(visit);
            }
        }
        return freeVisits;
    }
}
