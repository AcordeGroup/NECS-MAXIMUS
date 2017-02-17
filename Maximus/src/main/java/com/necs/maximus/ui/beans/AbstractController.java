package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.entity.Product;
import com.necs.maximus.db.facade.AbstractFacade;
import com.necs.maximus.db.facade.HasFacade;
import com.necs.maximus.db.facade.LazyEntityDataModel;
import com.necs.maximus.db.facade.ProductFacade;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.ui.beans.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJBException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Represents an abstract shell of to be used as JSF Controller to be used in
 * AJAX-enabled applications. No outcomes will be generated from its methods
 * since handling is designed to be done inside one page.
 *
 * @param <T> the concrete Entity type of the Controller bean to be created
 */
public abstract class AbstractController<T> implements Serializable {

    /**
     * Session scoped managed bean that stores information about the logged
     * user.
     */
    @Inject
    private UserManagedBean userManagedBean;

    private static final long serialVersionUID = 1L;

    // se crea variable que identifica que controller esta actualmente utilizaondo la modal
    public static final String SESSION_CONTROLLER_WRAPPER = "session_controller_wrapper";
    public static final String NAME_CLASS = "NAME_CLASS";
    public static final String NAME_CONTROLLER = "NAME_CONTROLLER";

    @Inject
    private AbstractFacade<T> ejbFacade;
    private Class<T> itemClass;
    private T selected;
    private Collection<T> items;
    private LazyEntityDataModel<T> lazyItems;

    @EJB
    private HasFacade hasFacade;
    @EJB
    private ProductFacade productFacade;

    private enum PersistAction {

        CREATE,
        DELETE,
        UPDATE
    }

    public AbstractController() {
    }

    public AbstractController(Class<T> itemClass) {
        this.itemClass = itemClass;
    }

    /**
     * Retrieve the currently selected item.
     *
     * @return the currently selected Entity
     */
    public T getSelected() {
        return selected;
    }

    /**
     * Pass in the currently selected item.
     *
     * @param selected the Entity that should be set as selected
     */
    public void setSelected(T selected) {
        this.selected = selected;
    }

    /**
     * Sets any embeddable key fields if an Entity uses composite keys. If the
     * entity does not have composite keys, this method performs no actions and
     * exists purely to be overridden inside a concrete controller class.
     */
    protected void setEmbeddableKeys() {
        // Nothing to do if entity does not have any embeddable key.
    }

    ;

    /**
     * Sets the concrete embedded key of an Entity that uses composite keys.
     * This method will be overriden inside concrete controller classes and does
     * nothing if the specific entity has no composite keys.
     */
    protected void initializeEmbeddableKey() {
        // Nothing to do if entity does not have any embeddable key.
    }

    /**
     * Returns all items as a Collection object.
     *
     * @return a collection of Entity items returned by the data layer
     */
    public Collection<T> getItems() {
        if (items == null) {
            items = this.ejbFacade.findAll();
        }
        return items;
    }

    /**
     * Pass in collection of items
     *
     * @param items a collection of Entity items
     */
    public void setItems(Collection<T> items) {
        this.items = items;
    }

    /**
     *
     * @return Entity-specific Lazy Data Model
     */
    public LazyEntityDataModel<T> getLazyItems() {
        if (lazyItems == null) {
            lazyItems = new LazyEntityDataModel<>(this.ejbFacade);
        }
        return lazyItems;
    }

    public void setLazyItems(LazyEntityDataModel<T> lazyItems) {
        this.lazyItems = lazyItems;
    }

    public void setLazyItems(Collection<T> items) {
        if (items instanceof List) {
            lazyItems = new LazyEntityDataModel<>((List<T>) items);
        } else {
            lazyItems = new LazyEntityDataModel<>(new ArrayList<>(items));
        }
    }

    /**
     * Apply changes to an existing item to the data layer.
     *
     * @param event an event from the widget that wants to save an Entity to the
     * data layer
     */
    public void save(ActionEvent event) {
        String msg = ResourceBundle.getBundle("/MaximusBundle").getString(itemClass.getSimpleName() + "Updated");
        persist(PersistAction.UPDATE, msg);
    }

