package romspkg;

import java.util.StringTokenizer;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;


public class patient implements HttpSessionBindingListener {


    public String patID = "";
    public String First_Name = "";
    public String Last_Name = "";
    public String Middle_Name = "";
    public String Gender = "";
    public String Marital_Status = "";
    public String Date_Birth = "";
    public String Language = "";
    public String Home_Phone = "";
    public String Occupation = "";
    public String Street = "";
    public String City = "";
    public String Province = "";
    public String Postal = "";
    public String Employer = "";
    public String FundingType = "";
    public String Insurer = "";
    public String Cliam = "";
    public String ReferredBy = "";
    public String CaseMgtFirm = "";
    public String FamilyDoctor = "";
    public String ID = "";
    public String Education = "";
    public String English = "";
    public String Canada = "";

    public combob cb = null;
    public combob ct = null;

    public boolean saved = true;   //var sostoyaniya formy na HTML forme
    public boolean isNew = true;   //var sostoyaniya v Progress DB exist or does not exist
    public tests  ts = new tests();
  
    public patient(User user) {
     //   get all comboboxes for pat profile and tests
        String out="";
        String par = "";//user.getfacilityID() + "\t"+ user.getUserId() + "\t" + patID;
        String proc = "getcmbpat";
        try {

            Cli10 c10 = new Cli10(proc,par);
            c10.getOutput();
            out = c10.out;
            if (out.equals("OK")){
                   cb = new combob(c10);
            }

            // get test combo
          proc =  "getcmbtst";
            c10 = new Cli10(proc,par);
            c10.getOutput();
            out = c10.out;
            if (out.equals("OK")){
                   ct = new combob(c10);
            }

         } catch (Throwable e) {
            System.out.print(Constants.logTimeFormatter.format(new java.util.Date()) + " " + this.getClass().getName() + " ");
            System.out.println(e.getMessage());
        }

    }

    public String put(User user, String PatientID,
     String First_Name,   String Last_Name,  String Middle_Name,
     String Gender,  String Marital_Status,  String Date_Birth,
     String Language,  String Home_Phone,  String Occupation,
     String Street, String City,  String Province, String Postal,
     String Employer,  String FundingType,  String Insurer,
     String Cliam,  String ReferredBy,  String CaseMgtFirm,
     String FamilyDoctor,  String ID,  String Education,
     String English,   String Canada ) {



        String out = "Error write profile";

        if (!this.patID.equals(PatientID) ||  !this.First_Name.equals(First_Name) ||
                !this.Last_Name.equals(Last_Name) || !this.Middle_Name.equals(Middle_Name) ||
                !this.Gender.equals(Gender) || !this.Marital_Status.equals(Marital_Status) ||
                !this.Date_Birth.equals(Date_Birth) || !this.Language.equals(Language) ||
                !this.Home_Phone.equals(Home_Phone) || !this.Occupation.equals(Occupation) ||
                !this.Street.equals(Street) || !this.City.equals(City) || !this.Province.equals(Province) ||
                !this.Postal.equals(Postal) || !this.Employer.equals(Employer) ||
                !this.FundingType.equals(FundingType) || !this.Insurer.equals(Insurer) ||
                !this.Cliam.equals(Cliam) || !this.ReferredBy.equals(ReferredBy) ||
                !this.CaseMgtFirm.equals(CaseMgtFirm) || !this.FamilyDoctor.equals(FamilyDoctor) ||
                !this.ID.equals(ID) ||  !this.Education.equals(Education) ||
                !this.English.equals(English) ||!this.Canada.equals(Canada)
                ) {

            this.patID = Globals.notNull(PatientID);
            this.First_Name = Globals.notNull(First_Name);
            this.Last_Name = Globals.notNull(Last_Name);
            this.Middle_Name = Globals.notNull(Middle_Name);
            this.Gender = Globals.notNull(Gender);
            this.Marital_Status = Globals.notNull(Marital_Status);
            this.Date_Birth = Globals.notNull(Date_Birth);
            this.Language = Globals.notNull(Language);
            this.Home_Phone = Globals.notNull(Home_Phone);
            this.Occupation = Globals.notNull(Occupation);
            this.Street = Globals.notNull(Street);
            this.City = Globals.notNull(City);
            this.Province = Globals.notNull(Province);
            this.Postal = Globals.notNull(Postal);
            this.Employer = Globals.notNull(Employer);
            this.FundingType = Globals.notNull(FundingType);
            this.Insurer = Globals.notNull(Insurer);
            this.Cliam = Globals.notNull(Cliam);
            this.ReferredBy = Globals.notNull(ReferredBy);
            this.CaseMgtFirm = Globals.notNull(CaseMgtFirm);
            this.FamilyDoctor = Globals.notNull(FamilyDoctor);
            this.ID = Globals.notNull(ID);
            this.Education = Globals.notNull(Education);
            this.English = Globals.notNull(English);
            this.Canada = Globals.notNull(Canada);

            out = writePatProfile(user);
            
        } else {
            saved = true;
            out = "OK";
        }
        return out + "\t" + this.Last_Name + " " + this.Middle_Name + " " + this.First_Name
                + "  " + this.Home_Phone;
    }

