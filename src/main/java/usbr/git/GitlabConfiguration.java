/*
 * Copyright 2023 United States Bureau of Reclamation (USBR).
 * United States Department of the Interior
 * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval
 * from USBR
 */

package usbr.git;

import com.google.common.flogger.FluentLogger;
import org.jdom.Element;
import usbr.git.cli.GitProperty;

import java.net.MalformedURLException;
import java.net.URL;

public class GitlabConfiguration {
    static final String ROOT_ELEMENT_NAME = "GitlabConfiguration";
    private static final String URL_ELEMENT_NAME = "URL";
    private static final String APPLICATION_KEY_ELEMENT_NAME = "ApplicationKey";
    private static final String APPLICATION_SECRET_ELEMENT_NAME = "ApplicationSecret";

    private URL _url;

    private String _applicationKey;

    private String _applicationSecret;

    public GitlabConfiguration() {
        super();
    }

    public URL getUrl() {
        return _url;
    }

    public void setUrl(URL _url) {
        this._url = _url;
    }

    public String getApplicationKey() {
        return _applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this._applicationKey = applicationKey;
    }

    public String getApplicationSecret() {
        return _applicationSecret;
    }

    public void setApplicationSecret(String applicationSecret) {
        this._applicationSecret = applicationSecret;
    }

    public GitProperty getClientIdProperty() {
        String propertyName = "credential."+getUrl().toString()+".gitLabDevClientId";
        String propertyValue = getApplicationKey();

        return new GitProperty(propertyName, propertyValue);
    }

    public GitProperty getClientSecretProperty() {
        String propertyName = "credential."+getUrl().toString()+".gitLabDevClientSecret";
        String propertyValue = getApplicationSecret();

        return new GitProperty(propertyName, propertyValue);
    }

    public GitProperty getAuthModesProperty() {
        String propertyName = "credential."+getUrl().toString()+".gitLabAuthModes";
        String propertyValue = "browser";

        return new GitProperty(propertyName, propertyValue);
    }

    public GitProperty getProviderProperty() {
        String propertyName = "credential."+getUrl().toString()+".provider";
        String propertyValue = "gitlab";

        return new GitProperty(propertyName, propertyValue);
    }

    public GitProperty getIgnoreProperty() {
        String propertyName = "wtmp."+getUrl().toString() + ".ignore";
        String propertyValue = "true";

        return new GitProperty(propertyName, propertyValue);
    }

    public static GitlabConfiguration fromXML(Element element) throws XMLParseException {
        if(!ROOT_ELEMENT_NAME.equals(element.getName())) {
            throw new XMLParseException("Invalid root element! Provided name: " + element.getName() + " expected: " + ROOT_ELEMENT_NAME);
        }
        GitlabConfiguration configuration = new GitlabConfiguration();
        Element urlElement = element.getChild(URL_ELEMENT_NAME);
        if(urlElement != null) {
            try {
                configuration.setUrl(new URL(urlElement.getText()));
            } catch (MalformedURLException e) {
                throw new XMLParseException(e);
            }
        }

        Element applicationKeyElement = element.getChild(APPLICATION_KEY_ELEMENT_NAME);
        if(applicationKeyElement != null) {
            configuration.setApplicationKey(applicationKeyElement.getText());
        }

        Element applicationSecretElement = element.getChild(APPLICATION_SECRET_ELEMENT_NAME);
        if(applicationSecretElement != null) {
            configuration.setApplicationSecret(applicationSecretElement.getText());
        }

        return configuration;
    }
}
