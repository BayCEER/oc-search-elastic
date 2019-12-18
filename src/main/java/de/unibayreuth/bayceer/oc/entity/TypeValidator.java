package de.unibayreuth.bayceer.oc.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeValidator {
	
	
	public static enum Type {
		TEXT , BOOLEAN, FLOAT, LONG, DOUBLE, DATE
	};
	
	public static final String dateString = "2015/09/02";
	public static final String dateTimeString = "2015/09/02 10:00:00";
	
	private static final Logger log = LoggerFactory.getLogger(TypeValidator.class);
	
	public static boolean isValid(String value, Enum<Type> type) {
		return isValid(value, type.toString().toLowerCase());
	}

	public static boolean isValid(String value, String type) {
		try {
			if (value == null || type == null) {
				return true;
			}
			switch (type) {
			case "text":
				return true;
			case "boolean":
				return (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"));
			case "float":
				Float.valueOf(value);
				return true;
			case "long":
				Long.valueOf(value);
				return true;
			case "double":
				Double.valueOf(value);
				return true;
			case "date":
				if (value.length() == dateString.length()) {
					DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
					sdf.parse(value);
					return true;
				} else if (value.length() == dateTimeString.length()) {
					DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					sdf.parse(value);
					return true;
				} else {
					return false;
				}
			default:
				log.warn("Unknown type:{}", type);
				return false;
			}

		} catch (NumberFormatException | ParseException e) {
			return false;
		}

	}
}
