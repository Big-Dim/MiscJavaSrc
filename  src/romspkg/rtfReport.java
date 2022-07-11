/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package romspkg;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.graphic.RtfShape;
import com.lowagie.text.rtf.graphic.RtfShapePosition;
import com.lowagie.text.rtf.graphic.RtfShapeProperty;
import com.lowagie.text.rtf.style.RtfFont;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Admin
 */
public class rtfReport extends HttpServlet {
    String title00 = "ROMS® Rehabilitation Checklist Report";    
    String title01 = "ROMS® Rehabilitation Survey of Problems and Coping ";
    String title02 = "ROMS® Functional Status Markers Report";
    String title03 = "ROMS® Progress and Outcome Summary Graphs";
   // String title03 = " ROMS® Rehabilitation Survey of Problems and Coping ";

    String title1 = "";
    String foot = "";

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/vnd.ms-word");
        response.setHeader("Content-Disposition", "attachment; filename=Report.rtf");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);

        String test = request.getParameter("test");
        subsession sub = Globals.getsub(request);

        String ret="";
        User user = sub.getUser();
        patient pat  = sub.pt;
        if (user == null || pat == null) {
            PrintWriter writer = response.getWriter();
            ret = "Time Out Relogin Please";
            writer.println(ret);
            return;
        }

        String mid=pat.Middle_Name.trim();
        if(!mid.equals(""))mid=mid+" ";
        String name = pat.First_Name.trim() + " "+ mid +  pat.Last_Name.trim();

        
        title1 = "\nClient: " + name +" Facility: JDS   Report Date: " + Constants.dd ;
        if(sub.isaggr){
            title1 = "\nClient: " + sub.aggrPat +" Facility: JDS   Report Date: " + Constants.dd
                    + "\n" +sub.filter;
        }
        foot = "©2002 Dr. J. Douglas Salmon, Jr. / RREES Inc. Toronto, Canada & Multi-Health Systems Inc., Toronto, Canada.\n"+
                 "email:  rrees@rrees.com  ph: 416-265-2599/877-804-7204  All rights reserved. Derived with permission.";


        DocumentException ex = null;
        ByteArrayOutputStream baosRTF = null;


        try {
            baosRTF = put(sub, test);

            response.setContentLength(baosRTF.size());
            ServletOutputStream sos;
            sos = response.getOutputStream();
            baosRTF.writeTo(sos);
            sos.flush();

        } catch (Throwable e) {
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.println(
                    this.getClass().getName()
                    + " caught an exception: "
                    + e.getClass().getName()
                    + "<br>");
            writer.println("<pre>");
            e.printStackTrace(writer);
            writer.println("</pre>");
        } finally {
            if (baosRTF != null) baosRTF.reset();
        }
    }

    public ByteArrayOutputStream put(subsession sub, String test) throws DocumentException {
        Document document = new Document(PageSize.LETTER, 2, 2, 2, 2);
        

        ByteArrayOutputStream baosRTF = new ByteArrayOutputStream();
        RtfWriter2 writer =null;
        try {
            writer = RtfWriter2.getInstance(document,baosRTF);
            Color blck = new Color(0x0, 0x0, 0x0);
            Color blue = new Color(0x4, 0x11, 0x77);
            Color wht = new Color(0xFF, 0xFF, 0xFF);

            RtfFont f0 = new RtfFont( "COURIER", 0, RtfFont.BOLD , wht );
            RtfFont fnt0 = new RtfFont( "COURIER", 10, RtfFont.BOLD , blck );
            RtfFont fnt1 = new RtfFont( "COURIER", 6, RtfFont.NORMAL );
            RtfFont fnt2 = new RtfFont( "COURIER", 12, RtfFont.NORMAL );
            RtfFont fnt3 = new RtfFont( "Helvetica", 8, RtfFont.NORMAL,blue );
            Paragraph p0 = new Paragraph("",f0);
            document.open();
            // checklist report
            if (test.equals("checklist") || test.equals("all")) {

                Chapter chapter1 = new Chapter(p0, 0);
                Phrase hpr0 = new Phrase(title00+title1, fnt0);
                //Phrase hpr1 = new Phrase(title1, fnt1);
                
                Paragraph hdp = new Paragraph();
                hdp.add(hpr0);
                //hdp.add(hpr1);
                hdp.setLeading(0f);
                HeaderFooter header = new HeaderFooter(hdp, false);
                header.setBorder(Rectangle.NO_BORDER);
                header.setAlignment(1);
                document.setHeader(header);

                //HeaderFooter footer = new HeaderFooter(new Phrase("--- Page: ", fonts[2]), new Phrase(" xxxxx", fonts[1]));
                HeaderFooter footer = new HeaderFooter(new Phrase(foot, fnt3), false);
                footer.setBorder(Rectangle.NO_BORDER);
                footer.setAlignment(1);
                document.setFooter(footer);

                //Image logo = Image.getInstance(inFile1);

                //JFreeChart chart = createChart();
                JFreeChart chart = sub.chart[1];
                Image img0 = createChartImage(chart,1);
                
                Paragraph imgp = new Paragraph();
                imgp.setAlignment("CENTER");
                imgp.add(img0);
                imgp.add("\n\n\n");
                //document.add(imgp);
/*
                String str0 = "\n\nAverage Perceived Life Role Disability Scores greater than 50% have\n"
                        + "demonstrated to reflect a significant risk factor for psychological\n"
                        + "disorders, particularly beyond the acute stage of physical pathology\n\n\n\n\n";
                Paragraph p = new Paragraph(str0, fnt1);
                p.setAlignment("CENTER");*/
                RtfShapePosition position = new RtfShapePosition(1400, 2000, 10000, 4800);
                RtfShape shape = new RtfShape(RtfShape.SHAPE_RECTANGLE, position);
                shape.setWrapping(RtfShape.SHAPE_WRAP_TOP_BOTTOM);
                shape.setProperty(new RtfShapeProperty(RtfShapeProperty.PROPERTY_IMAGE, img0));
                //p.add(shape);
                //document.add(p);

                JFreeChart chart1 = sub.chart[0];
                Image img1 = createChartImage(chart1,1);
                img1.setBorder(Rectangle.NO_BORDER);
                String str1 = "";
                Paragraph p1 = new Paragraph();
                p1.setAlignment("CENTER");
                p1.add(img1);
                p1.add("\n\n\n");

                JFreeChart chart2 = sub.chart[2];
                Image img2 = createChartImage(chart2,1);
                p1.add(img2);
                //document.add(p1);
                chapter1.add(imgp);
               // chapter1.add(p);
                chapter1.add(p1);
                document.add(chapter1);
            }

//Rehabilitation Survey of Problems and Coping Report
            if (test.equals("problems") || test.equals("all")) {
                Chapter chapter1 = new Chapter(p0, 0);
                
                document.setPageSize(PageSize.LETTER.rotate());


                Phrase hpr0 = new Phrase(title01 + title1, fnt0);
                //Phrase hpr1 = new Phrase(title1, fnt1);

                Paragraph hdp = new Paragraph();
                hdp.add(hpr0);
                //hdp.add(hpr1);
                HeaderFooter header = new HeaderFooter(hdp, false);
                header.setBorder(Rectangle.NO_BORDER);
                header.setAlignment(1);
                document.setHeader(header);

                //HeaderFooter footer = new HeaderFooter(new Phrase("--- Page: ", fonts[2]), new Phrase(" xxxxx", fonts[1]));
                HeaderFooter footer = new HeaderFooter(new Phrase(foot, fnt3), false);
                footer.setBorder(Rectangle.NO_BORDER);
                footer.setAlignment(1);
                document.setFooter(footer);

                JFreeChart chart = sub.chart[3];
                Image img0 = createChartImage(chart,2);
                chart = sub.chart[4];
                Image img1 = createChartImage(chart,2);
                chart = sub.chart[5];
                Image img2 = createChartImage(chart,2);
                chart = sub.chart[6];
                Image img3 = createChartImage(chart,2);

                Paragraph imgp = new Paragraph();
                imgp.setAlignment("CENTER");
                imgp.add(img0);
                imgp.add(img1);
                imgp.add("\n\n\n");
                imgp.add(img2);
                imgp.add(img3);

                //document.add(imgp);
                chapter1.add(imgp);
                document.add(chapter1);
            }

            if (test.equals("markers") || test.equals("all")) {
                Chapter chapter1 = new Chapter(p0, 0);
                document.setPageSize(PageSize.LETTER);
               // document.setPageSize(PageSize.LETTER);
                
                
                Phrase hpr0 = new Phrase(title03+title1, fnt0);
               // Phrase hpr1 = new Phrase(title1, fnt1);

                Paragraph hdp = new Paragraph();
                hdp.add(hpr0);
                //hdp.add(hpr1);
                HeaderFooter header = new HeaderFooter(hdp, false);
                header.setBorder(Rectangle.NO_BORDER);
                header.setAlignment(1);
                document.setHeader(header);

                //HeaderFooter footer = new HeaderFooter(new Phrase("--- Page: ", fonts[2]), new Phrase(" xxxxx", fonts[1]));
                HeaderFooter footer = new HeaderFooter(new Phrase(foot, fnt3), false);
                footer.setBorder(Rectangle.NO_BORDER);
                footer.setAlignment(1);
                document.setFooter(footer);

                JFreeChart chart = sub.chart[7];
                Image img0 = createChartImage(chart,3);

                Paragraph imgp = new Paragraph();
                imgp.setIndentationLeft(50.0f);
                imgp.setIndentationRight(50.0f);
                imgp.setLeading(0.0f);
                imgp.setAlignment("LEFT");
                imgp.setFont(fnt3);
                imgp.add(img0);
                imgp.add("\n\n");
                imgp.add("**Unless otherwise specified, and unless the data has been collected over a series of " +
                        "consecutive days (each approximating a competitive work day), then a participant "+
                        "who meets all of the above noted physical demands, cannot necessarily be considered " +
                        "to meet his/her essential job demands even from the strictly physical perspective. "+
                        "A competitive work day must consider such issues as the cognitive and psychosocial " +
                        "occupational demands as well as such factors as stamina, endurance, and continuous "+
                        "concentration, persistence and competitive work pace. These cautions apply to this " +
                        "graph as well as the summary graphs derived from it. ");

                //document.add(imgp);
                chapter1.add(imgp);
                document.add(chapter1);

            } 
            if (test.equals("progress")) {
                document.setPageSize(PageSize.LETTER.rotate());
                Phrase hpr0 = new Phrase(title03+title1, fnt0);
                //Phrase hpr1 = new Phrase(title1, fnt1);

                Paragraph hdp = new Paragraph();
                hdp.add(hpr0);
                //hdp.add(hpr1);
                HeaderFooter header = new HeaderFooter(hdp, false);
                header.setBorder(Rectangle.NO_BORDER);
                header.setAlignment(1);
                document.setHeader(header);

                //HeaderFooter footer = new HeaderFooter(new Phrase("--- Page: ", fonts[2]), new Phrase(" xxxxx", fonts[1]));
                HeaderFooter footer = new HeaderFooter(new Phrase(foot, fnt3), false);
                footer.setBorder(Rectangle.NO_BORDER);
                footer.setAlignment(1);
                document.setFooter(footer);

                JFreeChart chart = sub.chart[8];
                Image img0 = createChartImage(chart,4);
                chart = sub.chart[9];
                Image img1 = createChartImage(chart,4);
                chart = sub.chart[6];
                Image img2 = createChartImage(chart,4);
                chart = sub.chart[10];
                Image img3 = createChartImage(chart,4);
                chart = sub.chart[11];
                Image img4 = createChartImage(chart,4);
                chart = sub.chart[12];
                Image img5 = createChartImage(chart,4);

                Paragraph imgp = new Paragraph();
                imgp.setFont(fnt1);
                imgp.setAlignment("LEFT");
                imgp.add(img0);
                imgp.add(img1);
                imgp.add(img2);
                // imgp.add("\nHigher scores reflect better perfomance    Higher scores reflect better perfomance\n\n");
                imgp.add(img3);
                imgp.add(img4);
                imgp.add(img5);
                // imgp.add("\nHigher scores reflect better perfomance    Higher scores reflect better perfomance\n\n");
                document.add(imgp);

            } 

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }

        if (baosRTF.size() < 1) {
            throw new DocumentException(
                    "document has "
                    + baosRTF.size()
                    + " bytes");
        }
        return baosRTF;
    }

    private static Image createChartImage(JFreeChart chart, int nsize)
            throws DocumentException, IOException {
        BufferedImage bi = null;
        if(nsize==1){        
            bi= chart.createBufferedImage(800, 290);
        }else if(nsize==2){
            bi= chart.createBufferedImage(600, 350);
        }else if(nsize==3){
            bi= chart.createBufferedImage(850, 700);
        }else if(nsize==4){
            bi= chart.createBufferedImage(350, 280);
        }else bi= chart.createBufferedImage(400, 200);
        //createChart().createBufferedImage(320, 180);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(bi, "png", out);
        Image image = Image.getInstance(out.toByteArray());
        image.setAlignment(com.lowagie.text.Image.MIDDLE);
        scaleImage(image);
        return image;
    }
    private static JFreeChart createChart() {
        DefaultCategoryDataset chartData = new DefaultCategoryDataset();
        chartData.addValue((double) 10, "First", "First");
        chartData.addValue((double) 50, "Second", "Second");
        chartData.addValue((double) 100, "Third", "Third");
        JFreeChart chart = ChartFactory.createBarChart3D(
                "Test chart", "Columns", "Values", chartData,
                PlotOrientation.VERTICAL,
                false, false, false);
        chart.setAntiAlias(true);
        chart.setBorderVisible(true);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    protected static void scaleImage(Image image) {
        // if RTF images supposedly look 130% too large, this should work?
        // but they still look too wide.
        float rtfPercent = (72.0f / 96.0f) * 80.0f;
        image.scalePercent(rtfPercent, rtfPercent);

    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
