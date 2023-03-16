/*
 * Copyright 2023 United States Bureau of Reclamation (USBR).
 * United States Department of the Interior
 * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval
 * from USBR
 */

package usbr.git.cli;

public class GitProperty {

    private String _key;
    private String _value;

    public GitProperty() {
        super();
    }

    public GitProperty(String key, String value) {
        _key = key;
        _value = value;
    }

    public String getKey() {
        return _key;
    }

    public String getValue() {
        return _value;
    }

    public void setKey(String key) {
        _key = key;
    }

    public void setValue(String value) {
        _value = value;
    }

}
