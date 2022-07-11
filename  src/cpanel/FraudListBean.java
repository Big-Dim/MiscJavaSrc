

public class FraudListBean extends FonbetControlPanelBean {

    private static final Logger logger = LoggerFactory.getLogger(FraudListBean.class);
    private static final String NOTHING_FOUND = "fraudList_nothing_found";
    private static final String ERROR_EXISTS = "fraudList_exists_exception";
    private static final String ERROR_EXISTS_GLOBAL = "fraudList_exists_global_exception";
    private static final String SAVED = "fraudList_saved";
    private static final String ERROR_CHRONOPAY = "fraudList_chronopay_error";
    private FraudItemType type;
    private FraudItemType[] types;
    private PS ps;
    private String account;
    private String operator;
    private Date startDate;
    private Date endDate;
    private String comment;
    private FraudItemType typeToAdd = FraudItemType.ACCOUNT;
    private PS psToAdd;
    private PS psToCSV;
    private String accountToAdd;
    private String commentToAdd;
    private Integer pin;
    private Integer pinToAdd;
    // select items
    private List<SelectItem> fraudItemTypeSelectItems = new ArrayList<SelectItem>();
    private List<SelectItem> psSelectItems = new ArrayList<SelectItem>();
    private List<SelectItem> transferTypeSelectItems = new ArrayList<SelectItem>();
    private List<SelectItem> psTransferFilterSelectItems = new ArrayList<SelectItem>();
    //converters
    private FraudItemTypeConverter fraudItemTypeConverter = new FraudItemTypeConverter();
    private MoneyTransferTypeConverter moneyTransferTypeConverter = new MoneyTransferTypeConverter();
    //
    private final FraudListLocal fraudListBean = JNDIHelper.lookup("FraudListBean/local");
    //private final ChronopayFraudListLocal chronopayFraudListBean = JNDIHelper.lookup("ChronopayFraudListBean/local");
    private List<FraudListItem> fraudList = new ArrayList<FraudListItem>();
    private boolean showFilterWarnMessage = true;
    private boolean showAddFraudItem = false;
    private MoneyTransferType transferType = MoneyTransferType.DEPOSIT;
    private PS psFilter;
    private Date filterStartDate;
    private Date filterEndDate;
    private List<? extends TransferFonbetComPaymentIF> paymentsList = new ArrayList();
    private AbstractBalanceSheet paymentsBean;
    private UploadedFile uploadedFile;

    private static final String REG_SAVED = "FraudListBean_saved";
    private static final String NOT_SELECTED = "FraudListBean_fileNotSelected";
    private static final String UPLOAD_ERROR = "FraudListBean_uploadError";
    private static final String ERROR_IN_LINE = "FraudListBean_errorInLine";
    private static final String ALREADY_IN_LIST = "FraudListBean_alreayInList";

    public static final List<PS> BLACK_LIST_PS = CollectionUtil.list(new PS[]{PS.QIWI, PS.YANDEX, PS.WEBMONEY,
            PS.ACCENT_CARDS, PS.NETELLER, PS.MONETA, PS.PAYPAL_CYPRUS, PS.MONEYBOOKERS });
   // public static final List<PS> WITHDRAW_PS = CollectionUtil.list(new PS[]{QIWI, WEBMONEY, MONETA, MONEYBOOKERS, NETELLER, NETELLER_CYPRUS, YANDEX, ECOPAYZ, CPS_YANDEX, EWS_PROFEE, EWS_YANDEX});

    protected static final String PS_PREFIX = "ps_key.";

    public FraudListBean() {
        if (paymentsBean == null) {
            paymentsBean = new FonBalanceSheet();
        }
        initFraudItemTypes();
        initFraudItemTypeSelectItems();
        initPSSelectItems();
        initTransferTypeSelectItems();
        initPsTransferFilterSelectItems();
        initFilterDates();
    }


