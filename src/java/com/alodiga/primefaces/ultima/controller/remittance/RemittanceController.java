package com.alodiga.primefaces.ultima.controller.remittance;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.alodiga.ws.remittance.services.WSRemittenceMobileProxy;
import com.alodiga.ws.remittance.services.WsCountryListResponse;
import com.alodiga.ws.remittance.services.WsDeliveryFormsReponse;
import com.alodiga.ws.remittance.services.WsLoginResponse;
import com.alodiga.ws.remittance.services.WsPaymentNetworkService;
import com.alodiga.ws.remittance.services.WsRemittenceResponse;
import com.alodiga.ws.remittance.services.WsSummaryPaymentNetworkAndRateResponse;
import com.portal.business.commons.remittance.RemittanceCity;
import com.portal.business.commons.remittance.RemittanceCountry;
import com.portal.business.commons.remittance.RemittanceDeliveryForm;
import com.portal.business.commons.remittance.PaymentNetwork;
import com.portal.business.commons.remittance.RemittancePaymentInfo;
import com.portal.business.commons.remittance.RemittancePerson;
import com.portal.business.commons.remittance.RemittanceState;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "remittanceController")
@ViewScoped
public class RemittanceController {

    private Date applicationDate;
    private RemittancePaymentInfo paymentInfo;
    private RemittancePerson remittent;
    private RemittancePerson receiver;

    private RemittanceCountry selectedRemittentCountry;
    private RemittanceCountry selectedReceiverCountry;
    private List<RemittanceCountry> countries;

    private RemittanceState selectedRemittentState;
    private RemittanceState selectedReceiverState;
    private List<RemittanceState> states;

    private RemittanceCity selectedRemittentCity;
    private RemittanceCity selectedReceiverCity;
    private List<RemittanceCity> cities;

    private RemittanceDeliveryForm selectedDeliveryForm;
    private List<RemittanceDeliveryForm> deliveryForms;

    private PaymentNetwork selectedPaymentNetwork;
    private List<PaymentNetwork> paymentNetworks;

    private String transactionId = "Prueba";
    private String creationDate;
    private String creationHour;

    private int phase;

    private boolean hasError = false;

    private String errorMessage;

    private WSRemittenceMobileProxy remittenceProxy;
    private String token;

    private int retries = 0;

    private final String correspondentId = "1";

    private float businessFee = 0;
    private float otherFee = 0;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    ResourceBundle msg;

    @ManagedProperty(value = "#{languajeBean}")
    LanguajeBean lenguajeBean;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    //Remettence
    public static final String COMMENTARY_REMETTENCE = "REMESA";
    public static final Boolean SENDING_OPTION_SMS_REMETTENCE = true;
    public static final String BANK_REMETTENCE = "1";
    public static final String PAYMENT_SERVICE_REMETTENCE = "1";
    public static final String SALES_TYPE_REMETTENCE = "1";
    public static final String LANGUAGE_REMETTENCE = "1";
    public static final String STORE_REMETTENCE = "1";
    public static final String PAYMENT_METHOD_REMITTANCE = "3";
    public static final String SERVICE_TYPE_REMITTANCE = "1";
    public static final String USER_REMITTANCE = "1";
    public static final String CASH_BOX_REMITTANCE = "CAJERO";
    public static final Float ADDITIONAL_CHANGES_REMITTANCE = 1F;
    public static final String SALES_PRICE_REMITTANCE = "1";
    public static final String POINT_REMITTANCE = "1";
    public static final Long PRODUCT_REMITTANCE = 2L;

