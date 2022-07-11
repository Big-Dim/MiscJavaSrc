
@Stateless
public class SmsApiRestClientBean implements SmsApiRestClientLocal {

    private static final Logger logger = LoggerFactory.getLogger(SmsApiRestClientBean.class);

    private final HttpClient httpClient = createHttpClient();

    @EJB
    private SettingsLocal settings;

    public HttpClient createHttpClient() {
        X509TrustManager trustManager = OriginConnectorTransport.TrustAllX509TrustManager.INSTANCE;
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build()
                )
                .setSSLContext(sslContext(null, new TrustManager[]{trustManager}))
                .build();
    }

    public static SSLContext sslContext(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        try {
            SSLContext sslContext = SSLContext.getInstance(SSLType.TLS_V1_2.getProtocolName());
            sslContext.init(keyManagers,
                    trustManagers,
                    null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Couldn't init TLS_V1_2 context", e);
        }
    }

    //Gson instances are Thread-safe so you can reuse them freely across multiple threads
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    public SmsBlackListResponseGet getBlackList(SmsBlackListRequestGet request) throws IOException {
        return sendRequest(request, SmsBlackListResponseGet.class, SMS_BLACKLIST_URI_GET);
    }

    public void addToBlackList(SmsBlackListRequestAdd request) throws IOException {
        sendRequest(request, null, SMS_BLACKLIST_URI_ADD);
    }

    public void removeFromBlackList(SmsBlackListRequestRemove request) throws IOException {
        sendRequest(request, null, SMS_BLACKLIST_URI_REMOVE);
    }

    @Override
    public SmsCountryGroupResponseList listCountryGroups(SmsCountryGroupRequestList request) throws IOException {
        return sendRequest(request, SmsCountryGroupResponseList.class, SMS_COUNTRY_GROUP_URI_LIST);
    }

    @Override
    public void addToCountryGroups(SmsCountryGroupRequestAdd request) throws IOException {
        sendRequest(request, null, SMS_COUNTRY_GROUP_URI_ADD);
    }

    @Override
    public void editCountryGroup(SmsCountryGroupRequestEdit request) throws IOException {
        sendRequest(request, null, SMS_COUNTRY_GROUP_URI_EDIT);
    }

    @Override
    public void removeFromCountryGroups(SmsCountryGroupRequestRemove request) throws IOException {
        sendRequest(request, null, SMS_COUNTRY_GROUP_URI_REMOVE);
    }

    @Override
    public SmsListResponseGet findSmsByFilter(SmsListRequestGet request) throws IOException{
        return sendRequest(request, SmsListResponseGet.class, SMS_SMS_URI_LIST);
    }

    @Override
    public Integer findSmsCountByFilter(SmsListRequestGet request) throws IOException{
        return sendRequest(request, Integer.class, SMS_SMS_URI_COUNT);
    }

    @Override
    public MailingStat getThemeStat(Date startDate, String operatorName) throws IOException{
        MailingStatRequestGet request = new MailingStatRequestGet(startDate, operatorName);
        return sendRequest(request, MailingStat.class, SMS_SMS_URI_THEMESTAT);
    }

    @Override
    public SmsApiResponse sendRequest(SmsSettingListRequestGet request, String prot, boolean crypt) throws IOException{
        if(crypt)
            return sendRequest(request, SmsApiResponse.class, SMS_SETTING_CRYPT_URI, prot);
        else
            return sendRequest(request, SmsApiResponse.class, SMS_SETTING_URI, prot);
    }


    private <Response> Response sendRequest(
            SmsApiRequestAbstract request, Class<Response> responseClass, String uri) throws IOException {

        createSign(request, uri);
        final String reqStr = gson.toJson(request);
        logger.info("HTTP request: {} " , reqStr);

        HttpPost post = new HttpPost(concatUri(settings.get(SMS_API_ROOT_URI), uri)) {{
            setEntity(new StringEntity(reqStr, ContentType.APPLICATION_JSON));
        }};

        Pair<Integer, String> pair = httpClient.execute(post, new CodeAndBodyResponseHandler(logger));

        if ((pair.getA() != HttpURLConnection.HTTP_OK && pair.getA() != HttpURLConnection.HTTP_NO_CONTENT)) {
            throw new IOException("Bad response code: " + pair.getA());
        }

        try {
            if (responseClass != null) {
                return gson.fromJson(pair.getB(), responseClass);
            }
        } catch (JsonParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private <Response> Response sendRequest(
            SmsApiRequestAbstract request, Class<Response> responseClass, String uri, String prot) throws IOException {

        createSign(request);

        Pair<Integer, String> pair = null;
        Integer getA =null;
        String getB = null;

        String uriparam = uri + request.getURIparams();
        final String body =  request.getBody();

        logger.info("HTTP request: {} " , request);

        HttpUriRequest req = null;
        switch (prot){
            case "POST":
                req = new HttpPost(concatUri(settings.get(SMS_API_ROOT_URI), uriparam)) {{
                    setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
                }};
                break;
            case "GET":
                 req = new HttpGet(concatUri(settings.get(SMS_API_ROOT_URI), uriparam)) {{
                    setHeader("Accept", "application/json");
                }};
                 break;
            case "PUT":
                req = new HttpPut(concatUri(settings.get(SMS_API_ROOT_URI), uriparam)) {{
                    setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
                }};
                break;
            case "DELETE":
                req = new HttpDelete(concatUri(settings.get(SMS_API_ROOT_URI), uriparam)) {{
                    setHeader("Accept", "application/json");
                }};
                break;
        }

        pair = httpClient.execute(req, new CodeAndBodyResponseHandler(logger));

        if (pair != null) {
            getA = pair.getA();
            getB = pair.getB();
        }

        logger.info("HTTP response code: " + getA + ". Response: " + getB);

        if ((getA != HttpURLConnection.HTTP_OK && getA != HttpURLConnection.HTTP_NO_CONTENT)) {
            throw new IOException("Bad response code: " + getA);
        }

        try {
            if (responseClass != null) {
                 return gson.fromJson(getB, responseClass);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    private void createSign(SmsApiRequestAbstract request, String uri) {
        //Не используем JodaTime, т.к. он сдвигает время на 1 час (используя какой-то British Standard Time)
        //Здесь 1 час критичен, т.к. веб-сервис не пропускает запросы с временем позднее одного часа назад, возвращая ошибку 401
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        String date = dateFormat.format(new Date());
        String operatorLogin = settings.get(SMS_API_LOGIN);
        String sign = uri + date + request.getOperatorName()
                + operatorLogin + settings.getCrypt(SMS_API_PASS);

        request.setOperatorLogin(operatorLogin);
        request.setDate(date);
        //request.setTimeStamp(String.valueOf(new Date().getTime()));
        request.setSign(Hasher.createSHA512(sign));
    }


    private void createSign(SmsApiRequestAbstract request) {

        String operatorLogin = settings.get(SMS_API_LOGIN);
        Long time =  System.currentTimeMillis();//oneHourAgo.getTime();

        String sign = time + operatorLogin + settings.getCrypt(SMS_API_PASS);

        String md5Hash = Hasher.createMD5Hash(sign);
        request.setTimeStamp(time);
        request.setSign(md5Hash);
    }

    private String concatUri(String root, String path) {
        //root настраивается пользователем админки, поэтому возникает вероятность человеческого фактора. Все возможные
        //проблемы вряд ли получится отследить, но есть высокая вероятность того, что в IP-адресе может присутствовать
        //завершающий слеш. Если к нему будет добавлен REST-метод, начинающийся также со слеша, то будет дублирование (//),
        //поэтому очищаем дубли
        if (root.lastIndexOf("/") == root.length() - 1) {
            root = root.substring(0, root.length() - 2);
        }
        return root + path;
    }

}