    private void processLoadFraudCSV(String data) throws ReestrFormerException {
            List<FraudListItem> records = FraudCSVUtil.parse(data, psToCSV, FacesContextUtils.getRemoteUser() );
            for(FraudListItem r: records){
                try{
                    fraudListBean.ban(r, FacesContextUtils.getRemoteAddr());
                }
                catch(Exception ex){
                    FacesMessagesUtils.error(ALREADY_IN_LIST, r.getAccount(),r.getPaymentSystem().name());
                }
            }
    }

    public void upload() {

        try {
            if (uploadedFile.getBytes() == null || uploadedFile.getName() == null) {
                FacesMessagesUtils.error(NOT_SELECTED);
                return;
            }

            processLoadFraudCSV(new String(uploadedFile.getBytes()));
            FacesMessagesUtils.info(REG_SAVED);

        } catch (IOException ex) {
            logger.warn(ex.getMessage());
            FacesMessagesUtils.error(UPLOAD_ERROR);
        }
        catch (ReestrFormerException ex) {
            logger.warn(ex.getMessage());
            FacesMessagesUtils.error(UPLOAD_ERROR);
            if (ex.getLineError() != null){
                FacesMessagesUtils.error(ERROR_IN_LINE, ex.getLineError());
            }
        }
    }

    private void initTransferTypeSelectItems() {
        transferTypeSelectItems.clear();
        for (MoneyTransferType mtt : MoneyTransferType.values()) {
            transferTypeSelectItems.add(new SelectItem(mtt, moneyTransferTypeConverter.getLabel(mtt)));
        }
    }

    private void initFraudItemTypeSelectItems() {
        fraudItemTypeSelectItems.clear();
        for (FraudItemType bit : FraudItemType.values()) {
            fraudItemTypeSelectItems.add(new SelectItem(bit, fraudItemTypeConverter.getLabel(bit)));
        }
    }

    private void initFraudItemTypes() {
        types = new FraudItemType[1];
        types[0] = FraudItemType.ACCOUNT;
    }

    private void initPSSelectItems() {
        psSelectItems.clear();
        for (PS paymentSystem : BLACK_LIST_PS) {
            psSelectItems.add(new SelectItem(paymentSystem, generateNodeName(FacesMessagesUtils.getMessage(
                    PS_PREFIX + paymentSystem.name()))));
        }
    }

    private void initPsTransferFilterSelectItems() {
        psTransferFilterSelectItems.clear();
        switch (transferType) {
            case DEPOSIT:
                for (PS paymentSystem : BLACK_LIST_PS) {
                     psTransferFilterSelectItems.add(new SelectItem(paymentSystem,
                             generateNodeName(FacesMessagesUtils.getMessage(PS_PREFIX + paymentSystem.name()))));
                }
                break;
            case WITHDRAW:
                for (PS paymentSystem : PS.WITHDRAW_PS) {
                    psTransferFilterSelectItems.add(new SelectItem(paymentSystem, paymentSystem.name()));
                }
                break;
        }
    }


