package com.fss.pos.host;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import com.fss.pos.base.api.transactions.ModifyTransaction;
import com.fss.pos.base.commons.Data;
import com.fss.pos.base.commons.IsoBuffer;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.factory.AbstractFactory;

public abstract class AbstractHostFactory extends AbstractFactory {

	@Override
	protected void registerMethods(Class<?> clazz, Object api) {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			AbstractHostApi ahapi = (AbstractHostApi) api;
			Method[] methods = clazz.getDeclaredMethods();
			for (Method m : methods) {
				if (m.isAnnotationPresent(ModifyTransaction.class)) {
					Class<?>[] params = m.getParameterTypes();
					if (!validateMethodDefenition(params, ahapi, m))
						continue;
					String mKey = m.getAnnotation(ModifyTransaction.class)
							.value().toString();
					MethodHandle methodHandle = lookup.findVirtual(api
							.getClass(), m.getName(), MethodType.methodType(
							void.class, IsoBuffer.class, Data.class));
					methodHandle = methodHandle.asType(methodHandle.type()
							.changeParameterType(0, AbstractHostApi.class));
					checkDuplicatePut(ahapi, mKey, ahapi, m, params,
							methodHandle);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(this.getClass() + " Host Methods ", e);
		}
	}

	private boolean validateMethodDefenition(Class<?>[] params, Object api,
			Method m) {
		if (params.length == 2 && IsoBuffer.class.equals(params[0])
				&& Data.class.equals(params[1]))
			return true;
		Log.trace("Invalid method definition for " + api.getClass()
				+ " Method " + m.getName());
		new Exception("Invalid method definition for " + api.getClass()
				+ " Method " + m.getName()
				+ " Method params should be (IsoBuffer, Data)")
				.printStackTrace();
		return false;
	}

	private void checkDuplicatePut(AbstractHostApi ahapi, String mKey,
			Object api, Method m, Class<?>[] params, MethodHandle methodHandle) {
		if (!ahapi.getMethodHandles().containsKey(mKey)) {
			ahapi.getMethodHandles().put(mKey, methodHandle);
		} else {
			Log.trace("Duplicate method definition for " + api.getClass()
					+ " Method" + m.getName());
			new Exception("Duplicate method definition for " + api.getClass()
					+ " Method " + m.getName() + " annotated as "
					+ m.getAnnotation(ModifyTransaction.class).value())
					.printStackTrace();
		}
	}

}
