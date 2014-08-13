package io.github.benwhitehead.util.props;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static io.github.benwhitehead.util.props.Utils.*;

/**
 * @author Ben Whitehead
 */
public final class Main {

    public static final List<String> emptyStringList = Collections.emptyList();

    public static void main(@NotNull final String[] args) throws IOException {
        final Map<String, List<String>> params = parseArgs(args);
        final Properties properties = new Properties();
        final InputStream in = getInputStream(params);
        properties.load(in);

        final StringBuilder sb = new StringBuilder();
        for (String arg : getOrElse(params, "props", emptyStringList)) {
            sb.append(arg).append("\n");
        }

        properties.load(new StringReader(sb.toString()));

        for (String key : Utils.newTreeSet(properties.stringPropertyNames())) {
            System.out.print(key);
            System.out.print(" => ");
            System.out.println(properties.getProperty(key));
        }
    }

    public static InputStream getInputStream(@NotNull final Map<String, List<String>> params) throws FileNotFoundException {
        if (getOrElse(params, "in", newArrayList("no")).equals(newArrayList("system.in"))) {
            return System.in;
        } else {
            final List<String> files = getOrElse(params, "files", emptyStringList);
            if (!files.isEmpty()) {
                return new FileInputStream(head(files));
            } else {
                throw new IllegalArgumentException("No input detected");
            }
        }
    }

}
