/*
 * Copyright 2023 United States Bureau of Reclamation (USBR).
 * United States Department of the Interior
 * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval
 * from USBR
 */

package usbr.git.cli;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GitConfig {

    public static Map<String, String> listGitConfig() throws IOException, InterruptedException {
        CLIOutput output = GitCLI.git(Paths.get(""), "config", "--global", "-l");
        if (output.getExitCode() == 0) {
            HashMap<String, String> result = new HashMap<>();
            String[] lines = output.getStdOut().replace("\r", "").split("\n");
            for(String line : lines) {
                String[] keyVal = line.split("=");
                String key = keyVal[0];
                String val = (keyVal.length > 1) ? keyVal[1] : null;
                result.put(key, val);
            }
            return result;
        } else {
            return null;
        }
    }

    public static CLIOutput setGlobalConfigProperty(GitProperty property) throws IOException, InterruptedException {
        return GitCLI.git(Paths.get(""), "config", "--global", property.getKey(), property.getValue());
    }
}
