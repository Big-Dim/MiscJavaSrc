
@SuppressWarnings("unchecked")
public class GamesConnector implements GamesConnectorIF {

    private static final String LOCAL = "/local";
    private static final String EXTERNAL = "/external";
    private final GamesConnectorTransport transport;
    protected static final Logger LOGGER = LoggerFactory.getLogger(GamesConnector.class);

     public static final String GAME_HOST_KEY = "game_api.host.";
    public static final String GAME_PORT_KEY = "game_api.port.";
    private static final String API_LOGIN_KEY = "game_api.login.";
    private static final String API_PASSWORD_KEY = "game_api.pass.";
    protected GameProvider provider;

    private static final Logger logger = LoggerFactory.getLogger(GamesConnector.class);

    public static GamesConnectorTransport getTransport(SettingsLocal settings, GameProvider provider) {
        String postfix = provider.getStringForKey();
        String host = settings.get(GAME_HOST_KEY + postfix);
        Integer port = settings.getInteger(GAME_PORT_KEY + postfix);
        String login = settings.get(API_LOGIN_KEY + postfix);
        String pass = settings.getCrypt(API_PASSWORD_KEY + postfix);

        checkSettings(postfix, host, port, login, pass);

        return new GamesConnectorTransport(host, port, login, pass);
    }

    public static GamesConnectorIF getConnector(SettingsLocal settings, GameProvider provider) {
        return new GamesConnector(getTransport(settings, provider), provider);
    }

    private static void checkSettings(String postfix, String host, Integer port, String login, String pass) {
        Objects.requireNonNull(host, GAME_HOST_KEY + postfix + " is null!");
        Objects.requireNonNull(port, GAME_PORT_KEY + postfix + " is null!");
        Objects.requireNonNull(login, API_LOGIN_KEY + postfix + " is null!");
        Objects.requireNonNull(pass, API_PASSWORD_KEY + postfix + " is null!");
    }

    private GamesConnector(GamesConnectorTransport transport, GameProvider gp) {
        this.transport = transport;
        provider = gp;
    }

    @Override
    public String getCSV(CSVRequest req) throws RegistryNotAvailableException {
        String res = transport.sendRequest(req, Endpoints.CSV.url, String.class);
        return res == null || res.trim().equals("null") ? "" : res;
    }

    @Override
    public List<AbstractSheetRecord> getDeposits(SheetRequest request) throws RegistryNotAvailableException {
        return transport.sendRequest(request, Endpoints.DEPOSITS.url, ListResponse.class, AbstractSheetRecord.class).getRecords();
    }

    @Override
    public List<AbstractSheetRecord> getWithdraws(SheetRequest request) throws RegistryNotAvailableException {
        return transport.sendRequest(request, Endpoints.WITHDRAWS.url, ListResponse.class, AbstractSheetRecord.class).getRecords();
    }

    @Override
    public List<AbstractSheetRecord> getDepositsOrWithdraws1(SheetRequest request, boolean deposit) throws RegistryNotAvailableException {
        Map Total = getTotalDeposit(request);
        return transport.sendRequest(request, deposit ? Endpoints.DEPOSITS.url : Endpoints.WITHDRAWS.url, ListResponse.class, AbstractSheetRecord.class).getRecords();
    }

    @Override
    public List<? extends VirtualPaymentIF> getPayments(PaymentListRequest request) throws RegistryNotAvailableException {
        return transport.sendRequest(request, Endpoints.PAYMENTS.url, ListResponse.class, AbstractPaymentRecord.class).getRecords();
    }

    @Override
    public VirtualPaymentIF getPayment(Long id) throws RegistryNotAvailableException {
         InternalAPIRequest request = new InternalAPIRequest();
        return transport.sendRequest(request, String.format(Endpoints.PAYMENT.url, id), AbstractPaymentRecord.class);
    }

    @Override
    public Map<String, String> getDetails(Long paymentId) throws RegistryNotAvailableException {
         InternalAPIRequest request = new InternalAPIRequest();
        return transport.sendRequest(request, String.format(Endpoints.DETAILS.url, provider, paymentId), Map.class, String.class, String.class);
    }



    public Map<String, String> getTotalDeposit(SheetRequest request) throws RegistryNotAvailableException {
         return transport.sendRequest(request,  Endpoints.TOTAL_DEPOSITS.url, Map.class, String.class, String.class);
    }

    @Override
    public <T extends GameRegistryRecord> List<T> getRegistryLocal(RegistryRequest registryRequest) throws RegistryNotAvailableException {
        try {
            String urlPart = provider.getDataFormat().getUrlPart();
            if (urlPart == null) urlPart = provider.getNameProviderForServer();
            if (urlPart.equals(GameProvider.IFORIUM_SEAMLESS.getNameProviderForServer())) {
                urlPart = GameProvider.IFORIUM.getNameProviderForServer().toLowerCase();
            }
            return transport.sendRequest(registryRequest, Endpoints.REGISTRY.url + urlPart.toLowerCase() + LOCAL, ListResponse.class,
                    provider.getDataFormat().getRegistryType()).getRecords();
        } catch (Exception e) {
            throw new RegistryNotAvailableException(e);
        }
    }

    @Override
    public <T extends GameRegistryRecord> List<T> getRegistryExternal(RegistryRequest registryRequest) throws RegistryNotAvailableException {
        return transport.sendRequest(registryRequest, Endpoints.REGISTRY.url + provider.getNameProviderForServer().toLowerCase() + EXTERNAL, ListResponse.class, provider.getDataFormat().getRegistryType()).getRecords();
    }

    @Override
    public PlayerBlockResponse blockPlayer(InternalAPIRequest request, String uri) throws RegistryNotAvailableException {
        return transport.sendRequest(
                request,
                Endpoints.PLAYER_BLOCK.url + uri,
                PlayerBlockResponse.class, provider.getDataFormat().getRegistryType());
    }
}
