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

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.util.VersionNumber;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Java Version Specification.
 * Implemented according to <a href="https://openjdk.java.net/jeps/223">JEP 223</a>
 * @author Oleg Nenashev
 * @since 1.6
 */
public class JavaSpecificationVersion extends VersionNumber {

    private static final String JAVA_SPEC_VERSION_PROPERTY_NAME = "java.specification.version";

    public static final JavaSpecificationVersion JAVA_5 = new JavaSpecificationVersion("1.5");
    public static final JavaSpecificationVersion JAVA_6 = new JavaSpecificationVersion("1.6");
    public static final JavaSpecificationVersion JAVA_7 = new JavaSpecificationVersion("1.7");
    public static final JavaSpecificationVersion JAVA_8 = new JavaSpecificationVersion("1.8");
    public static final JavaSpecificationVersion JAVA_9 = new JavaSpecificationVersion("9");
    public static final JavaSpecificationVersion JAVA_10 = new JavaSpecificationVersion("10");
    public static final JavaSpecificationVersion JAVA_11 = new JavaSpecificationVersion("11");
    public static final JavaSpecificationVersion JAVA_12 = new JavaSpecificationVersion("12");
    public static final JavaSpecificationVersion JAVA_13 = new JavaSpecificationVersion("13");

    private static final NavigableMap<Integer, Integer> RELEASE_TO_CLASS;
    private static final NavigableMap<Integer, Integer> CLASS_TO_RELEASE;

    static {
        NavigableMap<Integer, Integer> releaseToClass = new TreeMap<>();
        releaseToClass.put(1, 45);
        releaseToClass.put(2, 46);
        releaseToClass.put(3, 47);
        releaseToClass.put(4, 48);
        releaseToClass.put(5, 49);
        releaseToClass.put(6, 50);
        releaseToClass.put(7, 51);
        releaseToClass.put(8, 52);
        releaseToClass.put(9, 53);
        releaseToClass.put(10, 54);
        releaseToClass.put(11, 55);
        releaseToClass.put(12, 56);
        releaseToClass.put(13, 57);
        releaseToClass.put(14, 58);
        releaseToClass.put(15, 59);
        releaseToClass.put(16, 60);
        releaseToClass.put(17, 61);
        releaseToClass.put(18, 62);
        releaseToClass.put(19, 63);
        releaseToClass.put(20, 64);
        RELEASE_TO_CLASS = Collections.unmodifiableNavigableMap(releaseToClass);

        NavigableMap<Integer, Integer> classToRelease = new TreeMap<>();
        for (Map.Entry<Integer, Integer> entry : releaseToClass.entrySet()) {
            classToRelease.put(entry.getValue(), entry.getKey());
        }
        CLASS_TO_RELEASE = Collections.unmodifiableNavigableMap(classToRelease);
    }

    /**
     * Constructor which automatically normalizes version strings.
     * @param version Java specification version, should follow JEP-223 or the previous format.
     * @throws NumberFormatException Illegal Java specification version number
     */
    public JavaSpecificationVersion(@NonNull String version)
            throws NumberFormatException {
        super(normalizeVersion(version));
    }

    /**
     * Given a release version, get the corresponding {@link JavaSpecificationVersion}.
     *
     * @param releaseVersion The release version; e.g., 8, 11, or 17.
     * @return The {@link JavaSpecificationVersion}; e.g., 1.8, 11, or 17.
     */
    public static JavaSpecificationVersion fromReleaseVersion(int releaseVersion) {
        if (releaseVersion > 8) {
            return new JavaSpecificationVersion(Integer.toString(releaseVersion));
        } else {
            return new JavaSpecificationVersion("1." + releaseVersion);
        }
    }

    /**
     * Get the corresponding release version.
     *
     * @return The release version; e.g., 8, 11, or 17.
     */
    public int toReleaseVersion() {
        int first = getDigitAt(0);
        return first == 1 ? getDigitAt(1) : first;
    }

    /**
     * Given a class file version, get the corresponding {@link JavaSpecificationVersion}.
     *
     * @param classVersion The class version; e.g., 52, 55, or 61.
     * @return The {@link JavaSpecificationVersion}; e.g., 1.8, 11, or 17.
     * @throws IllegalArgumentException If the Java specification version for the given class version is unknown.
     */
    public static JavaSpecificationVersion fromClassVersion(int classVersion) {
        Integer releaseVersion = CLASS_TO_RELEASE.get(classVersion);
        if (releaseVersion == null) {
            throw new IllegalArgumentException("Unknown Java specification version for class version: " + classVersion);
        }
        return fromReleaseVersion(releaseVersion);
    }

    /**
     * Get the corresponding class file version.
     *
     * @return The class file version; e.g., 52, 55, or 61.
     * @throws IllegalArgumentException If the class version for the given Java Specification Version is unknown.
     */
    public int toClassVersion() {
        int releaseVersion = toReleaseVersion();
        Integer classVersion = RELEASE_TO_CLASS.get(releaseVersion);
        if (classVersion == null) {
            throw new IllegalArgumentException("Unknown class version for release version: " + releaseVersion);
        }
        return classVersion;
    }

    @NonNull
    private static String normalizeVersion(@NonNull String input)
            throws NumberFormatException {
        input = input.trim();
        if (input.startsWith("1.")) {
            String[] split = input.split("\\.");
            if (split.length != 2) {
                throw new NumberFormatException("Malformed old Java Specification Version. " +
                        "There should be exactly one dot and something after it: " + input);
            }
            input = split[1];
        }

        int majorVersion = Integer.parseInt(input);
        if (majorVersion > 8) {
            return input;
        } else {
            return "1." + input;
        }
    }

    /**
     * Get the Java Specification version for the current JVM
     * @return Java Specification version
     * @throws NumberFormatException Version parsing error
     * @throws IllegalStateException JVM does not specify the mandatory {@link #JAVA_SPEC_VERSION_PROPERTY_NAME} property.
     */
    @NonNull
    public static JavaSpecificationVersion forCurrentJVM() throws NumberFormatException {
        final String value = System.getProperty(JAVA_SPEC_VERSION_PROPERTY_NAME);
        if (value == null) {
            throw new IllegalStateException("Missing mandatory JVM system property: " + JAVA_SPEC_VERSION_PROPERTY_NAME);
        }
        return new JavaSpecificationVersion(value);
    }
}
