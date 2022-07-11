
// Source File Name:   updUser.java

package romspkg;

import java.io.*;
import java.text.Format;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.*;

// Referenced classes of package romspkg:
//            User, Cli2, Constants, Globals

public class updUser extends HttpServlet
{

    public updUser()
    {
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if(user == null)
        {
            response.getWriter().write("<retvalue>TO</retvalue>");
            session.setAttribute("msg", "Session Timeout");
            return;
        }
        String userid = user.getUserId();
        String retvalue = "<retvalue>FAILED</retvalue>";
        String act = getPar(request.getParameter("act"));
        String tag = getPar(request.getParameter("tag"));
        String fn = getPar(request.getParameter("fn"));
        String ln = getPar(request.getParameter("ln"));
        String fc = getPar(request.getParameter("fc"));
        String rl = getPar(request.getParameter("rl"));
        String pwd = getPar(request.getParameter("pwd"));
        String parms = act + "\t" + tag + "\t" + fn + "\t" + ln + "\t" + fc + "\t" + rl + "\t" + pwd;
        try
        {
            Cli2 smc = new Cli2("xxusrupd.p", parms);
            String outln[] = smc.getOutput();
            System.out.println("Patient update out;" + outln[0]);
            String res = outln[0] != null ? outln[0] : "FAILED";
            retvalue = "<retvalue>" + res + "</retvalue>";
        }
        catch(Throwable e)
        {
            System.out.println(Constants.formatter.format(new Date()) + " " + getClass().getName());
            System.err.println(e);
        }
        response.getWriter().write(retvalue);
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

    public String getPar(String par)
    {
        String ret = Globals.unescape(par);
        if(ret == null || ret.length() == 0)
            ret = "|";
        return ret;
    }
}