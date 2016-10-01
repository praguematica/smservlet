package co.uk.praguematica.urlmapping.serializers;

import co.uk.praguematica.urlmapping.MappingProcessorException;

public class SerializerNoneImpl implements Serializer {

	@Override
	public <T> T deserialize(String str, Class<T> type) {
		return type.cast(str);
	}

	@Override
	public Object serialize(Object obj) throws MappingProcessorException {
		return obj;
	}

	@Override
	public String type() {
		return Format.UNSPECIFIED;
	}

}