    @PostConstruct
    public void init() {
        try {
            if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
                msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
            } else {
                msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
            }
            phase = 0;
            countries = new ArrayList();
            states = new ArrayList();
            cities = new ArrayList();
            deliveryForms = new ArrayList();
            paymentNetworks = new ArrayList();
            remittent = new RemittancePerson();
            receiver = new RemittancePerson();
            paymentInfo = new RemittancePaymentInfo();
            remittenceProxy = new WSRemittenceMobileProxy();
            loginWs();
            //List of Countries TODO cambiar a ws
            WsCountryListResponse response = remittenceProxy.getCountries();
            switch (response.getCode()) {
                case "0": {

                    for (com.alodiga.ws.remittance.services.Country wsCountry : response.getCountry()) {
                        RemittanceCountry country = new RemittanceCountry();
                        country.setId(wsCountry.getId());
                        country.setIso(wsCountry.getIso());
                        country.setName(wsCountry.getName());
                        country.setShortName(wsCountry.getShortName());
                        countries.add(country);
                    }
                }
                break;
                default:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.general") + " : " + response.getCode()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.generalConnection")));
        }

    }

    private void loginWs() {
        try {
            WsLoginResponse loginResponse = remittenceProxy.loginWS("alodiga", "d6f80e647631bb4522392aff53370502");
            if (loginResponse.getCode().equals("0")) {
                token = loginResponse.getToken();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public RemittancePaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(RemittancePaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public RemittancePerson getRemittent() {
        return remittent;
    }

    public void setRemittent(RemittancePerson remittent) {
        this.remittent = remittent;
    }

    public RemittancePerson getReceiver() {
        return receiver;
    }

    public void setReceiver(RemittancePerson receiver) {
        this.receiver = receiver;
    }

    public RemittanceCountry getSelectedRemittentCountry() {
        return selectedRemittentCountry;
    }

    public void setSelectedRemittentCountry(RemittanceCountry selectedRemittentCountry) {
        for (RemittanceCountry country : countries) {
            if (country.equals(selectedRemittentCountry)) {
                this.selectedRemittentCountry = country;
                break;
            }
        }
        this.remittent.getAddress().setCountry(this.selectedRemittentCountry);
        this.remittent.getAddress().setCountryName(this.selectedRemittentCountry.getName());

    }

    public RemittanceCountry getSelectedReceiverCountry() {
        return selectedReceiverCountry;

    }

    public void setSelectedReceiverCountry(RemittanceCountry selectedReceiverCountry) {
        for (RemittanceCountry country : countries) {
            if (country.equals(selectedReceiverCountry)) {
                this.selectedReceiverCountry = country;
                break;
            }
        }
        this.receiver.getAddress().setCountry(this.selectedReceiverCountry);
        this.receiver.getAddress().setCountryName(this.selectedReceiverCountry.getName());
    }

    public List<RemittanceCountry> getCountries() {
        return countries;
    }

    public RemittanceState getSelectedRemittentState() {
        return selectedRemittentState;
    }

    public void setSelectedRemittentState(RemittanceState selectedRemittentState) {
        this.selectedRemittentState = selectedRemittentState;
    }

    public RemittanceState getSelectedReceiverState() {
        return selectedReceiverState;
    }

    public void setSelectedReceiverState(RemittanceState selectedReceiverState) {
        this.selectedReceiverState = selectedReceiverState;
    }

    public List<RemittanceState> getStates() {
        return states;
    }

    public RemittanceCity getSelectedRemittentCity() {
        return selectedRemittentCity;
    }

    public void setSelectedRemittentCity(RemittanceCity selectedRemittentCity) {
        this.selectedRemittentCity = selectedRemittentCity;
    }

    public RemittanceCity getSelectedReceiverCity() {
        return selectedReceiverCity;
    }

    public void setSelectedReceiverCity(RemittanceCity selectedReceiverCity) {
        this.selectedReceiverCity = selectedReceiverCity;
    }

    public List<RemittanceCity> getCities() {
        return cities;
    }

    public List<RemittanceDeliveryForm> getDeliveryForms() {
        return deliveryForms;
    }

    public RemittanceDeliveryForm getSelectedDeliveryForm() {
        return selectedDeliveryForm;
    }

    public void setSelectedDeliveryForm(RemittanceDeliveryForm selectedDeliveryForm) {
        this.selectedDeliveryForm = selectedDeliveryForm;
        this.paymentInfo.setDeliveryForm(selectedDeliveryForm);
    }

    public PaymentNetwork getSelectedPaymentNetwork() {
        return selectedPaymentNetwork;
    }

    public void setSelectedPaymentNetwork(PaymentNetwork selectedPaymentNetwork) {
        for (PaymentNetwork paymentNetwork : paymentNetworks) {
            if (paymentNetwork.equals(selectedPaymentNetwork)) {
                this.selectedPaymentNetwork = paymentNetwork;
            }
        }
        this.paymentInfo.setPaymentNetwork(this.selectedPaymentNetwork);
    }

    public List<PaymentNetwork> getPaymentNetworks() {
        return paymentNetworks;
    }

    public boolean isHasError() {
        return hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getCreationHour() {
        return creationHour;
    }

    public int getPhase() {
        return phase;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public String getDialogTitle() {
        if (hasError) {
            return msg.getString("error");
        }
        switch (phase) {
            case 0:
            case 1:
            case 2:
                return msg.getString("dataAdquisition");
            case 3:
                return msg.getString("confirm");
            case 4:
                return msg.getString("summary");

        }
        return msg.getString("confirm");
    }

    public float getTotalFee() {
        return otherFee + businessFee;
    }

    public void handleReceiverCountryChange() {
        try {
            deliveryForms = new ArrayList();
            paymentNetworks = new ArrayList();
            WsPaymentNetworkService response = remittenceProxy.getPaymentNetworkByCountryId(token, Long.toString(this.selectedReceiverCountry.getId()));
            switch (response.getCode()) {
                case "0": {
                    for (com.alodiga.ws.remittance.services.PaymentNetwork wsPayment : response.getPaymentNetworks()) {
                        PaymentNetwork payment = new PaymentNetwork();
                        payment.setId(wsPayment.getId());
                        payment.setName(wsPayment.getName());
                        payment.setCountry(this.selectedReceiverCountry);
                        paymentNetworks.add(payment);
                    }
                    retries = 0;
                }
                break;
                case "50": {
                    if (retries < 3) {
                        loginWs();
                        ++retries;
                        this.handleReceiverCountryChange();

                    }
                }
                break;
                default:
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.general") + " : " + response.getCode()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.generalConnection")));
        }
    }

    public void handlePaymentNetworkChange() {
        try {
            deliveryForms = new ArrayList();
            if (this.selectedPaymentNetwork != null) {
                WsDeliveryFormsReponse response = remittenceProxy.getDeliveryFormByPamentNetwork(token, Long.toString(this.selectedPaymentNetwork.getId()));
                switch (response.getCode()) {
                    case "0": {
                        deliveryForms = new ArrayList();
                        for (com.alodiga.ws.remittance.services.DeliveryForm wsDelivery : response.getDeliveryForms()) {
                            RemittanceDeliveryForm delivery = new RemittanceDeliveryForm();
                            delivery.setId(wsDelivery.getId());
                            delivery.setName(wsDelivery.getName());
                            deliveryForms.add(delivery);
                        }
                    }
                    break;
                    case "50": {
                        if (retries < 3) {
                            loginWs();
                            ++retries;
                            this.handlePaymentNetworkChange();
                        }
                    }
                    break;
                    default:
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.general") + " : " + response.getCode()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg.getString("error.generalConnection")));
        }
    }

    public void submitPaymentInfo() {
        phase = 1;
    }

    public void submitRemittentInfo() {
        phase = 2;
    }

    private float truncAmount(float in) {
        return (float) Math.floor(in * 100) / 100;
    }

    public void submitReceiverInfo() {
        try {
            float originAmount = truncAmount(paymentInfo.getAmountOrigin());
            if (paymentInfo.isIncludeFee()) {
                businessFee = truncAmount((paymentInfo.getAmountOrigin() * loginBean.getBusinessPercentFee()) / (100 + loginBean.getBusinessPercentFee()));
                originAmount = originAmount - businessFee;
            }

            WsSummaryPaymentNetworkAndRateResponse response = remittenceProxy.getRemettenceSummary(token,
                    Long.toString(remittent.getAddress().getCountry().getId()),
                    Long.toString(receiver.getAddress().getCountry().getId()),
                    paymentInfo.getDeliveryForm().getId(), originAmount, paymentInfo.isIncludeFee());
            switch (response.getCode()) {
                case "0": {
                    if (!paymentInfo.isIncludeFee()) {
                        businessFee = truncAmount((response.getRealAmountToSend() * loginBean.getBusinessPercentFee() / 100));
                    }

                    float totalAmount = response.getRealAmountToSend() + businessFee;

                    paymentInfo.setTotalAmount(totalAmount);
                    paymentInfo.setAmountDestiny(Float.parseFloat(response.getReceiverAmount()));
                    paymentInfo.setAmountOrigin(response.getAmountToSendRemettence());
                    paymentInfo.setExchangeRateId(Long.toString(response.getExchangeRateDestiny().getId()));
                    paymentInfo.setRatePaymentNetworkId(Long.toString(response.getRatePaymentNetwork().getId()));
                    remittent.setCurrencyId(Long.toString(response.getExchangeRateSource().getCurrency().getId()));
                    receiver.setCurrencyId(Long.toString(response.getExchangeRateDestiny().getCurrency().getId()));
                    otherFee = response.getRealAmountToSend() - response.getAmountToSendRemettence();

                    phase = 3;
                }
                break;
                case "50": {
                    if (retries < 3) {
                        loginWs();
                        ++retries;
                        this.submitReceiverInfo();
                    }
                }
                break;
                default:
                    hasError = true;
                    errorMessage = msg.getString("error.general") + " : " + response.getCode();
            }

        } catch (Exception e) {
            e.printStackTrace();
            hasError = true;
            errorMessage = msg.getString("error.generalConnection");
        }

    }

    public void confirmRemittance() {
        System.out.println("en confirmRemittance");
        applicationDate = new Date();
        try {

            WsRemittenceResponse response = remittenceProxy.saverRemittence(
                    dateFormat.format(applicationDate),
                    COMMENTARY_REMETTENCE,
                    paymentInfo.getAmountOrigin(),
                    paymentInfo.getTotalAmount(),
                    SENDING_OPTION_SMS_REMETTENCE,
                    paymentInfo.getAmountDestiny(),
                    BANK_REMETTENCE,
                    PAYMENT_SERVICE_REMETTENCE,
                    ADDITIONAL_CHANGES_REMITTANCE,
                    correspondentId,
                    SALES_TYPE_REMETTENCE,
                    paymentInfo.getExchangeRateId(),
                    paymentInfo.getRatePaymentNetworkId(),
                    SALES_PRICE_REMITTANCE,
                    LANGUAGE_REMETTENCE,
                    remittent.getCurrencyId(),
                    receiver.getCurrencyId(),
                    STORE_REMETTENCE,
                    PAYMENT_METHOD_REMITTANCE,
                    SERVICE_TYPE_REMITTANCE,
                    Long.toString(paymentInfo.getPaymentNetwork().getId()),
                    POINT_REMITTANCE,
                    USER_REMITTANCE,
                    CASH_BOX_REMITTANCE,
                    Long.toString(paymentInfo.getDeliveryForm().getId()),
                    remittent.getFirstName(),
                    remittent.getMiddleName(),
                    remittent.getLastName(),
                    remittent.getSecondSurname(),
                    remittent.getPhoneNumber(),
                    remittent.getEmail(),
                    ((remittent.getAddress().getCountry() != null) ? Long.toString(remittent.getAddress().getCountry().getId()) : null),
                    ((remittent.getAddress().getCity() != null) ? Long.toString(remittent.getAddress().getCity().getId()) : null),
                    ((remittent.getAddress().getState() != null) ? Long.toString(remittent.getAddress().getState().getId()) : null),
                    remittent.getAddress().getCountryName(),
                    remittent.getAddress().getCityName(),
                    remittent.getAddress().getAddress(),
                    remittent.getAddress().getZipCode(),
                    receiver.getFirstName(),
                    receiver.getMiddleName(),
                    receiver.getLastName(),
                    receiver.getSecondSurname(),
                    receiver.getPhoneNumber(),
                    receiver.getEmail(),
                    ((receiver.getAddress().getCountry() != null) ? Long.toString(receiver.getAddress().getCountry().getId()) : null),
                    ((receiver.getAddress().getCity()) != null ? Long.toString(receiver.getAddress().getCity().getId()) : null),
                    ((receiver.getAddress().getState()) != null ? Long.toString(receiver.getAddress().getState().getId()) : null),
                    receiver.getAddress().getCountryName(),
                    receiver.getAddress().getCityName(),
                    receiver.getAddress().getAddress(),
                    receiver.getAddress().getZipCode()
            );
            switch (response.getCode()) {
                case "0": {
                    transactionId = response.getRemittanceId();
                    creationDate = response.getRemittanceSingleResponse().getCreationDate();
                    creationHour = response.getRemittanceSingleResponse().getCreationHour();
                    phase = 4;
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(msg.getString("remittanceSuccess")));
                }
                break;
                default:
                    hasError = true;
                    errorMessage = msg.getString("error.general") + " : " + response.getCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasError = true;
            errorMessage = msg.getString("error.generalConnection");
        }
    }

    public void reset() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().clear();
    }

    public void errorBack() {
        hasError = false;
    }
}
