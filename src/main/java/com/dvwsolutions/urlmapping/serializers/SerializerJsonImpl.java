package com.dvwsolutions.urlmapping.serializers;

import com.dvwsolutions.urlmapping.MappingProcessorException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializerJsonImpl implements Serializer {
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
	public String serialize(Object obj) throws MappingProcessorException {
		return gson.toJson(obj);
	}

	public <T> T deserialize(String str, Class<T> type) {
		return type.cast(gson.fromJson(str, type));		
	}

	public String type() {
		return Format.JSON;
	}

}
