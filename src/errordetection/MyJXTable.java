/*
 * SimpleJXTableDemo.java is a 1.45 application that requires no other files. It is derived from
 * SimpleTableDemo in the Swing tutorial.
 */
package errordetection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Line;
import javax.imageio.ImageIO;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import jxl.read.biff.BiffException;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.AbstractActionExt;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MyJXTable  {
    private static final Color MARKER_COLOUR = Color.GREEN;
    private static final Color ERROR_COLOUR = Color.RED;
    private static final Color WARNING_COLOUR = Color.YELLOW;
    private static int BAR_NUMS = 100;
    private static Integer[] INDEX_ARRAY = new Integer[]{4, 15, 32, 36, 58, 74, 92};
    private static List<Integer> ERROR_INDEX_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List<Integer> DUR_STD_ERRORS_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List<Integer> DUR_STD_WARNINGS_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List<Integer> DUR_KNN_ERRORS_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List<Integer> DUR_KNN_WARNINGS_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List<Integer> CASE_ERROR_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List<Integer> INSUFF_ERROR_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static JXTable jxTable = new JXTable();
    private JFileChooser fileChooser;
    private JPanel config;
    private JPanel parameter;
    private ChartPanel charts;
    private static String address;
    private static SampleTableModel model;
    private static ListIndexBar bar;
    private static JXFrame frame = new JXFrame("Error Detection", true);
    private static DEEventLog deeventLog;  
    private static double currentstd = 0;
    private static double currentknn = 0;
    private static DEEvent currentevent;
    private HistogramDataset dataset = new HistogramDataset();
    private JFreeChart chart;
    private JComponent content;
    private MouseAdapter a1;
    private MouseAdapter a2;
    
    public MyJXTable() {
        this.address = "/Day&Night.csv";
        this.BAR_NUMS = 100;
        this.bar = new ListIndexBar(BAR_NUMS);
    }
    public MyJXTable(int num, String filename) {
        this.BAR_NUMS = num;
        this.address = filename;
        this.bar = new ListIndexBar(BAR_NUMS);
    }
    
    private MyJXTable(int num, String filename, DEEventLog e){
        this.BAR_NUMS = num;
        this.address = filename;
        this.deeventLog = e;
    }      
 
    private JComponent initUI() {
        content = new JPanel(new BorderLayout());
//        content.setBorder(BorderFactory.createTitledBorder("Error Det"));
        jxTable = initTable();
        configureJXTable(jxTable);
        parameter = new JPanel();
//        charts = new ChartPanel();
        
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(jxTable);
        scrollPane.setPreferredSize(new Dimension(400,400));
        JComponent tabbedPane = new JTabbedPane(3);
        JComponent tablePane = new JPanel(new BorderLayout());
        tablePane.add(scrollPane, BorderLayout.CENTER);
        tablePane.add(bar, BorderLayout.EAST);
        tablePane.setBackground(Color.WHITE);
        tabbedPane.setBorder(BorderFactory.createTitledBorder("Table Panel"));
        tabbedPane.add("EventView",tablePane);
        tabbedPane.setPreferredSize(new Dimension(400,400));
        tabbedPane.setBackground(Color.WHITE);
        //Add the scroll pane to this panel.
        content.add(tabbedPane, BorderLayout.WEST);
        content.add(initChartPanel(), BorderLayout.CENTER);
        content.add(initConfigPanel(jxTable), BorderLayout.NORTH);
        return content;
    }
    
    /** Initialize our JXTable; this is standard stuff, just as with JTable */
    private JXTable initTable() {
        // boilerplate table-setup; this would be the same for a JTable
        jxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // if we would want a per-table ColumnFactory we would have
        // to set it here, before setting the model
        // table.setColumnFactory(myVerySpecialColumnFactory);
        model = new SampleTableModel();
        jxTable.setModel(model);
//        model.loadData();
//        System.out.println("filename: " + this.address);
        if(address!=null){
            String[] folders = address.split("/");
            String filename = folders[folders.length - 1];
            model.loadDataFromCSV(address);
//            model.loadDataFromEventLog(deeventLog.events());
        }
        else{
            model.loadDefaultData();
        }
        jxTable.setAutoResizeMode(JXTable.AUTO_RESIZE_ALL_COLUMNS);
        return jxTable;
    }
       
    public void setFilename(String filename){
        this.address = filename;
//        this.initTable();
    }
    
    private void configureJXTable(JXTable jxTable) {
        // set the number of visible rows
        jxTable.setVisibleRowCount(30);
        // set the number of visible columns
        jxTable.setVisibleColumnCount(8);
        // Setting this flag causes the table to be scrollable right to left.
        jxTable.setHorizontalScrollEnabled(true);

        // This shows the column control on the right-hand of the header.
        // All there is to it--users can now select which columns to view
        jxTable.setColumnControlVisible(true);
     
        Comparator numberComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                Double d1 = Double.valueOf(o1 == null ? "0" : (String)o1);
                Double d2 = Double.valueOf(o2 == null ? "0" : (String)o2);
                return d1.compareTo(d2);
            }
        };
        
        jxTable.setHighlighters(
                HighlighterFactory.createSimpleStriping());
        
        jxTable.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.BLACK,
                Color.WHITE));
       
        // resize all the columns in the table to fit their contents
        // this is available as an item in the column control drop down as well, so the user can trigger it.
        int margin = 5;
        jxTable.packTable(margin);
        
        // we want the country name to always show, so we'll repack just that column
        // we can set a max size; if -1, the column is forced to be as large as necessary for the
        // text
        margin = 10;
    }
    
    /** This shows off some additional JXTable configuration, controlled by checkboxes in a Panel. */
    private JPanel initConfigPanel(final JXTable jxTable) {
        config = new JPanel();
        config.setBorder(BorderFactory.createTitledBorder("config Panel"));
        config.setBackground(Color.WHITE);
        FlowLayout fll = (FlowLayout)config.getLayout();
        fll.setAlignment(FlowLayout.LEFT);
        fll.setHgap(30);
        
        // This shows or hides the column control--note this is possible at runtime
        final JCheckBox control = new JCheckBox();
        control.setSelected(jxTable.isColumnControlVisible());
        control.setAction(new AbstractAction("Show column control") {
            public void actionPerformed(ActionEvent e) {
                jxTable.setColumnControlVisible(control.isSelected());
            }
        });
        
        // turn sorting by column on or off
        // bug: there is no API to read the current value! we will assume it is false
        final JCheckBox sorting = new JCheckBox();
        
        sorting.setSelected(!jxTable.isSortable());
        sorting.setAction(new AbstractAction("Sortable") {
            public void actionPerformed(ActionEvent e) {
                jxTable.setSortable(sorting.isSelected());
            }
        });
        
        // add checkbox for horizontal scrolling. basically, the table has an action for this,
        // and we need to link the checkbox up in both directions--so that if the property changes
        // the checkbox is updated, and vice-versa. we use an AbstractActionExt to make this easier.
        // you aren't supposed to understand this :) and yes, it will be refactored
        final JCheckBox horiz = new JCheckBox();
        
        AbstractActionExt hA = (AbstractActionExt)jxTable.getActionMap().get(JXTable.HORIZONTALSCROLL_ACTION_COMMAND);
        hA.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                
                if (propertyName.equals("selected")) {
                    Boolean selected = (Boolean)evt.getNewValue();
                    horiz.setSelected(selected.booleanValue());
                }
            }
        });
        horiz.addItemListener(hA);
        horiz.setAction(hA);
        
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
//        FileNameExtensionFilter filter = new FileNameExtensionFilter("csv");
//        fileChooser.setFileFilter(filter);
        fileChooser.setAutoscrolls(true);
        fileChooser.setApproveButtonText("Save");
        
        JButton button_ExportFile = new JButton("export");
        JButton button_ImportFile = new JButton("import");
        JButton parameterButton = new JButton("Parameter");
        JButton button_hide = new JButton("hide");
        button_ExportFile.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        button_ImportFile.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        button_hide.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        parameterButton.setFont(new java.awt.Font("Lucida Grande", 0, 10));

        button_ImportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem_ImportFileActionPerformed(evt);
            }
        });

        button_ExportFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    saveJTableAsCSVActionPerformed(evt);
                } catch (IOException ex) {
                    Logger.getLogger(MyJXTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        button_hide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parameter.setVisible(false);
                button_hide.setText("display config");
            }
        });
        
        parameterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parameter = initParameterPanel();
            }
        });
        
        String[] filterlist = {"Default","All Errors","invalid & insuff errors","Duration STD","Duration KNN","Case KNN"};
        final JComboBox filterbox = new JComboBox(filterlist);
        filterbox.setSelectedIndex(0);
        filterbox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String item = (String)filterbox.getSelectedItem();
                switch(item){
                    case "Default": 
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        break;
                    case "All Errors":
                        final HighlightPredicate myPredicate = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return ERROR_INDEX_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighter = new ColorHighlighter(
                            myPredicate,
                            ERROR_COLOUR,   // background color
                            null);       // no change in foreground color                       
                        final HighlightPredicate myPredicateMed = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return DUR_STD_WARNINGS_LIST.contains(adapter.row) ||
                                          DUR_KNN_WARNINGS_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterMed = new ColorHighlighter(
                            myPredicateMed,
                            WARNING_COLOUR,   // background color
                            null);       // no change in foreground color
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        jxTable.addHighlighter(highlighterMed);
                        jxTable.addHighlighter(highlighter);
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        bar.setForeground(ERROR_COLOUR);
                        List<Integer> warnings = new ArrayList();
                        warnings.addAll(DUR_STD_WARNINGS_LIST);
                        for(Integer i : DUR_KNN_WARNINGS_LIST){
                            if(!warnings.contains(i)) warnings.add(i);
                        }
                        bar.addMedMarkers(warnings); 
                        bar.addMarkers(ERROR_INDEX_LIST);
//                        frame.add(bar);
                        break;
                    case "invalid & insuff errors":
                        final HighlightPredicate myPredicateInv = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return INSUFF_ERROR_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterInsuff = new ColorHighlighter(
                            myPredicateInv,
                            ERROR_COLOUR,   // background color
                            null);       // no change in foreground color
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        jxTable.addHighlighter(highlighterInsuff);
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        bar.setForeground(ERROR_COLOUR);
//                        bar.addMedMarkers(INSUFF_ERROR_LIST_LIGHT); 
                        bar.addMarkers(INSUFF_ERROR_LIST);
                       
//                        frame.add(bar);
//                        frame.add(bar);
                        break;
                    case "Duration STD":
                        final HighlightPredicate myPredicateDur = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return DUR_STD_ERRORS_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterDur = new ColorHighlighter(
                              myPredicateDur,
                              ERROR_COLOUR,   // background color
                              null);       // no change in foreground color
                        final HighlightPredicate myPredicateDurLight = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return DUR_STD_WARNINGS_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterDurLight = new ColorHighlighter(
                              myPredicateDurLight,
                              WARNING_COLOUR,   // background color
                              null);       // no change in foreground color
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        jxTable.addHighlighter(highlighterDurLight);
                        jxTable.addHighlighter(highlighterDur);
                        
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        bar.setForeground(ERROR_COLOUR);
//                        bar.addLightMarkers(ERROR_INDEX_LIST);
                        
                        bar.addMedMarkers(DUR_STD_WARNINGS_LIST); 
                        bar.addMarkers(DUR_STD_ERRORS_LIST);
//                        frame.add(bar);
                        break;
                        
                    case "Duration KNN":
                        final HighlightPredicate myPredicateDurKNN = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return DUR_KNN_ERRORS_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterDurKNN = new ColorHighlighter(
                              myPredicateDurKNN,
                              ERROR_COLOUR,   // background color
                              null);       // no change in foreground color
                        final HighlightPredicate myPredicateDurKNNLight = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return DUR_KNN_WARNINGS_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterDurKNNLight = new ColorHighlighter(
                              myPredicateDurKNNLight,
                              WARNING_COLOUR,   // background color
                              null);       // no change in foreground color
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        jxTable.addHighlighter(highlighterDurKNNLight);
                        jxTable.addHighlighter(highlighterDurKNN);
                        
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        bar.setForeground(ERROR_COLOUR);
//                        bar.addLightMarkers(ERROR_INDEX_LIST);
                        
                        bar.addMedMarkers(DUR_KNN_WARNINGS_LIST); 
                        bar.addMarkers(DUR_KNN_ERRORS_LIST);
//                        frame.add(bar);
                        break;
//                    case "Case errors":
//                        final HighlightPredicate myPredicateCase = new HighlightPredicate() {
//                            @Override 
//                            public boolean isHighlighted(
//                                  Component renderer, 
//                                  ComponentAdapter adapter) {
//
//                                  return CASE_ERROR_LIST.contains(adapter.row);
//                            }
//                        };
//                        ColorHighlighter highlighterCase = new ColorHighlighter(
//                              myPredicateCase,
//                              ERROR_COLOUR,   // background color
//                              null);       // no change in foreground color
//                        final HighlightPredicate myPredicateCaseLight = new HighlightPredicate() {
//                            @Override 
//                            public boolean isHighlighted(
//                                  Component renderer, 
//                                  ComponentAdapter adapter) {
//
//                                  return CASE_ERROR_LIST_LIGHT.contains(adapter.row);
//                            }
//                        };
//                        ColorHighlighter highlighterCaseLight = new ColorHighlighter(
//                              myPredicateCaseLight,
//                              WARNING_COLOUR,   // background color
//                              null);       // no change in foreground color
//                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
//                                Color.BLACK,
//                                Color.WHITE));
//                        jxTable.addHighlighter(highlighterCaseLight);
//                        jxTable.addHighlighter(highlighterCase);
//                        bar.clearMarkers();
//                        bar.clearMedMarkers();
//                        bar.setForeground(ERROR_COLOUR);
//                        bar.addMedMarkers(CASE_ERROR_LIST_LIGHT);
//                        bar.addMarkers(CASE_ERROR_LIST);
////                        frame.add(bar);
//                        break;
                }
            }
        });
        
        String[] chartlist = {"invalid", "durstd", "durknn", "caseknn"};
        final JComboBox chartbox = new JComboBox(chartlist);
        chartbox.setSelectedIndex(0);
        chartbox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String item = (String)chartbox.getSelectedItem();
                switch(item){
                    case "durstd":
                        a1 = new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                int row = jxTable.rowAtPoint(evt.getPoint());
                                currentstd = deeventLog.events().get(row).getStd();
                                System.out.println("row:" + row + "std:" + currentstd);
                                double[] std = loadSTD();
                                double[] cur = new double[2000];
                                for(int i = 0; i < cur.length; i++) cur[i] = currentstd;
                                dataset = new HistogramDataset();
                                dataset.addSeries("current", cur, BINS/2);
                                dataset.addSeries("distribution", std, BINS/2);
                                ((XYPlot) chart.getPlot()).setDataset(dataset);
                            }
                        };
                        jxTable.addMouseListener(a1);
                        break;
                    case "durknn":
