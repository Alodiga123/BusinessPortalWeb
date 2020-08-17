package com.alodiga.primefaces.ultima.controller.businessclose;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.portal.business.commons.data.BusinessCloseData;
import com.portal.business.commons.data.OperatorData;
import com.portal.business.commons.data.PosData;
import com.portal.business.commons.data.StoreData;
import com.portal.business.commons.enumeration.OperationType;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import com.portal.business.commons.exceptions.NullParameterException;
import com.portal.business.commons.models.BusinessClose;
import com.portal.business.commons.models.BusinessSell;
import com.portal.business.commons.models.BusinessTransaction;
import com.portal.business.commons.models.Operator;
import com.portal.business.commons.models.Pos;
import com.portal.business.commons.models.Store;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author hvarona
 */
@ManagedBean(name = "businessCloseController")
@ViewScoped
public class BusinessCloseController {

    private BusinessClose businessClose;

    private List<BusinessTransaction> transactions = new ArrayList();

    private List<BusinessSell> sells = new ArrayList();

    private List<StorePosSellReport> storePosReport;

    private ResourceBundle msg;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean lenguajeBean;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        if (lenguajeBean == null || lenguajeBean.getLanguaje() == null || lenguajeBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(lenguajeBean.getLanguaje()));
        }

        // Business Close for testing
        /*businessClose = new BusinessClose(loginBean.getCurrentBusiness(), new Date(), BusinessClose.CloseStatus.PROCESSED);
        businessClose.setId(1L);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.roll(Calendar.SECOND, -7200);
        long transactionId = 1;
        transactions.add(new BusinessTransaction(loginBean.getCurrentBusiness(), businessClose, loginBean.getUserSession(), cal.getTime(), transactionId, OperationType.CARD_CONSULT, 0, 0, 0));
        cal.roll(Calendar.SECOND, 150);
        ++transactionId;
        transactions.add(new BusinessTransaction(loginBean.getCurrentBusiness(), businessClose, loginBean.getUserSession(), cal.getTime(), transactionId, OperationType.CARD_ACTIVATED, 0, 0, 0));
        cal.roll(Calendar.SECOND, 150);
        ++transactionId;
        transactions.add(new BusinessTransaction(loginBean.getCurrentBusiness(), businessClose, loginBean.getUserSession(), cal.getTime(), transactionId, OperationType.RECHARGE, 10, 190, 200));
        cal.roll(Calendar.SECOND, 150);
        ++transactionId;
        transactions.add(new BusinessTransaction(loginBean.getCurrentBusiness(), businessClose, loginBean.getUserSession(), cal.getTime(), transactionId, OperationType.RECHARGE, 20, 380, 400));
        cal.roll(Calendar.SECOND, 150);
        ++transactionId;
        transactions.add(new BusinessTransaction(loginBean.getCurrentBusiness(), businessClose, loginBean.getUserSession(), cal.getTime(), transactionId, OperationType.RECHARGE, 5, 95, 100));

        cal.roll(Calendar.SECOND, 150);
        ++transactionId;
        transactions.add(new BusinessTransaction(loginBean.getCurrentBusiness(), businessClose, loginBean.getUserSession(), cal.getTime(), transactionId, OperationType.WITHDRAW, 5, 95, 100));

        cal.roll(Calendar.SECOND, 150);
        ++transactionId;
        sells.add(new BusinessSell(cal.getTime(), loginBean.getCurrentBusiness(), 25, "AloWallet", transactionId, businessClose));

        cal.roll(Calendar.SECOND, 150);
        ++transactionId;
        sells.add(new BusinessSell(cal.getTime(), loginBean.getCurrentBusiness(), 12.63f, "AloWallet", transactionId, businessClose));

        processSellsStorePos();*/
    }

    private void processSellsStorePos() {
        try {
            storePosReport = new ArrayList();
            StorePosSellReport mainReport = new StorePosSellReport(null, null);
            Map<Store, StorePosSellReport> storeReports = new HashMap();
            Map<Pos, StorePosSellReport> posReports = new HashMap();

            StoreData storeData = new StoreData();
            List<Store> stores = new ArrayList();
            try {
                stores = storeData.getStores(loginBean.getCurrentBusiness());
            } catch (EmptyListException ex) {
            }

            for (Store store : stores) {
                StorePosSellReport report = new StorePosSellReport(store, null);
                storeReports.put(store, report);
            }

            PosData posData = new PosData();
            List<Pos> posList = new ArrayList();
            try {
                posList = posData.getPosList(loginBean.getCurrentBusiness());
            } catch (EmptyListException ex) {
            }

            for (Pos pos : posList) {
                StorePosSellReport report = new StorePosSellReport(pos.getStore(), pos);
                posReports.put(pos, report);
            }

            for (BusinessSell sell : sells) {
                if (sell.getPos() != null) {
                    posReports.get(sell.getPos()).addAmount(sell.getAmount());
                } else if (sell.getStore() != null) {
                    storeReports.get(sell.getStore()).addAmount(sell.getAmount());
                } else {
                    mainReport.addAmount(sell.getAmount());
                }
            }

            storePosReport.add(mainReport);
            storePosReport.addAll(storeReports.values());
            storePosReport.addAll(posReports.values());
        } catch (GeneralException | NullParameterException ex) {
            Logger.getLogger(BusinessCloseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LanguajeBean getLenguajeBean() {
        return lenguajeBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public BusinessClose getBusinessClose() {
        return businessClose;
    }

    public void setBusinessClose(BusinessClose businessClose) {
        this.businessClose = businessClose;
        BusinessCloseData businessCloseData = new BusinessCloseData();
        try {
            transactions = businessCloseData.getBusinessCloseTransactions(businessClose);
        } catch (EmptyListException ex) {
            transactions = new ArrayList();
        } catch (GeneralException ex) {
            transactions = new ArrayList();
            Logger.getLogger(BusinessCloseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sells = businessCloseData.getBusinessCloseSells(businessClose);
        } catch (EmptyListException ex) {
            sells = new ArrayList();
        } catch (GeneralException ex) {
            sells = new ArrayList();
            Logger.getLogger(BusinessCloseController.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.processSellsStorePos();
    }

    public List<BusinessTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<BusinessTransaction> transactions) {
        this.transactions = transactions;
    }

    public List<BusinessSell> getSells() {
        return sells;
    }

    public void setSells(List<BusinessSell> sells) {
        this.sells = sells;
    }

    public int getSummarySuccessTransaction() {
        return transactions.size();
    }

    public int getSummaryFailedTransaction() {
        return 0;
    }

    public int getSummaryTotalTransaction() {
        return getSummarySuccessTransaction() + getSummaryFailedTransaction();
    }

    public OperationType[] getOperationTypes() {
        return OperationType.values();
    }

    public String getOperationName(OperationType operationType) {
        return msg.getString("OperationType." + operationType.name());
    }

    public int getOperationAmount(OperationType operationType) {
        int answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(operationType)) {
                answer++;
            }
        }

        return answer;
    }

    public float getOperationCommission(OperationType operationType) {
        float answer = 0;

        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(operationType)) {
                answer += transaction.getBusinessFee();
            }
        }
        return answer;
    }

    public int getRechargeTotal() {
        int answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.RECHARGE)) {
                answer++;
            }
        }
        return answer;
    }

    public float getRechargeAmount() {
        float answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.RECHARGE)) {
                answer += transaction.getTotalAmount();
            }
        }
        return answer;
    }

    public int getWithdrawTotal() {
        int answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.WITHDRAW)) {
                answer++;
            }
        }
        return answer;
    }

    public float getWithdrawAmount() {
        float answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.WITHDRAW)) {
                answer += transaction.getTotalAmount();
            }
        }
        return answer;
    }

    public float getCashTotal() {
        return getRechargeAmount() - getWithdrawAmount();
    }

    public List<Operator> getOperators() {
        OperatorData operatorData = new OperatorData();
        try {
            return operatorData.getOperatorList(loginBean.getCurrentBusiness());
        } catch (EmptyListException | GeneralException ex) {
            Logger.getLogger(BusinessCloseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList();
    }

    public int getRechargeTotalByOperator(Operator operator) {
        int answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.RECHARGE)
                    && Objects.equals(transaction.getUser().getId(), operator.getId())) {
                answer++;
            }
        }
        return answer;
    }

    public float getRechargeAmountByOperator(Operator operator) {
        float answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.RECHARGE)
                    && Objects.equals(transaction.getUser().getId(), operator.getId())) {
                answer += transaction.getTotalAmount();
            }
        }
        return answer;
    }

    public int getWithdrawTotalByOperator(Operator operator) {
        int answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.WITHDRAW)
                    && Objects.equals(transaction.getUser().getId(), operator.getId())) {
                answer++;
            }
        }
        return answer;
    }

    public float getWithdrawAmountByOperator(Operator operator) {
        float answer = 0;
        for (BusinessTransaction transaction : transactions) {
            if (transaction.getType().equals(OperationType.WITHDRAW)
                    && Objects.equals(transaction.getUser().getId(), operator.getId())) {
                answer += transaction.getTotalAmount();
            }
        }
        return answer;
    }

    public float getCashTotalByOperator(Operator operator) {
        return getRechargeAmountByOperator(operator) - getWithdrawAmountByOperator(operator);
    }

    public int getSellsSize() {
        return sells.size();
    }

    public float getSellsAmount() {
        float answer = 0;
        for (BusinessSell sell : sells) {
            answer += sell.getAmount();
        }
        return answer;
    }

    public List<StorePosSellReport> getStorePosReport() {
        return storePosReport;
    }

    public class StorePosSellReport implements Comparable {

        private final Store store;
        private final Pos pos;
        private int sellSize = 0;
        private final String settlement;
        private float sellAmount = 0;

        public StorePosSellReport(Store store, Pos pos) {
            this.settlement = "100%";
            this.store = store;
            this.pos = pos;
        }

        public String getStoreName() {
            if (store == null) {
                return msg.getString("main");
            }
            return store.getName();
        }

        public String getPosName() {
            if (pos == null) {
                return "";
            }
            return pos.getPosCode();
        }

        public Store getStore() {
            return store;
        }

        public Pos getPos() {
            return pos;
        }

        public int getSellSize() {
            return sellSize;
        }

        public void setSellSize(int sellSize) {
            this.sellSize = sellSize;
        }

        public String getSettlement() {
            return settlement;
        }

        public float getSellAmount() {
            return sellAmount;
        }

        public void setSellAmount(float sellAmount) {
            this.sellAmount = sellAmount;
        }

        public void addAmount(float floatAmount) {
            sellSize++;
            sellAmount += floatAmount;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 61 * hash + Objects.hashCode(this.store);
            hash = 61 * hash + Objects.hashCode(this.pos);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StorePosSellReport other = (StorePosSellReport) obj;
            if (!Objects.equals(this.store, other.store)) {
                return false;
            }
            if (!Objects.equals(this.pos, other.pos)) {
                return false;
            }
            return true;
        }

        @Override
        public int compareTo(Object obj) {
            if (this == obj) {
                return 0;
            }
            if (obj == null) {
                return 1;
            }

            if (getClass() != obj.getClass()) {
                return 1;
            }

            final StorePosSellReport other = (StorePosSellReport) obj;
            if (this.store == null && other.getStore() == null) {
                return 0;
            }
            if (this.store == null) {
                return 1;
            }
            if (other.getStore() == null) {
                return -1;
            }
            if (!Objects.equals(this.store, other.store)) {
                return (int) (this.store.getId() - other.store.getId()) * 100;
            }
            if (this.pos == null && other.getPos() == null) {
                return 0;
            }
            if (this.pos == null) {
                return 1;
            }
            if (other.getPos() == null) {
                return -1;
            }
            return (int) (this.pos.getId() - other.getPos().getId());
        }

    }
}
