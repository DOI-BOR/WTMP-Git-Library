/*
 * Copyright 2023 United States Bureau of Reclamation (USBR).
 * United States Department of the Interior
 * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval
 * from USBR
 */

package usbr.git;

import org.jdom.Element;
import usbr.git.cli.GitProperty;

import java.util.ArrayList;
import java.util.List;

public class WTMPGitConfig {

    private static final String ROOT_ELEMENT_NAME = "WTMPGitConfig";
    private static final String SCHANNEL_ELEMENT_NAME = "UseSChannel";

    private boolean _useSChannel;
    private List<GitlabConfiguration> _gitlabConfigs = new ArrayList<>();

    public WTMPGitConfig() {
        super();
    }

    public boolean usesSChannel() {
        return _useSChannel;
    }

    public void setUsesSChannel(boolean useSChannel) {
        _useSChannel = useSChannel;
    }

    public List<GitlabConfiguration> getGitlabConfigs() {
        return _gitlabConfigs;
    }

    public void setGitlabConfigs(List<GitlabConfiguration> gitlabConfigs) {
        if (gitlabConfigs == null) {
            _gitlabConfigs.clear();
        } else {
            _gitlabConfigs = gitlabConfigs;
        }
    }

    public GitProperty getSSLBackendProperty() {
        String key = "http.sslBackend";
        String val = usesSChannel() ? "schannel" : "openssl";

        return new GitProperty(key, val);
    }

    public GitProperty getDoNotSetSChannelProperty() {
        String key = "wtmp.ignoreSChannel";
        String val = "true";

        return new GitProperty(key, val);
    }

    public static WTMPGitConfig fromXML(Element rootElement) {
        if (!ROOT_ELEMENT_NAME.equals(rootElement.getName())) {
            return null;
        }

        WTMPGitConfig config = new WTMPGitConfig();
        Element schannelElement = rootElement.getChild(SCHANNEL_ELEMENT_NAME);
        if (schannelElement != null) {
            config.setUsesSChannel(Boolean.parseBoolean(schannelElement.getText()));
        }

        List<GitlabConfiguration> gitlabConfigurations = new ArrayList<>();
        for (Element element : (List<Element>) rootElement.getChildren()) {
            if (GitlabConfiguration.ROOT_ELEMENT_NAME.equals(element.getName())) {
                GitlabConfiguration configuration = GitlabConfiguration.fromXML(element);
                if (configuration != null) {
                    gitlabConfigurations.add(configuration);
                }
            }
        }
        config.setGitlabConfigs(gitlabConfigurations);

        return config;
    }
}
