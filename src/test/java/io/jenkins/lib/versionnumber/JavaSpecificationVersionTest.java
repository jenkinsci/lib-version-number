/*
 * The MIT License
 *
 * Copyright (c) 2019 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jenkins.lib.versionnumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.jvnet.hudson.test.For;

/**
 * Tests for {@link JavaSpecificationVersion}.
 */
@For(JavaSpecificationVersion.class)
public class JavaSpecificationVersionTest {

    @Test
    public void shouldParseValidNumbersCorrectly() {
        assertSpecEquals(JavaSpecificationVersion.JAVA_6, "1.6");
        assertSpecEquals(JavaSpecificationVersion.JAVA_7, "1.7");
        assertSpecEquals(JavaSpecificationVersion.JAVA_8, "1.8");
        assertSpecEquals(JavaSpecificationVersion.JAVA_9, "9");
        assertSpecEquals(JavaSpecificationVersion.JAVA_10, "10");
        assertSpecEquals(JavaSpecificationVersion.JAVA_11, "11");
    }

    @Test
    public void shouldParseOldSpecCorrectly() {
        assertSpecEquals(JavaSpecificationVersion.JAVA_9, "1.9");
        assertSpecEquals(JavaSpecificationVersion.JAVA_10, "1.10");
        assertSpecEquals(JavaSpecificationVersion.JAVA_11, "1.11");
        assertSpecEquals(JavaSpecificationVersion.JAVA_12, "1.12");
    }

    @Test
    public void shouldResolveIncorrectSpecs() {
        assertSpecEquals(JavaSpecificationVersion.JAVA_8, "8");
        assertSpecEquals(JavaSpecificationVersion.JAVA_7, "7");
        assertSpecEquals(JavaSpecificationVersion.JAVA_5, "5");
    }

    @Test
    public void shouldCompareVersionsProperly() {
        assertTrue(JavaSpecificationVersion.JAVA_5.isOlderThan(JavaSpecificationVersion.JAVA_6));
        assertTrue(JavaSpecificationVersion.JAVA_6.isOlderThan(JavaSpecificationVersion.JAVA_7));
        assertTrue(JavaSpecificationVersion.JAVA_7.isOlderThan(JavaSpecificationVersion.JAVA_8));
        assertTrue(JavaSpecificationVersion.JAVA_8.isOlderThan(JavaSpecificationVersion.JAVA_9));
        assertTrue(JavaSpecificationVersion.JAVA_8.isNewerThan(JavaSpecificationVersion.JAVA_7));
        assertTrue(JavaSpecificationVersion.JAVA_9.isOlderThan(JavaSpecificationVersion.JAVA_10));
        assertTrue(JavaSpecificationVersion.JAVA_10.isOlderThan(JavaSpecificationVersion.JAVA_11));
        assertTrue(JavaSpecificationVersion.JAVA_10.isNewerThan(JavaSpecificationVersion.JAVA_8));
    }

    @Test
    public void shouldRetrieveSpecVersionForTheCurrentJVM() {
        assertNotNull(JavaSpecificationVersion.forCurrentJVM());
    }

    @Test
    public void invalidVersions() {
        assertThrows(NumberFormatException.class, () -> new JavaSpecificationVersion("1.1.1"));
        assertThrows(NumberFormatException.class, () -> new JavaSpecificationVersion("fubar"));
    }

    @Test
    public void releaseVersion() {
        assertEquals(new JavaSpecificationVersion("1.8"), JavaSpecificationVersion.fromReleaseVersion(8));
        assertEquals(new JavaSpecificationVersion("11"), JavaSpecificationVersion.fromReleaseVersion(11));
        assertEquals(new JavaSpecificationVersion("17"), JavaSpecificationVersion.fromReleaseVersion(17));

        assertEquals(8, new JavaSpecificationVersion("1.8").toReleaseVersion());
        assertEquals(11, new JavaSpecificationVersion("11").toReleaseVersion());
        assertEquals(17, new JavaSpecificationVersion("17").toReleaseVersion());
    }

    @Test
    public void classVersion() {
        assertEquals(new JavaSpecificationVersion("1.8"), JavaSpecificationVersion.fromClassVersion(52));
        assertEquals(new JavaSpecificationVersion("11"), JavaSpecificationVersion.fromClassVersion(55));
        assertEquals(new JavaSpecificationVersion("17"), JavaSpecificationVersion.fromClassVersion(61));

        assertEquals(52, new JavaSpecificationVersion("1.8").toClassVersion());
        assertEquals(55, new JavaSpecificationVersion("11").toClassVersion());
        assertEquals(61, new JavaSpecificationVersion("17").toClassVersion());
    }

    public void assertSpecEquals(JavaSpecificationVersion version, String value) {
        JavaSpecificationVersion actualSpec = new JavaSpecificationVersion(value);
        assertEquals("Wrong Java version", version, actualSpec);
    }

    public void assertOlder(JavaSpecificationVersion version1, JavaSpecificationVersion version2) {
        assertTrue(String.format("Version %s should be older than %s", version1, version2),
                version1.isOlderThan(version2));
    }

    public void assertNewer(JavaSpecificationVersion version1, JavaSpecificationVersion version2) {
        assertTrue(String.format("Version %s should be newer than %s", version1, version2),
                version1.isNewerThan(version2));
    }
}
