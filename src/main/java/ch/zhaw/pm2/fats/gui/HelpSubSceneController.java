package ch.zhaw.pm2.fats.gui;

import javafx.application.HostServices;
import javafx.fxml.FXML;

/**
 * The controller class of the HelpSubScene.
 */
public class HelpSubSceneController {
    private HostServices services;

    /**
     * Set the host service.
     *
     * @param services The hostService object.
     */
    public void setServices(HostServices services) {
        this.services = services;
    }

    /**
     * Opens the ZHAW WikiPage of the group MERS.
     */
    @FXML
    public void openWiki() {
        services.showDocument("https://github.zhaw.ch/pm2-it19azh-ehri-fame-muon/gruppe7-Mers-projekt2-fats/wiki");
    }
}
