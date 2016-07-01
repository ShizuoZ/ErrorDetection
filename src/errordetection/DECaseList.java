package errordetection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class DECase implements Iterable<DEEvent> {
    private Integer caseID;
    private List<DEEvent> events;

    public DECase(Integer caseID) {
        this.caseID = caseID;
        events = new ArrayList<DEEvent>();
    }
    public void add(DEEvent e) throws IllegalArgumentException {
        if (e.caseID() == null || !e.caseID().equals(this.caseID)) {
            throw new IllegalArgumentException("event of case " + e.caseID()
                    + " not eligible for case " + this.caseID);
        } else {
            events.add(e);
        }
    }

    public Integer caseID() {
        return caseID;
    }
    public List<DEEvent> events() {
        return events;
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
    public long averageMidTime() {
        long sum = 0;
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                sum += e.midTime();
            }
        }
        return sum / size();
    }
    public long sigmaMidTime() {
        long avg = averageMidTime();
        long var = 0;
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                var += Math.pow(e.midTime() - avg, 2);
            }
        }
        return (long) Math.sqrt(var / size());
    }
    public long averageStampTime() {
        long sum = 0;
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                sum += e.start() + e.end();
            }
        }
        return sum / (2 * size());
    }
    public long sigmaStampTime() {
        long avg = averageStampTime();
        long var = 0;
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                var += Math.pow(e.start() - avg, 2)
                        + Math.pow(e.end() - avg, 2);
            }
        }
        return (long) Math.sqrt(var / (2 * size()));
    }
    public long rangeStampTime() {
        long min = Integer.MAX_VALUE;
        long max = Integer.MIN_VALUE;
        
        for (DEEvent e: events) {
        if (!e.isInvalid()) {
            if (e.start() < min) {
                min = e.start();
            }
            if (e.end() > max) {
                max = e.end();
            }
        }}
        
        return max-min;
    }
    
    class DEStamp {

        private DEEvent e;
        private Long stamp;
        private boolean which;

        public DEStamp(DEEvent e, boolean which) {
            this.e = e;
            this.which = which;
            this.stamp = which ? e.start() : e.end();
        }

        public DEEvent event() {
            return e;
        }

        public boolean which() {
            return which;
        }

        public Long stamp() {
            return stamp;
        }
    }
    public Map<DEEvent, Long> knnMid(int k) {
        Map<DEEvent, Long> knn = new HashMap<DEEvent, Long>();
        events.sort(new MidTimeComparator());

        int r, l;
        Long d, rd, ld, c;
        for (int i = 0; i < events.size(); i++) {
            if (!events.get(i).isInvalid()) {
                r = i;
                l = i;
                d = -1L;
                c = events.get(i).midTime();
                for (int n = 0; n < k; n++) {
                    // CALCULATE POTENTIAL RD
                    if (r + 1 < events.size() && !events.get(r + 1).isInvalid()) {
                        rd = Math.abs(events.get(r + 1).midTime() - c);
                    } else {
                        rd = null;
                    }

                    // CALCULATE POTENTIAL LD
                    if (l - 1 >= 0 && !events.get(l - 1).isInvalid()) {
                        ld = Math.abs(events.get(l - 1).midTime() - c);
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
    public Map<DEEvent, Long[]> knnStamp(int k) {
        Map<DEEvent, Long[]> knn = new HashMap<DEEvent, Long[]>();

        List<DEStamp> stamps = new ArrayList<DEStamp>();
        for (DEEvent e : events) {
            if (!e.isInvalid()) {
                stamps.add(new DEStamp(e, true));
                stamps.add(new DEStamp(e, false));
            }
        }
        stamps.sort(new Comparator<DEStamp>() {
            @Override
            public int compare(DEStamp a, DEStamp b) {
                return a.stamp().compareTo(b.stamp());
            }
        });

        DEStamp s;
        int r, l;
        Long d, rd, ld, c;
        for (int i = 0; i < stamps.size(); i++) {
            s = stamps.get(i);
            if (!s.event().isInvalid()) {
                r = i;
                l = i;
                d = -1L;
                c = s.stamp();
                for (int n = 0; n < k; n++) {
                    // CALCULATE POTENTIAL RD
                    if (r + 1 < stamps.size() && !stamps.get(r + 1).event().isInvalid()) {
                        rd = Math.abs(stamps.get(r + 1).stamp() - c);
                    } else {
                        rd = null;
                    }

                    // CALCULATE POTENTIAL LD
                    if (l - 1 >= 0 && !stamps.get(l - 1).event().isInvalid()) {
                        ld = Math.abs(stamps.get(l - 1).stamp() - c);
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

                if (knn.get(s.event()) == null) {
                    knn.put(s.event(), new Long[2]);
                }
                knn.get(s.event())[s.which() ? 0 : 1] = d;
            } else {
                knn.put(s.event(), null);
            }
        }

        return knn;
    }

    @Override
    public Iterator<DEEvent> iterator() {
        events.sort(new MidTimeComparator());
        return events.iterator();
    }
    @Override
    public String toString() {
        //events.sort(new MidTimeComparator());
        events.sort(new Comparator<DEEvent>() {
            @Override
            public int compare(DEEvent a, DEEvent b) {
                return ((Integer) a.index()).compareTo(((Integer) b.index()));
            }
        });

        String s = DEEventLog.header + "\n";
        for (DEEvent e : events) {
            s += e + "\n";
        }
        return s;
    }
}

public class DECaseList implements Iterable<DECase> {
    private TreeMap<Integer, DECase> cases;

    public DECaseList() {
        cases = new TreeMap<Integer, DECase>();
    }
    public void add(DEEvent e) {
        Integer caseID = e.caseID() == null ? -1 : e.caseID();
        DECase cas = cases.get(caseID);
        if (cas == null) {
            cases.put(caseID, new DECase(caseID));
            cas = cases.get(caseID);
        }
        cas.add(e);
    }
    public DECase get(Integer caseID) {
        return cases.get(caseID == null ? -1 : caseID);
    }
    @Override
    public Iterator<DECase> iterator() {
        return cases.values().iterator();
    }
    public int size() {
        return cases.size();
    }
}
