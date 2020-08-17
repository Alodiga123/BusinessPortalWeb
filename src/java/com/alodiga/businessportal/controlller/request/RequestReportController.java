package com.alodiga.businessportal.controlller.request;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.portal.business.commons.cms.CmsCity;
import com.portal.business.commons.cms.CmsCountry;
import com.portal.business.commons.cms.CmsDocumentPersonType;
import com.portal.business.commons.cms.CmsPersonType;
import com.portal.business.commons.cms.CmsState;
import com.portal.business.commons.cms.data.CmsData;
import com.portal.business.commons.data.CardPreRequestData;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.exceptions.RegisterNotFoundException;
import com.portal.business.commons.models.CardPreRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author henry
 */
@ManagedBean(name = "requestReportController")
@ViewScoped
public class RequestReportController {

    private boolean hasDate;

    private Date startDate;

    private Date endDate;

    private boolean statusPrerequest = true;
    private boolean statusPending = true;
    private boolean statusaproved = true;

    private List<CardPreRequest> resultList;

    private List<CardPreRequest> filteredResult;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private ResourceBundle msg;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean lenguajeBean;

    private CardPreRequestData requestData;

    private CmsData cmsData;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }
        requestData = new CardPreRequestData();
        cmsData = new CmsData();
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<CardPreRequest> getResultList() {
        return resultList;
    }

    public void setResultList(List<CardPreRequest> resultList) {
        this.resultList = resultList;
    }

    public List<CardPreRequest> getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(List<CardPreRequest> filteredResult) {
        this.filteredResult = filteredResult;
    }

    public Date getMaxDate() {
        return new Date();
    }

    public CmsCountry getCountry(long idCountry) {
        try {
            return cmsData.getCountry(idCountry);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public CmsCity getCity(long idCity) {
        try {
            return cmsData.getCity(idCity);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public CmsState getState(long idState) {
        try {
            return cmsData.getState(idState);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public CmsPersonType getPersonType(long idPersonType) {
        try {
            return cmsData.getPersonType(idPersonType);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public CmsDocumentPersonType getDocumentPersonType(long idDocumentPersonType) {
        try {
            return cmsData.getDocumentPersonType(idDocumentPersonType);
        } catch (RegisterNotFoundException | NullParameterException | GeneralException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getStatus(CardPreRequest.CardPreRequestStatus status) {
        return msg.getString("cardrequestStatus" + status.name());
    }

    public boolean isHasDate() {
        return hasDate;
    }

    public void setHasDate(boolean hasDate) {
        this.hasDate = hasDate;
    }

    public boolean isStatusPrerequest() {
        return statusPrerequest;
    }

    public void setStatusPrerequest(boolean statusPrerequest) {
        this.statusPrerequest = statusPrerequest;
    }

    public boolean isStatusPending() {
        return statusPending;
    }

    public void setStatusPending(boolean statusPending) {
        this.statusPending = statusPending;
    }

    public boolean isStatusaproved() {
        return statusaproved;
    }

    public void setStatusaproved(boolean statusaproved) {
        this.statusaproved = statusaproved;
    }

    public void doAllReport() {
        try {
            resultList = requestData.getAllRequest();
        } catch (EmptyListException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doFilteredReport() {
        try {
            List<CardPreRequest.CardPreRequestStatus> statusReport = new ArrayList();

            if (statusPending) {
                statusReport.add(CardPreRequest.CardPreRequestStatus.PENDING);
            }
            if (statusPrerequest) {
                statusReport.add(CardPreRequest.CardPreRequestStatus.PREREQUEST);
            }
            if (statusaproved) {
                statusReport.add(CardPreRequest.CardPreRequestStatus.APROVED);
            }
            if (hasDate) {
                resultList = requestData.getRequestsReport(startDate, endDate, statusReport);
            } else {
                resultList = requestData.getRequestsReport(null, null, statusReport);
            }
        } catch (EmptyListException ex) {
            resultList = new ArrayList();
            
        } catch (GeneralException ex) {
            Logger.getLogger(RequestReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
