package com.dvwsolutions.urlmapping.serializers;

import com.dvwsolutions.urlmapping.MappingProcessorException;

public class SerializerStringImpl implements Serializer {
	
	public <T> T deserialize(String str, Class<T> type) {
		return type.cast(str);
	}

	public String serialize(Object obj) throws MappingProcessorException {
		return String.valueOf(obj);
	}

	public String type() {
		return Format.STRING;
	}

}
