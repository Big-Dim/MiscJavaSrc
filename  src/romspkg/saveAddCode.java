/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package romspkg;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
public class saveAddCode extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String ret = "Save error";
        subsession sub = Globals.getsub(request);
        User user = sub.getUser();
        patient pat  = sub.pt;
        if (user == null || pat == null) {
            ret = "TO";
            out.write(ret);
            return;
        }
        String ind = (String) request.getParameter("ind");
        String val = (String) request.getParameter("val");
        String lbl = (String) request.getParameter("lbl");
        String sel = (String) request.getParameter("sel");

        combob cmb  = pat.cb;

        try {
            ret = cmb.put(user, ind, val, lbl, sel);
        }catch (Throwable e) {
            System.err.print(Constants.logTimeFormatter.format(new java.util.Date()) + " " + this.getClass().getName());
            System.err.println(e.getMessage());
            ret = e.getMessage();

        } finally {
            out.write(ret);
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

}
