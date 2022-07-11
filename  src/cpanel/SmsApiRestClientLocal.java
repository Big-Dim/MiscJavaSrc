
@Local
public interface SmsApiRestClientLocal {

    SmsBlackListResponseGet getBlackList(SmsBlackListRequestGet request) throws IOException;
    void addToBlackList(SmsBlackListRequestAdd request) throws IOException;
    void removeFromBlackList(SmsBlackListRequestRemove request) throws IOException;


    SmsCountryGroupResponseList listCountryGroups(SmsCountryGroupRequestList request) throws IOException;
    void addToCountryGroups(SmsCountryGroupRequestAdd request) throws IOException;
    void editCountryGroup(SmsCountryGroupRequestEdit request) throws IOException;
    void removeFromCountryGroups(SmsCountryGroupRequestRemove request) throws IOException;

    SmsListResponseGet findSmsByFilter(SmsListRequestGet request) throws IOException;
    Integer findSmsCountByFilter(SmsListRequestGet request) throws IOException;
    MailingStat getThemeStat(Date startDate, String operatorName) throws IOException;

    SmsApiResponse sendRequest(SmsSettingListRequestGet request, String prot, boolean crypt) throws IOException;

}
