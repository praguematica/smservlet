package uk.co.praguematica.smservlet.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import uk.co.praguematica.urlmapping.MappingProcessor;
import uk.co.praguematica.urlmapping.MappingProcessorException;
import uk.co.praguematica.urlmapping.MappingProcessorExceptionType;
import uk.co.praguematica.urlmapping.annotations.PathVariable;
import uk.co.praguematica.urlmapping.annotations.RequestMapping;
import uk.co.praguematica.urlmapping.annotations.SelfMapped;

@SelfMapped(responseHandler = JUnitResponseHandler.class)
public class MappingProcessorTest {

	private MappingProcessor mp;

	@Before
	public void init() throws MappingProcessorException {
		mp = new MappingProcessor(this);
	}

	@Test
	public void testNonExisting() throws MappingProcessorException {
		try {
			mp.process("notexisting", null, null);
		} catch (MappingProcessorException e) {
			if (e.getType() == MappingProcessorExceptionType.MAPPING_NOT_FOUND)
				assertEquals("non existing mapping test", true, true);
			else
				fail("non existing mapping test failure");
		}

	}

	// Simple URL test
	@Test
	public void testSimpleUrl() throws MappingProcessorException { 
		mp.process("simpleurl", null, null);
		assertEquals("simpleurl test", JUnitResponseHandler.responseResult, "simpleurl");
	}

	@Test
	public void testPathVariable() throws MappingProcessorException {
		mp.process("pathvariable/111/anything/222", null, null);
		assertEquals("pathvariable test", JUnitResponseHandler.responseResult, "pathvariable:111/222");
		
		mp.process("pathvariable/abc/anything/def", null, null);
		assertEquals("pathvariable test", JUnitResponseHandler.responseResult, "pathvariable:abc/def");
		
		mp.process("pathvariable2/111/222", null, null);
		assertEquals("pathvariable test", JUnitResponseHandler.responseResult, "pathvariable2:111/222");
		
		mp.process("pathvariable2/abc/def", null, null);
		assertEquals("pathvariable test", JUnitResponseHandler.responseResult, "pathvariable2:abc/def");
	}

	@Test
	public void testSecurity() throws MappingProcessorException {
		mp.process("test-allowed-security", null, null);
		assertEquals("allowed security test", JUnitResponseHandler.responseResult, "RESULT");

		try {
			mp.process("test-restricted-security", null, null);
			assertTrue("restricted security test", false); // it should not get here
		} catch (Exception e) {
			assertEquals("restricted security test - correct exception fired", MappingProcessorException.class, e.getClass());
			assertEquals("restricted security test - correct exception type fired", MappingProcessorExceptionType.class, ((MappingProcessorException) e).getType().getClass());
		}

	}

	
	// ******************************************************
	// Mapped methods	
	// ******************************************************	
	@RequestMapping(value = "simpleurl")
	public Object testSimpleUrl_map() {
		return "simpleurl";
	}	

	@RequestMapping(value = "pathvariable/{a}/anything/{b}")
	public Object testPathVariable_map(@PathVariable("a") String a, @PathVariable("b") String b) throws MappingProcessorException {
		return "pathvariable:" + a + "/" + b;
	}
	
	@RequestMapping(value = "pathvariable2/{a}/{b}")
	public Object testPathVariable_map2(@PathVariable("a") String a, @PathVariable("b") String b) throws MappingProcessorException {
		return "pathvariable2:" + a + "/" + b;
	}

	@RequestMapping(value = "test-restricted-security", securityHandler = TestRestrictedSecurityHandler.class)
	public String testRestrictedSecurity() throws MappingProcessorException {
		return "RESULT";
	}

	@RequestMapping(value = "test-allowed-security", securityHandler = TestAllowedSecurityHandler.class)
	public String testAllowedSecurity() throws MappingProcessorException {
		return "RESULT";
	}



}