    private String writePatProfile(User user) {

        String out = "FALSE";       
        try {
            String par = user.getfacilityID()+ "\t" + user.getUserId() + "\t" + patID + "\t" +
                    First_Name + "\t" + Last_Name + "\t" + Middle_Name + "\t" +
                    Gender + "\t" + Marital_Status + "\t" + Date_Birth + "\t" +
                    Language + "\t" + Home_Phone + "\t" + Occupation + "\t"+
                    Street + "\t" + City + "\t" + Province + "\t" + Postal + "\t" +
                    Employer + "\t" + FundingType + "\t" + Insurer + "\t" +
                    Cliam + "\t" + ReferredBy + "\t" + CaseMgtFirm + "\t" +
                    FamilyDoctor + "\t" + ID + "\t" + Education + "\t" +
                    English + "\t" + Canada  ;

            String proc = "patprofsave";
            Cli10 c10 = new Cli10(proc,par);
            c10.getOutput();
            out = c10.out;
            if (Constants.debug) {
                System.out.print(Constants.logTimeFormatter.format(new java.util.Date()) + " " + this.getClass().getName());
                System.out.println(" " + proc + " params:" + par);
            }

        } catch (Throwable e) {
            System.err.println(e);
            out = e.getMessage();
        }

        return out;

    }

    private void emptyPat() {
        patID = "";
        First_Name = "";
        Last_Name = "";
        Middle_Name = "";
        Gender = "";
        Marital_Status = "";
        Date_Birth = "";
        Language = "";
        Home_Phone = "";
        Occupation = "";
        Street = "";
        City = "";
        Province = "";
        Postal = "";
        Employer = "";
        FundingType = "";
        Insurer = "";
        Cliam = "";
        ReferredBy = "";
        CaseMgtFirm = "";
        FamilyDoctor = "";
        ID = "";
        Education = "";
        English = "";
        Canada = "";

        ts = new tests();
   }

    public synchronized String getExistPat(User user, String patID) {

        emptyPat();
        String out = "Error getting the patient Profile record";

        String par = user.getfacilityID() + "\t"+ user.getUserId() + "\t" + patID;
        String proc = "patprofget";
        try {

            Cli10 c10 = new Cli10(proc,par);
            c10.getOutput();
            out = c10.out;
            if (out.startsWith("OK")){
                c10.set(1);//set to patient fields recordset
                this.patID = c10.getString(0, 1);
                First_Name = c10.getString(0, 2);
                Last_Name = c10.getString(0, 3);
                Middle_Name = c10.getString(0, 4);
                Gender = c10.getString(0, 5);
                Marital_Status = c10.getString(0, 6);
                Date_Birth = Globals.sql2dt(c10.getString(0, 7));
                Language = c10.getString(0, 8);
                Home_Phone = c10.getString(0, 9);
                Occupation = c10.getString(0, 10);
                Street = c10.getString(0, 11);
                City = c10.getString(0, 12);
                Province = c10.getString(0, 13);
                Postal = c10.getString(0, 14);
                Employer = c10.getString(0, 15);
                FundingType = c10.getString(0, 16);
                Insurer = c10.getString(0, 17);
                Cliam = c10.getString(0, 18);
                ReferredBy = c10.getString(0, 19);
                CaseMgtFirm = c10.getString(0, 20);
                FamilyDoctor = c10.getString(0, 21);
                ID = c10.getString(0, 22);
                Education = c10.getString(0, 23);
                English = c10.getString(0, 24);
                Canada = c10.getString(0, 25);
            }
            if (Constants.debug) {
                System.out.println("xxgetpatprof.p: " + par);
            }
         } catch (Throwable e) {
            System.out.print(Constants.logTimeFormatter.format(new java.util.Date()) + " " + this.getClass().getName() + " ");
            System.out.println(e.getMessage());
        }

        return out;
    }

     public String del(User user){

        String out = "FALSE del patient";

        String par = user.getfacilityID() + "\t"+ user.getUserId() + "\t" + patID ;
        String proc = "patprofdel";
        try {

            Cli10 c10 = new Cli10(proc,par);
            c10.getOutput();
            out = c10.out;

            if (Constants.debug) {
                System.out.println(proc+ ": " + par);
            }

         } catch (Throwable e) {
            System.out.print(Constants.logTimeFormatter.format(new java.util.Date()) + " " + this.getClass().getName() + " ");
            System.out.println(e.getMessage());
        }

        return out;
    }

    public synchronized String getNewPat(User user) {

        emptyPat();
        String out = "Error getting new patient ID";

        String par = user.getfacilityID() + "\t"+ user.getUserId();
        String proc = "patprofnew";
        try {

            Cli10 c10 = new Cli10(proc,par);
            c10.getOutput();
            out = c10.out;
            if (out.startsWith("OK")){
                c10.set(0);//set to patient fields recordset
                this.patID = c10.getString(0, 1);
             }
            if (Constants.debug) {
                System.out.println("patprofnew: " + par);
            }
         } catch (Throwable e) {
            System.out.print(Constants.logTimeFormatter.format(new java.util.Date()) + " " + this.getClass().getName() + " ");
            System.out.println(e.getMessage());
        }

        return out;
    }

    //  public String addRow( String custID,String plate, String year, String item ,
    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
    }
}