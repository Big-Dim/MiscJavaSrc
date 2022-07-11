
public abstract class SmsApiRequestAbstract {

    protected String date;
    protected Long timeStamp;
    protected String operatorName;
    protected String operatorLogin;

    protected String sign;
 
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    public String getOperatorLogin() {
        return operatorLogin;
    }

    public void setOperatorLogin(String operatorLogin) {
        this.operatorLogin = operatorLogin;
    }

     public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getURIparams() {
        try {
            return    "?timeStamp=" + String.valueOf(timeStamp) + "&operatorName=" + URLEncoder.encode(operatorName, "UTF-8") + "&sign=" + sign;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public abstract String getBody() ;

}
