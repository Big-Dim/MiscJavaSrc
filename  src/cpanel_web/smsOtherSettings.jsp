<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@ taglib prefix="l" uri="/info/LitotaLib" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<f:subview id="SMSOtherSettingsPage">

<c:if test="${modeBean.jbossMode}">
    <a4j:keepAlive beanName="SMSOtherSettingsBean"/>
</c:if>

    <h:outputFormat>
        <h4><h:outputText value="#{msg.jsp_menu_other_settings_sms}"/></h4>
    </h:outputFormat>

    <rich:spacer height="5"/>

    <rich:messages id="messages"
                   errorClass="messagesError"
                   infoClass="messagesInfo"/>

    <a4j:form id="tableForm" ajaxSubmit="true">
        <l:checkbox labelPosition="left" value="#{SMSOtherSettingsBean.settingsManagementBean.securityMode}" size="120%" label="Режим безопасных настроек" rendered="#{isRole.SETTINGS_SECURITY_OPERATOR}">
            <a4j:support event="onchange" reRender="tableForm" action="#{SMSOtherSettingsBean.settingsManagementBean.refreshData}"/>
        </l:checkbox>

        <rich:spacer height="5"/>

        <h:panelGrid>
            <h:outputText value="#{msg.sms_smsSettings_filtr}"/>
            <h:inputText value="#{SMSOtherSettingsBean.settingsManagementBean.filterValue}" id="input">
                <a4j:support requestDelay="1250" event="onkeyup" reRender="table" focus="input"/>
            </h:inputText>
            <rich:dataTable width="470px" rows="15" id="table"
                            value="#{SMSOtherSettingsBean.settingsManagementBean.settingList}" var="setting"
                            style="width: 100%; cursor: pointer;"
                            onRowMouseOver="this.style.backgroundColor='#F1F1F1'"
                            onRowMouseOut="this.style.backgroundColor='#{a4jSkin.tableBackgroundColor}'"
            >
                <rich:column width="10px"
                             style="background-color: #{SMSOtherSettingsBean.settingsManagementBean.settingKey == setting.settingKey ? SMSOtherSettingsBean.settingsManagementBean.COLOR_RED : a4jSkin.tableBackgroundColor};"/>
                <rich:column width="200px" filterMethod="#{SMSOtherSettingsBean.settingsManagementBean.filter}">
                    <f:facet name="header">
                        <h:outputText value="#{msg.sms_smsSettings_klyuch}"/>
                    </f:facet>
                    <h:outputText value="#{setting.settingKey}"/>
                </rich:column>
                <rich:column width="250px">
                    <f:facet name="header">
                        <h:outputText value="#{msg.sms_smsSettings_znachenie}"/>
                    </f:facet>
                    <h:outputText value="#{setting.settingValue}"/>
                </rich:column>
                <rich:column>
                    <f:facet name="header">
                        <h:outputText value="#{msg.sms_smsSettings_opisanie}"/>
                    </f:facet>
                    <h:outputText value="#{setting.description}"/>
                </rich:column>
                <a4j:support event="onRowClick" reRender="tableForm"
                             action="#{settingsManagementBean.selectSetting}">
                    <f:setPropertyActionListener value="#{setting.settingKey}" target="#{settingsManagementBean.selectedSettingKey}"/>
                </a4j:support>

                <f:facet name="footer">
                    <rich:datascroller align="left"
                                       maxPages="10"
                                       renderIfSinglePage="false"/>
                </f:facet>

            </rich:dataTable>

        </h:panelGrid>

        <h:panelGrid columns="2">
            <h:outputText value="Ключ"/>
            <h:inputText size="51" value="#{SMSOtherSettingsBean.settingsManagementBean.settingKey}"/>
            <h:outputText value="Значение"/>
            <h:inputText size="51" value="#{SMSOtherSettingsBean.settingsManagementBean.settingValue}"/>
            <h:outputText value="Описание"/>
            <h:inputText size="51" value="#{SMSOtherSettingsBean.settingsManagementBean.description}"/>
            <h:outputText value="Комментарий"/>
            <h:inputText size="51" value="#{SMSOtherSettingsBean.settingsManagementBean.comment}"/>
        </h:panelGrid>
        <h:inputTextarea cols="100" rows="10" value="#{SMSOtherSettingsBean.settingsManagementBean.pack}"/>
        <h:panelGrid columns="2">
        <a4j:commandButton value="#{msg.sms_smsSettings_sohranit}" reRender="#{msg.sms_smsSettings_maingrid_tableform}"
                           action="#{SMSOtherSettingsBean.saveSettings}"/>
        <a4j:commandButton value="#{msg.sms_smsSettings_udalit}" reRender="#{msg.sms_smsSettings_maingrid_tableform}"
                           action="#{SMSOtherSettingsBean.settingsManagementBean.delete}"/>
        </h:panelGrid>
    </a4j:form>
</f:subview>
