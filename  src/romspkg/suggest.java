
// Source File Name:   suggest.java

package romspkg;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

// Referenced classes of package romspkg:
//            Cli10,, subsession, User

public class suggest extends HttpServlet
{

    public suggest()
    {
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        
        ComOpr op = new ComOpr();
        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        javax.servlet.http.HttpSession session = request.getSession();
        String ret = "ERROR suggest";
        subsession sub = op.getsub(request);
        User user = sub.getUser();
        if(user == null)
        {
            ret = "TO";
            response.getWriter().write(ret);
            return;
        }
        ret = "[";
        String out = "";
        try
        {
            String npar = request.getParameter("npar");
            String subst = request.getParameter("subst");
            if(subst.length() > 8)
                subst = subst.substring(0, 8);
            String par = user.getfacilityID() + "\t" + subst + "\t" + npar;
            String proc = "suggst";
            Cli10 c10 = new Cli10(proc, par);
            c10.getOutput();
            out = c10.out;
            System.out.println(proc + ": " + par);
            c10.set(0);
            for(int i = 0; i < c10.rowsize(); i++)
            {
                ret = ret + "'";
                for(int j = 0; j < c10.colsize(); j++)
                {
                    String val = c10.getString(i, j);
                    ret = ret + val + "|";
                }

                ret = ret + "',\n";
            }

        }
        catch(Throwable e)
        {
            System.err.println(e);
            ret = ret + "***Error***";
        }
        ret = ret + "]";
        response.getWriter().write(ret);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        processRequest(request, response);
    }

    public String getServletInfo()
    {
        return "Short description";
    }

    protected String replaceString(String aSearch, String aFind)
    {
        String result = aSearch;
        String aS = aSearch.toUpperCase();
        String aF = aFind.toUpperCase();
        if(result != null && result.length() > 0)
        {
            int a = 0;
            a = aS.indexOf(aF, 0);
            if(a == 0)
                result = "<b>" + result.substring(0, aFind.length()) + "</b>" + result.substring(a + aFind.length());
        }
        return result;
    }
}