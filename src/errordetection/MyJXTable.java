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
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import java.util.regex.Pattern;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

/**
 * This SimpleJXTableDemo is a very simple example of how to use the extended features of the
 * JXTable in the SwingX project. The major features are covered, step-by-step. You can run
 * this demo from the command-line without arguments
 * java org.jdesktop.demo.sample.SimpleJXTableDemo
 *
 * If looking at the source, the interesting code is in configureJXTable().
 *
 * This is derived from the SimpleTableDemo in the Swing tutorial.
 *
 * @author Patrick Wright (with help from the Swing tutorial :))
 */
public class MyJXTable  {
    private static final Color MARKER_COLOUR = Color.GREEN;
    private static final Color INVALID_COLOUR = Color.RED;
    private static final Color DUR_COLOUR = Color.ORANGE;
    private static final Color TIME_COLOUR = Color.BLUE;
    private static final Color CASE_COLOUR = Color.MAGENTA;
    private static final Color ERROR_COLOUR = Color.RED;
    private static final Color WARNING_COLOUR = Color.YELLOW;
    private static int BAR_NUMS = 100;
    private static Integer[] INDEX_ARRAY = new Integer[]{4, 15, 32, 36, 58, 74, 92};
    private static List<Integer> ERROR_INDEX_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List INVALID_ERROR_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List DUR_ERROR_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List TIME_ERROR_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List CASE_ERROR_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List INSUFF_ERROR_LIST = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List ERROR_INDEX_LIST_LIGHT = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List INVALID_ERROR_LIST_LIGHT = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List DUR_ERROR_LIST_LIGHT = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List TIME_ERROR_LIST_LIGHT = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List CASE_ERROR_LIST_LIGHT = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static List INSUFF_ERROR_LIST_LIGHT = new LinkedList(Arrays.asList(INDEX_ARRAY));
    private static JXTable jxTable = new JXTable();
    private JFileChooser fileChooser;
    private JPanel config;
    private JPanel parameter;
    private JPanel charts;
    private static String address;
    private static SampleTableModel model;
    private static ListIndexBar bar;
    private static JXFrame frame = new JXFrame("Error Detection", true);
    private static DEEventLog deeventLog;  
    private static double[] parArray;
    private static double currentstd = 0;
    private static DEEvent currentevent;
    private HistogramDataset dataset = new HistogramDataset();
    private JFreeChart chart;
    
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
    
    private MyJXTable(int num, String filename, double[] p, DEEventLog e){
        this.BAR_NUMS = num;
        this.address = filename;
        this.parArray = p;
        this.deeventLog = e;
    }
    
//    public MyJXTable(int num, String filename, List<DEEvent> e) {
//        this.BAR_NUMS = num;
//        this.address = filename;
//        this.eventlog = e;
    
//        this.bar = new ListIndexBar(BAR_NUMS);
//    }
          
 
    private JComponent initUI() {
        JComponent content = new JPanel(new BorderLayout());
//        content.setBorder(BorderFactory.createTitledBorder("Error Det"));
        jxTable = initTable();
        configureJXTable(jxTable);
        parameter = new JPanel();
        charts = new JPanel();
        
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(jxTable);
        JComponent tabbedPane = new JTabbedPane(3);
        JComponent tablePane = new JPanel(new BorderLayout());
        tablePane.add(scrollPane, BorderLayout.CENTER);
        tablePane.add(bar, BorderLayout.EAST);
        tabbedPane.setBorder(BorderFactory.createTitledBorder("Table Panel"));
        tabbedPane.add("EventView",tablePane);
        tabbedPane.setPreferredSize(new Dimension(400,400));
        //Add the scroll pane to this panel.
        content.add(tabbedPane, BorderLayout.SOUTH);
        
        content.add(initParameterPanel(), BorderLayout.WEST);
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
        System.out.println("filename: " + this.address);
        if(address!=null){
            String[] folders = address.split("/");
            String filename = folders[folders.length - 1];
            model.loadDataFromCSV(address);
//            model.loadDataFromEventLog(deeventLog.events());
        }
        else{
            model.loadDefaultData();
        }
        return jxTable;
    }
       