//                        double[] dur = loadDur();
//                        dataset = new HistogramDataset();
//                        dataset.addSeries("dur", dur, 1024/2);
////                        System.out.println("Knn size: " + dur.length);
//                        chart = ChartFactory.createHistogram("standard deviation", "Value",
//                            "Count", dataset, PlotOrientation.VERTICAL, true, true, false);     
//                        XYDataset dataset1 = loadDurKnn();
////                        dataset1.addSeries("knn", knn, BINS/2); 
//                        XYPlot plot = (XYPlot) charts.getChart().getPlot();
//                        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//                        XYBarRenderer render1 = new XYBarRenderer();
//                        Shape shape  = new Ellipse2D.Double(0,0,1,1);
//                        renderer.setBaseShape(shape);
//                        render1.setDefaultShadowsVisible(false);
//                        render1.setShadowXOffset(0);
//                        render1.setShadowYOffset(0);
//                        plot.setDataset(0, dataset);
//                        plot.setRenderer(0, render1); 
//                        plot.setDataset(1, dataset1);
//                        plot.setRenderer(1, renderer); 
//                        plot.setDomainPannable(true);
//                        plot.setRangePannable(true);
//                        plot.setRangeAxis(0, new NumberAxis("Series 1"));
//                        plot.setRangeAxis(1, new NumberAxis("Series 2"));
////                        plot.setDomainAxis(0, new NumberAxis("Series 1"));
//                        plot.setDomainAxis(1, new NumberAxis("Series 2"));
//                        //Map the data to the appropriate axis
//                        plot.mapDatasetToRangeAxis(0, 0);
//                        plot.mapDatasetToRangeAxis(1, 1); 
//                        plot.mapDatasetToDomainAxis(0, 0);
//                        plot.mapDatasetToDomainAxis(1, 1); 
                        content.remove(charts);
                        double std = deeventLog.knnstd;
                        XYDataset dataset1 = loadDurKnn();
                        XYSeries series2 = new XYSeries("knn value");
                        XYSeriesCollection dataset2 = new XYSeriesCollection();
                        System.out.println(deeventLog.knnstd);
                        series2.add(0,deeventLog.knnstd );
                        series2.add(40000,deeventLog.knnstd );
                        dataset2.addSeries(series2);
                        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false); 
                        chart = ChartFactory.createScatterPlot("Duration KNN", "dur",
                            "knn", dataset1, PlotOrientation.VERTICAL, true, true, false);     
                        
