package com.alodiga.primefaces.ultima.controller.wallet;

import com.alodiga.remittance.beans.LanguajeBean;
import com.alodiga.remittance.beans.LoginBean;
import com.alodiga.wallet.ws.APIAlodigaWalletProxy;
import com.alodiga.wallet.ws.ProductListResponse;
import com.alodiga.wallet.ws.RechargeValidationResponse;
import com.alodiga.wallet.ws.TransactionResponse;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import com.ericsson.alodiga.ws.Usuario;
import com.portal.business.commons.data.BusinessData;
import com.portal.business.commons.enumeration.OperationType;
import com.portal.business.commons.models.BusinessBalanceIncoming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author hvarona
 */
@ManagedBean(name = "rechargeController")
@ViewScoped
public class rechargeController {

    private String userEmailOrPhone;

    private String userEmail;

    private String userPhone;

    private String firstName;

    private String lastName;

    private List<Product> activateProducts;

    private Product selectedProduct;

    private String accountNumber;

    private boolean includeFees;

    private Double rechargeAmount = new Double(0);

    private float rechargeAfterBusiness;

    private float businessFee;

    private float alodigaFee;

    private float totalCharge;

    private long transactionId;

    private int phase = 0;

    private boolean hasError = false;

    private String errorMessage;

    private APIAlodigaWalletProxy allodigaWS = new APIAlodigaWalletProxy();

    private APIRegistroUnificadoProxy registerWS = new APIRegistroUnificadoProxy();

    private Usuario userForRecharge;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{languajeBean}")
    private LanguajeBean languageBean;

    private ResourceBundle msg;

    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d+");

