package com.alodiga.businessportal.controlller.request;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.alodiga.wallet.ws.APIAlodigaWalletProxy;
import com.portal.business.commons.cms.CmsCity;
import com.portal.business.commons.cms.CmsCountry;
import com.portal.business.commons.cms.CmsDocumentPersonType;
import com.portal.business.commons.cms.CmsState;
import com.portal.business.commons.cms.data.CmsData;
import com.portal.business.commons.data.CardPreRequestData;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.models.CardPreRequest;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.primefaces.PrimeFaces;

/**
 *
 * @author henry
 */
@ManagedBean(name = "requestController")
@ViewScoped
public class RequestController {

    private static final String LINK_URL = "http://tajetas.alodiga.com?idRequest=";
    private static final String FROM_EMAIL = "businessportal@alodiga.com";

    private List<CmsDocumentPersonType> documentTypes = new ArrayList();
    private CmsDocumentPersonType documentType;

    private List<CmsCountry> countries;
    private CmsCountry country;

    private List<CmsCity> cities = new ArrayList();
    private CmsCity city;

    private List<CmsState> states = new ArrayList();
    private CmsState state;

    private CardPreRequest cardPreRequest;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private ResourceBundle msg;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean lenguajeBean;

    private CmsData cmsData;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }
        cmsData = new CmsData();
        cardPreRequest = new CardPreRequest();
        try {
            countries = cmsData.getCountries();
        } catch (GeneralException ex) {
            ex.printStackTrace();
            countries = new ArrayList();
        }
    }

    public List<CmsDocumentPersonType> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(List<CmsDocumentPersonType> documentTypes) {
        this.documentTypes = documentTypes;
    }

    public CmsDocumentPersonType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(CmsDocumentPersonType documentType) {
        this.documentType = documentType;
        this.cardPreRequest.setDocumentType(documentType);
    }

    public List<CmsCountry> getCountries() {
        return countries;
    }

    public void setCountries(List<CmsCountry> countries) {
        this.countries = countries;
    }

    public CmsCountry getCountry() {
        return country;
    }

    public void setCountry(CmsCountry country) {
        this.country = country;
        this.state = null;
        this.city = null;
        this.documentType = null;
        this.cities = new ArrayList();
        this.getCardPreRequest().setCountry(country);
        if (country != null) {
            try {
                this.states = cmsData.getStates(country);
                this.documentTypes = cmsData.getDocumentTypes(country);
            } catch (NullParameterException | GeneralException ex) {
                ex.printStackTrace();
                this.states = new ArrayList();
            }
        } else {
            this.states = new ArrayList();
            this.documentTypes = new ArrayList();
        }
    }

    public List<CmsCity> getCities() {
        return cities;
    }

    public void setCities(List<CmsCity> cities) {
        this.cities = cities;
    }

    public CmsCity getCity() {
        return city;
    }

    public void setCity(CmsCity city) {
        this.city = city;
    }

    public List<CmsState> getStates() {
        return states;
    }

    public void setStates(List<CmsState> states) {
        this.states = states;

    }

    public CmsState getState() {
        return state;
    }

    public void setState(CmsState state) {
        this.state = state;
        this.city = null;
        this.getCardPreRequest().setState(state);
        if (state != null) {
            try {
                this.cities = cmsData.getCities(state);
            } catch (NullParameterException | GeneralException ex) {
                ex.printStackTrace();
                this.cities = new ArrayList();
            }
        }
    }

    public CardPreRequest getCardPreRequest() {
        return cardPreRequest;
    }

    public void setCardPreRequest(CardPreRequest cardPreRequest) {
        this.cardPreRequest = cardPreRequest;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public Date getMaxBirthdate() {
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.YEAR, -18);
        return cal.getTime();
    }

    private void clearData() {
        documentTypes = new ArrayList();
        documentType = null;
        country = null;

        cities = new ArrayList();
        city = null;

        states = new ArrayList();
        state = null;

        cardPreRequest = new CardPreRequest();
    }

    public void save() {

        try {

            cardPreRequest.setStatus(CardPreRequest.CardPreRequestStatus.PREREQUEST);
            cardPreRequest.setDateRequest(new Date());

            CardPreRequest preRequest = new CardPreRequestData().saveorUpdateCardPreReques(cardPreRequest);
            FacesContext.getCurrentInstance().addMessage("notification", new FacesMessage(FacesMessage.SEVERITY_INFO, "", msg.getString("cardrequest.savesuccesfull")));
            if (cardPreRequest.getSendInfo().equals(CardPreRequest.CardPreRequestSendInfo.EMAIL) || cardPreRequest.getSendInfo().equals(CardPreRequest.CardPreRequestSendInfo.BOTH)) {
                sendMail(preRequest);
                FacesContext.getCurrentInstance().addMessage("notification", new FacesMessage(FacesMessage.SEVERITY_INFO, "", msg.getString("cardrequest.emailsent")));
            }
            if (cardPreRequest.getSendInfo().equals(CardPreRequest.CardPreRequestSendInfo.SMS) || cardPreRequest.getSendInfo().equals(CardPreRequest.CardPreRequestSendInfo.BOTH)) {
                sendSms(preRequest);
                FacesContext.getCurrentInstance().addMessage("notification", new FacesMessage(FacesMessage.SEVERITY_INFO, "", msg.getString("cardrequest.smssent")));
            }
            clearData();
        } catch (NullParameterException | GeneralException ex) {
            ex.printStackTrace();
        }
    }

    private void sendMail(CardPreRequest cardPreRequest) {
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/><style type='text/css'>.Estilo11 {font:13px/0.6em Arial,Helvetica,sans-serif,lighter;color: #333333;font-size:13px;font-weight:bold;}.Estilo12{font:13px/0.6em Arial,Helvetica,sans-serif,lighter;color: #666;font-size:13px;}.EstiloColumn {background-color: #555555;color:#FFBF00;font:12px/1.8em Arial,Helvetica,sans-serif,lighter;font-weight:bold;padding-left:10px}.style1{font:13px/0.6em Arial,Helvetica,sans-serif,lighter;color: #666;font-size:13px;}.style2{background-color: #07b49c;color:#ffff;font:16px/1.8em Arial,Helvetica,sans-serif,lighter;font-weight:bold;padding-left:10px'}</style></head><body><div align='center'><table width='756' border='0'><tr><th width='750'<p><img src='http://sales.alodiga.com/images/img-alodiga-logo.png' align='left' width='114' height='90' longdesc='Logo alodiga' /></p></th></tr></table><table  width='730' border='0' ><tr><th width='728' height='20' align='right' bgcolor='#283593' style='color:#FFFF;font:16px/1.8em Arial,Helvetica,sans-serif,lighter;'> </th></tr><tr><th width='728' height='5' bgcolor='#232323'></th></tr></table><table width='728' border='0'><tr><th width='728'><p align='left' class='Estilo11'><br/>&iexcl;");
        body.append(msg.getString("cardrequest.email.greetings")).append(" ").append(cardPreRequest.getFirstName()).append(" ").append(cardPreRequest.getLastName());
        body.append("&nbsp;! <br/></p></th></tr><tr><th><p align='left' style='font: 16px/1.8em Arial,Helvetica,sans-serif,lighter ; color: #666; font-weight:bold; display: table;  margin: 0; padding:0;' >");
        body.append(msg.getString("cardrequest.email.message"));
        body.append("</p><br/><p align=\"left\"><a href='");
        body.append(LINK_URL).append(cardPreRequest.getId()).append("&isEmail");
        body.append("'>").append(msg.getString("cardrequest.email.linkText"));
        body.append("</a></p></th></tr><tr height='3px'><th width='728' bgcolor='#232323'></th></tr><tr><th><tr height='3px'><th width='728' bgcolor='#232323'></th></tr><tr height='40px'><th height='40px'><div class='Estilo11' align='left'>");
        body.append(msg.getString("cardrequest.email.moreInfo"));
        body.append("<span style='font-size: 13px'><a href='https://www.alodiga.com/'>www.alodiga.com</a></span></div></th></tr><tr><th height='31' bordercolor='#999999'><div align='center'><p align='center' style='font: 10px/1.8em Arial,Helvetica,sans-serif,lighter ; color: #666; display: table;  margin: 0; padding:0;'>");
        body.append(msg.getString("cardrequest.email.thanks"));
        body.append("</p><p align='center' style='font: 10px/1.8em Arial,Helvetica,sans-serif,lighter ; color: #666; display: table;  margin: 0; padding:0;'>");
        body.append(msg.getString("cardrequest.email.messageFooter"));
        body.append("</p></div></th></tr></th></tr></table><div align='center'><p align='center' style='font: 10px/1.8em Arial,Helvetica,sans-serif,lighter ; color: #666; display: table;  margin: 0; padding:0;'>&copy;");
        body.append(msg.getString("copyright"));
        body.append("<br/></div></div></body></html>");

        APIAlodigaWalletProxy proxy = new APIAlodigaWalletProxy();
        try {
            proxy.sendMail(msg.getString("cardrequest.email.subject"), body.toString(), cardPreRequest.getEmail(), FROM_EMAIL);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void sendSms(CardPreRequest cardPreRequest) {
        String text = msg.getString("cardrequest.sms.text") + LINK_URL + cardPreRequest.getId() + "&amp;isSms";

        APIAlodigaWalletProxy proxy = new APIAlodigaWalletProxy();
        try {
            proxy.sendSMS(cardPreRequest.getPhoneNumber(), text);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void reset() {
        this.setCountry(null);
        this.cardPreRequest = new CardPreRequest();
        PrimeFaces.current().resetInputs("GenerateRequestForm:dataGrid");

    }

}
