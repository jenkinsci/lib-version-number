/*
 * The MIT License
 * 
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Xavier Le Vourch
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
package hudson.util;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.hamcrest.CoreMatchers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.jvnet.hudson.test.Issue;

public class VersionNumberTest {

    @Rule
    public ErrorCollector errors = new ErrorCollector();

    @Test
    public void isNewerThan() {
       assertTrue(new VersionNumber("2.0.*").isNewerThan(new VersionNumber("2.0")));
       assertTrue(new VersionNumber("2.1-SNAPSHOT").isNewerThan(new VersionNumber("2.0.*")));
       assertTrue(new VersionNumber("2.1").isNewerThan(new VersionNumber("2.1-SNAPSHOT")));
       assertTrue(new VersionNumber("2.0.*").isNewerThan(new VersionNumber("2.0.1")));
       assertTrue(new VersionNumber("2.0.1").isNewerThan(new VersionNumber("2.0.1-SNAPSHOT")));
       assertTrue(new VersionNumber("2.0.1-SNAPSHOT").isNewerThan(new VersionNumber("2.0.0.99")));
       assertTrue(new VersionNumber("2.0.0.99").isNewerThan(new VersionNumber("2.0.0")));
       assertTrue(new VersionNumber("2.0.0").isNewerThan(new VersionNumber("2.0.ea")));
       assertTrue(new VersionNumber("2.0").isNewerThan(new VersionNumber("2.0.ea")));
       // the inversion of the previous test case from the old behaviour is explained by
       // which makes more sense than before
       assertEquals(new VersionNumber("2.0.0"), new VersionNumber("2.0"));
    }

    @Issue("https://gitter.im/jenkinsci/configuration-as-code-plugin?at=5b4f2fc455a7e23c014da2af")
    @Test
    public void alpha() {
       assertTrue(new VersionNumber("2.0").isNewerThan(new VersionNumber("2.0-alpha-1")));
       assertTrue(new VersionNumber("2.0-alpha-1").isNewerThan(new VersionNumber("2.0-alpha-1-rc9999.abc123def456")));
    }
    
    @Test
    public void earlyAccess() {
       assertTrue(new VersionNumber("2.0.ea2").isNewerThan(new VersionNumber("2.0.ea1")));
       assertTrue(new VersionNumber("2.0.ea1").isNewerThan(new VersionNumber("2.0.ea")));
       assertEquals(new VersionNumber("2.0.ea"), new VersionNumber("2.0.ea0"));
    }
    
    @Test
    public void snapshots() {
        assertTrue(new VersionNumber("1.12").isNewerThan(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)")));
        assertTrue(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)").isNewerThan(new VersionNumber("1.11")));
        assertEquals(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)"), new VersionNumber("1.12-SNAPSHOT"));
        // This is changed from the old impl because snapshots are no longer a "magic" number
        assertNotEquals(new VersionNumber("1.12-SNAPSHOT"), new VersionNumber("1.11.*"));
        assertTrue(new VersionNumber("1.11.*").isNewerThan(new VersionNumber("1.11.9")));
        /* TODO the reverse:
        assertTrue(new VersionNumber("1.12-SNAPSHOT").isNewerThan(new VersionNumber("1.12-rc9999.abc123def456")));
        */
    }

    @Test
    public void timestamps() {
        assertTrue(new VersionNumber("2.0.3-20170207.105042-1").isNewerThan(new VersionNumber("2.0.2")));
        assertTrue(new VersionNumber("2.0.3").isNewerThan(new VersionNumber("2.0.3-20170207.105042-1")));
        assertEquals(new VersionNumber("2.0.3-20170207.105042-1"), new VersionNumber("2.0.3-SNAPSHOT"));
        assertEquals(new VersionNumber("2.0.3-20170207.105042-1"), new VersionNumber("2.0.3-SNAPSHOT (private-08/24/2008 12:13-hudson)"));
        assertTrue(new VersionNumber("2.0.3-20170207.105043-2").isNewerThan(new VersionNumber("2.0.3-20170207.105042-1")));
        assertTrue(new VersionNumber("2.0.3-20170207.105042-2").isNewerThan(new VersionNumber("2.0.3-20170207.105042-1")));
        assertTrue(new VersionNumber("2.0.3-20170207.105042-13").isNewerThan(new VersionNumber("2.0.3-20170207.105042-2")));
        assertFalse(new VersionNumber("2.0.3-20170207.105042-1").isNewerThan(new VersionNumber("2.0.3-SNAPSHOT")));
        assertFalse(new VersionNumber("2.0.3-20170207.105042-1").isOlderThan(new VersionNumber("2.0.3-SNAPSHOT")));
    }

    @Test
    public void digit() {
        assertEquals(32, new VersionNumber("2.32.3.1-SNAPSHOT").getDigitAt(1));
        assertEquals(3, new VersionNumber("2.32.3.1-SNAPSHOT").getDigitAt(2));
        assertEquals(1, new VersionNumber("2.32.3.1-SNAPSHOT").getDigitAt(3));
        assertEquals(-1, new VersionNumber("2.32.3.1-SNAPSHOT").getDigitAt(4));
        assertEquals(2, new VersionNumber("2.7.22.0.2").getDigitAt(4));
        assertEquals(3, new VersionNumber("2.7.22.0.3-SNAPSHOT").getDigitAt(4));
        assertEquals(-1, new VersionNumber("2.0.3-20170207.105042-1").getDigitAt(4));
        assertEquals(-1, new VersionNumber("2.0.3").getDigitAt(5));
        assertEquals(2, new VersionNumber("2.0.3").getDigitAt(0));
        assertEquals(-1, new VersionNumber("2.0.3").getDigitAt(-1));
        assertEquals(-1, new VersionNumber("1.0.0.GA.2-3").getDigitAt(3));
        assertEquals(-1, new VersionNumber("").getDigitAt(-1));
        assertEquals(-1, new VersionNumber("").getDigitAt(0));
    }

    @Test
    public void isSnapshot() {
        assertTrue(new VersionNumber("2.32.3.1-SNAPSHOT").isSnapshot());
        assertTrue(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)").isSnapshot());
        assertTrue(new VersionNumber("1.12-SNAPSHOT").isSnapshot());

        assertFalse(new VersionNumber("2.32.3.1").isSnapshot());
        assertFalse(new VersionNumber("1.11.*").isSnapshot());
        assertFalse(new VersionNumber("1.11.*").isSnapshot());
        assertFalse(new VersionNumber("200.vabcd1234abcd").isSnapshot());
    }

    @Test
    public void isRelease() {
        assertTrue(new VersionNumber("2.32.3.1").isRelease());
        assertTrue(new VersionNumber("1.11.*").isRelease());
        assertTrue(new VersionNumber("1.11.*").isRelease());
        assertTrue(new VersionNumber("200.vabcd1234abcd").isRelease());

        assertFalse(new VersionNumber("2.32.3.1-SNAPSHOT").isRelease());
        assertFalse(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)").isRelease());
        assertFalse(new VersionNumber("1.12-SNAPSHOT").isRelease());
    }

    private void assertOrderAlsoInMaven(String... versions) {
        errors.checkThat("Maven order is correct", Stream.of(versions).map(ComparableVersion::new).sorted().map(ComparableVersion::toString).collect(Collectors.toList()), CoreMatchers.is(Arrays.asList(versions)));
        errors.checkThat("Jenkins order is correct", Stream.of(versions).map(VersionNumber::new).sorted().map(VersionNumber::toString).collect(Collectors.toList()), CoreMatchers.is(Arrays.asList(versions)));
    }

    @Ignore("TODO still pretty divergent: …was <[2.0.ea, 2.0.0, 2.0, 2.0.1-alpha-1, 2.0.1-SNAPSHOT, 2.0.*, 2.0.0.99, 2.0.1-alpha-1-rc9999.abc123def456, 2.0.1-rc9999.abc123def456, 2.0.1]>")
    @Issue("JENKINS-51594")
    @Test
    public void mavenComparison() {
        assertOrderAlsoInMaven("2.0.0", "2.0", "2.0.*", "2.0.ea", "2.0.0.99", "2.0.1-alpha-1-rc9999.abc123def456", "2.0.1-alpha-1", "2.0.1-rc9999.abc123def456", "2.0.1-SNAPSHOT", "2.0.1");
    }

    @Issue("https://github.com/jenkinsci/commons-lang3-api-plugin/pull/21")
    @Test
    public void libraryWrapper() {
        assertOrderAlsoInMaven("3.12.0.0", "3.12.0-44.v1234deadbeef", "3.12.0-55.v1234deadbeef", "3.12.1-66.v1234deadbeef");
    }

    @Issue("JEP-229")
    @Test
    public void backportJep229() {
        // Maven considers 99.1.abcd1234abcd to sort before 99.1234deadbeef so we cannot simply use 99. as the branch prefix.
        // Nor can we use 99.1234deadbeef. as the prefix because Maven would compare 5 and 10 lexicographically.
        // 100._. seems to work but is not intuitive.
        // Using changelist.format=%d.v%s behaves better, apparently because then the hash is never treated like a number.
        assertOrderAlsoInMaven("99.v1234deadbeef", "99.5.vabcd1234abcd", "99.10.vabcd1234abcd", "100.vdead9876beef");
    }

    @Issue("https://github.com/jenkinsci/incrementals-tools/issues/29")
    @Test
    public void majorVersion() {
        assertOrderAlsoInMaven("1.1", "391.ve4a_38c1b_cf4b_");
        // Weird but TBD if this matters in practice:
        assertEquals(0, new DefaultArtifactVersion("391.ve4a_38c1b_cf4b_").getMajorVersion());
        // More natural behavior of majorVersion:
        assertOrderAlsoInMaven("1.1", "200.vabcd1234abcd", "391-ve4a_38c1b_cf4b_");
        assertEquals(391, new DefaultArtifactVersion("391-ve4a_38c1b_cf4b_").getMajorVersion());
    }

    @Test
    public void testOrEqualTo() {
        assertTrue(new VersionNumber("1.8").isNewerThanOrEqualTo(new VersionNumber("1.8")));
        assertTrue(new VersionNumber("1.9").isNewerThanOrEqualTo(new VersionNumber("1.8")));
        assertTrue(new VersionNumber("2").isNewerThanOrEqualTo(new VersionNumber("1.8")));

        assertTrue(new VersionNumber("1.8").isOlderThanOrEqualTo(new VersionNumber("1.8")));
        assertTrue(new VersionNumber("1.7").isOlderThanOrEqualTo(new VersionNumber("1.8")));
        assertTrue(new VersionNumber("1").isOlderThanOrEqualTo(new VersionNumber("1.8")));
    }

}
