package io.github.benwhitehead.util.props;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.github.benwhitehead.util.props.Utils.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Ben Whitehead
 */
public class UtilsTest {

    @Test
    public void testAppendToMultiMap() throws Exception {
        final Map<String, List<String>> mmap = Maps.newHashMap();
        assertThat(mmap).doesNotContainKey("testKey");

        appendToMultiMap(mmap, "testKey", "value1");
        assertThat(mmap).containsKey("testKey");
        assertThat(mmap.get("testKey")).isEqualTo(Lists.newArrayList("value1"));

        appendToMultiMap(mmap, "testKey", "value2");
        assertThat(mmap.get("testKey")).isEqualTo(Lists.newArrayList("value1", "value2"));
    }

    @Test
    public void testGetOrElse() throws Exception {
        final Map<String, String> map = Maps.newHashMap();
        map.put("key", "value1");
        assertThat(getOrElse(map, "key", "value2")).isEqualTo("value1");
        assertThat(getOrElse(map, "key2", "value2")).isEqualTo("value2");
    }

    @Test
    public void testNewArrayList() throws Exception {
        assertThat(newArrayList("Hello")).isEqualTo(Lists.newArrayList("Hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHeadEmptyList() throws Exception {
        head(Collections.emptyList());
    }

    @Test
    public void testHead() throws Exception {
        assertThat(head(Lists.newArrayList("head", "mid", "tail"))).isEqualTo("head");
    }
}