//                        dataset1.addSeries("knn", knn, BINS/2); 
                        XYPlot plot = (XYPlot) chart.getPlot();
                        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);;
                        Shape shape  = new Ellipse2D.Double(0,0,1,1);
                        renderer.setBaseShape(shape);
                        renderer.setSeriesPaint(0, Color.red);
                        plot.setDataset(0,dataset1);
                        plot.setDataset(1,dataset2);
                        plot.setRenderer(0,renderer); 
                        plot.setRenderer(1,renderer2);
//                        plot.setDomainPannable(true);
//                        plot.setRangePannable(true);
                        //Map the data to the appropriate axis
                        plot.mapDatasetToRangeAxis(0,0);
                        plot.mapDatasetToRangeAxis(0,0); 
                        chart.setBorderVisible(false);  
                        chart.setTitle("Duration KNN");
//                        chart.setBackgroundPaint(new Color(10,255,255,0));  
//                        chart.setBackgroundImageAlpha(0.9f);
                        charts = new ChartPanel(chart);
//                        charts.setBorder(BorderFactory.createTitledBorder("Chart Panel"));
                        charts.setMouseWheelEnabled(true);
                        charts.setPreferredSize(new Dimension(400,400));
                        jxTable.removeMouseListener(a1);                   
                        a2 = new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                int row = jxTable.rowAtPoint(evt.getPoint());
                                currentknn = deeventLog.events().get(row).getKnn();
                                System.out.println("row:" + row + "knn:" + currentknn);
                                XYSeries series3 = new XYSeries("selected");
                                XYSeriesCollection dataset2 = new XYSeriesCollection();
                                series3.add(0,currentknn);
                                series3.add(40000,currentknn);
                                dataset2.addSeries(series3);    
                                XYPlot plot = (XYPlot) chart.getPlot();
                                XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false); 
                                renderer2.setSeriesPaint(0, Color.BLACK);
                                plot.setDataset(2, dataset2);
                                plot.setRenderer(2, renderer2);
                            }
                        };
                        jxTable.addMouseListener(a2);
                        jxTable.revalidate();
                        content.add(charts, BorderLayout.CENTER);
                        content.revalidate();
                        content.repaint();
                        System.out.println("Finish");
                        break;
                }
            }
        });
        JLabel t1 = new JLabel("Files:");
        JLabel t2 = new JLabel("ErrorType:");
        JLabel t3 = new JLabel("ChartType:");
        config.add(t1);
        config.add(button_ImportFile);
        config.add(button_ExportFile);
        config.add(parameterButton);
        config.add(t2);
        config.add(filterbox);  
        config.add(t3);
        config.add(chartbox);
        return config;
    }
    
    private JPanel initParameterPanel(){
        JLabel l1 = new JLabel("insuffThreshold: ");
        final JTextField t1 = new JTextField("30");
//        t1.setText("3");
        JLabel l2 = new JLabel("actdurSTDbnd: ");
        final JTextField t2 = new JTextField("-2.5");
        final JTextField t3 = new JTextField("2.5");
        JLabel l3 = new JLabel("actdurknnmax: ");
        final JTextField t4 = new JTextField("2.5");
        JLabel l4 = new JLabel("actdurCLUSTbnd: ");
        final JTextField t5 = new JTextField("-2.5");
        final JTextField t6 = new JTextField("2.5");
        JLabel l5 = new JLabel("actdurCLUSTtest: ");
        final JTextField t7 = new JTextField("5");
        JLabel l6 = new JLabel("acttimeSTDbnd: ");
        final JTextField t8 = new JTextField("-2");
        final JTextField t9 = new JTextField("2");
        JLabel l7 = new JLabel("acttimeKNNmax: ");
        final JTextField t10 = new JTextField("2");
        JLabel l8 = new JLabel("caseSTDbnd: ");
        final JTextField t11 = new JTextField("-1.5");
        final JTextField t12 = new JTextField("1.5");
        JLabel l9 = new JLabel("caseRANGEbnd: ");
        final JTextField t13 = new JTextField("-1.5");
        final JTextField t14 = new JTextField("1.5");
        
        JButton b = new JButton("apply");
        JButton h = new JButton("close");
        b.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        h.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        JFrame parameterWindow = new JFrame("parameter");
        
        if(deeventLog == null) {
            System.out.println("null parArray");
            t1.setText("30");
            t2.setText("-2.5");
            t3.setText("2.5");
            t4.setText("2.5");
            t5.setText("-2.5");
            t6.setText("2.5");
            t7.setText("5");
            t8.setText("-2");
            t9.setText("2");
            t10.setText("2");
            t11.setText("-1.5");
            t12.setText("1.5");
            t13.setText("-1.5");
            t13.setText("1.5");
        }
        else {
            double parArray[] = deeventLog.getbnd();
            t1.setText(parArray[0]+"");
            t2.setText(parArray[1]+"");
            t3.setText(parArray[2]+"");
            t4.setText(parArray[3]+"");
            t5.setText(parArray[4]+"");
            t6.setText(parArray[5]+"");
            t7.setText(parArray[6]+"");
            t8.setText(parArray[7]+"");
            t9.setText(parArray[8]+"");
            t10.setText(parArray[9]+"");
            t11.setText(parArray[10]+"");
            t12.setText(parArray[11]+"");
            t13.setText(parArray[12]+"");
            t13.setText(parArray[13]+"");
        }
        b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                double[] p = new double[14];
                p[0] = Double.parseDouble(t1.getText());
                p[1] = Double.parseDouble(t2.getText());
                p[2] = Double.parseDouble(t3.getText());
                p[3] = Double.parseDouble(t4.getText());
                p[4] = Double.parseDouble(t5.getText());
                p[5] = Double.parseDouble(t6.getText());
                p[6] = Double.parseDouble(t7.getText());
                p[7] = Double.parseDouble(t8.getText());
                p[8] = Double.parseDouble(t9.getText());
                p[9] = Double.parseDouble(t10.getText());
                p[10] = Double.parseDouble(t11.getText());
                p[11] = Double.parseDouble(t12.getText());
                p[12] = Double.parseDouble(t13.getText());
                p[13] = Double.parseDouble(t14.getText());
                deeventLog.setbnd(p);
                double[] parArray = new double[14];
                parArray = deeventLog.getbnd();
                initApplicationDefaults();
                t1.setText(parArray[0]+"");
                t2.setText(parArray[1]+"");
                t3.setText(parArray[2]+"");
                t4.setText(parArray[3]+"");
                t5.setText(parArray[4]+"");
                t6.setText(parArray[5]+"");
                t7.setText(parArray[6]+"");
                t8.setText(parArray[7]+"");
                t9.setText(parArray[8]+"");
                t10.setText(parArray[9]+"");
                t11.setText(parArray[10]+"");
                t12.setText(parArray[11]+"");
                t13.setText(parArray[12]+"");
                t13.setText(parArray[13]+"");
            }
        });
        
        h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parameterWindow.pack();
                parameterWindow.setVisible(false);
            }
        });
        GroupLayout parameterLayout = new GroupLayout(parameter);
        parameter.setLayout(parameterLayout);
        parameter.setBorder(BorderFactory.createTitledBorder("Parameter Panel"));
        
        parameterLayout.setAutoCreateGaps(true);
        parameterLayout.setAutoCreateContainerGaps(true);
        parameterLayout.setHorizontalGroup(
            parameterLayout.createSequentialGroup()
                .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(l2)
                    .addComponent(l4)
                    .addComponent(l6)
                    .addComponent(l8)
                    .addComponent(l9)
                )
                .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(t2)
                    .addComponent(t5)
                    .addComponent(t8)
                    .addComponent(t11)
                    .addComponent(t13)
                )
                .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(t3)
                    .addComponent(t6)
                    .addComponent(t9)
                    .addComponent(t12)
                    .addComponent(t14)
                )
                .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(l1)
                    .addComponent(l3)
                    .addComponent(l5)
                    .addComponent(l7)
                    .addComponent(b)
                )
                .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(t1)
                    .addComponent(t4)
                    .addComponent(t7)
                    .addComponent(t10)
                    .addComponent(h)
                )
        );
        parameterLayout.setVerticalGroup(
        parameterLayout.createSequentialGroup()
            .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(l2)
                .addComponent(t2)
                .addComponent(t3)
                .addComponent(l1)
                .addComponent(t1)
            )
            .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(l4)
                .addComponent(t5)
                .addComponent(t6)
                .addComponent(l3)
                .addComponent(t4)
            )
            .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(l6)
                .addComponent(t8)
                .addComponent(t9)
                .addComponent(l5)
                .addComponent(t7)
            )
            .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(l8)
                .addComponent(t11)
                .addComponent(t12)
                .addComponent(l7)
                .addComponent(t10)
            )
            .addGroup(parameterLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(l9)
                .addComponent(t13)
                .addComponent(t14)
                .addComponent(b)
                .addComponent(h)
            )
        );
        parameter.setBackground(Color.WHITE);
        parameterWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        parameterWindow.getContentPane().add(parameter,BorderLayout.CENTER);
        parameterWindow.pack();
        parameterWindow.setVisible(true);
        return parameter;
    }
    
    private static final int BINS = 256;
    private final BufferedImage image = getImage();

    private BufferedImage getImage() {
        try {
            return ImageIO.read(new URL(
                "http://i.imgur.com/kxXhIH1.jpg"));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    private ChartPanel initChartPanel() {
        if(deeventLog == null || deeventLog.events().size() == 0) {
            Raster raster = image.getRaster();
            final int w = image.getWidth();
            final int h = image.getHeight();
            double[] r = new double[w * h];
            r = raster.getSamples(0, 0, w, h, 0, r);
            dataset.addSeries("Red", r, BINS);
            r = raster.getSamples(0, 0, w, h, 1, r);
            dataset.addSeries("Green", r, BINS);
            r = raster.getSamples(0, 0, w, h, 2, r);
            dataset.addSeries("Blue", r, BINS);
        }
        else { 
            double[] std = loadSTD();
            double[] cur = new double[1000];
            for(int i = 0; i < 1000; i++) cur[i] = currentevent.getStd();
            dataset.addSeries("current", cur, BINS/2);
            dataset.addSeries("distribution", std, BINS/2);   
            }
        // chart
        chart = ChartFactory.createHistogram("standard deviation", "Value",
            "Count", dataset, PlotOrientation.VERTICAL, true, true, false);
//        chart.getPlot().setBackgroundPaint( new Color(0, 255, 0, 0) );
//        chart.getPlot().setBackgroundAlpha(1.0f);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        // translucent red, green & blue
        Paint[] paintArray = {
            new Color(0x80ff000f, true),
            new Color(0x80000ff0, true),
            new Color(0x800000ff, true)
        };
        plot.setDrawingSupplier(new DefaultDrawingSupplier(
            paintArray,
            DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
        ValueAxis range = plot.getRangeAxis();
        range.setVisible(false);
        chart.setBorderVisible(false);  
        charts = new ChartPanel(chart);
        charts.setMouseWheelEnabled(true);
        charts.setPreferredSize(new Dimension(400,400));
        charts.setBackground(Color.WHITE);
        charts.setBorder(BorderFactory.createTitledBorder("Chart Panel"));
        return charts;
    }
    
    private double[] loadSTD(){
        List<Double> tmp = new ArrayList();
        tmp = deeventLog.allactstd;
        double[] std = new double[tmp.size()];
        for(int i = 0; i <  std.length; i++){
            std[i] = tmp.get(i);
//            System.out.println(std[i]);
        }
        return std;
    }
    
    private double[] loadDur(){
        List<DEEvent> tmp = new ArrayList();
        tmp = deeventLog.events();
        List<Double> dur = new ArrayList();
//        tmp = deeventLog.actstd.get(currentevent.activity());
//        System.out.println(currentevent.getStd());
        for(DEEvent e : tmp){
            if((!e.isInvalid())&& (!e.isInsufficient())) dur.add((double) e.duration());
        }
        double[] res = new double[dur.size()];
        for(int i = 0; i < dur.size(); i++){
            res[i] = dur.get(i);
        }
        return res;
    }
     
    private XYDataset loadDurKnn(){
        XYSeries series1 = new XYSeries("durknn");
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        List<DEEvent> tmp = new ArrayList();
        tmp = deeventLog.events();
        for(DEEvent e : tmp){
            if((!e.isInvalid())&& (!e.isInsufficient())) {
                double dur = e.duration();
//                if(dur > 10000) continue;
                double knn = e.getKnn();
                series1.add(dur,knn);
            }
        }
        dataset1.addSeries(series1);
        return dataset1;
    }
    
    private void saveJTableAsCSVActionPerformed(ActionEvent evt) throws IOException{
        int response = fileChooser.showOpenDialog(config);
        if (response == JFileChooser.APPROVE_OPTION) {
            // Read csv file here
            String out_address = fileChooser.getSelectedFile().getAbsolutePath();
//            System.out.println(out_address);
            File file = new File(out_address + ".csv");
            ExportJTable e1 = new ExportJTable();
            e1.exportToCSV(jxTable, file);
        }
    }
    
    /**
     * Initialize application wide behaviour.
     */
    private static void initApplicationDefaults() {
        ColumnFactory.setInstance(createColumnFactory());
    }

    /**
     * A ColumnFactory is used by JXTable to create and 
     * configure all columns. It can be set per-application (before
     * creating an JXTable) or per-table (before setting the model).
     * 
     * This ColumnFactory changes the column titles to a more human readable
     * form. The column title is a convenience property on TableColumnExt, it's
     * the String representation of the headerValue.
     * 
     * @return a custom ColumnFactory which sets column title
     *   while keeping the identifier as the old header value.
     */
    private static ColumnFactory createColumnFactory() {
        ColumnFactory factory = new ColumnFactory() {
            /**
             * We'll do a trick, though, and that is to set the identifiers
             * of each column to their current header value, so we can still
             * use the same names for identifiers in the rest of our code.
             * 
             * First, a trick: by default, the "identifier" for a
             * TableColumn is actually null unless we specifically set it;
             * the header value is used instead. By doing this get, we're
             * pulling the header value, and setting that as the identifier;
             * then we can change the header value independently. 
             */
            @Override
            public void configureTableColumn(TableModel model,
                    TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);
                columnExt.setIdentifier(columnExt.getIdentifier());
                // ...and now change the title
                String title = columnExt.getTitle();
                title = title.substring(0, 1).toUpperCase()
                        + title.substring(1).toLowerCase();
                columnExt.setTitle(title.replace('_', ' '));
            }

        };
        return factory;
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {     
        initApplicationDefaults();
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        //Create and set up the window.
//        final JXFrame frame = new JXFrame("EventLog", true);
        //Create and set up the content pane.
        JComponent newContentPane = new MyJXTable(BAR_NUMS,address).initUI();
        frame.setContentPane(newContentPane);
        newContentPane.setBackground(Color.WHITE);
        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // create the list index bar and configure it
//        final ListIndexBar bar = new ListIndexBar(BAR_NUMS);
//        bar.setBackground(new Color(0, 255, 0, 0));        
        bar.setForeground(MARKER_COLOUR);
//        bar.setOpaque(true);
        // add a set of example markers
//        bar.addMarkers(ERROR_INDEX_LIST);

        // add a selection listener to select the corresponding item in the list when the marker is selected
        bar.addSelectionListener(new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            int selectedIndex = e.getFirstIndex();
            System.out.println("index selected " + selectedIndex);
            // mark selected row
            jxTable.setRowSelectionInterval(selectedIndex,selectedIndex);
            // scroll selected row into center of viewport
            if (!(jxTable.getParent() instanceof JViewport)) {
                return;
            }
            JViewport viewport = (JViewport) jxTable.getParent();
            Rectangle rect = jxTable.getCellRect(selectedIndex, 0, true);
            Rectangle viewRect = viewport.getViewRect();
            rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

            int centerX = (viewRect.width - rect.width) / 2;
            int centerY = (viewRect.height - rect.height) / 2;
            if (rect.x < centerX) {
              centerX = -centerX;
            }
            if (rect.y < centerY) {
              centerY = -centerY;
            }
            rect.translate(centerX, centerY);
            viewport.scrollRectToVisible(rect);
            }
        });  
//        frame.add(scroll, BorderLayout.CENTER);
//        newContentPane.add(bar,BorderLayout.EAST);
    }
    
    public static void setMarkerList(List<Integer> err, List<Integer> insuffErr,
            List<Integer> durstdErr, List<Integer> durstdWarn, 
            List<Integer> durknnErr, List<Integer> durknnWarn
            ){
        MyJXTable.ERROR_INDEX_LIST = err;
        MyJXTable.INSUFF_ERROR_LIST = insuffErr;
        MyJXTable.DUR_STD_ERRORS_LIST = durstdErr;
        MyJXTable.DUR_STD_WARNINGS_LIST = durstdWarn;
        MyJXTable.DUR_KNN_ERRORS_LIST = durknnErr;
        MyJXTable.DUR_KNN_WARNINGS_LIST = durknnWarn;
    }
    
//    public static void startTable() {
//        //Schedule a job for the event-dispatching thread:
//        //creating and showing this application's GUI.
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                createAndShowGUI();
//            }
//        });
//    }
    
    public static void main(String args[]) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();  
            }
        });
        try {
               UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        SwingUtilities.updateComponentTreeUI(frame);
//        frame.pack();

    }
    
    private void menuItem_ImportFileActionPerformed(java.awt.event.ActionEvent evt){
        int response = fileChooser.showOpenDialog(config);
        if (response == JFileChooser.APPROVE_OPTION) {
            // Read csv file here
            String address = fileChooser.getSelectedFile().getAbsolutePath();
//            System.out.println(address);
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            import_display(address);
        }
    }
    
    private static void import_display(String address){
//        DEEventLog deeventLog;  
        try {        
            deeventLog = new DEEventLog(address);
            List<DEEvent> errors = deeventLog.detectError();
            System.out.println("detecting");
            List<DEEvent> insuffErrors = deeventLog.insuffError();
            List<DEEvent> durstdErrors = deeventLog.durstdErrors();
            List<DEEvent> durstdWarnings = deeventLog.durstdWarnings();
            List<DEEvent> durknnErrors = deeventLog.durknnErrors();
            List<DEEvent> durknnWarnings = deeventLog.durknnWarnings();
            List<Integer> errMarkers = new ArrayList<Integer>();
            List<Integer> insuffErrMarkers = new ArrayList<Integer>();
            List<Integer> durstdErrMarkers = new ArrayList<Integer>();
            List<Integer> durstdWarnMarkers = new ArrayList<Integer>();
            List<Integer> durknnErrMarkers = new ArrayList<Integer>();
            List<Integer> durknnWarnMarkers = new ArrayList<Integer>();    

            for(DEEvent e : errors){
                errMarkers.add(e.index());
            };
            for(DEEvent e : insuffErrors){
                insuffErrMarkers.add(e.index());
            };
            for(DEEvent e : durstdErrors){
                durstdErrMarkers.add(e.index());
            };
            for(DEEvent e : durstdWarnings){
                durstdWarnMarkers.add(e.index());
            };
            for(DEEvent e : durknnErrors){
                durknnErrMarkers.add(e.index());
            };
            for(DEEvent e : durknnWarnings){
                durknnWarnMarkers.add(e.index());
            };
            currentevent = deeventLog.events().get(0);
            currentstd = currentevent.getStd();
            MyJXTable myJXTable = new MyJXTable(deeventLog.getEventNum(),address);//,deeventLog.events());
            myJXTable.setMarkerList(errMarkers, insuffErrMarkers, 
                    durstdErrMarkers, durstdWarnMarkers, 
                    durknnErrMarkers, durknnWarnMarkers
                    );
            myJXTable.createAndShowGUI();
        } catch (BiffException ex) {
            Logger.getLogger(MyJXTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyJXTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    class SampleTableModel extends DefaultTableModel {
        void loadData() {
            try {
//                URL url = SampleTableModel.class.getResource("/org/jdesktop/demo/sample/resources/weather.txt");
                String url = "171traces_Corrected.csv";
                loadDataFromCSV(url);
            } catch ( Exception e ) {
                e.printStackTrace();
                loadDefaultData();
            }
        }
        
        private void loadDataFromCSV(String filename) {
            try {
                FileInputStream fstream = new FileInputStream(filename);
                BufferedReader lnr = new BufferedReader(new InputStreamReader(fstream));
                String line = lnr.readLine();
                String[] cols = line.split(",");
                for ( String col : cols ) {
                    addColumn(col);
                }
                System.out.println("column established!");
                while (( line = lnr.readLine()) != null ) {
                    String[] attr = line.split(",");
                    addRow(Arrays.copyOfRange(attr, 0, attr.length));
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                loadDefaultData();
            }
        }
        
        private void loadDataFromEventLog(List<DEEvent> log) {
            try {
                String[] cols = {"Case ID", "Activity", "Start Time", "End Time", "Mid time", "Duration", 
                    "INVALID", "INSUFF", "ACT_DUR_STD", "ACT_DUR_KNN", "ACT_DUR_CLUST",
                    "ACT_TIME_STD", "ACT_TIME_KNN", "CASE_STD", "CASE_RANGE"};
                for ( String col : cols ) {
                    addColumn(col);
                }
                System.out.println("column established!");
                for(DEEvent e : log) {
                    String[] attr = { "" + e.caseID(), "" + e.activity(), "" + e.start(), "" + e.end(),
                    "" + e.midTime(), "" + e.duration(), "" + (e.isInvalid()?1:0), "" + (e.isInsufficient()?1:0),
                    "" + e.errors()[0], "" + e.errors()[1], "" + e.errors()[2], "" + e.errors()[3], "" + e.errors()[4],
                    "" + e.errors()[5], "" + e.errors()[6], "" + e.errors()[7], "" + e.errors()[8]};
                    addRow(Arrays.copyOfRange(attr, 0, attr.length));
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                loadDefaultData();
            }
        }
        
        private void loadDefaultData() {
            int colCnt = 6;
            int rowCnt = 10;
            for ( int i=0; i < colCnt; i++ ) {
                addColumn("Column-" + (i + 1));
            }
            for ( int i=0; i <= rowCnt; i++ ) {
                String[] row = new String[colCnt];
                for ( int j=0; j < colCnt; j++ ) {
                    row[j] = "Row-" + i + "Column-" + (j + 1);
                }
                addRow(row);
            }
        }
    }
}