    public void setFilename(String filename){
        this.address = filename;
//        this.initTable();
    }
    
//    public void setEventLog(List<DEEvent> e){
//        this.eventlog = e;
//    }
    
    /** For demo purposes, the special features of the JXTable are configured here. There is
     * otherwise no reason not to do this in initTable().
     */
    private void configureJXTable(JXTable jxTable) {
        // set the number of visible rows
        jxTable.setVisibleRowCount(30);
        // set the number of visible columns
        jxTable.setVisibleColumnCount(8);
        // This turns horizontal scrolling on or off. If the table is too large for the scrollpane,
        // and horizontal scrolling is off, columns will be resized to fit within the pane, which can
        // cause them to be unreadable. Setting this flag causes the table to be scrollable right to left.
        jxTable.setHorizontalScrollEnabled(true);

        // This shows the column control on the right-hand of the header.
        // All there is to it--users can now select which columns to view
        jxTable.setColumnControlVisible(true);
        
        // our data is pulling in too many columns by default, so let's hide some of them
        // column visibility is a property of the TableColumnExt class; we can look up a
        // TCE using a column's display name or its index

        // Sorting by clicking on column headers is on by default. However, the comparison
        // between rows uses a default compare on the column's type, and elevations
        // are not sorting how we want.
        //
        // We will override the Comparator assigned to the TableColumnExt instance assigned
        // to the elevation column. TableColumnExt has a property comparator will be used
        // by JXTable's sort methods. 
        // By using a custom Comparator we can control how sorting in any column takes place
        Comparator numberComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                Double d1 = Double.valueOf(o1 == null ? "0" : (String)o1);
                Double d2 = Double.valueOf(o2 == null ? "0" : (String)o2);
                return d1.compareTo(d2);
            }
        };
        
        // comparators are good for special situations where the default comparator doesn't
        // understand our data.
        
        // We'll add a highlighter to offset different row numbers
        // Note the setHighlighters() takes an array parameter; you can chain these together.
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
        jxTable.addMouseListener(new java.awt.event.MouseAdapter() {
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
        });
    }
    
    /** This shows off some additional JXTable configuration, controlled by checkboxes in a Panel. */
    private JPanel initConfigPanel(final JXTable jxTable) {
        config = new JPanel();
        config.setBorder(BorderFactory.createTitledBorder("config Panel"));
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
        JButton button_hide = new JButton("hide");
        button_ExportFile.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        button_ImportFile.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        button_hide.setFont(new java.awt.Font("Lucida Grande", 0, 10));

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
        
        String[] filterlist = {"Default","All Errors","invalid & insuff errors","Duration errors","Time errors","Case errors"};
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

                                  return INVALID_ERROR_LIST.contains(adapter.row);
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

                                  return ERROR_INDEX_LIST.contains(adapter.row);
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
//                        bar.addLightMarkers(ERROR_INDEX_LIST);
                        bar.addMedMarkers(ERROR_INDEX_LIST_LIGHT); 
                        bar.addMarkers(INVALID_ERROR_LIST);
//                        frame.add(bar);
                        break;
                    case "invalid & insuff errors":
                        final HighlightPredicate myPredicateInv = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return INVALID_ERROR_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterInv = new ColorHighlighter(
                            myPredicateInv,
                            ERROR_COLOUR,   // background color
                            null);       // no change in foreground color
                         final HighlightPredicate myPredicateInsuff = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return INSUFF_ERROR_LIST_LIGHT.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterInsuff = new ColorHighlighter(
                            myPredicateInsuff,
                            WARNING_COLOUR,   // background color
                            null);       // no change in foreground color
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        jxTable.addHighlighter(highlighterInsuff);
                        jxTable.addHighlighter(highlighterInv);
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        bar.setForeground(ERROR_COLOUR);
//                        bar.addLightMarkers(ERROR_INDEX_LIST);
                        bar.addMedMarkers(INSUFF_ERROR_LIST_LIGHT); 
                        bar.addMarkers(INVALID_ERROR_LIST);
                       
//                        frame.add(bar);
//                        frame.add(bar);
                        break;
                    case "Duration errors":
                        final HighlightPredicate myPredicateDur = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return DUR_ERROR_LIST.contains(adapter.row);
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

                                  return DUR_ERROR_LIST_LIGHT.contains(adapter.row);
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
                        
                        bar.addMedMarkers(DUR_ERROR_LIST_LIGHT); 
                        bar.addMarkers(DUR_ERROR_LIST);
//                        frame.add(bar);
                        break;
                    case "Time errors":
                        final HighlightPredicate myPredicateTime = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return TIME_ERROR_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterTime = new ColorHighlighter(
                              myPredicateTime,
                              ERROR_COLOUR,   // background color
                              null);       // no change in foreground color
                        final HighlightPredicate myPredicateTimeLight = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return TIME_ERROR_LIST_LIGHT.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterTimeLight = new ColorHighlighter(
                              myPredicateTimeLight,
                              WARNING_COLOUR,   // background color
                              null);       // no change in foreground color
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        jxTable.addHighlighter(highlighterTimeLight);
                        jxTable.addHighlighter(highlighterTime);
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        bar.setForeground(ERROR_COLOUR);
                        bar.addMedMarkers(TIME_ERROR_LIST_LIGHT);
                        bar.addMarkers(TIME_ERROR_LIST);
                        
//                        frame.add(bar);
                        break;
                    case "Case errors":
                        final HighlightPredicate myPredicateCase = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return CASE_ERROR_LIST.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterCase = new ColorHighlighter(
                              myPredicateCase,
                              ERROR_COLOUR,   // background color
                              null);       // no change in foreground color
                        final HighlightPredicate myPredicateCaseLight = new HighlightPredicate() {
                            @Override 
                            public boolean isHighlighted(
                                  Component renderer, 
                                  ComponentAdapter adapter) {

                                  return CASE_ERROR_LIST_LIGHT.contains(adapter.row);
                            }
                        };
                        ColorHighlighter highlighterCaseLight = new ColorHighlighter(
                              myPredicateCaseLight,
                              WARNING_COLOUR,   // background color
                              null);       // no change in foreground color
                        jxTable.setHighlighters(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
                                Color.BLACK,
                                Color.WHITE));
                        jxTable.addHighlighter(highlighterCaseLight);
                        jxTable.addHighlighter(highlighterCase);
                        bar.clearMarkers();
                        bar.clearMedMarkers();
                        bar.setForeground(ERROR_COLOUR);
                        bar.addMedMarkers(CASE_ERROR_LIST_LIGHT);
                        bar.addMarkers(CASE_ERROR_LIST);
