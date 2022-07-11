/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package romspkg;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.ui.VerticalAlignment;

/**
 *
 * @author Admin
 */
public class jBar1 extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(10.0, "S1", "C---1");
            dataset.addValue(4.0, "S1", "C---2");
            dataset.addValue(15.0, "S1", "C---3");
            dataset.addValue(14.0, "S1", "C---4");

            dataset.addValue(-5.0, "S1", "C---5");
            dataset.addValue(-7.0, "S1", "C---6");
            dataset.addValue(14.0, "S1", "C---7");
            dataset.addValue(-3.0, "S1", "C---8");


            dataset.addValue(6.0, "S2", "C---1");
            dataset.addValue(17.0, "S2", "C---2");
            dataset.addValue(-12.0, "S2", "C---3");
            dataset.addValue(7.0, "S2", "C---4");
            dataset.addValue(7.0, "S2", "C---5");
            dataset.addValue(15.0, "S2", "C---6");
            dataset.addValue(11.0, "S2", "C---7");
            dataset.addValue(0.0, "S2", "C---8");

            dataset.addValue(-8.0, "S3", "C---1");
            dataset.addValue(-6.0, "S3", "C---2");
            dataset.addValue(10.0, "S3", "C---3");
            dataset.addValue(-9.0, "S3", "C---4");
            dataset.addValue(9.0, "S3", "C---5");
            dataset.addValue(8.0, "S3", "C---6");
            dataset.addValue(null, "S3", "C---7");
            dataset.addValue(6.0, "S3", "C---8");

            dataset.addValue(-10.0, "S4", "C---1");
            dataset.addValue(9.0, "S4", "C---3");
            dataset.addValue(7.0, "S4", "C---3");
            dataset.addValue(7.0, "S4", "C---4");
            dataset.addValue(11.0, "S4", "C---5");
            dataset.addValue(13.0, "S4", "C---6");
            dataset.addValue(9.0, "S4", "C---7");
            dataset.addValue(9.0, "S4", "C---8");

            DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
            dataset1.addValue(10.0, "50", "C---1");
            dataset1.addValue(10.0, "50", "C---2");
            dataset1.addValue(10.0, "50", "C---3");
            dataset1.addValue(10.0, "50", "C---4");

            dataset1.addValue(10.0, "50", "C---5");
            dataset1.addValue(10.0, "50", "C---6");
            dataset1.addValue(10.0, "50", "C---7");
            dataset1.addValue(15.0, "50", "C---8");

            JFreeChart chart = getchart(dataset, dataset1);
            response.setContentType("image/png");


            ChartUtilities.writeChartAsPNG(out, chart, 800, 600);
            File fileName = new File(System.getProperty("user.home") + "/jfreechart1.png");
            out = new BufferedOutputStream(new FileOutputStream(fileName));
            ChartUtilities.writeChartAsPNG(out, chart, 800, 600);
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private JFreeChart getchart(DefaultCategoryDataset dataset,
            DefaultCategoryDataset dataset1) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Client Perception of Progress and Expected Outcome", // chart title
                "", // domain axis label
                "Percentage deterioration                                      Percentage Improvment", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                false, // tooltips?
                false // URLs?
                );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
         chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
        TextTitle tit = chart.getTitle();
        tit.setFont(new Font("Sans_Serif", Font.BOLD, 14));

        int cl = dataset.getColumnCount();
       /* for (int i = 0; i < cl; i++) {
            Comparable key = dataset.getColumnKey(i);
            CategoryMarker cm = new CategoryMarker(key);
            cm.setPaint(Color.lightGray);
            cm.setAlpha(0.5f);
            plot.addDomainMarker(cm, Layer.BACKGROUND);
        }*/
        plot.setRangeGridlinePaint(Color.black);
        plot.setRangeGridlineStroke(new BasicStroke(0.5f));
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setRange(0, 100);

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        //renderer.setItemMargin(0.4);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);

        renderer.setMaximumBarWidth(3);

        //      renderer.setLabelGenerator(new BarChartDemo7.LabelGenerator());
        renderer.setItemLabelsVisible(true);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("Sans_Serif", Font.PLAIN, 14));
        domainAxis.setCategoryMargin(0.02);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);

        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        chart.getLegend().setVerticalAlignment(VerticalAlignment.CENTER);

        CategoryItemRenderer renderer1 = new LineAndShapeRenderer();
        renderer1.setSeriesShape(0, new Rectangle2D.Float());
                //Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));

        renderer1.setStroke(new BasicStroke(5f));
        //renderer1.s

        plot.setDataset(1, dataset1);
        plot.setRenderer(1, renderer1);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        plot.setDomainGridlinesVisible(true);


        return chart;
    }
}