    @PostConstruct
    public void init() {
        if (languageBean == null || languageBean.getLanguaje() == null || languageBean.getLanguaje().isEmpty()) {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag("es"));
        } else {
            msg = ResourceBundle.getBundle("com.alodiga.remittance.messages.message", Locale.forLanguageTag(languageBean.getLanguaje()));
        }
    }

    public String getUserEmailOrPhone() {
        return userEmailOrPhone;
    }

    public void setUserEmailOrPhone(String userEmailOrPhone) {
        this.userEmailOrPhone = userEmailOrPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public boolean isIncludeFees() {
        return includeFees;
    }

    public void setIncludeFees(boolean includeFees) {
        this.includeFees = includeFees;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        if (selectedProduct != null) {
            for (Product prod : activateProducts) {
                if (prod.getId() == selectedProduct.getId()) {
                    this.selectedProduct = prod;
                }
            }
        }
    }

    public List<Product> getActivateProducts() {
        return activateProducts;
    }

    public void setActivateProducts(List<Product> activateProducts) {
        this.activateProducts = activateProducts;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public float getRechargeAfterBusiness() {
        return rechargeAfterBusiness;
    }

    public void setRechargeAfterBusiness(float rechargeAfterBusiness) {
        this.rechargeAfterBusiness = rechargeAfterBusiness;
    }

    public float getBusinessFee() {
        return businessFee;
    }

    public void setBusinessFee(float businessFee) {
        this.businessFee = businessFee;
    }

    public float getAlodigaFee() {
        return alodigaFee;
    }

    public void setAlodigaFee(float alodigaFee) {
        this.alodigaFee = alodigaFee;
    }

    public float getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(float totalCharge) {
        this.totalCharge = totalCharge;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setLanguageBean(LanguajeBean languageBean) {
        this.languageBean = languageBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public void searchUser() {
        try {
            if (this.userEmailOrPhone == null || this.userEmailOrPhone.isEmpty()) {
                hasError = true;
                errorMessage = msg.getString("rechargeWalletRechargeErrorInvalidEmailPhone");
            } else {
                RespuestaUsuario response;
                if (PHONE_PATTERN.matcher(userEmailOrPhone).matches()) {
                    response = registerWS.getUsuariopormovil("usuarioWS", "passwordWS", userEmailOrPhone);
                } else {
                    response = registerWS.getUsuarioporemail("usuarioWS", "passwordWS", userEmailOrPhone);
                }
                if (response != null) {
                    switch (response.getCodigoRespuesta()) {
                        case "00": {
                            userForRecharge = response.getDatosRespuesta();
                            this.processUserInfo();
                            this.getUserProductList();
                        }
                        break;
                        default:
                            hasError = true;
                            errorMessage = msg.getString("error.general") + " : " + response.getCodigoRespuesta();
                    }
                } else {
                    hasError = true;
                    errorMessage = msg.getString("error.registerwsconnection");
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(rechargeController.class.getName()).log(Level.SEVERE, null, ex);
            hasError = true;
            errorMessage = msg.getString("error.registerwsconnection");
        }

    }

    private void getUserProductList() throws RemoteException {
        activateProducts = new ArrayList();
        ProductListResponse productResponse = allodigaWS.getProductsByUserId(Integer.toString(userForRecharge.getUsuarioID()));
        switch (productResponse.getCodigoRespuesta()) {
            case "00": {
                com.alodiga.wallet.ws.Product[] userProducts = productResponse.getProducts();
                if (userProducts == null || userProducts.length <= 0) {
                    hasError = true;
                    errorMessage = msg.getString(("rechargeWalletRechargeErrorNoProducts"));
                } else {
                    for (com.alodiga.wallet.ws.Product userProduct : userProducts) {
                        if (!userProduct.getName().equalsIgnoreCase("Tarjeta Prepagada")) {
                            activateProducts.add(new Product(userProduct.getId(),
                                    userProduct.getName(), userProduct.getSymbol()));
                        }
                    }
                    hasError = false;
                    phase = 1;
                }
            }
            break;
            default:
                hasError = true;
                errorMessage = msg.getString(("rechargeWalletRechargeErrorNoProducts"));
        }
    }

    public float getTotalFee() {
        return truncAmount(businessFee + alodigaFee);
    }

    private float truncAmount(float in) {
        return (float) Math.floor(in * 100) / 100;
    }

    private void processUserInfo() {
        this.firstName = userForRecharge.getNombre();
        this.lastName = userForRecharge.getApellido();
        this.accountNumber = userForRecharge.getCuenta().getNumeroCuenta();
        this.userEmail = userForRecharge.getEmail();
        this.userPhone = userForRecharge.getMovil();
    }

    public void verifyRecharge() {
        try {
            rechargeAmount = (double) truncAmount(rechargeAmount.floatValue());
            if (includeFees) {
                businessFee = truncAmount((rechargeAmount.floatValue() * loginBean.getBusinessPercentFee()) / (100 + loginBean.getBusinessPercentFee()));
            }
            double rechargeSubmit = includeFees ? rechargeAmount - businessFee : rechargeAmount;
            RechargeValidationResponse response = allodigaWS.validateRechargeProduct((long) userForRecharge.getUsuarioID(), selectedProduct.getId(), rechargeSubmit, includeFees);
            if (response != null) {
                switch (response.getCodigoRespuesta()) {
                    case "00": {
                        hasError = false;
                        alodigaFee = response.getTotalFee().floatValue();
                        rechargeAmount = response.getAmountBeforeFee();
                        if (!includeFees) {
                            businessFee = ((long) ((totalCharge * loginBean.getBusinessPercentFee() / 100) * 100)) / 100;
                        }

                        totalCharge = response.getTotalAmount().floatValue() + businessFee;
                        phase = 2;
                    }
                    break;
                    default:
                        hasError = true;
                        errorMessage = msg.getString("error.general") + " : " + response.getCodigoRespuesta();
                }
            } else {
                hasError = true;
                errorMessage = msg.getString("error.registerwsconnection");
            }
        } catch (Exception ex) {
            Logger.getLogger(rechargeController.class.getName()).log(Level.SEVERE, null, ex);
            hasError = true;
            errorMessage = msg.getString("error.registerwsconnection");
        }
    }

    public void recharge() {

        try {
            TransactionResponse response = allodigaWS.rechargeWalletProduct(loginBean.getCurrentBusiness().getId(), (long) userForRecharge.getUsuarioID(), selectedProduct.getId(), (double) rechargeAmount, false);
            if (response != null) {
                switch (response.getCodigoRespuesta()) {
                    case "00": {
                        hasError = false;
                        transactionId = response.getResponse().getId();
                        phase = 3;

                        BusinessBalanceIncoming log = new BusinessBalanceIncoming();
                        log.setBusiness(loginBean.getCurrentBusiness());
                        log.setUser(loginBean.getUserSession());
                        log.setBusinessFee(businessFee);
                        log.setAmountWithoutFee(totalCharge - businessFee);
                        log.setTotalAmount(totalCharge);
                        log.setDateTransaction(new Date());
                        log.setTransactionId(transactionId);
                        log.setType(OperationType.RECHARGE);
                        BusinessData businessData = new BusinessData();
                        businessData.saveIncomingBalance(log);

                        //TODO save transaction log
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage("Recarga realizada con exito"));
                    }
                    break;
                    default:
                        hasError = true;
                        errorMessage = msg.getString("error.general") + " : " + response.getCodigoRespuesta();
                }
            } else {
                hasError = true;
                errorMessage = msg.getString("error.registerwsconnection");
            }
        } catch (Exception ex) {
            Logger.getLogger(rechargeController.class.getName()).log(Level.SEVERE, null, ex);
            hasError = true;
            errorMessage = msg.getString("error.registerwsconnection");
        }
    }

    public void reset() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().clear();
    }

    public static class Product {

        private long id;
        private String name;
        private String symbol;

        public Product() {
        }

        public Product(long id, String name, String symbol) {
            this.id = id;
            this.name = name;
            this.symbol = symbol;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
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
            final Product other = (Product) obj;
            if (this.id != other.id) {
                return false;
            }
            return true;
        }

    }

}
