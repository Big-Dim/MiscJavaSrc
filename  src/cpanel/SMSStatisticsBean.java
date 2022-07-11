
public final class SMSStatisticsBean extends FonbetControlPanelBean {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
    private static final String DESCR_DEALER_KEY = "sms_smsStatistics_descrDealaer";
    private static final String WRONG_STATE_KEY = "sms_smsStatistics_wrongState";
    private static final String SMSSTATUS_PREFIX = SMSStatus.class.getSimpleName().toLowerCase() + "_title_";
    //
    private static final String BLUE_COLOR = "#0000ff";
    private static final String RED_COLOR = "#ff0000";
    private static final int ALL = -1;
    private static final int ONE_ITEM = 1;

    SMSDealerFeatures grFeatures = new GrFeatures();
    SMSDealerFeatures redFeatures = new RedFeatures();
    SMSDealerFeatures atlanticBetFeatures = new AtlanticBetFeatures();
    SMSDealerFeatures fonFeatures = new FonFeatures();
    SMSDealerFeatures byFeatures = new ByFeatures();
    SMSDealerFeatures cyprusFeatures = new CyprusFeatures();
    SMSDealerFeatures kzFeatures = new KzFeatures();

    List<SMSDealerFeatures> dealerFeatures = new ArrayList<>();

    {
        List<SMSDealerFeatures> features = Arrays.asList(grFeatures, redFeatures, atlanticBetFeatures, fonFeatures, byFeatures, cyprusFeatures, kzFeatures);
        for (SMSDealerFeatures feature : features) {
            if (checkRolesAccessAll(feature.getClass())) {
                dealerFeatures.add(feature);
            }
        }

        if (dealerFeatures.size() == 1) {
            dealerFeatures.get(0).setSelected(true);
        }
    }

    private static Map<SMSStatus, String> statusLabels = new ConcurrentHashMap<>();
    private List<SMSWrapper> smsList = new ArrayList<>();
    private final SMSFilterQuery query = new SMSFilterQuery();
    private String clientPin;
    private List<TypedSelectItem<TimeFilter>> timeFilters = new ArrayList<>();
    private List<TypedSelectItem<Integer>> providerFilters = new ArrayList<>();
    private List<TypedSelectItem<Integer>> phoneCountryFilters = new ArrayList<>();
    //
    private OperatorActionLogLocal actionLog = lookup("OperatorActionLogBean/local");

