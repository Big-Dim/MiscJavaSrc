/*
 * updUserForm.java
 *
 * Created on October 9, 2007, 2:03 PM
 */

package romspkg;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Alex1
 * @version
 */
public class mUserDetails extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        //ANSWER TO REQUESTIOR
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");

        HttpSession session = request.getSession();
        romspkg.User user = (romspkg.User)session.getAttribute("user");
        if (user==null) { 
            response.getWriter().write("<user>\n<retvalue>TO</retvalue></user>\n");
            session.setAttribute("msg", "Session Timeout"); 
            return; 
        }
        String userid = user.getUserId();
        String  ret = "<user>\n";

        String id = getPar(request.getParameter("ID"));  
 

  
        Cli2 smc = new Cli2("xxusrdet.p",id,20);
        String[] qadOutput = null;
        cmbx facil = null;
        try{
            
          qadOutput = smc.getOutput();
        
          StringTokenizer st = new StringTokenizer(qadOutput[0],"\t");
          String tag =st.hasMoreTokens()?st.nextToken():"";  //LoginID
          String fn = st.hasMoreTokens()?st.nextToken():"";   //LastName
          String ln = st.hasMoreTokens()?st.nextToken():"";   //FirstName
          String rl = st.hasMoreTokens()?st.nextToken():"";   //Role
          String em = st.hasMoreTokens()?st.nextToken():"";   //e-mail
         
          //facil = new cmbx(qadOutput[1]);
 
            
          
         // Vector v = facil.val;
         // Vector o = facil.optn;
          String fc = "<fc>";
          
          //for (int i = 0; i < v.size(); i++) {
          //  String  vS = (String)v.elementAt(i);
          //  String  oS = (String)o.elementAt(i);
          //  fc +=" &lt;option value='" + vS + "'&gt;" + oS + "&lt;/option&gt; ";
         // }
          fc +="</fc>\n";
          ret +="<tag>" + tag + "</tag>\n" ;
          ret +="<ln>" + ln + "</ln>\n" ;
          ret +="<fn>" + fn + "</fn>\n" ;
          ret +="<rl>" + rl + "</rl>\n" ;
          ret +="<em>" + em + "</em>\n" ;
          
          ret += fc  ;
          
                  
          ret += "<retvalue>OK</retvalue>";
          ret += "</user>\n";
            
 
          
         } catch(Throwable e){
            System.out.println(Constants.formatter.format(new java.util.Date())+" "+this.getClass().getName());
            System.err.println(e);
        }
        response.getWriter().write(ret);
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>

    public String getPar(String par) {
        String ret  = Globals.unescape(par); 
        if(ret==null || ret.length()==0)ret="|";  
    
        return ret;
    }


}


