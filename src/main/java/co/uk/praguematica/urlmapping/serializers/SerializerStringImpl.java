package co.uk.praguematica.urlmapping.serializers;

import co.uk.praguematica.urlmapping.MappingProcessorException;

public class SerializerStringImpl implements Serializer {
	
	public <T> T deserialize(String str, Class<T> type) {
		try {
			if (Long.class.equals(type)) {
				return type.cast(Long.valueOf(str));
			}
			if (Integer.class.equals(type)) {
				return type.cast(Integer.valueOf(str));
			}
			return type.cast(str);
			
		} catch (NumberFormatException e) {
			return null; // Long or integer not parsed correctly
		} catch (ClassCastException e) {
			return null; // Cast not successful;
		}		
	}

	public Object serialize(Object obj) throws MappingProcessorException {
		return String.valueOf(obj);
	}

	public String type() {
		return Format.STRING;
	}

}