    private void initFilterDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        filterEndDate = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        filterStartDate = calendar.getTime();
    }

    public void filter() {
        FraudListItemFilterQuery filter = new FraudListItemFilterQuery();
        Set<FraudItemType> s = new HashSet();
        s.addAll(Arrays.asList(types));
        filter.setItemTypes(s);
        filter.setPs(ps);
        filter.setAccount(account);
        filter.setOperator(operator);
        filter.setBanStartDate(startDate);
        filter.setBanEndDate(endDate);
        filter.setComment(comment);
        filter.setPin(pin);
        fraudList = fraudListBean.filter(filter);
        if (showFilterWarnMessage && fraudList.isEmpty()) {
            FacesMessagesUtils.error(NOTHING_FOUND);
        }
        showFilterWarnMessage = true;
    }

    public void filterPayments() {

        paymentsBean.setDetailsString(null);

        if (!checkDateInterval(filterStartDate, filterEndDate)) {
            return;
        }

        paymentsList = (new PaymentsSearcherUtil()).getBlackPayments(psFilter, filterStartDate, filterEndDate,
                transferType, false);

        if (paymentsList.isEmpty()) {
            FacesMessagesUtils.error(NOTHING_FOUND);
        }
    }

    public void selectAll() {
        setSelectedValueToAllFraudListItems(true);
    }

    public void clearAll() {
        setSelectedValueToAllFraudListItems(false);
    }

    private void setSelectedValueToAllFraudListItems(boolean b) {
        for (FraudListItem item : fraudList) {
            item.setSelected(b);
        }
    }

    public void fraudItemChange() {
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();

        UIData t = (UIData) root.findComponent("frame").findComponent("include").
                findComponent("fraudListManagement").findComponent("fraudListPanelGrid").
                findComponent("form").findComponent("fraudListTable");
        FraudListItem fraudListItem = (FraudListItem) t.getRowData();
        for (FraudListItem item : fraudList) {
            if (item.getId() == fraudListItem.getId()) {
                item.setSelected(!item.isSelected());
            }
        }
        fraudListItem.setSelected(!fraudListItem.isSelected());
    }

    public void unban() {
        for (FraudListItem item : fraudList) {
            if (item.isSelected()) {
                fraudListBean.unban(item, FacesContextUtils.getRemoteUser(), FacesContextUtils.getRemoteAddr() );
            }
        }
        showFilterWarnMessage = false;
        filter();
        FacesMessagesUtils.info(SAVED);
    }

    public void addFraudItem() {
        showAddFraudItem = true;
    }

    public void ban() {

        /*if (find(fraudListItem.getAccount(), blackListItem.getPaymentSystem(), blackListItem.getType()) != null) {
            throw new BlackItemExistsException();
        }*/

        try {
            FraudListItem item = new FraudListItem(accountToAdd, psToAdd,
                    commentToAdd, FacesContextUtils.getRemoteUser(), typeToAdd);

            item.setPin(pinToAdd);

            fraudListBean.ban(item, FacesContextUtils.getRemoteAddr());
        } catch (FraudItemExistsException ex) {
            logger.warn(ex.toString());
            FacesMessagesUtils.error(ERROR_EXISTS);
            return;
        } catch (FraudItemGlobalExistsException ex) {
            logger.warn(ex.toString());
            FacesMessagesUtils.error(ERROR_EXISTS_GLOBAL);
            return;
        }
        FacesMessagesUtils.info(SAVED);
    }

    public void transferTypeChange(ValueChangeEvent evt) {
        transferType = (MoneyTransferType) evt.getNewValue();
        initPsTransferFilterSelectItems();
    }

    public void clearAddForm(ActionEvent evt) {
        accountToAdd = "";
        commentToAdd = "";
    }

    @Override
    public void show() {
        getNavigationBean().showFraudListPage();
    }

    public boolean getShowResult() {
        return !fraudList.isEmpty();
    }

    public boolean getShowPaymentsResult() {
        return !paymentsList.isEmpty();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public PS getPs() {
        return ps;
    }

    public void setPs(PS ps) {
        this.ps = ps;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public FraudItemType getType() {
        return type;
    }

    public void setType(FraudItemType type) {
        this.type = type;
    }

    public List<SelectItem> getFraudItemTypeSelectItems() {
        return fraudItemTypeSelectItems;
    }

    public void setFraudItemTypeSelectItems(List<SelectItem> fraudItemTypeSelectItems) {
        this.fraudItemTypeSelectItems = fraudItemTypeSelectItems;
    }

    public List<SelectItem> getPsSelectItems() {
        return psSelectItems;
    }

    public void setPsSelectItems(List<SelectItem> psSelectItems) {
        this.psSelectItems = psSelectItems;
    }

    public FraudItemType[] getTypes() {
        return types;
    }

    public void setTypes(FraudItemType[] types) {
        this.types = types;
    }

    public List<FraudListItem> getFraudList() {
        return fraudList;
    }

    public void setFraudList(List<FraudListItem> fraudList) {
        this.fraudList = fraudList;
    }

    public boolean isShowAddFraudItem() {
        return showAddFraudItem;
    }

    public void setShowAddFraudItem(boolean showAddFraudItem) {
        this.showAddFraudItem = showAddFraudItem;
    }

    public String getAccountToAdd() {
        return accountToAdd;
    }

    public void setAccountToAdd(String accountToAdd) {
        this.accountToAdd = accountToAdd;
    }

    public String getCommentToAdd() {
        return commentToAdd;
    }

    public void setCommentToAdd(String commentToAdd) {
        this.commentToAdd = commentToAdd;
    }

    public PS getPsToAdd() {
        return psToAdd;
    }

    public void setPsToAdd(PS psToAdd) {
        this.psToAdd = psToAdd;
    }

    public FraudItemType getTypeToAdd() {
        return typeToAdd;
    }

    public void setTypeToAdd(FraudItemType typeToAdd) {
        this.typeToAdd = typeToAdd;
    }

    public MoneyTransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(MoneyTransferType transferType) {
        this.transferType = transferType;
    }

    public List<SelectItem> getTransferTypeSelectItems() {
        return transferTypeSelectItems;
    }

    public void setTransferTypeSelectItems(List<SelectItem> transferTypeSelectItems) {
        this.transferTypeSelectItems = transferTypeSelectItems;
    }

    public List<SelectItem> getPsTransferFilterSelectItems() {
        return psTransferFilterSelectItems;
    }

    public void setPsTransferFilterSelectItems(List<SelectItem> psTransferFilterSelectItems) {
        this.psTransferFilterSelectItems = psTransferFilterSelectItems;
    }

    public PS getPsFilter() {
        return psFilter;
    }

    public void setPsFilter(PS psFilter) {
        this.psFilter = psFilter;
    }

    public Date getFilterEndDate() {
        return filterEndDate;
    }

    public void setFilterEndDate(Date filterEndDate) {
        this.filterEndDate = filterEndDate;
    }

    public Date getFilterStartDate() {
        return filterStartDate;
    }

    public void setFilterStartDate(Date filterStartDate) {
        this.filterStartDate = filterStartDate;
    }

    public List<? extends TransferFonbetComPaymentIF> getPaymentsList() {
        return paymentsList;
    }

    public void setPaymentsList(List<? extends TransferFonbetComPaymentIF> paymentsList) {
        this.paymentsList = paymentsList;
    }

    public AbstractBalanceSheet getPaymentsBean() {
        return paymentsBean;
    }

    public void setPaymentsBean(AbstractBalanceSheet paymentsBean) {
        this.paymentsBean = paymentsBean;
    }

    /**
     * @return the pin
     */
    public Integer getPin() {
        return pin;
    }

    /**
     * @param pin the pin to set
     */
    public void setPin(Integer pin) {
        this.pin = pin;
    }

    /**
     * @return the pinToAdd
     */
    public Integer getPinToAdd() {
        return pinToAdd;
    }

    /**
     * @param pinToAdd the pinToAdd to set
     */
    public void setPinToAdd(Integer pinToAdd) {
        this.pinToAdd = pinToAdd;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public PS getPsToCSV() {
        return psToCSV;
    }

    public void setPsToCSV(PS psToCSV) {
        this.psToCSV = psToCSV;
    }

    protected String generateNodeName(String title, int... codes) {
        String str = null;
        if (codes != null && codes.length > 0) {
            StringBuilder sb = new StringBuilder(" (");
            for (int i = 0; i < codes.length; i++) {
                sb.append(codes[i]);
                if (i < codes.length - 1)
                    sb.append(", ");
            }
            sb.append(")");
            str = title + sb.toString();
        }
        return str != null ? str : title;
    }

}
