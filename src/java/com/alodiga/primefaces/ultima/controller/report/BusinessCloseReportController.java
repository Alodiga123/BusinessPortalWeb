package com.alodiga.primefaces.ultima.controller.report;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.portal.business.commons.data.BusinessCloseData;
import com.portal.business.commons.enumeration.OperationType;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.models.BusinessClose;
import com.portal.business.commons.models.BusinessSell;
import com.portal.business.commons.models.BusinessTransaction;
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
@ManagedBean(name = "businessCloseReportController")
@ViewScoped
public class BusinessCloseReportController {

    private Date startDate;

    private Date endDate;

    private List<BusinessClose> resultList;
    private List<BusinessClose> filterList;

    private BusinessCloseData businessCloseData;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    ResourceBundle msg;

    @ManagedProperty(value = "#{languajeBean}")
    LanguajeBean lenguajeBean;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }
        businessCloseData = new BusinessCloseData();

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

    public List<BusinessClose> getResultList() {
        return resultList;
    }

    public void setResultList(List<BusinessClose> resultList) {
        this.resultList = resultList;
    }

    public List<BusinessClose> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<BusinessClose> filterList) {
        this.filterList = filterList;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public Date getMaxDate() {
        return new Date();
    }

    public int getTransactionTotal(BusinessClose close) {
        try {
            return businessCloseData.getBusinessCloseTransactions(close).size();
        } catch (EmptyListException ex) {
            return 0;
        } catch (GeneralException ex) {
            Logger.getLogger(BusinessCloseReportController.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public float getCommisionTotal(BusinessClose close) {
        float answer = 0;
        try {

            List<BusinessTransaction> transactions = businessCloseData.getBusinessCloseTransactions(close);
            for (BusinessTransaction transaction : transactions) {
                answer += transaction.getBusinessFee();
            }
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            Logger.getLogger(BusinessCloseReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;
    }

    public int getSellTotal(BusinessClose close) {
        try {
            return businessCloseData.getBusinessCloseSells(close).size();
        } catch (EmptyListException ex) {
            return 0;
        } catch (GeneralException ex) {
            Logger.getLogger(BusinessCloseReportController.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public float getSellAmountTotal(BusinessClose close) {
        float answer = 0;
        try {
            List<BusinessSell> sells = businessCloseData.getBusinessCloseSells(close);
            for (BusinessSell sell : sells) {
                answer += sell.getAmount();
            }
        } catch (EmptyListException ex) {

        } catch (GeneralException ex) {
            Logger.getLogger(BusinessCloseReportController.class.getName()).log(Level.SEVERE, null, ex);

        }
        return answer;
    }

    public void doReport() {
        try {
            resultList = businessCloseData.getBusinessCloseReport(loginBean.getCurrentBusiness(), startDate, endDate);

        } catch (GeneralException ex) {
            Logger.getLogger(SellReportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EmptyListException ex) {
            resultList = new ArrayList();
        }
    }

}
