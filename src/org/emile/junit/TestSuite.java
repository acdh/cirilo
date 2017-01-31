package org.emile.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses ({
	  TestTEI.class,
	  TestLIDO.class
})

public class TestSuite {}  	