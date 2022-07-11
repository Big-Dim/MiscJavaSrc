
public class SmsApiResponse {

    protected Integer errorCode	;
    protected String errorMessage;
    protected String errorUrl;
    protected Integer itemCount;
    protected String result;
    protected List<Setting> items;


    public SmsApiResponse(){

    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Setting> getSettingList() {
        return items;
    }

    public List<SettingCrypt> getSettingCryptList() {
        List<SettingCrypt> settingList = new ArrayList<>();
        if(items != null && !items.isEmpty()){
            for (Setting s : items) {
                SettingCrypt sCry = SettingCrypt.of(s.getId(), s.getSettingKey(), s.getSettingValue(), s.getDescription(), true);
                settingList.add(sCry);
            }
        }
        return settingList;
    }

    public List<Setting> getItems() {
        return items;
    }

    public void setItems(List<Setting> items) {
        this.items = items;
    }

 /*   public Setting getSetting() {
        if(items != null && !items.isEmpty())
            return (Setting)items.get(0);
        else
            return null;
    }*/


    public Setting getSetting(String key) {
        if(items != null && !items.isEmpty()){
            for (Setting s : items) {
                if(s.getSettingKey().trim().equals(key))
                    return s;
            }
        }
        return null;
    }

    public SettingCrypt getSettingCrypt(String key) {
        if(items != null && !items.isEmpty()){
            for (Setting s : items) {
                if(s.getSettingKey().trim().equals(key))
                    return SettingCrypt.of(s.getId(), s.getSettingKey(), s.getSettingValue(), s.getDescription(), true);
            }
        }
        return null;
    }

}
