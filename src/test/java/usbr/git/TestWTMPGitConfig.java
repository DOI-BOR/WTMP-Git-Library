/*
 * Copyright 2023 United States Bureau of Reclamation (USBR).
 * United States Department of the Interior
 * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval
 * from USBR
 */

package usbr.git;

import org.jdom.JDOMException;
import org.junit.jupiter.api.Test;
import usbr.git.cli.GitProperty;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestWTMPGitConfig {

    @Test
    public void testReadXMLConfig() throws IOException, JDOMException {
        URL xml = getClass().getResource("testConfig.xml");

        GitlabConfigurator.prepareFromConfigurationFile(xml);
    }

    @Test
    public void testSChannelReadsSuccessfully() throws IOException, JDOMException {
        URL xml = getClass().getResource("testConfig.xml");

        GitlabConfigurator configurator = GitlabConfigurator.prepareFromConfigurationFile(xml);
        assertTrue(configurator.getConfiguration().usesSChannel());
    }

    @Test
    public void testGitlabConfigReadsSuccessfully() throws IOException, JDOMException {
        URL xml = getClass().getResource("testConfig.xml");

        GitlabConfigurator configurator = GitlabConfigurator.prepareFromConfigurationFile(xml);
        List<GitlabConfiguration> gitlabConfigs = configurator.getConfiguration().getGitlabConfigs();
        assertEquals(1, gitlabConfigs.size());

        GitlabConfiguration configuration = gitlabConfigs.get(0);
        assertEquals("TestApplicationKey", configuration.getApplicationKey());
        assertEquals("TestApplicationSecret", configuration.getApplicationSecret());
        assertEquals(new URL("https://www.example.com"), configuration.getUrl());
    }

    @Test
    public void testGitlabConfigHasCorrectApplicationProperty() throws IOException, JDOMException {
        URL xml = getClass().getResource("testConfig.xml");

        GitlabConfigurator configurator = GitlabConfigurator.prepareFromConfigurationFile(xml);
        List<GitlabConfiguration> gitlabConfigs = configurator.getConfiguration().getGitlabConfigs();
        assertEquals(1, gitlabConfigs.size());

        GitlabConfiguration configuration = gitlabConfigs.get(0);
        GitProperty clientIdProperty = configuration.getClientIdProperty();
        assertEquals("credential.https://www.example.com.gitLabDevClientId", clientIdProperty.getKey());
        assertEquals("TestApplicationKey", clientIdProperty.getValue());
    }

}
