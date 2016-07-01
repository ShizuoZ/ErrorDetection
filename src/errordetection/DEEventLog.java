package errordetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.DateTime;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;



enum ErrorType {
    INVALID         (0),
    INSUFF          (1),
    ACT_DUR_STD     (2),
    ACT_DUR_KNN     (3),
    ACT_DUR_CLUST   (4),
    ACT_TIME_STD    (5),
    ACT_TIME_KNN    (6),
    CASE_STD        (7),
    CASE_RANGE      (8);

    public final int code;
    ErrorType(int code) {
        this.code = code;
    }
}
class MidTimeComparator implements Comparator<DEEvent> {
    @Override
    public int compare(DEEvent a, DEEvent b) {
        try {
            return a.midTime().compareTo(b.midTime());
        } catch (NullPointerException e) {
            if (a.midTime() == null && b.midTime() == null) {
                return 0;
            } else if (a.midTime() == null) {
                return 1;
            } else {
                return -1;
            }

            /*/
            Long x = null; Long y = null;
            if (a.midTime() != null) {x = a.midTime();}
                else if (a.start() != null) {x = a.start();}
                else if (a.end() != null) {x = a.end();}
            if (b.midTime() != null) {y = b.midTime();}
                else if (b.start() != null) {y = b.start();}
                else if (b.end() != null) {y = b.end();}

            try {
                return x.compareTo(y);
            } catch (NullPointerException f) {
                if (x == null && y == null) {
                    return 0;
                } else if (x == null) {
                    return 1;
                } else if (y == null) {
                    return -1;
                }
            } //*/
        } //return 0;
    }
}
class DurationComparator implements Comparator<DEEvent> {
    @Override
    public int compare(DEEvent a, DEEvent b) {
        try {
            return a.duration().compareTo(b.duration());
        } catch (NullPointerException e) {
            if (a.duration() == null && b.duration() == null) {
                return 0;
            } else if (a.duration() == null) {
                return 1;
            } else {
                return -1;
            }

            /*/
            Long x = null; Long y = null;
            if (a.duration() != null) {x = a.duration();}
                else if (a.start() != null) {x = a.start();}
                else if (a.end() != null) {x = a.end();}
            if (b.duration() != null) {y = b.duration();}
                else if (b.start() != null) {y = b.start();}
                else if (b.end() != null) {y = b.end();}

            try {
                return x.compareTo(y);
            } catch (NullPointerException f) {
                if (x == null && y == null) {
                    return 0;
                } else if (x == null) {
                    return 1;
                } else if (y == null) {
                    return -1;
                }
            } //*/
        } //return 0;
    }
}

public class DEEventLog {
    public static int insuffThresh        = 30;
    public static double[] actdurSTDbnd   = {-2.5, 2.5};
    public static double actdurKNNmax     = 2.5;
    public static double[] actdurCLUSTbnd = {-2.5, 2.5};
    public static int actdurCLUSTtest     = 5;
    public static double[] acttimeSTDbnd  = {-2, 2};
    public static double acttimeKNNmax    = 2;
    public static double[] caseSTDbnd     = {-1.5, 1.5};
    public static double[] caseRANGEbnd    = {-1.5, 1.5};

    public static long milliday;
    public static SimpleDateFormat f;
    public static SimpleDateFormat g;
    public static long dateOrigin;
    public static String header;

    private List<DEEvent> events;
    private DECaseList cases;
    private DEActivityList acts;
    private static int eventNum = 0;

