package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Product;
import com.necs.maximus.db.facade.ProductFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;

@Named(value = "productController")
@ViewScoped
public class ProductController extends AbstractController<Product> {
    
    @Inject
    private MobilePageController mobilePageController;
    
    @Inject
    private ProductFacade ejbFacade;
    
    @Inject
    private IsSubstituteController substitute;
    
    private List<Product> productSubstituteList;
    
    private Product productSubstitute;
    
    private boolean createPart;
    private boolean createPartSustitute;
    
    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    private final Locale locale = facesContext.getViewRoot().getLocale();
    protected ResourceBundle bundle = ResourceBundle.getBundle("/MaximusBundle", locale);
    private boolean createSubstitute;
    
    public ProductController() {
        // Inform the Abstract parent controller of the concrete Product Entity
        super(Product.class);
        productSubstituteList = new ArrayList<>();
    }

    /**
     * Sets the "items" attribute with a collection of IsSubstitute entities
     * that are retrieved from Product?cap_first and returns the navigation
     * outcome.
     *
     * @return navigation outcome for IsSubstitute page
     */
    public String navigateIsSubstituteList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("IsSubstitute_items", this.getSelected().getIsSubstituteList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/isSubstitute/index";
    }
    
    public void prepareCreateProduct() {
        substitute.prepareCreate(null);
        this.prepareCreate(null);
    }

    /**
     * Sets the "items" attribute with a collection of Has entities that are
     * retrieved from Product?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Has page
     */
    public String navigateHasList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Has_items", this.getSelected().getHasList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/has/index";
    }

    /**
     * Sets the "items" attribute with a collection of ProductCost entities that
     * are retrieved from Product?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for ProductCost page
     */
    public String navigateProductCostList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("ProductCost_items", this.getSelected().getProductCostList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/productCost/index";
    }
    
    public void createProduct(String partNumber) {
        try {
            this.saveNew(null);
            
            if (productSubstituteList != null && !productSubstituteList.isEmpty()) {
                for (Product pro : productSubstituteList) {
                    //creo el producto
                    ejbFacade.create(pro);
                    //creo la asociacion en substitute entity
                    substitute.getSelected().setPartNumberBase(getSelected());
                    substitute.getSelected().setPartNumberSubstitute(pro);
                    substitute.saveNew(null);
                }
            }
            //inicializo los objetos
            setProductSubstitute(null);
            setProductSubstituteList(null);
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "", bundle.getString("error_save")));
            e.printStackTrace();
        }
        
    }

    /**
     * valida los campos del producto substituto...
     *
     * @param product
     */
//    public void validateFieldSubstitute(Product product) {
//        if (product != null) {
//            if (product.getPartNumber() == null) {
//                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "debe introducir un numero de parte para el substituto"));
//            } else if (product.getManufacture() == null) {
//                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "debe introducir un numero de parte para el substituto"));
//            } else if (product.getDescription() == null) {
//                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "debe introducir un numero de parte para el substituto"));
//            } else if (product.getType() == null) {
//                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "debe introducir un numero de parte para el substituto"));
//            }
//            
//        }
//    }

    /**
     * Metodo encargado de validar si el partnumber introducido ya se encuentra
     * en BD
     *
     * @param query
     * @return
     */
    public void validProductExist(String part, boolean substitute) {
//        String part = getSelected().getPartNumber();
        Product partNumber;
        HashMap param = new HashMap();
        param.put("partNumber", part);
        partNumber = ejbFacade.listUniqueNamedQuery(Product.class, "Product.findByPartNumber", param);
        if (partNumber != null) {
            if (substitute) {
                RequestContext.getCurrentInstance().execute("document.getElementById('ProductCreateFormSubs:messageExistingSub').style.display='block';");
            } else {
                RequestContext.getCurrentInstance().execute("document.getElementById('ProductCreateForm:messageExisting').style.display='block';");
            }
        }
    }

    /**
     * Encargado de inicializar el producto sustituto con la informacion del
     * padre, y posteriormente renderiza el dialogo con la informacion
     * precargada
     */
    public void rederFormSubstitute() {
        productSubstitute = new Product();
        if (getSelected() != null) {
            productSubstitute.setDescription(getSelected().getDescription());
            productSubstitute.setNotes(getSelected().getNotes());
            productSubstitute.setPrice(getSelected().getPrice());
            productSubstitute.setRetailPrice(getSelected().getRetailPrice());
            productSubstitute.setType(getSelected().getType());
            productSubstitute.setManufacture(getSelected().getManufacture());
            productSubstitute.setWholesalePrice(getSelected().getWholesalePrice());
            
            RequestContext.getCurrentInstance().execute("PF('ProductCreateDialogSubs').show();");
        }
        RequestContext.getCurrentInstance().update("displaySubs");
        
    }

    /**
     * Permite añadir un product substituto a la lista que posteriormente sera
     * persistida
     */
    public void addSubstitute() {
        
        if (productSubstitute != null) {
            productSubstituteList.add(productSubstitute);
        }
        RequestContext.getCurrentInstance().update("display");
        RequestContext.getCurrentInstance().update("panelSubstitutes");
    }

    /**
     * Elimina un producto substituto de la lista
     *
     * @param pro
     */
    public void deletePartSubstitute(Product pro) {
        if (productSubstituteList.contains(pro)) {
            productSubstituteList.remove(pro);
        }
    }
    
    public void createPartTrue() {
        createPart = true;
    }
    
    public void createPartSustituteTrue() {
        createPartSustitute = true;
    }
    
    public MobilePageController getMobilePageController() {
        return mobilePageController;
    }
    
    public void setMobilePageController(MobilePageController mobilePageController) {
        this.mobilePageController = mobilePageController;
    }
    
    public boolean isCreatePart() {
        return createPart;
    }
    
    public void setCreatePart(boolean createPart) {
        this.createPart = createPart;
    }
    
    public boolean isCreatePartSustitute() {
        return createPartSustitute;
    }
    
    public void setCreatePartSustitute(boolean createPartSustitute) {
        this.createPartSustitute = createPartSustitute;
    }
    
    public boolean isCreateSubstitute() {
        return createSubstitute;
    }
    
    public void setCreateSubstitute(boolean createSubstitute) {
        this.createSubstitute = createSubstitute;
    }
    
    public Product getProductSubstitute() {
        return productSubstitute;
    }
    
    public void setProductSubstitute(Product productSubstitute) {
        this.productSubstitute = productSubstitute;
    }
    
    public List<Product> getProductSubstituteList() {
        return productSubstituteList;
    }
    
    public void setProductSubstituteList(List<Product> productSubstituteList) {
        this.productSubstituteList = productSubstituteList;
    }
    
}
