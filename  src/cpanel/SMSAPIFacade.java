
@Stateless
public class SMSAPIFacade implements SMSAPIFacadeLocal {

    private static final Integer RESULTS_ON_PAGE = 1000;
    private SmsApiRestClientLocal restClientBean = JNDIHelper.lookupLocal(SmsApiRestClientBean.class);
 
    @Override
    public List<SMS> findSMSByFilterQuery(SMSFilterQuery filterQuery, String operatorName)  throws IOException {

        SmsListRequestGet request =  buildCriteria(filterQuery, operatorName);
        request.setPageNumber(1);
        request.setResultsOnPage(RESULTS_ON_PAGE);

        List<SMS> responce = new ArrayList<SMS>();

        SmsListResponseGet itemList = restClientBean.findSmsByFilter(request);

        if(itemList != null){
            for(Object smsItem : itemList){
                SMS  sms = new SMS((SmsListItem)smsItem)  ;
                responce.add(sms);
            }
        }
        return responce;
    }

    @Override
    public Integer findCountSMSByFilterQuery(SMSFilterQuery filterQuery, String operatorName)  throws IOException {
        SmsListRequestGet request = buildCriteria(filterQuery, operatorName);
        Integer responce = null;
        try {
            responce = restClientBean.findSmsCountByFilter(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responce;
    }

    private SmsListRequestGet buildCriteria(SMSFilterQuery query, String operatorName) {

        SmsListRequestGet request = new SmsListRequestGet(query.getPin(),
                query.getPhone(),
                query.getProviderId(),
                query.getAllowCopy(),
                query.getSinceLong(),
                query.getTillLong(),
                query.getProviderCodes(),
                query.getThemes(),
                query.getStatuses(),
                query.isReverseOrder()) ;
        request.setOperatorName(operatorName);
        return request;
    }

    @Override
    public MailingStat getMailingStat(Date startDate, String operatorName)  throws IOException {
        MailingStat stat = new MailingStat();
        try {
            stat = restClientBean.getThemeStat(startDate, operatorName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stat;
    }

}
