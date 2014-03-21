package com.dvwsolutions.urlmapping.serializers;

import com.dvwsolutions.urlmapping.MappingProcessorException;

public interface Serializer {
	public String type();
	public Object serialize(Object obj) throws MappingProcessorException;
	public <T> T deserialize(String str, Class<T> type) throws MappingProcessorException;
}
