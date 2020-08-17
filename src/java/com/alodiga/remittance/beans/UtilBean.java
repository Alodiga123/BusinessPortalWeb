package com.alodiga.remittance.beans;

import com.portal.business.commons.data.BusinessData;
import com.portal.business.commons.data.BusinessSellData;
import com.portal.business.commons.data.OperatorData;
import com.portal.business.commons.data.PosData;
import com.portal.business.commons.enumeration.OperationType;
import com.portal.business.commons.exceptions.EmptyListException;
import com.portal.business.commons.exceptions.GeneralException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author hvarona
 */
@ManagedBean(name = "utilBean")
@SessionScoped
public class UtilBean {

    private static final PosData posData = new PosData();
    private static final OperatorData operatorData = new OperatorData();

    private static final int MAX_WEEEKS = 8;

    private BarChartModel salesChart = null;
    private ChartSeries salesSeries;
    long lastWeeksSales = 0;

    private BarChartModel rechargesChart = null;
    private ChartSeries rechargesSeries;
    long lastWeeksRecharges = 0;

    private BarChartModel withdrawChart = null;
    private ChartSeries withdrawSeries;
    long lastWeeksWithdraws = 0;

    private final BusinessSellData sellData = new BusinessSellData();
    private final BusinessData businessData = new BusinessData();

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
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public void setLenguajeBean(LanguajeBean lenguajeBean) {
        this.lenguajeBean = lenguajeBean;
    }

    private void loadSellChart() {
        salesChart = new BarChartModel();

        salesSeries = new ChartSeries();
        salesSeries.setLabel(msg.getString("sells"));
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        salesSeries.set(1, getWeekSales(startDate, endDate));

        for (int i = 1; i < MAX_WEEEKS + 1; i++) {
            endDate = cal.getTime();
            cal.setTimeInMillis(cal.getTimeInMillis() - (7 * 24 * 60 * 60000));
            startDate = cal.getTime();
            salesSeries.set(i, getWeekSales(startDate, endDate));
        }

        salesChart.addSeries(salesSeries);
        salesChart.setTitle("Bar Chart");
        salesChart.setLegendPosition("ne");

        Axis xAxis = salesChart.getAxis(AxisType.X);
        xAxis.setLabel(msg.getString("week"));
        

        Axis yAxis = salesChart.getAxis(AxisType.Y);
        yAxis.setLabel(msg.getString("sells"));
    }

    private void loadRechargeChart() {
        rechargesChart = new BarChartModel();

        rechargesSeries = new ChartSeries();
        rechargesSeries.setLabel(msg.getString("recharges"));
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        rechargesSeries.set(1, getWeekRecharges(startDate, endDate));

        for (int i = 1; i < MAX_WEEEKS + 1; i++) {
            endDate = cal.getTime();
            cal.setTimeInMillis(cal.getTimeInMillis() - (7 * 24 * 60 * 60000));
            startDate = cal.getTime();
            rechargesSeries.set(i, getWeekRecharges(startDate, endDate));
        }

        rechargesChart.addSeries(rechargesSeries);
        rechargesChart.setTitle("Bar Chart");
        rechargesChart.setLegendPosition("ne");

        Axis xAxis = rechargesChart.getAxis(AxisType.X);
        xAxis.setLabel(msg.getString("week"));

        Axis yAxis = rechargesChart.getAxis(AxisType.Y);
        yAxis.setLabel(msg.getString("recharges"));
    }

    private void loadWithdrawChart() {
        withdrawChart = new BarChartModel();

        withdrawSeries = new ChartSeries();
        withdrawSeries.setLabel(msg.getString("withdraws"));
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        withdrawSeries.set(1, getWeekWithdraws(startDate, endDate));

        for (int i = 1; i < MAX_WEEEKS + 1; i++) {
            endDate = cal.getTime();
            cal.setTimeInMillis(cal.getTimeInMillis() - (7 * 24 * 60 * 60000));
            startDate = cal.getTime();
            withdrawSeries.set(i, getWeekWithdraws(startDate, endDate));
        }

        withdrawChart.addSeries(withdrawSeries);
        withdrawChart.setTitle("Bar Chart");
        withdrawChart.setLegendPosition("ne");

        Axis xAxis = withdrawChart.getAxis(AxisType.X);
        xAxis.setLabel(msg.getString("week"));
        

        Axis yAxis = withdrawChart.getAxis(AxisType.Y);
        yAxis.setLabel(msg.getString("withdraws"));
    }

    public BarChartModel getSalesChart() {
        if (salesChart == null) {
            loadSellChart();
        }

        /*Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date startDate = cal.getTime();

        salesSeries.set(1, getWeekSales(startDate, new Date()));*/
        salesSeries.set(1, lastWeeksSales);

        return salesChart;
    }

    public BarChartModel getRechargesChart() {
        if (rechargesChart == null) {
            loadRechargeChart();
        }
        /*Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date startDate = cal.getTime();

        rechargesSeries.set(1, getWeekRecharges(startDate, new Date()));*/
        rechargesSeries.set(1, lastWeeksRecharges);

        return rechargesChart;
    }

    public BarChartModel getWithdrawsChart() {
        if (withdrawChart == null) {
            loadWithdrawChart();
        }
        /*Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date startDate = cal.getTime();

        withdrawSeries.set(1, getWeekWithdraws(startDate, new Date()));*/
        withdrawSeries.set(1, lastWeeksWithdraws);

        return withdrawChart;
    }

    public long getWeekSales() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date startDate = cal.getTime();

        lastWeeksSales = getWeekSales(startDate, new Date());
        return lastWeeksSales;
    }

    public long getWeekRecharges() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date startDate = cal.getTime();

        lastWeeksRecharges = getWeekRecharges(startDate, new Date());
        return lastWeeksRecharges;
    }

    public long getWeekWithdraws() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1);
        Date startDate = cal.getTime();

        lastWeeksWithdraws = getWeekWithdraws(startDate, new Date());
        return lastWeeksWithdraws;
    }

    private long getWeekSales(Date start, Date end) {
        //return (long) (Math.random() * 100) + 10;
        try {
            return sellData.getBusinessSalesNumber(loginBean.getCurrentBusiness(), start, end);
        } catch (GeneralException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private long getWeekRecharges(Date start, Date end) {
        //return (long) (Math.random() * 100);
        try {
            return businessData.getBusinessTransactionsNumber(loginBean.getCurrentBusiness(), start, end, OperationType.RECHARGE);
        } catch (GeneralException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private long getWeekWithdraws(Date start, Date end) {
        //return (long) (Math.random() * 100);
        try {
            return businessData.getBusinessTransactionsNumber(loginBean.getCurrentBusiness(), start, end, OperationType.WITHDRAW);
        } catch (GeneralException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getStoreAmount() {
        try {
            return posData.getStoreList(loginBean.getCurrentBusiness()).size();
        } catch (EmptyListException ex) {
            Logger.getLogger(UtilBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(UtilBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int getPosAmount() {
        try {
            return posData.getPosList(loginBean.getCurrentBusiness()).size();
        } catch (EmptyListException ex) {
            Logger.getLogger(UtilBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(UtilBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int getOperatorAmount() {
        try {
            return operatorData.getOperatorList(loginBean.getCurrentBusiness()).size();
        } catch (EmptyListException ex) {
            Logger.getLogger(UtilBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(UtilBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

}
