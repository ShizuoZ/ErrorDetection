package errordetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class DEActivity implements Iterable<DEEvent> {
    private String activity;
    private List<DEEvent> events;

    public DEActivity(String name) {
        this.activity = name;
        events = new ArrayList<DEEvent>();
    }
    public void add(DEEvent e) throws IllegalArgumentException {
        if (e.activity() == null || !e.activity().equals(this.activity)) {
            throw new IllegalArgumentException("event of activity " + e.activity()
                    + " not eligible for activity " + this.activity);
        } else {
            events.add(e);
        }
    }

    public String activity() {
        return activity;
    }
    public int size() {
        int size = 0;
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                size++;
            }
        }
        return size;
    }
    public long averageDuration() {
        long sum = 0;
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                sum += e.duration();
            }
        }
        return sum / size();
    }
    public long sigmaDuration() {
        long avg = averageDuration();
        long var = 0;
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                var += Math.pow(e.duration() - avg, 2);
            }
        }
        return (long) Math.sqrt(var / size());
    }

    public Map<DEEvent, Long> knn(int k) {
        Map<DEEvent, Long> knn = new HashMap<DEEvent, Long>();
        events.sort(new DurationComparator());

        int r, l;
        Long d, rd, ld, c;
        for (int i = 0; i < events.size(); i++) {
            if (!events.get(i).isInvalid()) {
                r = i;
                l = i;
                d = -1L;
                c = events.get(i).duration();
                for (int n = 0; n < k; n++) {
                    // CALCULATE POTENTIAL RD
                    if (r + 1 < events.size() && !events.get(r + 1).isInvalid()) {
                        rd = Math.abs(events.get(r + 1).duration() - c);
                    } else {
                        rd = null;
                    }

                    // CALCULATE POTENTIAL LD
                    if (l - 1 >= 0 && !events.get(l - 1).isInvalid()) {
                        ld = Math.abs(events.get(l - 1).duration() - c);
                    } else {
                        ld = null;
                    }

                    // COMPARE, CHOOSE, AND MOVE
                    if (rd != null && ld != null) {
                        if (rd <= ld) {
                            d = rd;
                            r++;
                        } else {
                            d = ld;
                            l--;
                        }
                    } else if (rd != null && ld == null) {
                        d = rd;
                        r++;
                    } else if (rd == null && ld != null) {
                        d = ld;
                        l--;
                    } else {
                        break;
                    }
                }
                knn.put(events.get(i), d);
            } else {
                knn.put(events.get(i), null);
            }
        }

        return knn;
    }
    public boolean isInsufficient() {
        return events.size() < DEEventLog.insuffThresh;
    }

    public List<DEEvent> events() {
        return events;
    }
    @Override
    public Iterator<DEEvent> iterator() {
        events.sort(new DurationComparator());
        return events.iterator();
    }
    @Override
    public String toString() {
        events.sort(new DurationComparator());
        String s = DEEventLog.header + "\n";
        for (DEEvent e : events) {
            s += e + "\n";
        }
        return s;
    }
}

public class DEActivityList implements Iterable<DEActivity> {
    private TreeMap<String, DEActivity> acts;

    public DEActivityList() {
        acts = new TreeMap<String, DEActivity>();
    }
    public void add(DEEvent e) {
        String activity = e.activity() == null ? "NULL" : e.activity();
        DEActivity act = acts.get(activity);
        if (act == null) {
            acts.put(activity, new DEActivity(activity));
            act = acts.get(activity);
        }
        act.add(e);
    }
    public DEActivity get(String activity) {
        return acts.get(activity == null ? "NULL" : activity);
    }
    @Override
    public Iterator<DEActivity> iterator() {
        return acts.values().iterator();
    }
}
