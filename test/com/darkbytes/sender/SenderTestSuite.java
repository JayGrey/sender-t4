package com.darkbytes.sender;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestImport.class,
        TestProcessDirection.class,
        TestLogging.class
})
public class SenderTestSuite {
}