    /**
     * Store a new item in the data layer.
     *
     * @param event an event from the widget that wants to save a new Entity to
     * the data layer
     */
    public void saveNew(ActionEvent event) {
        String msg = ResourceBundle.getBundle("/MaximusBundle").getString(itemClass.getSimpleName() + "Created");
        persist(PersistAction.CREATE, msg);
        if (!isValidationFailed()) {
            items = null; // Invalidate list of items to trigger re-query.
            lazyItems = null; // Invalidate list of lazy items to trigger re-query.
        }
    }

    /**
     * Remove an existing item from the data layer.
     *
     * @param event an event from the widget that wants to delete an Entity from
     * the data layer
     */
    public void delete(ActionEvent event) {
        String msg = ResourceBundle.getBundle("/MaximusBundle").getString(itemClass.getSimpleName() + "Deleted");
        persist(PersistAction.DELETE, msg);
        if (!isValidationFailed()) {
            selected = null; // Remove selection
            items = null; // Invalidate list of items to trigger re-query.
            lazyItems = null; // Invalidate list of lazy items to trigger re-query.
        }
    }

    /**
     * Performs any data modification actions for an entity. The actions that
     * can be performed by this method are controlled by the
     * {@link PersistAction} enumeration and are either CREATE, EDIT or DELETE.
     *
     * @param persistAction a specific action that should be performed on the
     * current item
     * @param successMessage a message that should be displayed when persisting
     * the item succeeds
     */
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            this.setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    this.ejbFacade.edit(selected);
                } else {
                    this.ejbFacade.remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                Throwable cause = JsfUtil.getRootCause(ex.getCause());
                if (cause != null) {
                    if (cause instanceof ConstraintViolationException) {
                        ConstraintViolationException excp = (ConstraintViolationException) cause;
                        for (ConstraintViolation s : excp.getConstraintViolations()) {
                            JsfUtil.addErrorMessage(s.getMessage());
                        }
                    } else {
                        String msg = cause.getLocalizedMessage();
                        if (msg.length() > 0) {
                            JsfUtil.addErrorMessage(msg);
                        } else {
                            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/MaximusBundle").getString("PersistenceErrorOccured"));
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/MaximusBundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    /**
     * Creates a new instance of an underlying entity and assigns it to Selected
     * property.
     *
     * @param event an event from the widget that wants to create a new,
     * unmanaged Entity for the data layer
     * @return a new, unmanaged Entity
     */
    public T prepareCreate(ActionEvent event) {
        T newItem;
        try {
            newItem = itemClass.newInstance();
            this.selected = newItem;
            initializeEmbeddableKey();
            return newItem;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Inform the user interface whether any validation error exist on a page.
     *
     * @return a logical value whether form validation has passed or failed
     */
    public boolean isValidationFailed() {
        return JsfUtil.isValidationFailed();
    }

    /**
     * Retrieve all messages as a String to be displayed on the page.
     *
     * @param clientComponent the component for which the message applies
     * @param defaultMessage a default message to be shown
     * @return a concatenation of all messages
     */
    public String getComponentMessages(String clientComponent, String defaultMessage) {
        return JsfUtil.getComponentMessages(clientComponent, defaultMessage);
    }

    /**
     * Retrieve a collection of Entity items for a specific Controller from
     * another JSF page via Request parameters.
     */
    @PostConstruct
    public void initParams() {
        Object paramItems = FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(itemClass.getSimpleName() + "_items");
        if (paramItems != null) {
            setItems((Collection<T>) paramItems);
            setLazyItems((Collection<T>) paramItems);
        }
    }

    /**
     * Metodo comun que permite navegar al home deacuerdo al usuario logueado
     *
     * @return
     */
    public String navigateToDashboard() {
        String index = null;
        switch (AgentType.valueOf(getUserManagedBean().getType())) {

            case Administrator:
                index = "indexAdmin";
                break;

            case Sales:
                index = "indexSales";
                break;
            case Purchasing:
                index = "indexPurchasing";
                break;
        }
        return index;
    }

    /**
     * Muestra el menu segun el tipo de usuario loqueado
     *
     * @return
     */
    public String appMenu() {
        String menu = null;
        switch (AgentType.valueOf(getUserManagedBean().getType())) {

            case Administrator:
                menu = "/WEB-INF/include/admin/appmenu.xhtml";
                break;

            case Sales:
                menu = "/sales/appmenu.xhtml";
                break;
            case Purchasing:
                menu = "/purchasing/appmenu.xhtml";
                break;
        }
        return menu;
    }

    /**
     * retorna el tipo de agente logueado
     *
     * @return
     */
    public String typeAgent() {
        return getUserManagedBean().getType();
    }

    /**
     * Utilitario que valida el formato correcto de un email
     *
     * @param email
     * @return
     */
    public boolean emailValidator(String email) {
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$");

        Matcher m = pattern.matcher(email);
        return m.matches();
    }

    /**
     * Metodo encargado de crear un objeto de session que almacena informacion
     * del controlador actual de la estación.
     *
     * @param nameController
     * @param nameClase
     */
    public void createWrapperSesionController(String nameController, Object nameClase) {

        HashMap<String, Object> var = new HashMap<>();
        FacesContext contex = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) contex.getExternalContext().getSession(true);

        if (session.getAttribute(SESSION_CONTROLLER_WRAPPER) != null) {
            session.removeAttribute(SESSION_CONTROLLER_WRAPPER);
        }

        var.put(NAME_CLASS, nameClase);
        var.put(NAME_CONTROLLER, nameController);

        session.setAttribute(SESSION_CONTROLLER_WRAPPER, var);

    }

    /**
     * Metodo encargado de recuperar el valor almacenado en el objeto de seccion
     * dado
     *
     * @param value
     * @return HashMap<String, Object>
     */
    public HashMap<String, Object> getValueSessionVar(String value) {

        HashMap<String, Object> var = null;
        FacesContext contex = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) contex.getExternalContext().getSession(false);

        if (session != null && session.getAttribute(value) != null) {
            var = (HashMap<String, Object>) session.getAttribute(value);
        }
        return var;

    }

    /**
     * Metodo encargado de setear la informacion del producto el atributos del
     * controlaro DialogProductInfoController, el mismo nutre de informacion al
     * dialogo
     *
     * @param product
     */
    public void viewInfoProduct(Product product) {
        List<Has> quoteListByProduct;

        ELContext context = FacesContext.getCurrentInstance().getELContext();
        DialogProductInfoController controller = (DialogProductInfoController) context.getELResolver()
                .getValue(context, null, "dialogProductInfoController");
        if (null != product) {
            controller.setPartNumber(product.getPartNumber());
            quoteListByProduct = hasFacade.findHasByIdProduct(product.getPartNumber());

            controller.setQuoteListByProduct(quoteListByProduct);
            //            if (quoteListByProduct != null && !quoteListByProduct.isEmpty()) {
            //                controller.setPartListSubstitutes(quoteListByProduct.get(0).getProduct().getIsSubstituteList());
            //            }

            controller.setPartListSubstitutes(product.getIsSubstituteList());
        }
    }

    /**
     * Metodo encargado de setear la informacion del producto asociado al agente
     * de vetas a los atributos del controlador DialogProductInfoController, el
     * mismo nutre de informacion al dialogo
     *
     * @param product
     */
    public void viewInfoProductByIdAgent(Product product, String idAgent) {
        List<Has> quoteListByProduct;

        ELContext context = FacesContext.getCurrentInstance().getELContext();
        DialogProductInfoController controller = (DialogProductInfoController) context.getELResolver()
                .getValue(context, null, "dialogProductInfoController");
        if (null != product) {
            controller.setPartNumber(product.getPartNumber());
            quoteListByProduct = hasFacade.findHasByIdProductAndIdAgent(product.getPartNumber(), idAgent);

            controller.setQuoteListByProduct(quoteListByProduct);
//            if (quoteListByProduct != null && !quoteListByProduct.isEmpty()) {
//                controller.setPartListSubstitutes(quoteListByProduct.get(0).getProduct().getIsSubstituteList());
//            }
            controller.setPartListSubstitutes(product.getIsSubstituteList());
        }
    }

    public UserManagedBean getUserManagedBean() {
        return userManagedBean;
    }

    public void setUserManagedBean(UserManagedBean userManagedBean) {
        this.userManagedBean = userManagedBean;
    }

}
