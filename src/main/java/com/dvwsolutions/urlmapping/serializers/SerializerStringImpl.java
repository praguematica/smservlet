package com.dvwsolutions.urlmapping.serializers;

import com.dvwsolutions.urlmapping.MappingProcessorException;

public class SerializerStringImpl implements Serializer {
	
	public <T> T deserialize(String str, Class<T> type) {
		if (Long.class.equals(type)) {
			return type.cast(Long.valueOf(str));
		}
		if (Integer.class.equals(type)) {
			return type.cast(Integer.valueOf(str));
		}
		return type.cast(str);
	}

	public Object serialize(Object obj) throws MappingProcessorException {
		return String.valueOf(obj);
	}

	public String type() {
		return Format.STRING;
	}

}
