
public class SettingsManagementBean extends FonbetControlPanelBean {

    public static final String BAD_KEY = "settings.bad_key";
    public static final String GOOD_DELETE = "settings.good_delete";
    public static final String BAD_MODE = "settings.bad_mode";
    public static final String BAD_VALUE = "settings.bad_value";
    public static final String CANCEL_SETTINGS = "settings.cancel_settings";
    public static final String CHANGE_SETTING = "settings.change_setting";
    public static final String COLOR_RED = "#ffd5d5";
    public static final String CREATE_SETTING = "settings.create_setting";
    private static final String FILTER_PARAM = "filter";
    private static final String KEY_PARAM = "key";
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsManagementBean.class);
    private static final String READ_DONE = "Настройки прочитаны";
    private static final String SECURITY_MODE_SELECTED = "Выбран режим безопасных настроек";
    private static final String SECURITY_SETTING = "settings.security";
    private static final char SPEC_SYMBOL = '$';
    private static final String VALUE_PARAM = "value";
    private String description;
    private String filterValue;
    private String pack;
    private StringBuilder report = new StringBuilder();
    private boolean securityMode = false;
    private boolean securityModeForPack = false;
    private boolean securityRole = false;
    private String selectedSettingKey;
    private String settingKey;
    private List<AbstractSetting> settingList = new ArrayList<>();
    private String settingValue;
    private SettingsLocal settingsBean;
    private boolean smsMode;
    private String comment;

    private String msg;
    private String user;
    private String addr;

    public boolean isSecurityMode() {
        return securityMode;
    }

    public void delete() {
        if (settingKey == null || settingKey.isEmpty()) {
            FacesMessagesUtils.error(BAD_KEY);
            if(!smsMode)
                report.append(FacesMessagesUtils.getMessage(BAD_KEY));
            return;
        }
        if (settingValue == null) {
            settingValue = "";
        }

        if (securityMode) {
            settingsBean.removeCrypt(settingKey, user, addr);
        } else {
            settingsBean.remove(settingKey, user, addr);
        }

        if(!smsMode)
            report.append(FacesMessagesUtils.getMessage(GOOD_DELETE)).append('\n');


        updateSettingList();
        setSelectedSettingAttr(Setting.of("", "", "", ""));
    }

    public boolean filter(Object o) {
        if (filterValue == null || filterValue.isEmpty()) {
            return true;
        }
        if (!(o instanceof AbstractSetting)) {
            return false;
        }
        AbstractSetting setting = (AbstractSetting) o;

        if ((setting.getSettingKey() != null && setting.getSettingKey().contains(filterValue)) || (setting.getSettingValue() != null && setting.getSettingValue().contains(filterValue))
            || (setting.getDescription() != null && setting.getDescription().contains(filterValue))) {
            return true;
        }
        return false;
    }

    public String getCOLOR_RED() {
        return COLOR_RED;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        this.comment ="";
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        if(!smsMode) {
            if (filterValue == null && this.filterValue != null) {
                settingsBean.setLog(msg, "Clear filter", this.filterValue, user, addr);
            } else if (filterValue != null && this.filterValue == null) {
                settingsBean.setLog(msg, "Set filter - " + filterValue, null, user, addr);

            } else if (filterValue != null && !filterValue.equals(this.filterValue)) {
                settingsBean.setLog(msg, "Change filter - " + filterValue, this.filterValue, user, addr);
            }
        }
        this.filterValue = filterValue;
    }

    public String getPack() {
        pack = "";
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getReport() {
        return report.toString();
    }

    public String getSelectedSettingKey() {return selectedSettingKey;}

    public void setSelectedSettingKey(String selectedSettingKey) {
        this.selectedSettingKey = selectedSettingKey;
    }

    public String getSettingKey() {return settingKey;}

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public List<AbstractSetting> getSettingList() {
        return settingList;
    }

    public void setSettingList(List<AbstractSetting> settingList) {
        this.settingList = settingList;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public void initSMSSettings() {
        smsMode = true;
        initSettings();
        refreshData();
    }

    @Override
    public void processRequestParams() {
        String filterStr = (String) NavigationUtils.getHttpParam(FILTER_PARAM);

        String key = null;

        if (filterStr != null) {
            key = filterStr;
        } else {
            String keyStr = (String) NavigationUtils.getHttpParam(KEY_PARAM);
            String valueStr = (String) NavigationUtils.getHttpParam(VALUE_PARAM);

            if (keyStr != null && valueStr != null) {
                settingKey = keyStr;
                settingValue = URLDecoder.decodeUTF8(valueStr);
                settingValue = URLDecoder.decodeUTF8(valueStr);
                save();

                key = settingKey;
            }
        }
        if (key != null) {
            AbstractSetting s;
            if (securityMode) {
                s = settingsBean.getCryptSetting(key, user, addr);
            } else {
                s = settingsBean.getSetting(key, user, addr);
            }
            if (s != null) {
                setSelectedSettingAttr(s);
            }
        }
    }

    public void refreshData() {
        updateSettingList();
        setSelectedSettingAttr(Setting.of("", "", "", ""));
        filterValue = null;
    }

    public void save() {
        try {
            securityRole = isUserInRole(Roles.SETTINGS_SECURITY_OPERATOR);
            if (settingKey != null && !settingKey.isEmpty()) saveOne(settingKey, settingValue, description, comment);
            if (pack != null && !pack.isEmpty()) {
                Properties p = preparePack();
                report.append(READ_DONE).append('\n');
                if (!checkSecuredPack(p)) return;
                savePack(p);
            }
        } catch (BadSettingException e) {
            report.append(e.getMessage());
            report.append('\n');
        }
        updateSettingList();
    }

    public void saveOne(String key, String val, String description, String comment) throws BadSettingException {
        user = FacesContextUtils.getRemoteUser();
        addr = FacesContextUtils.getRemoteAddr();

        if (key != null && !key.isEmpty()) {
            if (!isValidValue(key, val)) return;
            key = prepareKeyAndSecureMode(key);
            SmsSettingListRequestGet request = new SmsSettingListRequestGet(key, val, description, comment, user);

            if (securityModeForPack) {
                settingsBean.setCrypt(key, val, description, FacesContextUtils.getRemoteUser(), FacesContextUtils.getRemoteAddr(), true);
                if(!smsMode)
                    report.append(key).append(" ").append(FacesMessagesUtils.getMessage(SECURITY_SETTING)).append(" - ").append(getLogMessageForSetting(key));
            } else {
                settingsBean.set(key, val, description, comment, user, addr);
                if(!smsMode)
                    report.append(key).append(" - ").append(getLogMessageForSetting(key)).append('\n');
            }
        }
    }

    public void selectSetting() {
        user = FacesContextUtils.getRemoteUser();
        addr = FacesContextUtils.getRemoteAddr();

        AbstractSetting s = null;

        s = securityMode ? settingsBean.getCryptSetting(getSelectedSettingKey(), user, addr) : settingsBean.getSetting(getSelectedSettingKey(), user, addr);
        if(s != null)
            setSelectedSettingAttr(s);
        if(!smsMode)
            settingsBean.setLog(msg, "Select setting: settingKey - "
                        + (securityMode ? "encode value" : settingKey) + ", settingValue - " + (securityMode ? "encode value" : settingValue)
                        + ", description - " + description + ", securityMode - " + String.valueOf(securityMode)
                , null, user, addr);
    }

    public void setSecurityMode(boolean securityMode) {
        if((this.securityMode != securityMode) && !smsMode){
            settingsBean.setLog( msg, "Changed  securityMode - " + String.valueOf(securityMode), String.valueOf(this.securityMode), user, addr );
        }
        this.securityMode = securityMode;
    }

    @Override
    public void show() {
        getNavigationBean().showSettingsManagement();
        smsMode = false;
        initSettings();
        updateSettingList();
        setSelectedSettingAttr(Setting.of("", "", "",""));
    }

    public void initSettings() {
        user = FacesContextUtils.getRemoteUser();
        addr = FacesContextUtils.getRemoteAddr();

        securityMode = false;
        if (smsMode) {
            settingsBean = lookupLocal(SMSSettingsBean.class);
            msg = "Manage SMS settings";
        } else {
            settingsBean = lookupLocal(SettingsBean.class);
            msg = "Manage settings";
        }
    }

     private boolean checkSecuredPack(Properties p) {
         if (this.securityMode) {
            report.append(SECURITY_MODE_SELECTED).append('\n');
            boolean exception = false;
            for (Object keyObject : p.keySet()) {
                String key = (String) keyObject;
                if (key.charAt(0) == SPEC_SYMBOL) {
                    report.append(key).append(": ").append(FacesMessagesUtils.getMessage(BAD_MODE)).append('\n');
                }
            }
            if (exception) {
                report.append(FacesMessagesUtils.getMessage(CANCEL_SETTINGS)).append('\n');
                FacesMessagesUtils.error(BAD_MODE);
                return false;
            }
        }
        return true;
    }

    private String getLogMessageForSetting(String key) {
        return isExistsSetting(key) ? FacesMessagesUtils.getMessage(CHANGE_SETTING) : FacesMessagesUtils.getMessage(CREATE_SETTING);
    }

    private boolean isExistsSetting(String key) {
        final AbstractSetting existsSetting = securityModeForPack ?
            settingsBean.getCryptSetting(key, user, addr) : settingsBean.getSetting(key, user, addr);
        boolean exists = existsSetting != null && existsSetting.getSettingValue() != null && !existsSetting.getSettingValue().isEmpty();
        if (exists && description == null) {
            description = existsSetting.getDescription();
        }
        return exists;
    }

    private boolean isValidValue(String key, String val) {
        if (val == null || val.isEmpty()) {
            report.append(FacesMessagesUtils.getMessage(BAD_VALUE, key)).append('\n');
            return false;
        }
        return true;
    }

    private String prepareKeyAndSecureMode(String key) throws BadSettingException {
        if (key.charAt(0) == SPEC_SYMBOL) {
            if (this.securityMode || !securityRole) {
                FacesMessagesUtils.error(BAD_MODE);
                throw new BadSettingException(FacesMessagesUtils.getMessage(BAD_MODE));
            }
            securityModeForPack = true;
            key = key.substring(1);
            if (key.isEmpty()) {
                FacesMessagesUtils.error(BAD_KEY);
                throw new BadSettingException(FacesMessagesUtils.getMessage(BAD_KEY));
            }
        } else {
            securityModeForPack = this.securityMode;
        }
        return key;
    }

    private Properties preparePack() {
        pack = pack.trim();
        Properties p = new Properties();
        try {
            p.load(new StringReader(pack));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return p;
    }

    private void savePack(Properties p) throws BadSettingException {
        settingsBean.setSetOther(msg, "Save Pack", null, user, addr );
        for (Object keyObject : p.keySet()) {
            String key = (String) keyObject;
            saveOne(key, p.getProperty(key), null,"point1");
        }
    }

    private void setSelectedSettingAttr(AbstractSetting selectedSetting) {
        settingKey = selectedSetting.getSettingKey();
        settingValue = selectedSetting.getSettingValue();
        description = selectedSetting.getDescription();
    }

    private void updateSettingList() {
        settingList.clear();
        if (securityMode) {
            settingList.addAll(settingsBean.findAllCrypt(user, addr));
        } else {
            settingList.addAll(settingsBean.findAll(user, addr));
        }

        if (settingList == null || settingList.isEmpty()) {
            setSelectedSettingAttr(Setting.of("", "", "", ""));
        }

    }

 }
