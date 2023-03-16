/*
 * Copyright 2023 United States Bureau of Reclamation (USBR).
 * United States Department of the Interior
 * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval
 * from USBR
 */

package usbr.git.cli;

public final class CLIOutput {

    private int _exitCode;
    private String _stdOut;
    private String _stdErr;

    public CLIOutput(int exitCode, String stdOut, String stdErr) {
        _exitCode = exitCode;
        _stdOut = stdOut;
        _stdErr = stdErr;
    }

    public int getExitCode() {
        return _exitCode;
    }

    public String getStdOut() {
        return _stdOut;
    }

    public String getStdErr() {
        return _stdErr;
    }
}
