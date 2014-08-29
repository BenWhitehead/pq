package io.github.benwhitehead.util.props;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * @author Ben Whitehead
 */
public final class Props implements Closeable {
    static String VERSION;
    static {
        try {
            final Properties sys = new Properties();
            sys.load(new InputStreamReader(Props.class.getResourceAsStream("/build.properties")));
            VERSION = sys.getProperty("version");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @NotNull
    private final String inputSource;
    @NotNull
    private final List<String> props;
    @NotNull
    private final Reader input;
    @NotNull
    private final Writer output;

    public Props(@NotNull final String inputSource,
                 @NotNull final List<String> props,
                 @NotNull final Reader input,
                 @NotNull final Writer output
    ) {
        this.inputSource = inputSource;
        this.props = props;
        this.input = input;
        this.output = output;
    }

    public void run() throws IOException {
        final Properties properties = new Properties();
        properties.load(input);

        final StringBuilder sb = new StringBuilder();
        for (String arg : props) {
            sb.append(arg).append("\n");
        }

        properties.load(new StringReader(sb.toString()));

        properties.store(
                output,
                String.format("Generated by props v%s from %s", VERSION, inputSource)
        );

    }

    @Override
    public void close() throws IOException {
        input.close();
        output.flush();
        output.close();
    }
}
