
public final class SMSOtherSettingsBean extends FonbetControlPanelBean {
    private SettingsManagementBean settingsManagementBean = FacesContextUtils.findBean("settingsManagementBean", SettingsManagementBean.class);

    public void setSettingsManagementBean(SettingsManagementBean settingsManagementBean) {
        this.settingsManagementBean = settingsManagementBean;
    }

    public SettingsManagementBean getSettingsManagementBean() {
        return settingsManagementBean;
    }

    public void saveSettings() {
        getSettingsManagementBean().save();
    }

    @Override
    public void show() {
        settingsManagementBean.initSMSSettings();
        getNavigationBean().showSMSOtherSettings();
    }
}
