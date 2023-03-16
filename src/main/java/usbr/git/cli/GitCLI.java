/*
 * Copyright 2023 United States Bureau of Reclamation (USBR).
 * United States Department of the Interior
 * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 * Source may not be released without written approval
 * from USBR
 */
package usbr.git.cli;

import com.google.common.flogger.FluentLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Used to execute raw Git CLI Commands
 */
public final class GitCLI {

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    private GitCLI() {
        super();
    }

    /**
     * Checks if a directory is within a Git repository, as according to Git.
     * @param pathToCheck The directory to check for repository-status
     * @return True if the directory is a repository
     */
    public static boolean isGitRepo(Path pathToCheck) {
        try {
            CLIOutput result = git(Paths.get(""), "-C", pathToCheck.toAbsolutePath().toString(), "rev-parse");
            if (0 == result.getExitCode()) {
                return true;
            }
            LOGGER.atConfig().log("Git Output: %s", result.getStdOut());
            LOGGER.atConfig().log("Git Err Output: %s", result.getStdErr());
        } catch (InterruptedException e) {
            LOGGER.atWarning().withCause(e).log();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.atWarning().withCause(e).log();
        }
        return false;
    }

    /**
     * Check if Git is executable
     * @return True if `git` is in the path and can be executed
     */
    public static boolean isGitExectuable() {
        try {
            return git(Paths.get(""), "--help").getExitCode() == 0;
        } catch (InterruptedException | IOException e) {
            LOGGER.atWarning().withCause(e).log();
            return false;
        }
    }

    static void dumpOutputToStdOutError(CLIOutput output) {
        System.out.println(output.getStdOut());
        System.err.println(output.getStdErr());
    }

    /**
     * Execute Git in the working directory with the specified arguments
     * @param workingDir The working directory to run Git in
     * @param arguments The arguments to pass to Git
     * @return The CLI Output from Git executing
     * @throws InterruptedException
     * @throws IOException
     */
    public static CLIOutput git(Path workingDir, String... arguments) throws InterruptedException, IOException {
        return git(workingDir, new ArrayList<>(Arrays.asList(arguments)), Collections.emptyMap());
    }

    /**
     * Execute Git in the working directory with the specified arguments
     * @param workingDir The working directory to run Git in
     * @param arguments The arguments to pass to Git
     * @param environmentVariables Any environment variables to pass to the execution of Git
     * @return The CLI Output from Git executing
     * @throws InterruptedException
     * @throws IOException
     */
    public static CLIOutput git(Path workingDir, List<String> arguments, Map<String, String> environmentVariables) throws InterruptedException, IOException {
        return execCommand(workingDir, "git", arguments, environmentVariables);
    }

    private static CLIOutput execCommand(Path workingDir, String executableName, List<String> arguments, Map<String, String> environmentVariables) throws InterruptedException, IOException {
        List<String> gitArgs = new ArrayList<>();
        gitArgs.add(executableName);
        gitArgs.addAll(arguments);
        LOGGER.atInfo().log("%s", gitArgs);

        ProcessBuilder processBuilder = new ProcessBuilder(gitArgs);
        processBuilder.environment().putAll(environmentVariables);
        processBuilder.directory(workingDir.toAbsolutePath().toFile());
        Process newProcess = processBuilder.start();

        StreamConsumer stdOutCapture = new StreamConsumer(newProcess.getInputStream());
        Thread stdout = new Thread(stdOutCapture);
        stdout.setName("GIT STDOUT Monitor");
        stdout.start();
        StreamConsumer stdErrCapture = new StreamConsumer(newProcess.getErrorStream());
        Thread error = new Thread(stdErrCapture);
        error.setName("GIT STDERR Monitor");
        error.start();

        int exitCode = newProcess.waitFor();
        return new CLIOutput(exitCode, stdOutCapture.getAccumulatedOutput(), stdErrCapture.getAccumulatedOutput());
    }

    private static class StreamConsumer implements Runnable {
        private final InputStream _in;
        private final StringBuilder _collectedOutput;

        private StreamConsumer(InputStream in) {
            _in = in;
            _collectedOutput = new StringBuilder();
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(_in));
                String line = null;
                while ((line = input.readLine()) != null) {
                    _collectedOutput.append(line);
                    _collectedOutput.append(System.lineSeparator());
                }
            } catch (IOException e) {
                LOGGER.atWarning().withCause(e).log();
            }
        }

        public String getAccumulatedOutput() {
            return _collectedOutput.toString();
        }
    }
}