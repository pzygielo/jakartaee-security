/*
 * Copyright (c) 2007, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package ee.jakarta.tck.security.signaturetest;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class SigTestEE {

    protected SignatureTestDriver driver;

    /**
     * <p>
     * Returns a {@link SignatureTestDriver} appropriate for the particular TCK (using API check or the Signature Test
     * Framework).
     * </p>
     *
     * <p>
     * The default implementation of this method will return a {@link SignatureTestDriver} that will use API Check. TCK
     * developers can override this to return the desired {@link SignatureTestDriver} for their TCK.
     */
    protected SignatureTestDriver getSigTestDriver() {
        if (driver == null) {
            driver = SignatureTestDriverFactory.getInstance(SignatureTestDriverFactory.SIG_TEST);
        }

        return driver;

    } // END getSigTestDriver

    /**
     * Returns the list of Optional Packages which are not accounted for. By 'unlisted optional' we mean the packages which
     * are Optional to the technology under test that the user did NOT specifically list for testing. For example, with Java
     * EE 7 implementation, a user could additionally opt to test a JSR-88 technology along with the Java EE technology. But
     * if the user chooses NOT to list this optional technology for testing (via ts.jte javaee.level prop) then this method
     * will return the packages for JSR-88 technology with this method call.
     * <p/>
     * This is useful for checking for a scenarios when a user may have forgotten to identify a whole or partial technology
     * implementation and in such cases, Java EE platform still requires testing it.
     * <p/>
     * Any partial or complete impl of an unlistedOptionalPackage sends up a red flag indicating that the user must also
     * pass tests for this optional technology area.
     * <p/>
     * Sub-classes are free to override this method if they use a different signature repository directory. Most users
     * should be able to use this default implementation - which means that there was NO optional technology packages that
     * need to be tested.
     *
     * @return ArrayList<String>
     */
    protected ArrayList<String> getUnlistedOptionalPackages() {
        return null;
    }

    /**
     * <p>
     * Returns an array of individual classes that must be tested by the signature test framwork within the specified
     * vehicle. TCK developers may override this method when this functionality is needed. Most will only need package level
     * granularity.
     * </p>
     *
     * <p>
     * If the developer doesn't wish to test certain classes within a particular vehicle, the implementation of this method
     * must return a zero-length array.
     * </p>
     *
     * @param vehicleName The name of the vehicle the signature tests should be conducted in. Valid values for this property
     * are ejb, servlet, ejb and appclient.
     *
     * @return an Array of Strings containing the individual classes the framework should test based on the specifed
     * vehicle. The default implementation of this method returns a zero-length array no matter the vehicle specified.
     */
    protected String[] getClasses() {

        return new String[] {};

    } // END getClasses

    protected SigTestData testInfo;

    /**
     * Called by the test framework to initialize this test. The method simply retrieves some state information that is
     * necessary to run the test when when the test framework invokes the run method (actually the test1 method).
     *
     * @param args List of arguments passed to this test.
     * @param p Properties specified by the test user and passed to this test via the test framework.
     *
     * @throws Fault When an error occurs reading or saving the state information processed by this method.
     */
    public void setup() {
        try {
            TestUtil.logMsg("$$$ SigTestEE.setup() called");
            this.testInfo = new SigTestData();
            TestUtil.logMsg("$$$ SigTestEE.setup() complete");
        } catch (Exception e) {
            TestUtil.logErr("Unexpected exception " + e.getMessage());
        }
    }

    /**
     * Called by the test framework to cleanup any outstanding state. This method simply passes the message through to the
     * utility class so the implementation can be used by both framework base classes.
     *
     * @throws Fault When an error occurs cleaning up the state of this test.
     */
    public void cleanup() throws Fault {
        TestUtil.logMsg("$$$ SigTestEE.cleanup() called");
        try {
            getSigTestDriver().cleanupImpl();
            TestUtil.logMsg("$$$ SigTestEE.cleanup() returning");
        } catch (Exception e) {
            throw new Fault("Cleanup failed!", e);
        }
    }

    public static class Fault extends Exception {
        private static final long serialVersionUID = -1574745208867827913L;

        public Throwable t;

        /**
         * creates a Fault with a message
         */
        public Fault(String msg) {
            super(msg);
            TestUtil.logErr(msg);
        }

        /**
         * creates a Fault with a message.
         *
         * @param msg the message
         * @param t prints this exception's stacktrace
         */
        public Fault(String msg, Throwable t) {
            super(msg);
            this.t = t;
            TestUtil.logErr(msg, t);
        }

        /**
         * creates a Fault with a Throwable.
         *
         * @param t the Throwable
         */
        public Fault(Throwable t) {
            super(t);
            this.t = t;
        }

        /**
         * Prints this Throwable and its backtrace to the standard error stream.
         *
         */
        @Override
        public void printStackTrace() {
            if (this.t != null) {
                this.t.printStackTrace();
            } else {
                super.printStackTrace();
            }
        }

        /**
         * Prints this throwable and its backtrace to the specified print stream.
         *
         * @param s <code>PrintStream</code> to use for output
         */
        @Override
        public void printStackTrace(PrintStream s) {
            if (this.t != null) {
                this.t.printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        /**
         * Prints this throwable and its backtrace to the specified print writer.
         *
         * @param s <code>PrintWriter</code> to use for output
         */
        @Override
        public void printStackTrace(PrintWriter s) {
            if (this.t != null) {
                this.t.printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        @Override
        public Throwable getCause() {
            return t;
        }

        @Override
        public synchronized Throwable initCause(Throwable cause) {
            if (t != null)
                throw new IllegalStateException("Can't overwrite cause");
            if (!Exception.class.isInstance(cause))
                throw new IllegalArgumentException("Cause not permitted");
            this.t = cause;
            return this;
        }
    }

} // end class SigTestEE