//                        frame.add(bar);
                        break;
                }
            }
        });
        
        String[] chartlist = {" durstd    "," timestd   "," casestd   "};
        final JComboBox chartbox = new JComboBox(chartlist);
        chartbox.setSelectedIndex(0);
        JLabel t1 = new JLabel("Files:");
        JLabel t2 = new JLabel("ErrorType:");
        JLabel t3 = new JLabel("ChartType:");
//        config.add(fileChooser);
//        jxTable.setRowSorter(sorter);
        config.add(t1);
        config.add(button_ImportFile);
        config.add(button_ExportFile);
//        config.add(control);
//        config.add(sorting);
//        config.add(horiz);
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
        JButton h = new JButton("hide");
        b.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        h.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        
        if(parArray == null) System.out.println("null parArray");
        else {
            for(int i = 0; i< 14; i++)
                    System.out.println(parArray[i]);
            t1.setText(parArray[0]+"");
            System.out.print(parArray[0]+"");
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
                parArray = new double[14];
                parArray = deeventLog.getbnd();
                for(int i = 0; i< 14; i++)
                    System.out.println(parArray[i]);
                initApplicationDefaults();
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
//                SwingUtilities.updateComponentTreeUI(frame);
//                frame.pack();
            }
        });
        
        h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parameter.setVisible(false);
                frame.pack();
                h.setText("display config");
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
        chart.getPlot().setBackgroundPaint( new Color(0, 255, 0, 0) );
        chart.getPlot().setBackgroundAlpha(1.0f);
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
        ChartPanel panel = new ChartPanel(chart);
        panel.setBorder(BorderFactory.createTitledBorder("Chart Panel"));
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(400,400));
        return panel;
    }
    
    private double[] loadSTD(){
        List<Double> tmp = new ArrayList();
        tmp = deeventLog.allactstd;
//        tmp = deeventLog.actstd.get(currentevent.activity());
//        System.out.println(currentevent.getStd());
        double[] std = new double[tmp.size()];
        for(int i = 0; i <  std.length; i++){
            std[i] = tmp.get(i);
//            System.out.println(std[i]);
        }
        return std;
    }
    
    private void saveJTableAsCSVActionPerformed(ActionEvent evt) throws IOException{
        int response = fileChooser.showOpenDialog(config);
        if (response == JFileChooser.APPROVE_OPTION) {
            // Read csv file here
            String out_address = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println(out_address);
            File file = new File(out_address + ".csv");
            ExportJTable e1 = new ExportJTable();
            e1.exportToCSV(jxTable, file);
        }
    }
    
    /**
     * Initialize application wide behaviour.
     * Here: install a shared custom ColumnFactory to configure 
     * column titles in all JXTables. 
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
        newContentPane.setOpaque(true); //content panes must be opaque
//        newContentPane.setBorder(BorderFactory.createTitledBorder("Table Panel"));
//        frame = new JXFrame("EventLog", true);
        frame.setContentPane(newContentPane);
        
        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // create the list index bar and configure it
//        final ListIndexBar bar = new ListIndexBar(BAR_NUMS);
        bar.setBackground(new Color(0, 255, 0, 0));        
        bar.setForeground(MARKER_COLOUR);
        bar.setOpaque(true);
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
    
    public static void setMarkerList(List<Integer> err, List<Integer> invErr, List<Integer> insuffErr, 
            List<Integer> durErr, List<Integer> timeErr, List<Integer> caseErr,
            List<Integer> errLight, List<Integer> invErrLight, List<Integer> insuffErrLight, 
            List<Integer> durErrLight, List<Integer> timeErrLight, List<Integer> caseErrLight
            ){
        MyJXTable.ERROR_INDEX_LIST = err;
        MyJXTable.INVALID_ERROR_LIST = invErr;
        MyJXTable.DUR_ERROR_LIST = durErr;
        MyJXTable.TIME_ERROR_LIST = timeErr;
        MyJXTable.CASE_ERROR_LIST = caseErr;
        MyJXTable.INSUFF_ERROR_LIST = insuffErr;
        for(Integer e : err){
            if(!invErr.contains(e)) MyJXTable.ERROR_INDEX_LIST_LIGHT.add(e);
        }
        for(Integer e : invErrLight){
            if(!invErr.contains(e)) MyJXTable.INVALID_ERROR_LIST_LIGHT.add(e);
        }
        for(Integer e : durErrLight){
            if(!durErr.contains(e)) MyJXTable.DUR_ERROR_LIST_LIGHT.add(e);
        }
        for(Integer e : timeErrLight){
            if(!timeErr.contains(e)) MyJXTable.TIME_ERROR_LIST_LIGHT.add(e);
        }
        for(Integer e : caseErrLight){
            if(!caseErr.contains(e)) MyJXTable.CASE_ERROR_LIST_LIGHT.add(e);
        }
        for(Integer e : insuffErrLight){
            if(!invErr.contains(e)) MyJXTable.INSUFF_ERROR_LIST_LIGHT.add(e);
        }
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
            System.out.println(address);
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
            List<DEEvent> invErrors = deeventLog.invError();
            List<DEEvent> durErrors = deeventLog.DurError();
            List<DEEvent> TimeErrors = deeventLog.TimeError();
            List<DEEvent> CaseErrors = deeventLog.CaseError();
            List<DEEvent> insuffErrors = deeventLog.insuffError();
            List<Integer> errMarkers = new ArrayList<Integer>();
            List<Integer> invErrMarkers = new ArrayList<Integer>();
            List<Integer> durErrMarkers = new ArrayList<Integer>();
            List<Integer> timeErrMarkers = new ArrayList<Integer>();
            List<Integer> caseErrMarkers = new ArrayList<Integer>(); 
            List<Integer> insuffErrMarkers = new ArrayList<Integer>();

            for(DEEvent e : errors){
                errMarkers.add(e.index());
            };
            for(DEEvent e : invErrors){
                invErrMarkers.add(e.index());
            };
            for(DEEvent e : durErrors){
                durErrMarkers.add(e.index());
            };
            for(DEEvent e : TimeErrors){
                timeErrMarkers.add(e.index());
            };
            for(DEEvent e : CaseErrors){
                caseErrMarkers.add(e.index());
            };
            for(DEEvent e : insuffErrors){
                insuffErrMarkers.add(e.index());
            };

            deeventLog.loosenThreshold();
            List<DEEvent> errorsLight = deeventLog.detectError();
            List<DEEvent> invErrorsLight = deeventLog.invError();
            List<DEEvent> durErrorsLight = deeventLog.DurError();
            List<DEEvent> TimeErrorsLight = deeventLog.TimeError();
            List<DEEvent> CaseErrorsLight = deeventLog.CaseError();
            List<DEEvent> insuffErrorsLight = deeventLog.insuffError();
            List<Integer> errMarkersLight = new ArrayList<Integer>();
            List<Integer> invErrMarkersLight = new ArrayList<Integer>();
            List<Integer> durErrMarkersLight = new ArrayList<Integer>();
            List<Integer> timeErrMarkersLight = new ArrayList<Integer>();
            List<Integer> caseErrMarkersLight = new ArrayList<Integer>(); 
            List<Integer> insuffErrMarkersLight = new ArrayList<Integer>();

            for(DEEvent e : errorsLight){
                errMarkersLight.add(e.index());
            };
            for(DEEvent e : invErrorsLight){
                invErrMarkersLight.add(e.index());
            };
            for(DEEvent e : durErrorsLight){
                durErrMarkersLight.add(e.index());
            };
            for(DEEvent e : TimeErrorsLight){
                timeErrMarkersLight.add(e.index());
            };
            for(DEEvent e : CaseErrorsLight){
                caseErrMarkersLight.add(e.index());
            };
            for(DEEvent e : insuffErrorsLight){
                insuffErrMarkersLight.add(e.index());
            };
            deeventLog.resetThreshold();            
            System.out.println("Marker numbers: "+deeventLog.getEventNum());
            currentevent = deeventLog.events().get(0);
            currentstd = currentevent.getStd();
            MyJXTable myJXTable = new MyJXTable(deeventLog.getEventNum(),address);//,deeventLog.events());
            myJXTable.setMarkerList(errMarkers, invErrMarkers, insuffErrMarkers, 
                    durErrMarkers, timeErrMarkers, caseErrMarkers,
                    errMarkersLight, invErrMarkersLight, insuffErrMarkersLight, 
                    durErrMarkersLight, timeErrMarkersLight, caseErrMarkersLight
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