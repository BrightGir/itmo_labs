package ru.bright.operations;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SnapshotScriptsTest {
    @TempDir
    Path tempDirectory;

    @ParameterizedTest
    @ValueSource(strings = {"snapshot-save.sh", "snapshot-restore.sh"})
    void snapshotScriptHasWorkingHelp(String scriptName) throws Exception {
        Path script = Path.of("scripts", scriptName);
        Process process = new ProcessBuilder("bash", script.toString(), "--help")
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        assertThat(process.waitFor()).isZero();
        assertThat(output).contains("Usage:");
    }

    @Test
    void restoreUsesEtcdClusterSettingsFromCompose() throws Exception {
        Path snapshot = Files.createFile(tempDirectory.resolve("library.db"));
        Path commandLog = tempDirectory.resolve("engine.log");
        Path fakeEngine = tempDirectory.resolve("container-engine");
        Files.writeString(fakeEngine, """
                #!/usr/bin/env bash
                printf '%s\n' "$*" >> "$ENGINE_LOG"
                """);
        assertThat(fakeEngine.toFile().setExecutable(true)).isTrue();

        ProcessBuilder processBuilder = new ProcessBuilder(
                "bash", "scripts/snapshot-restore.sh", snapshot.toString());
        processBuilder.environment().put("CONTAINER_ENGINE", fakeEngine.toString());
        processBuilder.environment().put("ENGINE_LOG", commandLog.toString());
        Process process = processBuilder.redirectErrorStream(true).start();

        assertThat(process.waitFor()).isZero();
        assertThat(Files.readString(commandLog))
                .contains("volume rm -f library-etcd-data")
                .contains("--name=node1")
                .contains("--initial-cluster=node1=http://etcd:2380")
                .contains("--initial-advertise-peer-urls=http://etcd:2380");
    }
}
