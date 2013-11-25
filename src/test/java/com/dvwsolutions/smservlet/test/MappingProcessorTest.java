package com.dvwsolutions.smservlet.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.dvwsolutions.urlmapping.MappingProcessor;
import com.dvwsolutions.urlmapping.MappingProcessorException;
import com.dvwsolutions.urlmapping.MappingProcessorExceptionType;
import com.dvwsolutions.urlmapping.annotations.PathVariable;
import com.dvwsolutions.urlmapping.annotations.RequestMapping;
import com.dvwsolutions.urlmapping.annotations.SelfMapped;

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

}
