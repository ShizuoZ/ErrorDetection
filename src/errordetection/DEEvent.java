/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package errordetection;

import java.util.Date;

/**
 *
 * @author edward
 */
public class DEEvent {
    private Integer caseID;
    private String activity;
    private Long start;
    private Long end;
    private int index;
    private Double[] errors;
    private double durstd;
    private double durknn;
    private double caseRangeSTD;

    public DEEvent(Integer caseID, String activity,
            Long start, Long end, int index) {
        this.caseID = caseID;
        this.activity = activity;
        this.start = start;
        this.end = end;
        this.index = index;
        this.durstd = 0.0;
        this.durknn = 0.0;
        
        errors = new Double[ErrorType.values().length];
        checkInvalid();
    }

    public Integer caseID() {
        return caseID;
    }
    public String activity() {
        return activity;
    }
    public Long start() {
        return start;
    }
    public Long end() {
        return end;
    }
    public Long duration() {
        if (start == null || end == null) {
            return null;
        } else {
            return end - start;
        }
    }
    public Long midTime() {
        if (start == null || end == null) {
            return null;
        } else {
            return (start + end) / 2;
        }
    }
    public int index() {
        return index;
    }
    
    public Double[] errors() {
        return errors;
    }
    public boolean isInvalid() {
        return errors[ErrorType.INVALID.code] != null;
    }
    public boolean isInsufficient() {
        return errors[ErrorType.INSUFF.code] != null;
    }
    private boolean checkInvalid() {
        if (caseID == null || activity == null
                || start == null || start < 0
                || end == null || end < 0
                || start > end || duration() >= DEEventLog.milliday) {
            errors[ErrorType.INVALID.code] = 1.0;
            return true;
        }
        return false;
    }
    public void mark(ErrorType e, Double conf) {
        mark(e.code, conf);
    }
    public void mark(int e, Double conf) throws IllegalArgumentException {
        try {
            errors[e] = conf;
        } catch (IndexOutOfBoundsException f) {
            throw new IllegalArgumentException(e + " is invalid error code");
        }
    }
    public void flushErrors() {
        for (int i = 0; i < errors.length; i++) {
            errors[i] = null;
        }
        checkInvalid();
    }

    @Override
    public String toString() {
        String s = index + "\t";
        String tri = "   ";
        String time = "           ";

        for (int i = 0; i < errors.length; i++) {
            if (errors[i] != null) {
                s += i;
            } else {
                s += " ";
            }
        } s += tri;

        if (caseID != null) {
            s += caseID;
        } else {
            s += "       ";
        } s += tri;

        if (DEEventLog.f == null) {
            DEEventLog.init();
        }

        if (start != null) {
            s += DEEventLog.g.format(new Date(start));
        } else {
            s += time;
        } s += tri;

        if (end != null) {
            s += DEEventLog.g.format(new Date(end));
        } else {
            s += time;
        } s += tri;

        if (midTime() != null) {
            s += DEEventLog.g.format(new Date(midTime()));
        } else {
            s += time;
        } s += tri;

        if (duration() != null) {
            s += DEEventLog.g.format(new Date(duration()));
        } else {
            s += time;
        } s += tri;

        if (activity != null) {
            s += activity;
        }

        return s;
    }
    
    public void setStd(double STD){
        this.durstd = STD;
    }
    
    public double getStd(){
        return durstd;
    }
    
    public void setKnn(double knn){
        this.durknn = knn;
    }
    
    public double getKnn(){
        return durknn;
    }    
    
    public void setCaseRangeSTD(double caseRangeSTD){
        this.caseRangeSTD = caseRangeSTD;
    }
    
    public double getCaseRangeSTD(){
        return caseRangeSTD;
    }   
}
