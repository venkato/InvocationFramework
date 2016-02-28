package org.commonutils;

import junit.framework.Assert;
import net.sf.jremoterun.JrrUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class CommonToString2 {

	public static int maxStingLength = 1000;

	private static final Log log = LogFactory.getLog(CommonToString2.class);

	public static String toString(final Object object) {
		return toString(object, CommonToString2.maxStingLength);
	}

	public static String toStringMax(final Object object) {
		return toString(object, Integer.MAX_VALUE);
	}

	public static String toString(final Object object, final int maxLength) {
		return toString(object, maxLength, new StringBuffer(), new IdentityHashMap()).toString();
	}

	public static StringBuffer toString(final Object object, final int maxLength, final StringBuffer stringBuffer) {
		return toString(object, maxLength, stringBuffer, new IdentityHashMap());
	}

	// TODO add iterator, enumeration
	public static StringBuffer toString(final Object object, final int maxLength, final StringBuffer stringBuffer,
			final IdentityHashMap viewedObjects) {
		try {
			return toStringThrowRuntimeException(object, maxLength, stringBuffer, viewedObjects);
		} catch (final RuntimeException e) {
			return addException(object, maxLength, stringBuffer, e);
		}
	}

	private static StringBuffer addException(final Object object, final int maxLength, StringBuffer stringBuffer,
			Throwable e) {
		e = JrrUtils.getRootException(e);
		log.warn(object.getClass().getName(), e);
		final String msg = e.getMessage();
		stringBuffer = stringBuffer.append(CommonToString2.getSimpleName1(e.getClass()));
		if (msg == null || msg.length() == 0) {
		} else {
			stringBuffer = stringBuffer.append(": ").append(msg);
		}
		if (stringBuffer.length() > maxLength) {
			final String res = cropString(stringBuffer.toString(), maxLength);
			stringBuffer.setLength(0);
			stringBuffer = stringBuffer.append(res);
		}
		return stringBuffer;
	}

	public static String emptyString = "";

	public static String getSimpleName1(final Class clazz) {
		if (isAnonymous(clazz)) {
			return CommonToString2.emptyString;
		}
		try {
			return clazz.getSimpleName();
		} catch (final InternalError e) {
			log.info(clazz.getName(), e);
			return CommonToString2.emptyString;
		}
	}

	public static final Pattern annonymousPattern = Pattern.compile(".*\\$\\d+");

	public static final Object object = new Object();

	public static boolean isAnonymous(final Class class1) {
		return annonymousPattern.matcher(class1.getName()).matches();
	}

	public static StringBuffer toStringThrowRuntimeException(final Object object, final int maxLength,
			StringBuffer stringBuffer, final IdentityHashMap viewedObjects) {
		if (object == null) {
			return stringBuffer.append("null");
		}
		if (object.getClass().isEnum()) {
			stringBuffer.append(object);
			return stringBuffer;
		}
		if (object.getClass().getName().startsWith("java.lang.")) {
			toStringSimpleNotUsed(object, object.getClass(), stringBuffer, maxLength);
			// stringBuffer.append(object.toString());
			return stringBuffer;
		}
		if (viewedObjects.put(object, CommonToString2.object) != null) {
			log.debug(object.getClass());
			stringBuffer.append("visited " + CommonToString2.getSimpleName1(object.getClass()));
			return stringBuffer;
		}
		if (object instanceof Collection) {
			final Collection collection = (Collection) object;
			stringBuffer = stringBuffer.append("[");
			// if (collection.size() != 0) {
			l: {
				boolean first=true;
				for (final Object object2 : collection) {
					if(first) {
						first=false;
					}else {
						stringBuffer = stringBuffer.append(", ");
					}
					toString(object2, maxLength, stringBuffer, viewedObjects);
					if (stringBuffer.length() > maxLength) {
						stringBuffer.setLength(maxLength - 4);
						break l;
					}
				}
				// stringBuffer.setLength(stringBuffer.length() - 2);
			}
			stringBuffer = stringBuffer.append("]");
			return stringBuffer;
		}
		if (object instanceof Map) {
			final Map new_name = (Map) object;
			final Set<Entry> set = new_name.entrySet();
			toString(set, maxLength, stringBuffer, viewedObjects);
			return stringBuffer;
		}
		if (object instanceof Entry) {
			final Entry new_name = (Entry) object;
			toString(new_name.getKey(), maxLength, stringBuffer, viewedObjects);
			stringBuffer = stringBuffer.append("=");
			toString(new_name.getValue(), maxLength, stringBuffer, viewedObjects);
			return stringBuffer;
		}
		final Class class1 = object.getClass();
		if (class1.isArray()) {
			final int length = Array.getLength(object);
			stringBuffer = stringBuffer.append("[");
			for (int i = 0; i < length; i++) {
				toString(Array.get(object, i), maxLength, stringBuffer, viewedObjects);
				if (stringBuffer.length() > maxLength) {
					stringBuffer.setLength(maxLength - 4);
					break;
				}
				if (i != length - 1) {
					stringBuffer = stringBuffer.append(", ");
				}
			}
			stringBuffer = stringBuffer.append("]");
			return stringBuffer;
		}
		if ((object instanceof File) || !(object instanceof Serializable)) {
			// if class not Serializable, then it state can't be derived from
			// private fields
			return toStringSimpleNotUsed(object, object.getClass(), stringBuffer, maxLength);
		}
		Assert.assertNotNull(stringBuffer);
		// if class is Serializable, and sometimes toString mag hang, then it
		// state derived from private object fields
		return toStringBasedOnObjectFields(object, class1, stringBuffer, maxLength, viewedObjects);
	}

	private static StringBuffer toStringBasedOnObjectFields(final Object object, final Class class1, StringBuffer stringBuffer,
			final int maxLength, final IdentityHashMap viewedObjects) {
		try {
			Collection<Field> fields = new ArrayList();
			Class class2 = object.getClass();
			while (class2 != Object.class) {
				Field[] fields2 = class2.getDeclaredFields();
				for (Field field : fields2) {
					int modif = field.getModifiers();
					if ((modif & Modifier.TRANSIENT) == 0 && (modif & Modifier.STATIC) == 0
							&& (modif & Modifier.FINAL) == 0) {
						field.setAccessible(true);
						fields.add(field);
					}
				}
				class2 = class2.getSuperclass();
			}
			stringBuffer.append(getSimpleName1(class1)).append("[");
			boolean first = true;
			for (Field field : fields) {
				if (first) {
					first = false;
				} else {
					stringBuffer.append(", ");
				}
				stringBuffer.append(field.getName()).append("=");
				try {
					toString(field.get(object), maxLength, stringBuffer, viewedObjects);
				} catch (IllegalAccessException e) {
					log.info(object, e);
				}
			}
			stringBuffer.append("]");
			if (stringBuffer.length() > maxLength) {
				final String res = cropString(stringBuffer.toString(), maxLength);
				stringBuffer.setLength(0);
				stringBuffer = stringBuffer.append(res);
			}
			return stringBuffer;
		} catch (final NullPointerException e) {
			log.info(class1.getName(), e);
			return stringBuffer.append(e.getClass().getSimpleName());
		} catch (final RuntimeException e) {
			log.info(class1.getName(), e);
			return stringBuffer.append(e.getClass().getSimpleName());
		} catch (final StackOverflowError e) {
			return addException(object, maxLength, stringBuffer, e);
		} catch (final VirtualMachineError e) {
			log.info(class1.getName(), e);
			throw e;
		} catch (final Error e) {
			return addException(object, maxLength, stringBuffer, e);
		}

	}

	private static StringBuffer toStringSimpleNotUsed(final Object object, final Class class1,
			StringBuffer stringBuffer, final int maxLength) {
		try {
			final String r = object.toString();
			if (r == null) {
				log.warn(object.getClass().getName());
				return stringBuffer.append("null");
			}
			if (r.length() == 0) {
				return stringBuffer;
			}
			// if()
			stringBuffer = stringBuffer.append(r);
			if (stringBuffer.length() > maxLength) {
				final String res = cropString(stringBuffer.toString(), maxLength);
				stringBuffer.setLength(0);
				stringBuffer = stringBuffer.append(res);
			}
			return stringBuffer;
		} catch (final NullPointerException e) {
			log.info(class1.getName(), e);
			return stringBuffer.append(e.getClass().getSimpleName());
		} catch (final RuntimeException e) {
			log.info(class1.getName(), e);
			return stringBuffer.append(e.getClass().getSimpleName());
		} catch (final StackOverflowError e) {
			return addException(object, maxLength, stringBuffer, e);
		} catch (final VirtualMachineError e) {
			log.info(class1.getName(), e);
			throw e;
		} catch (final Error e) {
			return addException(object, maxLength, stringBuffer, e);
		}
	}

	public static Pattern manySpacesPattern = Pattern.compile("   +");

	private static String cropString(String in, final int maxLength) {
		in = in.replace('\t', ' ');
		in = in.replace('\n', ' ');
		in = CommonToString2.manySpacesPattern.matcher(in).replaceAll("  ");
		if (in.length() > maxLength) {
			return in.substring(0, maxLength) + " ..";
		}
		return in;
	}
}
