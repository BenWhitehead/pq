package io.github.benwhitehead.util.props;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

import static io.github.benwhitehead.util.props.Utils.*;

/**
 * @author Ben Whitehead
 */
public final class Main {

    private static final List<String> emptyStringList = Collections.emptyList();

    public static void main(@NotNull final String[] args) {
        try (
                final ParsedArgs params = parseArgs(args);
                final Tuple2<String, Reader> t = getInputStream(params.get());
                final Props props = new Props(
                        t._1,
                        getOrElse(params.get(), "props", emptyStringList),
                        t._2,
                        new OutputStreamWriter(System.out)
                )
        ) {
            props.run();
        } catch (IOException ioe) {
            System.err.print(String.format("%s: ", Props.NAME));
            System.err.println(ioe.getMessage());
            System.exit(1);
        } catch (ExitException ee) {
            System.exit(ee.status);
        } catch (IllegalArgumentException iae) {
            System.err.print(String.format("%s: ", Props.NAME));
            System.err.println(iae.getMessage());
            usage();
            System.exit(2);
        }
    }

    static Tuple2<String, Reader> getInputStream(@NotNull final Map<String, List<String>> params) throws FileNotFoundException {
        if (getOrElse(params, "in", newArrayList("no")).equals(newArrayList("system.in"))) {
            return new Tuple2<String, Reader>("stdin", new InputStreamReader(System.in));
        } else {
            final List<String> files = getOrElse(params, "files", emptyStringList);
            if (!files.isEmpty()) {
                final String head = head(files);
                return new Tuple2<String, Reader>(head, new FileReader(head));
            } else {
                throw new IllegalArgumentException("No input detected");
            }
        }
    }

    static void usage() {
        System.out.println("Usage: " + Props.NAME + " [--file <in>|-] [\"key=value\" | [ \"key2=value2\"]]");
        System.out.println();
        System.out.println("       -h | --help        Print this help");
        System.out.println();
        System.out.println("       -f | --file        Specify the properties files to read");
        System.out.println();
        System.out.println("       -                  Read properties from stdin");
        System.out.println();
        System.out.println("       --version          Prints version and jvm info");
        System.out.println();
    }

    static void usageAndExit() {
        usage();
        throw new ExitException(2);
    }

    static void version() {
        System.out.println(Props.NAME + " " + Props.VERSION);
        System.out.println("Copyright (C) 2014 Ben Whitehead");
        System.out.println("License: Apache License v2.0");

        System.out.println();

        final Properties sp = System.getProperties();
        System.out.printf("java version \"%s\"%n", sp.getProperty("java.version"));
        System.out.printf("%s%n", sp.getProperty("java.runtime.name"));
        System.out.printf("%s (build %s, %s)%n",
                sp.getProperty("java.vm.name"),
                sp.getProperty("java.vm.version"),
                sp.getProperty("java.vm.info")
        );
    }

    static void versionAndExit() {
        version();
        throw new ExitException(0);
    }

    static ParsedArgs parseArgs(@NotNull final String[] args) {
        if (args.length == 0) {
            usageAndExit();
        }
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            switch (arg) {
                case "-h":
                case "--help":
                    usageAndExit();
                case "-":
                    appendToMultiMap(map, "in", "system.in");
                    break;
                case "-f":
                case "--file":
                    final String filename = args[i+1];
                    i++;
                    appendToMultiMap(map, "files", filename);
                    break;
                case "--version":
                    versionAndExit();
                default:
                    appendToMultiMap(map, "props", arg);
                    break;
            }
        }
        return new ParsedArgs(map);
    }

    static final class Tuple2<T1, T2> implements Closeable {
        @NotNull
        private final T1 _1;
        @NotNull
        private final T2 _2;
        private Tuple2(@NotNull T1 _1, @NotNull T2 _2) {
            this._1 = _1;
            this._2 = _2;
        }

        @Override
        public void close() throws IOException { /*no-op*/ }
    }

    static class ExitException extends RuntimeException {
        private final int status;
        ExitException(final int status) {
            this.status = status;
        }
    }

    static final class ParsedArgs implements Closeable {
        @NotNull
        private final MyCloseable<Map<String, List<String>>> map;
        ParsedArgs(final Map<String, List<String>> map) {
            this.map = new MyCloseable<>(map);
        }

        public Map<String, List<String>> get() {
            return map.val;
        }

        @Override
        public void close() throws IOException { map.close(); }
    }

    static final class MyCloseable<T> implements Closeable {
        @NotNull
        private final T val;

        private MyCloseable(@NotNull final T val) {
            this.val = val;
        }

        @Override
        public void close() throws IOException { /*no-op*/ }
    }
}
