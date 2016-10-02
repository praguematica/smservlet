package uk.co.praguematica.urlmapping.serializers;

import uk.co.praguematica.urlmapping.MappingProcessorException;
import uk.co.praguematica.urlmapping.MappingProcessorExceptionType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializerJsonImpl implements Serializer {
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
	public Object serialize(Object obj) throws MappingProcessorException {
		try {
			return gson.toJson(obj);			
		} catch (Exception e) {
			throw new MappingProcessorException(MappingProcessorExceptionType.SERIALIZE_ERROR, "Error when serializing results", e);
		}
		
	}

	public <T> T deserialize(String str, Class<T> type) throws MappingProcessorException {
		T deserialized = null; 
		try {
			deserialized = type.cast(gson.fromJson(str, type));
		} catch (Exception e) {
			throw new MappingProcessorException(MappingProcessorExceptionType.DESERIALIZE_ERROR, "Error when deserializing parameter", e);
		}
		return deserialized;		
	}

	public String type() {
		return Format.JSON;
	}

}
