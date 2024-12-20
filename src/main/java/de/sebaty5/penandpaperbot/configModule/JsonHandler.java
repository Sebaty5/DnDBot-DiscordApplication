package de.sebaty5.penandpaperbot.configModule;

import de.sebaty5.penandpaperbot.logging.StandardLogger;
import de.sebaty5.penandpaperbot.utility.ExitCode;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JsonHandler<T> {
    private static final StandardLogger LOGGER = new StandardLogger("JsonHandler");

    protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    protected final Path configFilePath;
    protected final Class<T> dataClass;
    protected final Supplier<T> dataFactory;
    protected final AtomicBoolean needsSaving;
    protected final ReentrantReadWriteLock lock;
    protected final ReentrantReadWriteLock.ReadLock readLock;
    protected final ReentrantReadWriteLock.WriteLock writeLock;
    protected final long saveDelay;

    private boolean init = false;
    private T data = null;

    protected JsonHandler(Path filePath, long saveDelay, Class<T> dataClass, Supplier<T> dataFactory) throws IOException {
        this.configFilePath = filePath.toFile().getCanonicalFile().toPath();
        this.dataFactory = dataFactory;
        this.saveDelay = saveDelay;
        this.dataClass = dataClass;
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.needsSaving = new AtomicBoolean(false);
    }

    private void saveCheck() {
        while (true) {
            if (this.needsSaving.get()) forceSave();
            try {
                Thread.sleep(this.saveDelay);
            } catch (InterruptedException ex) {
                // NO-OP
            }
        }
    }

    public void init() throws IOException {
        try {
            this.writeLock.lock();

            if (this.init) throw new IllegalStateException("Init was already called");

            LOGGER.log("Reading %s...", this.configFilePath.getFileName().toString());

            this.init = true;

            File folder = new File(this.configFilePath.getParent().toString());
            if(!folder.exists()) {
                File file = new File(this.configFilePath.toString());
                if (!(file.getParentFile() != null && file.getParentFile().mkdirs())) {
                    LOGGER.error("Failed to create config file directory.");
                    System.exit(ExitCode.CONFIG_ERROR.getCode());
                }
                Files.createDirectories(this.configFilePath.getParent());
            } else {
                LOGGER.log("Config folder was already created.");
            }



            this.data = readFile(this.configFilePath, this.dataClass).orElseGet(() -> {
                this.needsSaving.set(true);
                return this.dataFactory.get();
            });

            Thread saveChecker = new Thread(this::saveCheck);
            saveChecker.start();
        } finally {
            this.writeLock.unlock();
        }
    }

    public void scheduleSave() {
        this.needsSaving.set(true);
    }

    public void forceSave() {
        try {
            this.needsSaving.set(false);
            this.saveFile();
        } catch (IOException ex) {
            LOGGER.error("Forced save encountered fatal error. Terminating...");
            System.exit(ExitCode.CONFIG_ERROR.getCode());
        }
    }

    @NotNull
    protected T getData() {
        if (this.data == null) {
            LOGGER.error("Data is not available. Terminating...");
            System.exit(ExitCode.CONFIG_ERROR.getCode());
        }
        return this.data;
    }

    private void saveFile() throws IOException {
        try {
            this.writeLock.lock();
            LOGGER.log("Saving %s...", this.configFilePath.getFileName().toString());
            writeFile(this.configFilePath, this.getData());
        } finally {
            this.writeLock.unlock();
        }
    }

    private static <T> Optional<T> readFile(Path file, Class<T> dataClass) {
        if (!Files.isRegularFile(file))
            return Optional.empty();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return Optional.of(GSON.fromJson(reader, dataClass));
        } catch (Exception ex) {
            LOGGER.error("Could not read file: %s", file);
            LOGGER.error("This likely indicates the file is corrupted. Full stacktrace:", ex);
            System.exit(ExitCode.CONFIG_ERROR.getCode());
            return Optional.empty();
        }
    }

    private static <T> void writeFile(Path file, T config) {
        try {
            Files.createDirectories(file.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (Exception ex) {
            LOGGER.log("Could not write config file: %s", file);
            System.exit(ExitCode.CONFIG_ERROR.getCode());
        }
    }
}
