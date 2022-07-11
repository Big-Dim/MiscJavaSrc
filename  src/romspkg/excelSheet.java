// Source File Name:   excelSheet.java
package romspkg;

import java.io.*;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.*;

// Referenced classes of package romspkg:
//            Cli2
public class excelSheet extends HttpServlet {
    ComOpr op;
    public excelSheet() {
        op = new ComOpr();
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        PrintWriter out = null;
        String ret;
        subsession sub;
        boolean nocol;
        int rnum;
        String par;
        String proc;
        String path = req.getParameter("path");
        try {
            sub = op.getsub(req);
            User user = sub.getUser();
            patient pat = sub.pt;
            if (user == null || pat == null) {
                ret = "TO";
                return;
            }
            
            par = op.aggrPar(req, user, sub, 1);
            proc = "exp_aggr_pat";
            Cli10 c10 = new Cli10(proc, par);
            c10.getOutput();

           // File xlsFile = new File("exp_data.xls");
           // Workbook wb = Workbook.getWorkbook(xlsFile);
           //Sheet sh = wb.getSheet(0);
            // int rows;// = sh.getRows();
            res.setContentType("application/vnd.ms-excel");
            WritableWorkbook w = Workbook.createWorkbook(res.getOutputStream());

            WritableFont font = new WritableFont(WritableFont.createFont("Arial"), 10);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.RIGHT);
            format.setVerticalAlignment(VerticalAlignment.JUSTIFY);
            format.setBorder(Border.ALL, BorderLineStyle.THIN);
            //int numRow = 0;
            int numSet = 1;
            ResultSetMetaData colInfo;
            ArrayList<String> colNames = new ArrayList<String>();
            ArrayList<String> colValues = new ArrayList<String>();
            String[] tName = new String[]{"---","Patient", "additional", "checklist", "problems", "coping",
                 "oswestry", "neckindex", "markers", "markeranalysis", "essmarkers",
                "radlsb", "radlsc", "radlss"};
            int beg=0;
            int col=0;
            int col_hd =0;
            float fval;
            float calc;
            int ntst;
            String fStr;
            int pars[][] = {{2, 18, 3, 5, 7, 8, 11, 12, 15, 23, 26},
            {6, 10, 14, 16, 19, 21, 22, 24, 25, 0, 0},
            {4, 9, 13, 17, 0, 0, 0, 0, 0, 0, 0}
            };
            String[][] addlbl =new String[][] {{"Physical Domains","Emotional Domains","Cognitive Domains"}
            };
       
            if (c10.out.startsWith("OK")) {
               // for (int k = 1; k < tName.length; k++) {
               for (int k = 1; k < 14; k++) {
                    c10.set(k);
                    colInfo = c10.arrRSMD.get(k);
                    WritableSheet s = w.createSheet(tName[k], k - 1);
                    

                    colNames.clear();
                    String kName;
                    String val;
                    fval=0;
                    for (int i = 1; i <= colInfo.getColumnCount(); i++) {
                        kName = colInfo.getColumnName(i);
                        colNames.add(kName);
                        //s.addCell(new Label(col++, 0, kName, format));
                    }
                    
                    for (int i = 0; i < c10.rowsize(); i++) {
                    /*    s.insertRow(i+1);
                        colValues.clear();
                        for (int j = beg; j < c10.colsize(); j++) {
                            colValues.add(c10.getString(i, j));
                            //s.addCell(new Label(col++, i + 1, c10.getString(i, j), format));
                        }*/
                          if(k == 3){                            
                             if (i == 0) {
                                col_hd = 0;
                                s.insertRow(0);
                                for (int f = 0; f < colNames.size(); f++) {
                                    kName = colNames.get(f);
                                    s.addCell(new Label(col_hd++, 0, kName, format));
                                }
                                s.addCell(new Label(col_hd, 0, "Life Role Aggregate", format));
                                
                            }
                            col = 0;
                            s.insertRow(i + 1);
                            calc =0 ;
                            ntst =0;        
                            for (int j = 0; j < c10.colsize(); j++) {
                                val =  c10.getString(i, j);
                                fval = tofl(val, -1F);
                                if(j > 2 && j < 17 && fval >-1F){
                                    calc += fval;
                                    ntst++;
                                }
                                s.addCell(new Label(col++, i + 1, val, format));
                            }
                            fStr = new java.text.DecimalFormat("0.00").format(calc/ntst); 
                            s.addCell(new Label(col, i+1, fStr, format));
                            
                          }else if(k == 4 || k == 5){                            
                            for (int npar = 0; npar < 3; npar++) {
                                if (i == 0) {                                    
                                    if(npar ==0){
                                        s.insertRow(0);
                                        col_hd=3;
                                        s.addCell(new Label(0, 0, "Patient", format));
                                        s.addCell(new Label(1, 0, "Test Num", format));
                                        s.addCell(new Label(2, 0, "Test Date", format));
                                   }
                                    for (int f = 0; f < 11; f++) {
                                        if(pars[npar][f] == 0) continue;
                                        kName = colNames.get(pars[npar][f]+2);
                                        s.addCell(new Label(col_hd++, 0, kName, format));
                                    }
                                    s.addCell(new Label(col_hd++, 0, addlbl[0][npar], format));
                                }
                                if(npar == 0){
                                    s.insertRow(i + 1);
                                    col=3;
                                    s.addCell(new Label(0, i+1, c10.getString(i, 1), format));
                                    s.addCell(new Label(1, i+1, c10.getString(i, 2), format));
                                    s.addCell(new Label(2, i+1, c10.getString(i, 3), format));
                                }
                                
                                calc=0;
                                ntst = 0;
                                for (int j = 0; j < 11; j++) {
                                    if(pars[npar][j] == 0) continue;
                                    int num = pars[npar][j]+2;
                                    val =  c10.getString(i, num);
                                    s.addCell(new Label(col++, i + 1, val, format));
                                    fval = tofl(val, -1F);
                                    if (fval > -1F) {
                                        calc += fval;
                                        ntst++;
                                    }
                                }
                                fStr = new java.text.DecimalFormat("0.00").format(calc/ntst); 
                                s.addCell(new Label(col++, i+1, fStr, format));

                              }
                        
                        }else if(k > 10){  
                            if (i == 0) {
                                col_hd = 1;
                                s.insertRow(0);
                                s.addCell(new Label(0, i+1, colNames.get(0), format));
                                for (int f = 2; f < colNames.size(); f++) {
                                    kName = colNames.get(f);
                                    s.addCell(new Label(col_hd++, 0, kName, format));
                                }
                            }
                            col = 1;
                            s.insertRow(i + 1);
                            s.addCell(new Label(0, i+1, c10.getString(i, 0), format));
                            for (int j = 2; j < c10.colsize(); j++) {
                                s.addCell(new Label(col++, i + 1, c10.getString(i, j), format));
                            }
                             
                        }else{  
                            if (i == 0) {
                                col_hd = 0;
                                s.insertRow(0);
                                for (int f = 1; f < colNames.size(); f++) {
                                    kName = colNames.get(f);
                                    s.addCell(new Label(col_hd++, 0, kName, format));
                                }
                            }
                            col = 0;
                            s.insertRow(i + 1);
                            for (int j = 1; j < c10.colsize(); j++) {
                                s.addCell(new Label(col++, i + 1, c10.getString(i, j), format));
                            }
                         }
                      }                                                
                        
                  
                   // beg =1;
                   // if(k > 10)beg=2;

                }

            }

 //           WritableSheet s;
            //           s = (WritableSheet)sh;
 /*           WritableCellFormat headerFont = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 9, WritableFont.BOLD, true));
             WritableCellFormat headerFnR = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 9, WritableFont.BOLD, true));
             WritableCellFormat headerFnL = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 9, WritableFont.BOLD, true));
             WritableCellFormat chrFont = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD, false));
             WritableCellFormat numFont = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD, false));
             WritableCellFormat dteFont = new WritableCellFormat(new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD, false));
             headerFont.setAlignment(Alignment.CENTRE);
             headerFnR.setAlignment(Alignment.RIGHT);
             headerFnL.setAlignment(Alignment.LEFT);
             chrFont.setAlignment(Alignment.LEFT);
             numFont.setAlignment(Alignment.RIGHT);
             dteFont.setAlignment(Alignment.CENTRE);
             int y = 0;
             s.addCell(new Label(0, y, "Part#", headerFnL));
             s.setColumnView(0, 25);
             s.addCell(new Label(1, y, "Description", headerFnL));
             s.setColumnView(1, 35);
             s.addCell(new Label(2, y, "Value", headerFont));
             s.setColumnView(2, 12);
             s.addCell(new Label(3, y, "Quantity", headerFont));
             s.setColumnView(3, 15);
             s.addCell(new Label(4, y, "STD Cost", headerFont));
             s.setColumnView(4, 15);
             s.addCell(new Label(5, y, "*Turns", headerFont));
             s.setColumnView(5, 15);
             s.addCell(new Label(6, y, "*Usage Units", headerFont));
             s.setColumnView(6, 15);
             s.addCell(new Label(7, y, "Usage Dollars", headerFont));
             s.setColumnView(7, 15);
             s.addCell(new Label(8, y, "Day Of Stock", headerFont));
             s.setColumnView(8, 15);
             s.addCell(new Label(9, y, "Open Purchases", headerFont));
             s.setColumnView(9, 15);
             s.addCell(new Label(10, y, "$ Due Before EOQ", headerFont));
             s.setColumnView(10, 15);
             s.addCell(new Label(11, y, "$ Due After EOQ", headerFont));
             s.setColumnView(11, 15);
             WritableCellFormat nf = new WritableCellFormat(new NumberFormat("#,##0.00;[Red](#,##0.00)")); */
            w.write();
            w.close();
        } catch (IOException e) {
            throw new ServletException("Exception in Excel Sample Servlet", e);
        } catch (WriteException e) {
            throw new ServletException("Exception in Excel Sample Servlet", e);
        } catch (SQLException ex) {
            Logger.getLogger(excelSheet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(excelSheet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (out != null) {
            out.close();
        }
        /* break MISSING_BLOCK_LABEL_831;
         Exception exception;
         exception;
         if(out != null)
         out.close();
         throw exception;*/
    }

    private float tofl(String s, float nul){
        float ret = nul;
        try
        {
            ret = Float.parseFloat(s);
        }
        catch(NumberFormatException e) { }
        return ret;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
