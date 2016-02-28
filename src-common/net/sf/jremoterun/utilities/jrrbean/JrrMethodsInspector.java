package net.sf.jremoterun.utilities.jrrbean;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javassist.util.proxy.MethodHandler;
import net.sf.jremoterun.utilities.javassist.BaseMethodHandler;

public class JrrMethodsInspector extends BaseMethodHandler {

	private final static Logger log = Logger.getLogger(JrrMethodsInspector.class.getName());

	private final JrrBeanMethods jrrBeanMethods;

	private final Date createDate = new Date();

	private final WeakReference<Thread> creatorThread = new WeakReference<Thread>(Thread.currentThread());

	public static int maxFieldsToStringLength = 160;

	private final boolean first;

	private Object lock = new Object();

	public JrrMethodsInspector(final JrrBeanMethods jrrBeanMethods) {
		this.jrrBeanMethods = jrrBeanMethods;
		first = jrrBeanMethods.getObject() == null;
		handleHashCode = true;
	}

	public Object invoke2(Object beanObject, Method methodFromBean, Method superMethod, Object[] args)
			throws Throwable {
		final String methodName = methodFromBean.getName();
		log.fine(methodName);
		final ArrayList<String> al = JrrBeanMaker.buildKey(methodFromBean);
		final Method methodToInvoke = jrrBeanMethods.getMethodsMap().get(al);
		if ("equals".equals(methodName) && args.length == 1) {
			if (beanObject == args[0]) {
				return true;
			}
		} else if (args.length == 0) {
			if ("toString".equals(methodName)) {
				if (methodToInvoke.getDeclaringClass() == Object.class) {
					log.finer("invoking specified toString method");
					return fieldsToString();
				}
				log.finer("invoking super toString method");
			}
		}
		if (methodToInvoke == null) {
			throw new NoSuchMethodError(al.toString());
		}
		final Object beanValue = jrrBeanMethods.getObject();
		if (beanValue == beanObject) {
			return superMethod.invoke(beanObject, args);
		}
		try {
			log.finer("invoking method from different classloads");
			final Object result = methodToInvoke.invoke(beanValue, args);
			return result;
		} catch (final InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public final static StringBuffer fieldsToString(final Object object, Class class1, final int maxLength)
			throws IllegalAccessException {
		final StringBuffer result = new StringBuffer();
		while (true) {
			if (class1 == Object.class) {
				if (result.length() > 2) {
					result.setLength(result.length() - 2);
				}
				break;
			}
			fieldsToString(object, class1, result, maxLength);
			class1 = class1.getSuperclass();
			if (result.length() > maxLength) {
				result.append(" ..");
				break;
			}
		}

		return result;
	}

	public static <T> void fieldsToString(final Object object, final Class clazz, final StringBuffer result,
			final int maxLength) throws IllegalAccessException {
		// boolean first=true;
		for (final Field field : clazz.getDeclaredFields()) {
			final int modif = field.getModifiers();
			if (!Modifier.isStatic(modif)) {
				field.setAccessible(true);
				final Object value = field.get(object);
				result.append(field.getName()).append('=').append(value).append(", ");
				if (result.length() > maxLength) {
					return;
				}
			}
		}
	}

	public String fieldsToString() {
		try {
			return fieldsToString(jrrBeanMethods.getObject(), jrrBeanMethods.getObject().getClass().getSuperclass(),
					maxFieldsToStringLength).toString();
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public JrrBeanMethods getJrrBeanMethods() {
		return jrrBeanMethods;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public boolean isFirst() {
		return first;
	}

	public Thread getCreatorThread() {
		return creatorThread.get();
	}

}

/* 
 * JRemoteRun.sf.net. License:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
 