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

import junit.framework.TestCase;
import org.jvnet.hudson.test.Issue;

/**
 * @author Xavier Le Vourch
 */
public class VersionNumberTest extends TestCase {

    public void testIsNewerThan() {
       assertTrue(new VersionNumber("2.0.*").isNewerThan(new VersionNumber("2.0")));
       assertTrue(new VersionNumber("2.1-SNAPSHOT").isNewerThan(new VersionNumber("2.0.*")));
       assertTrue(new VersionNumber("2.0.*").isNewerThan(new VersionNumber("2.0.1")));
       assertTrue(new VersionNumber("2.0.1").isNewerThan(new VersionNumber("2.0.1-SNAPSHOT")));
       assertTrue(new VersionNumber("2.0.1-SNAPSHOT").isNewerThan(new VersionNumber("2.0.0.99")));
       assertTrue(new VersionNumber("2.0.0.99").isNewerThan(new VersionNumber("2.0.0")));
       assertTrue(new VersionNumber("2.0.0").isNewerThan(new VersionNumber("2.0.ea")));
       assertTrue(new VersionNumber("2.0").isNewerThan(new VersionNumber("2.0.ea")));
       // the inversion of the previous test case from the old behaviour is explained by
       // which makes more sense than before
       assertTrue(new VersionNumber("2.0.0").equals(new VersionNumber("2.0")));
    }
    
    public void testEarlyAccess() {
       assertTrue(new VersionNumber("2.0.ea2").isNewerThan(new VersionNumber("2.0.ea1")));
       assertTrue(new VersionNumber("2.0.ea1").isNewerThan(new VersionNumber("2.0.ea")));
       assertEquals(new VersionNumber("2.0.ea"), new VersionNumber("2.0.ea0"));
    }

    @Issue("JENKINS-40899")
    public void testSnapshots() {
        assertTrue(new VersionNumber("1.12").isNewerThan(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)")));
        assertTrue(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)").isNewerThan(new VersionNumber("1.11")));
        assertTrue(new VersionNumber("1.12-SNAPSHOT (private-08/24/2008 12:13-hudson)").isNewerThan(new VersionNumber("1.12-SNAPSHOT")));
        // This is changed from the old impl because snapshots are no longer a "magic" number
        assertFalse(new VersionNumber("1.12-SNAPSHOT").equals(new VersionNumber("1.11.*")));
        assertTrue(new VersionNumber("1.11.*").isNewerThan(new VersionNumber("1.11.9")));
    }
}
