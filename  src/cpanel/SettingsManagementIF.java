public interface SettingsManagementIF {

    void save(String key, String val, String descr, String comment, boolean local, String operator, String ip);

    void remove(Setting setting);

    Setting get(String key, boolean local);

    List<Setting> findAll();
}
