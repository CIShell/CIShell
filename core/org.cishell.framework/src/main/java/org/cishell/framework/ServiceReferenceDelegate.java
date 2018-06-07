//package org.cishell.framework;
//
//import org.osgi.framework.ServiceReference;
//
//
//public class ServiceReferenceDelegate {
//	public static ServiceReference createUniqueServiceReference(
//			ServiceReference originalServiceReference) {
//		originalServiceReference.getClass().
//		return null;
//	}
//}

//
//import org.eclipse.osgi.framework.internal.core.ServiceReferenceImpl;
//import org.eclipse.osgi.framework.internal.core.ServiceRegistrationImpl;
//import org.osgi.framework.Bundle;
//import org.osgi.framework.ServiceReference;
//
//public class ServiceReferenceDelegate extends ServiceReferenceImpl {
//	public static final String REGISTRATION_FIELD_NAME =
//		"org.eclipse.osgi.framework.internal.core.ServiceReferenceImpl.registration";
//
//	private static int nextUniqueID = 0;
//
//	private ServiceReference actualServiceReference;
//	private int uniqueID;
//
//	public ServiceReferenceDelegate(ServiceReference actualServiceReference) {
//		super(getServiceRegistration(actualServiceReference));
//		this.actualServiceReference = actualServiceReference;
//		this.uniqueID = nextUniqueID;
//		nextUniqueID++;
//		
//	}
//
//	public Object getProperty(String key) {
//		return this.actualServiceReference.getProperty(key);
//	}
//
//	public String[] getPropertyKeys() {
//		return this.actualServiceReference.getPropertyKeys();
//	}
//
//	public Bundle getBundle() {
//		return this.actualServiceReference.getBundle();
//	}
//
//	public Bundle[] getUsingBundles() {
//		return this.actualServiceReference.getUsingBundles();
//	}
//
//	public boolean isAssignableTo(Bundle bundle, String className) {
//		return this.actualServiceReference.isAssignableTo(bundle, className);
//	}
//
//	@Override
//	public int compareTo(Object reference) {
//		if (reference instanceof ServiceReferenceDelegate) {
//			ServiceReferenceDelegate otherDelegate = (ServiceReferenceDelegate) reference;
//
//			return new Integer(this.uniqueID).compareTo(new Integer(otherDelegate.uniqueID));
//		} else {
//			return this.actualServiceReference.compareTo(reference);
//		}
//	}
//
//	@Override
//	public int hashCode() {
//		return this.actualServiceReference.hashCode() + new Integer(this.uniqueID).hashCode();
//	}
//
//	// TODO: Totally, disginstingly hacky.
//	private static ServiceRegistrationImpl getServiceRegistration(
//			ServiceReference actualServiceReference) {
//		try {
//			Field[] fields = actualServiceReference.getClass().getDeclaredFields();
//
////			for (Field field : fields) {
////				System.err.println(field);
////			}
//			Field registrationField = fields[0];
////				actualServiceReference.getClass().getField(REGISTRATION_FIELD_NAME);
//			boolean isAccessible = registrationField.isAccessible();
//			registrationField.setAccessible(true);
//			Object serviceRegistration = registrationField.get(actualServiceReference);
//			registrationField.setAccessible(isAccessible);
//
//			return (ServiceRegistrationImpl) serviceRegistration;
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		} /* catch (NoSuchFieldException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		} */
//	}
//}