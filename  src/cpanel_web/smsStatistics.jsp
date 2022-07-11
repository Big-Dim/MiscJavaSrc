<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<%@taglib uri="http://richfaces.org/rich" prefix="rich" %>
<%@ taglib prefix="l" uri="/info/LitotaLib" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<f:subview id="SMSStatisticsPage">
    <rich:messages errorClass="messagesError"
                   infoClass="messagesInfo"
                   id="smsStatisticsErrors"/>
    <c:if test="${modeBean.jbossMode}">
    <a4j:keepAlive beanName="SMSStatisticsBean"/>
    </c:if>
    <h:outputFormat>
        <h4><h:outputText value="#{msg.sms_smsStatistics_header_statistika_sms}"/></h4>
    </h:outputFormat>
    <h:panelGrid columns="1">
        <a4j:region>
            <h:form>
                <a4j:poll id="poll"
                          interval="5000"
                          enabled="#{SMSStatisticsBean.pollEnable}"
                          reRender="statistics"
                          action="#{SMSStatisticsBean.pull}"/>
            </h:form>
        </a4j:region>
        <a4j:form ajaxSubmit="true" reRender="statistics">
            <rich:panel>
                <h:panelGroup id="filters">
                    <%--<l:panelGrid columns="3" columnClasses="vertical-align">--%>
                    <l:panelGrid columns="3" columnClasses="vertical-align">
                        <h:outputText value="#{msg.sms_smsStatistics_pokazat_sms}"/>
                        <h:outputText value="#{msg.sms_smsStatistics_provaider}"/>
                        <h:outputText value="#{msg.sms_smsStatistics_tema_sms}"/>
                        <h:panelGrid>
                            <!-- Фильтр по статусам-->
                            <h:selectOneMenu value="#{SMSStatisticsBean.selectedSMSStatus}">
                                <f:selectItems value="#{SMSStatisticsBean.statuses}"/>
                            </h:selectOneMenu>


                            <h:panelGrid columns="3">
                                <!-- Фильтр по PIN клиента-->
                                <h:panelGrid columns="1">
                                    <h:outputText value="#{msg.sms_smsStatistics_pin_klienta}"/>
                                    <h:inputText value="#{SMSStatisticsBean.clientPin}"/>
                                    <h:outputText value="#{msg.sms_smsStatistics_vrong_value}"
                                                  style="color: red"
                                                  rendered="#{SMSStatisticsBean.incorrectPinFlag}"/>
                                </h:panelGrid>
                                <!-- Фильтр по номеру телефона-->
                                <h:panelGrid columns="1">
                                    <h:outputText value="#{msg.sms_smsStatistics_nomer_telefona}"/>
                                    <h:inputText value="#{SMSStatisticsBean.phone}"/>
                                </h:panelGrid>

                                <!-- Фильтр по стране -->
                                <h:panelGrid columns="1" rendered="#{SMSStatisticsBean.renderedPhoneCountryFilters}">
                                    <h:outputText value="#{msg.sms_smsStatistics_strana}"/>
                                    <h:selectOneMenu value="#{SMSStatisticsBean.selectedPhoneCountry}" >
                                        <f:selectItems value="#{SMSStatisticsBean.phoneCountryFilters}"/>
                                    </h:selectOneMenu>
                                </h:panelGrid>

                            </h:panelGrid>
                            <rich:dataTable rendered="#{visibilityBean.multidealerOperator}"
                                            title="#{msg.sms_smsStatistics_dealer}" var="d" value="#{SMSStatisticsBean.dealerFeatures}">
                                <rich:column>
                                    <h:outputText value="#{d.title}"/>
                                </rich:column>
                                <rich:column>
                                    <h:selectBooleanCheckbox value="#{d.selected}">
                                        <a4j:support event="onchange" reRender="frame"
                                                     action="#{SMSStatisticsBean.showProviders}" />
                                    </h:selectBooleanCheckbox>
                                </rich:column>
                            </rich:dataTable>

                            <!--Фильтр по времени создания-->
                            <h:panelGrid columns="2">
                                <h:outputText value="#{msg.sms_smsStatistics_ot}"/>
                                <h:outputText value="#{msg.sms_smsStatistics_do}"/>
                                <rich:calendar value="#{SMSStatisticsBean.since}"
                                               datePattern="dd.MM.yyyy HH:mm:ss" defaultTime="00:00:00"
                                               enableManualInput="true"
                                               showApplyButton="false"
                                               showWeeksBar="false"
                                               showFooter="false"
                                               onchanged="this.value = this.value.replace('ss', '00')"
                                               id="sinceInput"/>
                                <rich:calendar value="#{SMSStatisticsBean.till}"
                                               datePattern="dd.MM.yyyy HH:mm:ss" defaultTime="00:00:00"
                                               enableManualInput="true"
                                               showApplyButton="false"
                                               showWeeksBar="false"
                                               showFooter="false"
                                               onchanged="this.value = this.value.replace('ss', '00')"
                                               id="tillInput"/>
                                <h:outputText value="#{msg.sms_smsStatistics_ne_ukazany_granitsy_vremennogo_intervala}"
                                              rendered="#{SMSStatisticsBean.intervalNotSelected}"
                                              style="color: red"/>
                            </h:panelGrid>

                            <%--<h:commandLink value="#{msg.sms_smsStatistics_poslednii_chas}"
                                           style="white-space: nowrap; color: #{SMSStatisticsBean.hourColor};"
                                           action="#{SMSStatisticsBean.lastHour}"/>

                             <rich:spacer width="5"/>
                            <%--<h:commandLink value="#{msg.sms_smsStatistics_poslednie_sutki}"
                                           style="white-space: nowrap; color: #{SMSStatisticsBean.dayColor};"
                                           action="#{SMSStatisticsBean.lastDay}"/>
                            --%>
                            <h:panelGrid columns="3">
                                <h:commandButton value="#{msg.sms_smsStatistics_poslednii_chas}"
                                                 style="white-space: nowrap; color: #{SMSStatisticsBean.dayColor};"
                                                 action="#{SMSStatisticsBean.lastHour}"/>

                                <h:commandButton value="#{msg.sms_smsStatistics_naiti}"
                                                 action="#{SMSStatisticsBean.find}"/>

                                <h:commandButton value="#{msg.sms_smsStatistics_sbros}"
                                                 action="#{SMSStatisticsBean.clearFilterCriterion}"
                                >
                                    <a4j:support event="onclick" reRender="frame"/>
                                </h:commandButton>

                                <h:panelGrid columns="4">
                                    <h:outputText value="#{msg.sms_smsStatistics_obnovlyat_rezultaty}"/>
                                    <h:selectBooleanCheckbox value="#{SMSStatisticsBean.pollEnable}">
                                        <a4j:support event="onchange" reRender="poll"/>
                                    </h:selectBooleanCheckbox>

                                    <h:outputText value="#{msg.sms_smsStatistics_pokazyvat_sms}"/>
                                    <h:selectBooleanCheckbox value="#{SMSStatisticsBean.showSMS}">
                                        <a4j:support event="onchange" reRender=":statistics" action="#{SMSStatisticsBean.find}"/>
                                    </h:selectBooleanCheckbox>
                                </h:panelGrid>
                            </h:panelGrid>
                        </h:panelGrid>
                        <!-- Провайдер -->
                        <h:panelGrid>
                            <h:outputText value="#{msg.sms_smsStatistics_select_dealer}"
                                      style="color: red"
                                      rendered="#{!SMSStatisticsBean.existsProviders}"/>
                            <h:selectOneMenu value="#{SMSStatisticsBean.selectedProviderFilter}" styleClass="providerClass"
                                         rendered="#{SMSStatisticsBean.existsProviders}">
                                <f:selectItems value="#{SMSStatisticsBean.providerFilters}"/>
                            </h:selectOneMenu>
                        </h:panelGrid>
                        <h:selectManyCheckbox value="#{SMSStatisticsBean.selectedSMSThemes}"
                                              layout="pageDirection">
                            <f:selectItems value="#{SMSStatisticsBean.themes}"/>
                        </h:selectManyCheckbox>
                    </l:panelGrid>
                </h:panelGroup>
            </rich:panel>


            <h:panelGrid id="statistics">
                <rich:spacer height="20px"/>
                <h:outputText value="#{msg.sms_smsStatistics_vsego} #{SMSStatisticsBean.size}"/>
                <rich:spacer height="20px"/>
                <rich:spacer height="20px"/>
                <rich:dataTable value="#{SMSStatisticsBean.listOfSMS}"
                                var="sms"
                                rows="20"
                                rowKeyVar="rowid"
                                rendered="#{SMSStatisticsBean.showSMS}"
                                id="statTable"
                                ajaxKeys="#{SMSStatisticsBean.rowsForUpdate}"
                >
                    <f:facet name="header">
                        <rich:columnGroup>
                            <rich:column sortBy="#{sms.id}">
                                <h:outputText value="Id"/>
                            </rich:column>
                            <rich:column
                                    style="background-color: #{sms.dealerColor};">
                                <h:panelGrid columns="1">
                                    <h:outputText value="#{msg.sms_smsStatistics_dealer_header}" escape="false"/>
                                </h:panelGrid>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_nomer_poluchatelya}"/>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_pin_klienta}"/>
                            </rich:column>
                            <rich:column rendered="#{isRole.SMS_SECURITY_OPERATOR}">
                                <h:outputText value="#{msg.sms_smsStatistics_tekst_soobshcheniya}"/>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_vremya_sozdaniya}"/>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_status}"/>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_kolichestvo_povtornyh_otpravlenii}"/>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_provaider}"/>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_dlitelnost_dostavki_sek}."/>
                            </rich:column>
                            <rich:column>
                                <h:outputText value="#{msg.sms_smsStatistics_opisanie}"/>
                            </rich:column>
                        </rich:columnGroup>
                    </f:facet>
                    <rich:column sortBy="#{sms.id}">
                        <h:outputText value="#{sms.id}"/>
                    </rich:column>
                    <rich:column style="background-color: #{sms.dealerColor};">
                        <h:outputText/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.destNumber}"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.clientPin}"/>
                    </rich:column>
                    <rich:column rendered="#{isRole.SMS_SECURITY_OPERATOR}" id="textSmsColId" style="#{sms.showText?'':'padding: 0;'}">
                        <h:outputText rendered="#{sms.showText}" value="#{sms.text}"/>
                        <a4j:commandButton
                                style="width: 100%;margin: 0;height: 22px;"
                                rendered="#{!sms.showText}"
                                action="#{SMSStatisticsBean.showTextSms}"
                                value="Показать"
                                reRender="textSmsColId"
                                ajaxSingle="true">
                            <f:param name="smsid" value="#{sms.id}"/>
                            <f:param name="rowid" value="#{rowid}"/>
                        </a4j:commandButton>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.createTime}"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.status}"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.repeatCount}"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.providerName}"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.deliveryTime}"/>
                    </rich:column>
                    <rich:column>
                        <h:outputText value="#{sms.reason}"/>
                    </rich:column>

                    <f:facet name="footer">
                        <rich:datascroller id="sc" renderIfSinglePage="false"/>
                    </f:facet>
                </rich:dataTable>
            </h:panelGrid>
        </a4j:form>

        <a4j:form>
            <a4j:htmlCommandLink rendered="#{SMSStatisticsBean.showSMS and SMSStatisticsBean.linkVisible}"
                                 action="#{SMSStatisticsBean.deployToXLS}"
                                 value="#{msg.sms_smsStatistics_skachat_v_xls}">
            </a4j:htmlCommandLink>
        </a4j:form>
    </h:panelGrid>
    <h:outputText rendered="#{!SMSStatisticsBean.existsProviders}" value="#{msg.sms_smsStatistics_no_prov}"/>
</f:subview>