    public DEEventLog(String filename) throws BiffException, IOException {
        if (DEEventLog.f == null) {init();}
//        Workbook workbook = Workbook.getWorkbook(new File(filename));
//        Sheet sheet = workbook.getSheet(0);

        events = new ArrayList<DEEvent>();
        cases = new DECaseList();
        acts = new DEActivityList();
//        this.read(sheet);
        this.readFromCSV(filename);
//        workbook.close();
    }
    private void read(Sheet sheet) throws IOException {
        int[] head = null;
        head = head(sheet);

        Cell tempCell;
        Integer caseID;
        Long start;
        Long end;
        String actName;
        for (int n = 1; n < sheet.getRows(); n++) {
            tempCell = sheet.getCell(head[0], n);
            if (tempCell.getType() == CellType.NUMBER) {
                caseID = (int) ((NumberCell) sheet.getCell(head[0], n))
                        .getValue();
            } else {
                caseID = null;
            }

            tempCell = sheet.getCell(head[1], n);
            if (tempCell.getType() == CellType.DATE) {
                start = ((DateCell) sheet.getCell(head[1], n))
                        .getDate().getTime() - dateOrigin;
                if (start >= 172800000) {
                    start -= milliday;
                }
            } else {
                start = null;
            }

            tempCell = sheet.getCell(head[2], n);
            if (tempCell.getType() == CellType.DATE) {
                end = ((DateCell) sheet.getCell(head[2], n))
                        .getDate().getTime() - dateOrigin;
                if (end >= 172800000) {
                    end -= milliday;
                }
            } else {
                end = null;
            }

            tempCell = sheet.getCell(head[3], n);
            if (tempCell.getType() == CellType.LABEL) {
                actName = ((LabelCell) sheet.getCell(head[3], n))
                        .getString();
            } else {
                actName = null;
            }
            
            //if (!(caseID == 2015007 ||
            //  caseID == 2015018 ||
            //  caseID == 2015063)) {
                this.add(new DEEvent(caseID, actName, start, end, n + 1));
            //}
        }
    }
    private int[] head(Sheet sheet) throws IOException {
        int[] head = new int[4];
        for (int i = 0; i < 4; i++) {
            head[i] = -1;
        }
        String tempHead;
        for (int n = 0; n < sheet.getColumns(); n++) {
            try {
                tempHead = ((LabelCell) sheet.getCell(n, 0)).getString();
                if (tempHead.equalsIgnoreCase("caseid")
                        || tempHead.equalsIgnoreCase("caseid")) {
                    head[0] = n;
                } else if (tempHead.equalsIgnoreCase("starttime")
                        || tempHead.equalsIgnoreCase("start time")) {
                    head[1] = n;
                } else if (tempHead.equalsIgnoreCase("endtime")
                        || tempHead.equalsIgnoreCase("end time")) {
                    head[2] = n;
                } else if (tempHead.equalsIgnoreCase("activity name")
                        || tempHead.equalsIgnoreCase("activity")) {
                    head[3] = n;
                }
            } catch (java.lang.ClassCastException e) {
                continue;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (head[i] == -1) {
                throw new IOException("input file not formatted properly");
            }
        }

        return head;
    }
    public static void init() {
        milliday = 24 * 60 * 60 * 1000;

        f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            dateOrigin = (f.parse("1899/12/30 00:00:00")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        g = new SimpleDateFormat("dd HH:mm:ss");
        g.setTimeZone(TimeZone.getTimeZone("GMT"));

        header = 1 + "\tERROR";
        for (int i = 0; i < ErrorType.values().length-5; i++) {
            header += " ";
        }
        header += "   CASEID    START"
                + "         END           "
                + "MIDTIME       "
                + "DURATION      ACTIVITY";
    }

    public List<DEEvent> events() {
        return events;
    }
    public DECaseList cases() {
        return cases;
    }
    public DEActivityList acts() {
        return acts;
    }
    public void add(DEEvent e) {
        events.add(e);
        cases.add(e);
        acts.add(e);
        this.eventNum++;
    }

    public List<DEEvent> detectError() {
        return detectError(ErrorType.values());
    }
    public List<DEEvent> detectError(ErrorType et) {
        return detectError(new ErrorType[]{et});
    }
    public List<DEEvent> detectError(ErrorType[] ets) {
        List<DEEvent> errors = new ArrayList<DEEvent>();

        // FLUSH, INVALID, INSUFFICIENT
        for (DEEvent e : events) {
            e.flushErrors();
            if (e.isInvalid()) {
                errors.add(e);
            }
        }
        mergeUniquely(errors, insuff());

        // STATISTICAL
        for (ErrorType et : ets) {
            switch (et) {
                case ACT_DUR_STD:
                    mergeUniquely(errors, actDurStd());
                    break;
                case ACT_DUR_KNN:
                    mergeUniquely(errors, actDurKnn());
                    break;
                case ACT_DUR_CLUST:
                    mergeUniquely(errors, actDurClust());
                    break;
                case ACT_TIME_STD:
                    mergeUniquely(errors, actTimeStd());
                    break;
                case ACT_TIME_KNN:
                    mergeUniquely(errors, actTimeKnn());
                    break;
                case CASE_STD:
                    mergeUniquely(errors, caseStd());
                    break;
                case CASE_RANGE:
                    mergeUniquely(errors, caseRange());
                    break;
                default:
                    break;
            }
        }

        errors.sort(new Comparator<DEEvent>() {
            @Override
            public int compare(DEEvent a, DEEvent b) {
                return ((Integer) a.index()).compareTo(((Integer) b.index()));
            }
        });
        return errors;
    }

    private static <T> void mergeUniquely(List<T> a, List<T> b) {
        for (T t : b) {
            if (!a.contains(t)) {
                a.add(t);
            }
        }
    }
    
    public List<DEEvent> invError(){
        List<DEEvent> errors = new LinkedList<DEEvent>();
        for (DEEvent e : events) {
            e.flushErrors();
            if (e.isInvalid()) {
                errors.add(e);
            }
        }
        return errors;
    }
    
    public List<DEEvent> insuffError(){
        List<DEEvent> errors = new LinkedList<DEEvent>();
        mergeUniquely(errors, insuff());
        for(DEEvent e : errors){
            if(invError().contains(e)) errors.remove(e);
        }
        return errors;
    }
    
    public void loosenThreshold(){
        this.insuffThresh  = 20;
        this.actdurSTDbnd[0]  = -2;
        this.actdurSTDbnd[1]  = 2;
        this.actdurKNNmax     = 2;
        this.actdurCLUSTbnd[0] = -2;
        this.actdurCLUSTbnd[1] = 2;
        this.actdurCLUSTtest = 5;
        this.acttimeSTDbnd[0] = -2;
        this.acttimeSTDbnd[1] = 2;
        this.acttimeKNNmax    = 2;
        this.caseSTDbnd[0]     = -2;
        this.caseSTDbnd[1]     = 2;
        this.caseRANGEbnd[0]   = -2;
        this.caseRANGEbnd[1]   = 2;
    }
    
    public void resetThreshold(){
        this.insuffThresh  = 30;
        this.actdurSTDbnd[0]  = -2.5;
        this.actdurSTDbnd[1]  = 2.5;
        this.actdurKNNmax     = 2.5;
        this.actdurCLUSTbnd[0] = -2.5;
        this.actdurCLUSTbnd[1] = 2.5;
        this.actdurCLUSTtest = 5;
        this.acttimeSTDbnd[0] = -2;
        this.acttimeSTDbnd[1] = 2;
        this.acttimeKNNmax    = 1.5;
        this.caseSTDbnd[0]     = -1.5;
        this.caseSTDbnd[1]     = 2;
        this.caseRANGEbnd[0]   = -2;
        this.caseRANGEbnd[1]   = 2;
    }
    
    public List<DEEvent> DurError(){
        List<DEEvent> errors = new LinkedList<DEEvent>();
        mergeUniquely(errors, actDurStd());
        mergeUniquely(errors, actDurKnn());
        mergeUniquely(errors, actDurClust());
        return errors;
    }
    public List<DEEvent> TimeError(){
        List<DEEvent> errors = new LinkedList<DEEvent>();
        mergeUniquely(errors, actTimeStd());
        mergeUniquely(errors, actTimeKnn());
        return errors;
    }
    public List<DEEvent> CaseError(){
        List<DEEvent> errors = new LinkedList<DEEvent>();
        mergeUniquely(errors, caseStd());
        mergeUniquely(errors, caseRange());
        return errors;
    }
    private List<DEEvent> insuff() {
        List<DEEvent> errors = new LinkedList<DEEvent>();

        for (DEActivity act : acts) {
            if (act.isInsufficient()) {
                for (DEEvent e : act) {
                    e.mark(ErrorType.INSUFF, 1.0);
                    errors.add(e);
                }
            }
        }

        return errors;
    }
    private List<DEEvent> actDurStd() {
        List<DEEvent> errors = new LinkedList<DEEvent>();

        long avg, std;
        double z = 0;
        for (DEActivity act : acts) {
            avg = act.averageDuration();
            std = act.sigmaDuration();
            for (DEEvent e : act) {
                if (!e.isInvalid() && !e.isInsufficient()) {
                    z = ((double) e.duration() - avg) / std;
                    if (z <= actdurSTDbnd[0] || actdurSTDbnd[1] <= z) {
                        e.mark(ErrorType.ACT_DUR_STD, z);
                        errors.add(e);
                    }
                }
            }
        }

        return errors;
    }
    private List<DEEvent> actDurKnn() {
        List<DEEvent> errors = new LinkedList<DEEvent>();

        Map<DEEvent, Long> knn;
        long avg, std;
        double z = 0;
        int n;
        for (DEActivity act : acts) {
            if (!act.isInsufficient()) {
                knn = act.knn((int) Math.sqrt(act.size()));

                // CALCULATE AVERAGE KNN
                avg = 0;
                n = 0;
                for (DEEvent e : knn.keySet()) {
                    if (!e.isInvalid()) {
                        avg += knn.get(e);
                        n++;
                    }
                }
                avg /= n;

                // CALCULATE STD KNN
                std = 0;
                for (DEEvent e : knn.keySet()) {
                    if (!e.isInvalid()) {
                        std += Math.pow(knn.get(e) - avg, 2);
                    }
                }
                std = (long) Math.sqrt(std / n);

                if (std == 0) {
                    continue;
                }

                // CALCULATE Z-SCORE & MARK
                for (DEEvent e : knn.keySet()) {
                    if (!e.isInvalid()) {
                        z = ((double) knn.get(e) - avg) / std;
                        if (actdurKNNmax <= z) {
                            e.mark(ErrorType.ACT_DUR_KNN, z);
                            errors.add(e);
                        }
                    }
                }
            }
        }

        return errors;
    }
    private List<DEEvent> actDurClust() {
        List<DEEvent> errors = new LinkedList<DEEvent>();

        // INIT CLUSTERERS
        Map<DEActivity, DEKMeansClusterer> kmeans
                = new HashMap<DEActivity, DEKMeansClusterer>();
        for (DEActivity act : acts) {
            if (!act.isInsufficient()) {
                kmeans.put(act, new DEKMeansClusterer(act.events()));
            }
        }

        // BEGIN CLUSTERING
        DEKMeansClusterer km;
        int appClust;
        double[][] cents;
        int[] belong;
        ArrayList<DEEvent>[] clusts;
        for (DEActivity act : kmeans.keySet()) {
            km = kmeans.get(act);
            appClust = km.appropriateClust(actdurCLUSTtest);
            cents = km.centroids(appClust);
            belong = km.clustBelongs(cents);

            // SPLIT BASED ON CLUSTER BELONGED
            clusts = new ArrayList[appClust];
            for (int c = 0; c < appClust; c++) {
                clusts[c] = new ArrayList<DEEvent>();
            }
            for (int e = 0; e < km.events().size(); e++) {
                clusts[belong[e]].add(km.events().get(e));
            }

            // DETECT INTRA-CLUSTER OUTLIERS
            for (int c = 0; c < appClust; c++) {
                long avg = 0;
                for (DEEvent e : clusts[c]) {
                    avg += e.duration();
                }
                avg /= clusts[c].size();

                long std = 0;
                for (DEEvent e : clusts[c]) {
                    std += Math.pow(e.duration() - avg, 2);
                }
                if (std == 0) {
                    continue;
                }
                std = (long) Math.sqrt(std / clusts[c].size());

                double z = 0;
                for (DEEvent e : clusts[c]) {
                    z = ((double) e.duration() - avg) / std;
                    if (z <= actdurCLUSTbnd[0] || actdurCLUSTbnd[1] <= z) {
                        e.mark(ErrorType.ACT_DUR_CLUST, z);
                        errors.add(e);
                    }
                }
            }
        }

        return errors;
    }
    private List<DEEvent> actTimeStd() {
        List<DEEvent> errors = new LinkedList<DEEvent>();

        long avg, std;
        double z[] = new double[2];
        for (DECase cas : cases) {
            avg = cas.averageStampTime();
            std = cas.sigmaStampTime();
            for (DEEvent e : cas) {
                if (!e.isInvalid()) {
                    z[0] = ((double) e.start() - avg) / std;
                    if (z[0] <= acttimeSTDbnd[0] || acttimeSTDbnd[1] <= z[0]) {
                        z[1] = ((double) e.end() - avg) / std;
                        if (z[1] <= acttimeSTDbnd[0] || acttimeSTDbnd[1] <= z[1]) {
                            e.mark(ErrorType.ACT_TIME_STD,
                                    Math.abs(z[0]) > Math.abs(z[1]) ? z[0] : z[1]);
                            errors.add(e);
                        }
                    }
                }
            }
        }

        return errors;
    }
    private List<DEEvent> actTimeKnn() {
        List<DEEvent> errors = new LinkedList<DEEvent>();

        Map<DEEvent, Long[]> knn;
        long avg, std;
        double[] z;
        int n;
        for (DECase cas : cases) {
            if (cas.caseID() != null) {
                knn = cas.knnStamp((int) Math.sqrt(2 * cas.size()));

                // CALCULATE AVERAGE KNN
                avg = 0;
                n = 0;
                for (DEEvent e : knn.keySet()) {
                    if (!e.isInvalid()) {
                        avg += knn.get(e)[0] + knn.get(e)[1];
                        n++;
                    }
                }
                avg /= 2 * n;

                // CALCULATE STD KNN
                std = 0;
                for (DEEvent e : knn.keySet()) {
                    if (!e.isInvalid()) {
                        std += Math.pow(knn.get(e)[0] - avg, 2)
                                + Math.pow(knn.get(e)[1] - avg, 2);
                    }
                }
                std = (long) Math.sqrt(std / (2 * n));

                if (std == 0) {
                    continue;
                }

                // CALCULATE Z-SCORE & MARK
                z = new double[2];
                for (DEEvent e : knn.keySet()) {
                    if (!e.isInvalid()) {
                        z[0] = ((double) knn.get(e)[0] - avg) / std;
                        if (acttimeKNNmax <= z[0]) {
                            z[1] = ((double) knn.get(e)[1] - avg) / std;
                            if (acttimeKNNmax <= z[1]) {
                                e.mark(ErrorType.ACT_TIME_KNN,
                                        Math.abs(z[0]) > Math.abs(z[1]) ? z[0] : z[1]);
                                errors.add(e);
                            }
                        }
                    }
                }
            }
        }

        return errors;
    }
    private List<DEEvent> caseStd() {
        List<DEEvent> errors = new LinkedList<DEEvent>();

        long avgstd = 0;
        long[] stds = new long[cases.size()];
        int i = 0;
        for (DECase cas : cases) {
            stds[i] = cas.sigmaMidTime();
            avgstd += stds[i];
            i++;
        } avgstd /= i;

        long stdstd = 0;
        for (int j = 0; j < i; j++) {
            stdstd += Math.pow(stds[j] - avgstd, 2);
        } stdstd = (long) Math.sqrt(stdstd / i);

        double z = 0;
        Double[] toMark = new Double[i];
        for (int j = 0; j < i; j++) {
            z = ((double) stds[j] - avgstd) / stdstd;
            if (z <= caseSTDbnd[0] || caseSTDbnd[1] <= z) {
                toMark[j] = z;
            }
        }

        i = 0;
        for (DECase cas : cases) {
            if (toMark[i] != null) {
                for (DEEvent e : cas) {
                if (!e.isInvalid()) {
                    e.mark(ErrorType.CASE_STD, toMark[i]);
                    errors.add(e);
                }}
            }
            i++;
        }

        errors.sort(new Comparator<DEEvent>() {
            @Override
            public int compare(DEEvent a, DEEvent b) {
                return a.caseID().compareTo(b.caseID());
            }
        });

        return errors;
    }
    private List<DEEvent> caseRange() {
        List<DEEvent> errors = new LinkedList<>();
        
        long min, max;
        long avg = 0;
        int n = 0;
        Map<DECase, Long> ranges = new HashMap<>();
        for (DECase cas: cases) {
            ranges.put(cas, cas.rangeStampTime());
            avg += ranges.get(cas);
            n++;
        } avg /= n;
        
        long std = 0;
        for (DECase cas: cases) {
            std += Math.pow(ranges.get(cas)-avg, 2);
        } std = (long) Math.sqrt(std/n);
        
        //System.out.println("avg: "+DEEventLog.g.format(new Date(avg)));
        //System.out.println("std: "+DEEventLog.g.format(new Date(std)));
        
        double z = 0;
        for (DECase cas: cases) {
            z = ((double) ranges.get(cas)-avg)/std;
            if (z <= caseRANGEbnd[0] || caseRANGEbnd[1] <= z) {
                for (DEEvent e: cas) {
                if (!e.isInvalid()) {
                    e.mark(ErrorType.CASE_RANGE, z);
                    errors.add(e);
                }}
                
                //System.out.println(cas.events().get(0).caseID() + ": " + z + ": "
                //        + DEEventLog.g.format(new Date(ranges.get(cas))));
            }
        }
    
        return errors;
    }
    
    @Override
    public String toString() {
        return this.toString(0, 150);
    }
    public String toString(int x, int y) {
        String s = header + "\n";
        for (DEEvent e : events) {
            if (x <= e.index() && e.index() <= y) {
                s += e + "\n";
            }
        }
        return s;
    }
    public String toString(Integer caseID) {
        return cases.get(caseID).toString();
    }
    public String toString(String activity) {
        return acts.get(activity).toString();
    }

    public static void main(String[] args)
            throws BiffException, IOException, RowsExceededException, WriteException {
        // CREATES EVENTLOG BASED ON EXCEL FILE; SAMPLE PROVIDED
//        DEEventLog eventLog = new DEEventLog("timestamps_74 - Copy.xls");
        DEEventLog eventLog = new DEEventLog("171traces_Corrected.csv");       
        // GETS ALL EVENT ERRORS; YOU HAVE OPTIONS ON WHAT ERRORS TO DETECT
        List<DEEvent> errors = eventLog.detectError();

        // PRINTS ERRORS IN CONSOLE FOR DEBUGGING
        System.out.println(header);
        for (DEEvent e : errors) {
            System.out.println(e);
        }

        // EXPORTS TO EXCEL FILE
        //eventLog.exportExcel("analysis_74.xls");
        // EXPORTS TO TXT FILE
        //eventLog.txtAllCases("test.txt");
        // PRINTS OUT FIRST 150 IN EVENTLOG; SEE OTHER toString() METHODS
        //System.out.println(eventLog.toString());
    }

    public void txtAllCases(String filename)
            throws FileNotFoundException, UnsupportedEncodingException {

        events.sort(new Comparator<DEEvent>() {
            @Override
            public int compare(DEEvent a, DEEvent b) {
                return ((Integer) a.index()).compareTo(((Integer) b.index()));
            }
        });

        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        for (DECase cas : cases) {
            writer.println(cas.toString()
                    .replaceAll("\n", System.lineSeparator()));
        }
        writer.close();
    }
    public void exportExcel(String filename)
            throws IOException, RowsExceededException, WriteException {
        WritableWorkbook wb = Workbook.createWorkbook(new File(filename));
        WritableSheet sheet = wb.createSheet("Sheet1", 0);

        List<WritableCell> cells = new LinkedList<WritableCell>();
        cells.add(new Label(0, 0, "CASEID"));
        cells.add(new Label(1, 0, "START"));
        cells.add(new Label(2, 0, "END"));
        cells.add(new Label(3, 0, "MIDTIME"));
        cells.add(new Label(4, 0, "DURATION"));
        cells.add(new Label(5, 0, "ACTIVITY"));

        for (int i = 0; i < ErrorType.values().length; i++) {
            cells.add(new Label(i + 6, 0, ErrorType.values()[i].toString()));
        }

        int i;
        for (DEEvent e : events) {
            i = e.index() - 1;
            try {
                cells.add(new Number(0, i, e.caseID()));
            } catch (NullPointerException f) {
            };
            try {
                cells.add(new DateTime(1, i, new Date(e.start())));
            } catch (NullPointerException f) {
            };
            try {
                cells.add(new DateTime(2, i, new Date(e.end())));
            } catch (NullPointerException f) {
            };
            try {
                cells.add(new DateTime(3, i, new Date(e.midTime())));
            } catch (NullPointerException f) {
            };
            try {
                cells.add(new DateTime(4, i, new Date(e.duration())));
            } catch (NullPointerException f) {
            };
            try {
                cells.add(new Label(5, i, e.activity()));
            } catch (NullPointerException f) {
            };

            for (int j = 0; j < ErrorType.values().length; j++) {
                cells.add(new Number(j + 6, i, e.errors()[j] == null ? 0 : e.errors()[j]));
            }
        }

        for (WritableCell cell : cells) {
            sheet.addCell(cell);
        }

        wb.write();
        wb.close();

        System.out.println("EXPORTED TO: " + filename);
    }
    
    public void readFromCSV(String filename){
        try {
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader lnr = new BufferedReader(new InputStreamReader(fstream));
            String line = lnr.readLine();
            String[] cols = line.split(",");
            int COL_NUM = cols.length; 
            int tmpIdx = 0;
            while (( line = lnr.readLine()) != null) {
                String[] attr = line.split(",");
//                for(int i = 0; i < COL_NUM-1; i++){
//                    System.out.println("attr"+i+":"+attr[i]);
//                }
                Integer tmpID = (attr[0]!=null || !attr[0].isEmpty()) ? Integer.valueOf(attr[0]) : null;
//                System.out.print("ID: "+tmpID);
                String tmpAct = (attr[1]!=null || !attr[1].isEmpty()) ? attr[1] : null;
//                System.out.print("\tACTIVITY: "+tmpAct);
                Long tmpStr = (attr[2]!="" && !attr[2].isEmpty()) ? Long.valueOf(toS(attr[2])) : null;
//                System.out.print("\tSTART TIME: "+tmpStr);
                Long tmpEnd = (attr.length >= 4 && attr[3] != "") ? Long.valueOf(toS(attr[3])) : null; 
//                System.out.println("\tEND TIME: "+tmpEnd);
                this.add(new DEEvent(tmpID,tmpAct,tmpStr,tmpEnd,tmpIdx++));
            }
        }catch ( Exception e ) {
            e.printStackTrace();
        }
    }
        
    private int toS(String s) {
        String[] s1 = s.split(" ");
        String[] minSecond = s1[0].split(":");
        int hours = Integer.parseInt(minSecond[0]);
        int mins = Integer.parseInt(minSecond[1]);
        double seconds = Double.parseDouble(minSecond[2]);
        int ampm = 0;
        if(s1.length > 1) {
            ampm = (s1[1].equals("PM"))? 12 * 3600 : 0;
            if(ampm > 0 && hours == 12) hours = 0;
        }
        int minInSecond = (int) (hours * 3600 + mins * 60 + seconds + ampm);
        return (int) (minInSecond + seconds);
    }
    
    public int getEventNum(){
        return this.eventNum;
    }
}
