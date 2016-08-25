package com.necs.maximus.ui.beans;

import com.necs.maximus.ui.beans.util.MobilePageController;
import com.necs.maximus.db.entity.Agent;
import com.necs.maximus.enums.AgentType;
import com.necs.maximus.security.services.PasswordService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

@Named(value = "agentController")
@ViewScoped
public class AgentController extends AbstractController<Agent> {

    @Inject
    private MobilePageController mobilePageController;

    private boolean createAgent = false;

    private List<String> types;

    public AgentController() {
        // Inform the Abstract parent controller of the concrete Agent Entity
        super(Agent.class);
    }

    @PostConstruct
    public void init() {
        types = AgentType.getListValues();
    }

    /**
     * Sets the "items" attribute with a collection of Quote entities that are
     * retrieved from Agent?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Quote page
     */
    public String navigateQuoteList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Quote_items", this.getSelected().getQuoteList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/quote/index";
    }

    /**
     * Sets the "items" attribute with a collection of Manage entities that are
     * retrieved from Agent?cap_first and returns the navigation outcome.
     *
     * @return navigation outcome for Manage page
     */
    public String navigateManageList() {
        if (this.getSelected() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Manage_items", this.getSelected().getManageList());
        }
        return this.mobilePageController.getMobilePagesPrefix() + "/admin/manage/index";
    }

    @Override
    public void save(ActionEvent event) {
        Agent agent = getSelected();
        System.out.println("agent.getPasswordVal(): " + agent.getPasswordVal());
        WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        PasswordService passwordService = ctx.getBean("passwordEncoder", PasswordService.class);
        agent.setPasswordVal(passwordService.encode(agent.getPasswordVal()));
        System.out.println("encode agent.getPasswordVal(): " + agent.getPasswordVal());
        super.save(event);
    }

    @Override
    public void saveNew(ActionEvent event) {
        System.out.println("saveNew override");
        Agent agent = getSelected();
        System.out.println("agent.getPasswordVal(): " + agent.getPasswordVal());
        WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        PasswordService passwordService = ctx.getBean("passwordEncoder", PasswordService.class);
        agent.setPasswordVal(passwordService.encode(agent.getPasswordVal()));
        System.out.println("encode agent.getPasswordVal(): " + agent.getPasswordVal());
        super.saveNew(event);
    }

    public void createAgentTrue() {
        createAgent = true;
    }

    public boolean isCreateAgent() {
        return createAgent;
    }

    public void setCreateAgent(boolean createAgent) {
        this.createAgent = createAgent;
    }

    public MobilePageController getMobilePageController() {
        return mobilePageController;
    }

    public void setMobilePageController(MobilePageController mobilePageController) {
        this.mobilePageController = mobilePageController;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> validateAgent(String typeAgent) {
        if (typeAgent.equals(AgentType.Purchasing.getType())) {
            types.remove(AgentType.Administrator.getType());
        }
        return types;
    }
}