    private Integer selectedProviderFilter = ALL;
    private int selectedPhoneCountry = ALL;
    private TimeFilter selectedTimeFilter;
    private boolean isIntervalFilter = false;
    private Date since;
    private Date till;
    private SMSStatusWrapper selectedSMSStatus = SMSStatusWrapper.ALL;
    private List<String> selectedSMSThemes = new ArrayList<>();
    private List<SelectItem> statuses = new ArrayList<>();
    private List<SelectItem> themes = new ArrayList<>();
    private boolean intervalNotSelected = false;
    private boolean isIncorrectPin = false;
    private final SMSLocal smsBean = lookup("SMSBean/local");
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSStatisticsBean.class);
    private String phone;
    private boolean pollEnable = false;
    private String hourColor;
    private String dayColor;
    private boolean showSMS = true;
    private Set<Integer> rowsForUpdate;

    // dealer filter checbox values

    private Long smsCount;

    private OperatorLocal operatorLocal = JNDIHelper.lookupLocal(OperatorBean.class);

    private Set<Long> showedTextSms = new HashSet<>();

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public enum SMSStatusWrapper {

        ALL,
        QUEUED,
        DELIVERED,
        REDIRECTED_TOSMSC,
        FAILED,
    }

    enum SMSThemeWrapper {
        DEFAULT(SMSTheme.DEFAULT),
        //SUBSCRIPTION(SMSTheme.SUBSCRIPTION),
        //UNSUBSCRIPTION(SMSTheme.UNSUBSCRIPTION),
        CHANGE_PHONE(SMSTheme.PHONE_CHANGE),
        PAYMENT(SMSTheme.PAYMENT),
        TICKET_ANSWER(SMSTheme.TICKET_ANSWER),
        PASSWORD(SMSTheme.PASSWORD),
        //MOBILE(SMSTheme.MOBILE),
        //DEPOSIT(SMSTheme.DEPOSIT),
        //BETS(SMSTheme.BETS),
        ONE_TIME_PASSWORD(SMSTheme.ONE_TIME_PASSWORD),
        MAILING(SMSTheme.MAILING),
        //ACCOUNT_BINDING(SMSTheme.ACCOUNT_BINDING),
        //PIN_CODE(SMSTheme.PIN_CODE),
        PASS_CHANGE(SMSTheme.PASS_CHANGE),
        ADVERTISING(SMSTheme.ADVERTISING),
        MOBILE_APP(SMSTheme.MOBILE_APP),
        PHONE_CONFIRMATION(SMSTheme.PHONE_CONFIRMATION),
        //POMADORRO(SMSTheme.POMADORRO),
        //ACTUAL_MIRROR(SMSTheme.ACTUAL_MIRROR),
        CPS_REGISTRATION_V3(SMSTheme.CPS_REGISTRATION_V3),
        //MIGRATION(SMSTheme.MIGRATION),
        //BALANCE_CHANGE(SMSTheme.BALANCE_CHANGE),
        CONFIRMATION_CODE(SMSTheme.CONFIRMATION_CODE),
        INFO(SMSTheme.INFO);
        SMSTheme nativeTheme;

        public SMSTheme getNativeTheme() {
            return nativeTheme;
        }

        SMSThemeWrapper(SMSTheme nativeTheme) {
            this.nativeTheme = nativeTheme;
        }
    }

    public class SMSWrapper {

        private final SMS sms;
        private final Integer opId;
        private boolean showText;

        public void setShowText(boolean showText) {
            this.showText = showText;
        }

        public boolean isShowText() {
            return showText;
        }

        SMSWrapper(SMS sms, Integer opId, boolean showText) {
            this.sms = sms;
            this.opId = opId;
            this.showText = showText;
        }

        public Long getId() {
            return sms.getId();
        }

        public String getDestNumber() {
            return sms.getPhoneNumber();
        }

        public int getClientPin() {
            return sms.getClientPin();
        }

        public String getText() {
            return AESConfig.decrypt(sms.getCryptText());
        }

        private Dealer getDealer() {
           /* if (sms.getProviderCode() == 0) {
                // не известен провайдер - по дефолту фон.
                return Dealer.FON;
            }
            return SMSProviderType.getByCode(sms.getProviderCode()).getDealer();*/
           if(sms.getDealer() == null){
               return Dealer.FON;
           }

           return sms.getDealer();
        }

        public String getDealerColor() {
            return DealerColors.get(getDealer());
        }

        public String getCreateTime() {
            return dateFormat.format(sms.getCreateTime());
        }

        public String getStatus() {
            return getSMSStatusLabel(sms.getStatus());
        }

        public int getRepeatCount() {
            return sms.getCount();
        }

        public String getProviderName() {
            return SMSProviderType.getInstanceName(sms.getProviderCode());
        }

        public String getDeliveryTime() {
            return sms.getDeliveryTime() != null ? sms.getDeliveryTime().toString() : "";
        }

        public String getReason() {
            String msg = FacesMessagesUtils.getMessage(DESCR_DEALER_KEY, getDealer());
            return sms.getReason() == null ? msg : msg + sms.getReason();
        }
    }

    public SMSStatisticsBean() {

    }

    public void showProviders() {
         initProviderFilters();
    }

    public void showTextSms() {
        String smsid = FacesContextUtils.getExternalParam("smsid");
        Integer rowid = Integer.parseInt(FacesContextUtils.getExternalParam("rowid"));
        Long id;
        try {
            id = Long.parseLong(smsid);
        } catch (Exception e) {
            return;
        }
        showedTextSms.add(id);
        rowsForUpdate = Collections.singleton(rowid);
        for (SMSWrapper smsw : getListOfSMS()) {
            if (smsw.getId().equals(id)) {
                smsw.setShowText(true);
                actionLog.log(new LogRecord(
                        FacesContextUtils.getRemoteUser(),
                        smsw.sms.getClientPin(),
                        ActionType.VIEW_SMS_TEXT,
                        "",
                        "sms id: " + smsw.getId(),
                        FacesContextUtils.getRemoteAddr()));
                //Обновляем одну ячейку таблицы
                break;
            }
        }
    }

    {
        clientPin = null;
        initSMSStatuses();
        selectedSMSStatus = SMSStatusWrapper.ALL;
        initTimeFilters();
        initProviderFilters();
        initSMSThemes();
        initPhoneCountryFilters();
        deselectAllThemes();

        selectedProviderFilter = ALL;
        selectTimeFilter();
        lastHour();
    }

    @Override
    public void show() {
        getNavigationBean().showSMSStatistics();
    }

    private void resetColor() {
        dayColor = BLUE_COLOR;
        hourColor = BLUE_COLOR;
    }

    public void clearFilterCriterion() {

        resetColor();
        clientPin = null;
        isIncorrectPin = false;
        selectedSMSStatus = SMSStatusWrapper.ALL;
        deselectAllThemes();
        selectedTimeFilter = null;
        dayColor = RED_COLOR;
        selectedProviderFilter = ALL;
        selectTimeFilter();
        buildFilterCriterion();
        initSelectedPhoneCountry();
        phone = null;
        query.clear();
    }

    private void initSelectedPhoneCountry() {
        if (phoneCountryFilters == null || phoneCountryFilters.size() == 0 || phoneCountryFilters.size() > ONE_ITEM) {
            selectedPhoneCountry = ALL;
        } else {
            selectedPhoneCountry = phoneCountryFilters.get(0).getValue();
        }
    }

    public void selectTimeFilter() {
        since = null;
        till = null;
    }

    public void lastHour() {
        selectedTimeFilter = TimeFilter.LAST_HOUR;
        resetTime();
        buildFilterCriterion();
        resetColor();
        hourColor = RED_COLOR;
    }

    public void lastDay() {
        selectedTimeFilter = TimeFilter.LAST_DAY;
        resetTime();
        buildFilterCriterion();
        resetColor();
        dayColor = RED_COLOR;
    }

    public void find() {
        resetColor();
        buildFilterCriterion();
    }


    // проверяет фильтр на консистентность - нельзя выбрать красного провайдера в
    // выпадающем списке при отжатой галочке дилера
    private boolean checkFilterState() {
        if (selectedProviderFilter < 0 || !isSelectdealers()) return true;
        SMSProviderType provider = SMSProviderType.getByCode(selectedProviderFilter);
        boolean ret = false;
        for (SMSDealerFeatures d : getDealerFeatures()) {
            if (!d.isSelected()) continue;
            if (d.checkFilterState(provider)) ret = true;
        }
        return ret;

    }

    private void buildFilterCriterion() {

        if (!checkFilterState()) {
            FacesMessagesUtils.error(WRONG_STATE_KEY);
            return;
        }
        pollEnable = false;

        query.clear();
        query.setPin(isNotNullOrEmpty(clientPin) ? Integer.valueOf(clientPin) : null);

        LOGGER.debug("\n\n\n\n SELECTED SMS STATUS IS " + selectedSMSStatus + "\n\n\n\n");
        LOGGER.debug("\n\n\n\n EXTRACTED SMS STATUS IS " + extractSMSStatus(selectedSMSStatus) + "\n\n\n\n");

        query.addStatuses(extractSMSStatus(selectedSMSStatus));
        query.addThemes(extractSMSThemes(selectedSMSThemes));

        // Выбран какой-то 1 провайдер
        if (selectedProviderFilter >= 0) {
            query.addProviderCode(selectedProviderFilter);
        } else {
            // Добавляем всех провайдеров для дилера
            for (SMSDealerFeatures d : getDealerFeatures()) {
                if (d.isSelected()) {
                    query.addAllProviderCodes(d.getProviders());
                }
            }
        }
        boolean emptyPhone = phone == null || phone.trim().isEmpty();
        if (selectedPhoneCountry != ALL && emptyPhone) {
            LOGGER.debug("selectedPhoneCountry: " + selectedPhoneCountry);
            query.setPhone(selectedPhoneCountry + "%");
        } else {
            query.setPhone(emptyPhone ? null : phone + "%");
        }
        if (since == null && till == null && selectedTimeFilter != null) {
            query.setTimeFilter(selectedTimeFilter);
        }

        if (since != null) {
            query.setSince(since);
        }

        if (till != null) {
            query.setTill(till);
        }
        query.setRowCountOnly(!showSMS);
        query.setReverseOrder(true);
        LOGGER.debug("before updateSMSList: " + query.getPhone());

        updateSMSList();
        selectedTimeFilter = null;
    }


    private void resetTime() {
        till = null;
        since = null;
    }

    public void deselectAllThemes() {
        selectedSMSThemes = new ArrayList<>();
    }

    public void pull() {
        List<SMSWrapper> smsListOld = smsList;
        updateSMSList();
        if (smsListOld != null && smsList.size() == smsListOld.size()) {
            Set<String> ajaxAreasToRender = AjaxContext.getCurrentInstance().getAjaxAreasToRender();
            if (ajaxAreasToRender != null) ajaxAreasToRender.clear();
            Set<String> ajaxRenderedAreas = AjaxContext.getCurrentInstance().getAjaxRenderedAreas();
            if (ajaxRenderedAreas != null) ajaxRenderedAreas.clear();
        }
    }

    public void updateSMSList() {
        String operatorName = FacesContextUtils.getRemoteUser();
        Operator op = operatorLocal.findByName(operatorName);

        LOGGER.info("SMSStat Operator " + op.getName() + " " + FacesContextUtils.getRemoteAddr());
        LOGGER.info("SMSStat Operator query " + query.toString());

        try {
            if (query.isRowCount()) {
                smsList.clear();
                smsCount = smsBean.findCountSMSByFilterQuery(query, operatorName).longValue();
            } else {
                smsList = wrapSMSList(smsBean.findSMSByCriterion(query, operatorName), op.getNum());
                logger.debug("updateSMSList phone: " + query.getPhone() + " count: " + smsList.size());
            }
            if (smsList.isEmpty()) {
                FacesMessagesUtils.info("nothingFound");
            }

        }catch(Throwable t){
            logger.error(t.getMessage(), t);
            FacesMessagesUtils.error("unexpectedError", t.getMessage());
        }
    }

    /*
     * utils
     */
    private void initSMSStatuses() {
        for (SMSStatusWrapper status : SMSStatusWrapper.values()) {
            statuses.add(new SelectItem(status,
                    FacesMessagesUtils.getMessage("SMSStatusWrapper_" + status.name())));
        }
    }

    private void initSMSThemes() {

        for (SMSThemeWrapper theme : SMSThemeWrapper.values()) {
            themes.add(new SelectItem(theme.name(),
                    FacesMessagesUtils.getMessage("SMSThemeWrapper_" + theme.name())));
        }

    }

    public String deployToXLS() {
        try {
            DateFormat dFormat = new SimpleDateFormat("HH:mm:ss_dd/MM/yy");
            Date date = new Date();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HSSFWorkbook workBook = new SMSExcelFormer().formExcelWorkbook(smsList);
            workBook.write(baos);

            String filename = dFormat.format(date) + "_SMS_report.xls";

            downloadFile(filename, baos.toByteArray());
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    private static String getSMSStatusLabel(SMSStatus status) {
        String res = statusLabels.get(status);
        if (res == null) {
            res = FacesMessagesUtils.getMessage(SMSSTATUS_PREFIX + status.name());
            if (res == null) res = "";
            statusLabels.put(status, res);
        }
        return res;
    }

    public Set<Integer> getRowsForUpdate() {
        return rowsForUpdate;
    }

    private List<SMSWrapper> wrapSMSList(List<SMS> smsList, Integer opId) {
        List<SMSWrapper> result = new ArrayList<>();
        rowsForUpdate = null;
        for (SMS sms : smsList) {
            result.add(new SMSWrapper(sms, opId, showedTextSms.contains(sms.getId())));
        }

        return result;
    }

    private static List<SMSStatus> extractSMSStatus(SMSStatusWrapper wrapper) {
        switch (wrapper) {
            case ALL:
                return Arrays.asList(SMSStatus.values());
            case DELIVERED:
                return list(SMSStatus.DELIVERED);
            case QUEUED:
                return list(SMSStatus.QUEUED, SMSStatus.INIT);
            case REDIRECTED_TOSMSC:
                return list(SMSStatus.REDIRECTED_TOSMSC, SMSStatus.SENT);
            case FAILED:
                return SMSStatus.getFailed();
            default:
                return new ArrayList<>();
        }
    }


    private static List<SMSTheme> extractSMSThemes(List<String> names) {
        if (names.isEmpty()) {
            return new ArrayList<>();
        }

        List<SMSTheme> result = new ArrayList<>();

        for (String name : names) {
            result.add(SMSThemeWrapper.valueOf(name).getNativeTheme());
        }

        return result;
    }


    private static boolean isNotNullOrEmpty(String str) {
        return (str != null && !str.isEmpty());
    }

    private void initTimeFilters() {
        for (TimeFilter timeType : TimeFilter.values()) {
            timeFilters.add(new TypedSelectItem<>(timeType, timeType.getDescription()));
        }
    }

    public void initProviderFilters() {
        providerFilters = new ArrayList<>();
        TreeSet<TypedSelectItem<Integer>> items = new TreeSet<>();
        boolean all = !isSelectdealers();
        for (SMSDealerFeatures d : getDealerFeatures()) {
            if(d.selected || all){
                d.addProviders(items);
             }
        }
        providerFilters.addAll(items);

        if (providerFilters.size() > ONE_ITEM) {
            selectedProviderFilter = ALL;
            providerFilters.add(0, new TypedSelectItem<>(ALL, FacesMessagesUtils.getMessage("smsStat_prefix_all")));
        } else if (!providerFilters.isEmpty()) {
            selectedProviderFilter = providerFilters.get(0).getValue();
        }
    }

    public boolean isSelectdealers() {
        for (SMSDealerFeatures d : getDealerFeatures()) {
            if(d.selected) return true;
        }
        return false;
    }
    public boolean isExistsProviders() {
        return providerFilters != null && !providerFilters.isEmpty();
    }

    private void initPhoneCountryFilters() {
        phoneCountryFilters = new ArrayList<>();
        TreeSet<TypedSelectItem<Integer>> items = new TreeSet<>();
        for (SMSDealerFeatures d : getDealerFeatures()) {
            d.addPhonePrefix(items);
        }
        phoneCountryFilters.addAll(items);
        if (phoneCountryFilters.size() > ONE_ITEM) {
            phoneCountryFilters.add(0, new TypedSelectItem<>(ALL, FacesMessagesUtils.getMessage("smsStat_prefix_all")));
        }
        initSelectedPhoneCountry();
    }

    /*
     * getters and setters
     */
    public boolean isIncorrectPinFlag() {
        return isIncorrectPin;
    }

    public List<SMSWrapper> getListOfSMS() {
        return smsList;
    }

    public boolean isIntervalNotSelected() {
        return intervalNotSelected;
    }

    public String getSize() {
        return showSMS ? String.valueOf(smsList.size()) : String.valueOf(smsCount == null ? 0 : smsCount);
    }

    public String getClientPin() {
        return clientPin;
    }

    public void setClientPin(String value) {
        clientPin = value;
    }

    public List<TypedSelectItem<TimeFilter>> getTimeFilters() {
        return timeFilters;
    }

    public List<TypedSelectItem<Integer>> getProviderFilters() {
        return providerFilters;
    }

    public void setProviderFilters(List<TypedSelectItem<Integer>> value) {
        providerFilters = value;
    }

    public Integer getSelectedProviderFilter() {
        return selectedProviderFilter;
    }

    public void setSelectedProviderFilter(Integer value) {
        selectedProviderFilter = value == null ? ALL : value;
    }

    public void setTimeFilters(List<TypedSelectItem<TimeFilter>> value) {
        timeFilters = value;
    }

    public TimeFilter getSelectedTimeFilter() {
        return selectedTimeFilter;
    }

    public void setSelectedTimeFilter(TimeFilter value) {
        selectedTimeFilter = value;
    }

    public boolean isIntervalFilter() {
        return isIntervalFilter;
    }

    public void setIntervalFilter(boolean value) {
        this.isIntervalFilter = value;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public Date getTill() {
        return till;
    }

    public void setTill(Date till) {
        this.till = till;
    }

    public List<SelectItem> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<SelectItem> statuses) {
        this.statuses = statuses;
    }

    public SMSStatusWrapper getSelectedSMSStatus() {
        return selectedSMSStatus;
    }

    public void setSelectedSMSStatus(SMSStatusWrapper selectedSMSStatus) {
        this.selectedSMSStatus = selectedSMSStatus;
    }

    public List<String> getSelectedSMSThemes() {
        return selectedSMSThemes;
    }

    public void setSelectedSMSThemes(List<String> selectedSMSThemes) {
        this.selectedSMSThemes = selectedSMSThemes;
    }

    public List<SelectItem> getThemes() {
        return themes;
    }

    public void setThemes(List<SelectItem> themes) {
        this.themes = themes;
    }

    public boolean isPollEnable() {
        return pollEnable;
    }

    public void setPollEnable(boolean pollEnable) {
        this.pollEnable = pollEnable;
    }

    public String getDayColor() {
        return dayColor;
    }

    public void setDayColor(String dayColor) {
        this.dayColor = dayColor;
    }

    public String getHourColor() {
        return hourColor;
    }

    public void setHourColor(String hourColor) {
        this.hourColor = hourColor;
    }

    public boolean isRenderedPhoneCountryFilters() {
        return phoneCountryFilters.size() > ONE_ITEM;
    }

    public List<TypedSelectItem<Integer>> getPhoneCountryFilters() {
        return phoneCountryFilters;
    }

    public void setPhoneCountryFilters(List<TypedSelectItem<Integer>> phoneCountryFilters) {
        this.phoneCountryFilters = phoneCountryFilters;
    }

    public int getSelectedPhoneCountry() {
        return selectedPhoneCountry;
    }

    public void setSelectedPhoneCountry(int selectedPhoneCountry) {
        this.selectedPhoneCountry = selectedPhoneCountry;
    }

    public boolean isShowSMS() {
        return showSMS;
    }

    public boolean isLinkVisible() {
        return isUserInRole(Roles.SMS_SECURITY_OPERATOR);
    }

    public void setShowSMS(boolean showSMS) {
        this.showSMS = showSMS;
    }

    public List<SMSDealerFeatures> getDealerFeatures() {
        return dealerFeatures;
    }

}
