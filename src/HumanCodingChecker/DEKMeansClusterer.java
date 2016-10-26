package HumanCodingChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DEKMeansClusterer {
    private double clusterInitVariation = 0.1;
    private int maxIterations = 1000;
    private List<DEEvent> events;
    private double[][] data;

    public DEKMeansClusterer(double[][] data) {
        this.data = data;
    }
    public DEKMeansClusterer(List<DEEvent> events) {
        for (int i = events.size() - 1; i >= 0; i--) {
            if (events.get(i).isInvalid()) {
                events.remove(events.get(i));
            }
        }

        if (events.size() < DEEventLog.insuffThresh) {
            throw new IllegalArgumentException("there are not enough"
                    + " events to cluster");
        }

        this.data = new double[events.size()][1];
        this.events = events;
        for (int i = 0; i < events.size(); i++) {
            this.data[i][0] = events.get(i).duration();
        }
    }

    public List<DEEvent> events() {
        return events;
    }
    public double[][] data() {
        return data;
    }
    public double[][] centroids(int clust) {
        if (clust == 1) {
            double[][] ans = new double[1][dim()];
            List<double[]> pts = new ArrayList<double[]>();
            for (double[] d : data) {
                pts.add(d);
            }
            ans[0] = average(pts);
            return ans;
        } else if (clust >= pts()) {
            return data;
        }

        // MIN AND MAX INIT POINTS
        double[] min = new double[dim()];
        double[] max = new double[dim()];
        for (int i = 0; i < dim(); i++) {
            min[i] = Integer.MAX_VALUE;
            max[i] = Integer.MIN_VALUE;
        }
        for (int d = 0; d < dim(); d++) {
            for (int p = 0; p < pts(); p++) {
                if (data[p][d] < min[d]) {
                    min[d] = data[p][d];
                }
                if (data[p][d] > max[d]) {
                    max[d] = data[p][d];
                }
            }
        }

        // INCREMENT OF INIT POINTS
        double[] inc = new double[dim()];
        for (int i = 0; i < dim(); i++) {
            inc[i] = (max[i] - min[i]) / (clust - 1);
        }

        // INITIALIZE CENTROIDS
        Random r = new Random();
        double[][] cent = new double[clust][dim()];
        for (int c = 0; c < clust; c++) {
            for (int d = 0; d < dim(); d++) {
                cent[c][d] = min[d] + c * inc[d];
                if (c != 0 && c != clust - 1) {
                    cent[c][d]
                            += (r.nextDouble() * 2 - 1) * inc[d]
                            * clusterInitVariation;
                }
            }
        }

        @SuppressWarnings("unchecked")
        List<double[]>[] t = new ArrayList[clust];
        for (int c = 0; c < clust; c++) {
            t[c] = new ArrayList<double[]>();
        }
        for (double[] pt : data) {
            t[0].add(pt);
        }

        boolean moved = true;
        int[] clen = new int[clust];
        double[] pt;
        int cc;
        double[] avg;

        int counter = 0;
        while (moved && counter <= maxIterations) {
            // DISTRIBUTE POINTS
            moved = false;
            for (int c = 0; c < clust; c++) {
                clen[c] = t[c].size();
            }
            for (int c = 0; c < clust; c++) {
                for (int pic = clen[c] - 1; pic >= 0; pic--) {
                    pt = t[c].get(pic);
                    cc = closestCluster(pt, cent);
                    if (cc != c) {
                        t[cc].add(t[c].remove(pic));
                        moved = true;
                    }
                }
            }

            // UPDATE CENTROIDS
            for (int c = 0; c < clust; c++) {
                avg = average(t[c]);
                if (avg != null) {
                    cent[c] = average(t[c]);
                }
            }

            // RE-PURPOSE EMPTY CLUSTERS
            for (int c = 0; c < clust; c++) {
                if (t[c].isEmpty()) {
                    int i = r.nextInt(clust);
                    while (t[i].size() <= 1) {
                        i = r.nextInt(clust);
                    }
                    t[c].add(t[i].remove(r.nextInt(t[i].size())));
                    moved = true;
                }
            }

            counter++;
        }

        // CATCH EMPTY CLUSTERS
        boolean[] isClust = new boolean[clust];
        int finalClusts = 0;
        for (int c = 0; c < clust; c++) {
            if (!t[c].isEmpty()) {
                isClust[c] = true;
                finalClusts++;
            }
        }

        double[][] ans = new double[finalClusts][dim()];
        int ansCounter = 0;
        for (int c = 0; c < clust; c++) {
            if (isClust[c]) {
                ans[ansCounter] = cent[c];
                ansCounter++;
            }
        }

        return ans;
    }
    private int closestCluster(double[] pt, double[][] cent) {
        double min = Integer.MAX_VALUE;
        double dist = Integer.MAX_VALUE;
        int clust = 0;

        for (int c = 0; c < cent.length; c++) {
            dist = 0;
            for (int dim = 0; dim < pt.length; dim++) {
                dist += Math.pow(pt[dim] - cent[c][dim], 2);
            }
            if (dist < min) {
                min = dist;
                clust = c;
            }
        }

        return clust;
    }
    public double[] average(List<double[]> pts) {
        if (pts.isEmpty()) {
            return null;
        }

        double[] ans = new double[dim()];
        for (double[] pt : pts) {
            for (int dim = 0; dim < dim(); dim++) {
                ans[dim] += pt[dim];
            }
        }
        for (int dim = 0; dim < dim(); dim++) {
            ans[dim] /= pts.size();
        }
        return ans;
    }
    public double average(double[] pts) {
        double sum = 0;
        for (double p : pts) {
            sum += p;
        }
        return sum / pts.length;
    }
    public double standardDeviation(double[] pts) {
        double avg = average(pts);
        double var = 0;

        for (double p : pts) {
            var += Math.pow(p - avg, 2);
        }

        return Math.sqrt(var) / (pts.length - 1);
    }

    public int appropriateClust(int testClust) {
        double[][] sils = new double[testClust][data.length];
        double[] stds = new double[testClust];

        boolean bad = false;
        for (int c = 1; c <= testClust; c++) {
            sils[c - 1] = silhouette(c);
            bad = false;
            for (double s : sils[c - 1]) {
                if (s <= 0) {
                    bad = true;
                    break;
                }
            }
            if (!bad) {
                stds[c - 1] = standardDeviation(sils[c - 1]);
            } else {
                stds[c - 1] = Double.MAX_VALUE;
            }
        }

        int appClust = 1;
        double minStd = Double.MAX_VALUE;
        for (int c = 1; c <= testClust; c++) {
            if (stds[c - 1] < minStd) {
                minStd = stds[c - 1];
                appClust = c;
            }
        }

        return appClust;
    }
    public int[] clustBelongs(double[][] cents) {
        int[] belong = new int[pts()];
        for (int p = 0; p < belong.length; p++) {
            belong[p] = closestCluster(data[p], cents);
        }
        return belong;
    }

    public double[] silhouette(int clust) {
        return silhouette(this.centroids(clust));
    }
    public double[] silhouette(double[][] cent) {
        int[] closest = new int[data.length];
        int[] nextest = new int[data.length];
        for (int p = 0; p < data.length; p++) {
            closest[p] = closestCluster(data[p], cent);
            nextest[p] = nextClosestCluster(data[p], cent);
        }

        double[] sil = new double[data.length];
        double a;
        int as;
        double b;
        int bs;
        for (int dpt = 0; dpt < data.length; dpt++) {
            a = 0;
            as = 0;
            b = 0;
            bs = 0;
            for (int p = 0; p < data.length; p++) {
                if (closest[p] == closest[dpt]) {
                    a += distance(data[p], data[dpt]);
                    as++;
                } else if (closest[p] == nextest[dpt]) {
                    b += distance(data[p], data[dpt]);
                    bs++;
                }
            }
            as--;
            a = a / as;
            b = b / bs;
            sil[dpt] = (b - a) / Math.max(a, b);
        }

        return sil;
    }
    private int nextClosestCluster(double[] pt, double[][] cent) {
        if (cent.length < 2) {
            return 0;
        }

        int closest = closestCluster(pt, cent);
        double[][] ct = cent.clone();
        for (int c = 0; c < cent.length; c++) {
            ct[c] = cent[c].clone();
        }
        for (int d = 0; d < pt.length; d++) {
            ct[closest][d] = Integer.MAX_VALUE;
        }

        double min = Integer.MAX_VALUE;
        double dist = Integer.MAX_VALUE;
        int clust = 0;

        for (int c = 0; c < ct.length; c++) {
            dist = 0;
            for (int dim = 0; dim < pt.length; dim++) {
                dist += Math.pow(pt[dim] - ct[c][dim], 2);
            }
            if (dist <= min) {
                min = dist;
                clust = c;
            }
        }

        return clust;
    }
    private double distance(double[] x, double[] y) {
        double dist = 0;
        for (int d = 0; d < x.length; d++) {
            dist += Math.pow(x[d] - y[d], 2);
        }
        return Math.sqrt(dist);
    }

    public int pts() {
        return data.length;
    }
    public int dim() {
        return data[0].length;
    }

    public static void main(String[] args) {
        //System.out.println(1/0);
        /*/ NEXT CLOSEST CLUSTER
		double[] pt = new double[]{0.5};
		double[][] clust = new double[][]{
				{0},
				{1},
				{2},
				{3},
		};
		clust = new double[30][1];
		for (int c = 0; c < clust.length; c++) {
			clust[c][0] = (new Random()).nextDouble();
		}
		for (int c = 0; c < clust.length; c++) {
			System.out.println(c + "\t" + clust[c][0]);
		}
		System.out.println((new MLDataset(clust)).
				closestCluster(pt, clust));
		System.out.println((new MLDataset(clust)).
				nextClosestCluster(pt, clust));
		//*/

 /*/ GENERAL TESTING
		double[][] data = new double[][]{
				{0, 0},
				{0, 1},
				{1, 0},
				{1, 1},
		}; //*/
        double[][] data = new double[30][1];
        Random r = new Random(12345);
        for (double[] x : data) {
            for (int y = 0; y < x.length; y++) {
                x[y] = r.nextDouble();
            }
        }

        DEKMeansClusterer mld = new DEKMeansClusterer(data);
        double[][] cent = mld.centroids(5);
        for (double[] c : cent) {
            for (double d : c) {
                System.out.printf("%f\t", d);
            }
            System.out.println();
        }
        System.out.println(cent.length + " effective clusters");

        double[] sil = mld.silhouette(cent);
        System.out.println();
        System.out.println(data[1][0]);
        double[] t = new double[]{1};
        System.out.println(mld.closestCluster(t, cent));
        for (int p = 0; p < data.length; p++) {
            System.out.printf(
                    "%f\tC-%d\tN-%d\tS = %f\n",
                    data[p][0],
                    mld.closestCluster(data[p], cent),
                    mld.nextClosestCluster(data[p], cent),
                    sil[p]
            );
        }
        //*/
    }
}
