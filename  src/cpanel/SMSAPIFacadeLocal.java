public interface SMSAPIFacadeLocal {

    List<SMS> findSMSByFilterQuery(SMSFilterQuery filterQuery, String operatorName)  throws IOException;

    Integer findCountSMSByFilterQuery(SMSFilterQuery filterQuery, String operatorName)  throws IOException ;

    MailingStat getMailingStat(Date startDate, String operatorName)  throws IOException ;
}
