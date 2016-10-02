package uk.co.praguematica.urlmapping.serializers;

import uk.co.praguematica.urlmapping.MappingProcessorException;

public class SerializerNoneImpl implements Serializer {

	public <T> T deserialize(String str, Class<T> type) {
		return type.cast(str);
	}

	public Object serialize(Object obj) throws MappingProcessorException {
		return obj;
	}

	public String type() {
		return Format.UNSPECIFIED;
	}

}
