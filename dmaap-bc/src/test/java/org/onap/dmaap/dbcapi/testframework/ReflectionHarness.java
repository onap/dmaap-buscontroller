/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.dmaap.dbcapi.testframework;

import static java.lang.System.err;
import static java.lang.System.out;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ReflectionHarness {
	private static final String  fmt = "%24s: %s%n";


	// following 2 functions taken from: http://tutorials.jenkov.com/java-reflection/getters-setters.html
	public static boolean isGetter(Method method){
	  if(!method.getName().startsWith("get"))      return false;
	  if(method.getParameterTypes().length != 0)   return false;  
	  if(void.class.equals(method.getReturnType())) return false;
	  return true;
	}

	public static boolean isSetter(Method method){
	  if(!method.getName().startsWith("set")) return false;
	  if(method.getParameterTypes().length != 1) return false;
 	  return true;
	}

	private void testGetter( Class<?> c, Method m, Class<?>[] pType, String val ) {
		out.format( fmt, "testGetter: Method Name", m.getName() );
		Class retType = m.getReturnType();
		out.format( fmt, "testGetter: Return Type ", retType );
		out.format( fmt, "testGetter: val ", (val != null)?val:"null" );
		assertTrue( pType.length == 0 );

		try {
			Object t = c.newInstance();
			
				try {
					m.setAccessible(true);
					Object o = m.invoke( t );
					
					if( retType.equals( Class.forName( "java.lang.String" ) ) ) {
						if ( val == null ) {
							out.format( fmt, "testGetter: expected null, got  ", (o != null)?o:"null" );
							assert( o == null );
						} else {
							out.format( fmt, "testGetter: expected val, got  ", (o != null)?o:"null" );
							assert( o.equals( val ) );
						}
					} else {
						out.format( fmt, "testGetter: " + m.getName() + " untested retType", retType );

					}
			
				} catch (InvocationTargetException e ) {
					Throwable cause = e.getCause();
					err.format( "%s() returned %x%n", m.getName(), cause.getMessage() );
				}
					
		} catch (ClassNotFoundException nfe ){
	    	nfe.printStackTrace();
		} catch (IllegalArgumentException ae ) {
			ae.printStackTrace();
		} catch (InstantiationException ie ) {
			ie.printStackTrace();
		} catch (IllegalAccessException iae ) {
			iae.printStackTrace();
		}
	}

	private void testSetter( Class<?> c, Method m, Class<?>[] pType ) {
		//out.format( fmt, "testSetter: Method Name", m.getName() );
		Class retType = m.getReturnType();
		//out.format( fmt, "testSetter: Return Type ", retType );
		//out.format( fmt, "testSetter: val ", (val != null)?val:"null" );
		assertTrue( pType.length == 1 );

		try {
			Object t = c.newInstance();
			
				try {
					m.setAccessible(true);
					//out.format( fmt, "testSetter: " + m.getName() + " to try pType", pType[0] );
					if ( pType[0].equals( Class.forName( "java.lang.String" ) ) ) {
						String val = "Validate123";
						Object o = m.invoke( t, val );
					} else if ( pType[0].equals( boolean.class ) ) { // note primitive class notation 
						boolean b = true;
						Object o = m.invoke( t, b );
					} else {
						out.format( fmt, "testSetter: " + m.getName() + " untested pType", pType[0] );
					}
			
				} catch (InvocationTargetException e ) {
					Throwable cause = e.getCause();
					err.format( "%s() returned %x%n", m.getName(), cause.getMessage() );
				}
					
		} catch (ClassNotFoundException nfe ){
	    	nfe.printStackTrace();
		} catch (IllegalArgumentException ae ) {
			ae.printStackTrace();
		} catch (InstantiationException ie ) {
			ie.printStackTrace();
		} catch (IllegalAccessException iae ) {
			iae.printStackTrace();
		}
	}

    public void reflect(String... args) {
		try {
	    Class<?> c = Class.forName(args[0]);
	    Method[] allMethods = c.getDeclaredMethods();
	    String methodPrefix = args[1];
	    for (Method m : allMethods) {
			if (!m.getName().startsWith(methodPrefix)) {
		    	continue;
			}
			//out.format("%s%n", m.toGenericString());

			//out.format(fmt, "ReturnType", m.getReturnType());
			//out.format(fmt, "GenericReturnType", m.getGenericReturnType());

			Class<?>[] pType  = m.getParameterTypes();
			Type[] gpType = m.getGenericParameterTypes();
			for (int i = 0; i < pType.length; i++) {
		    	//out.format(fmt,"ParameterType", pType[i]);
		    	//out.format(fmt,"GenericParameterType", gpType[i]);
			}
			if ( isGetter( m ) ) {
				testGetter( c, m, pType , args[2]);
			} else if ( isSetter( m ) ) {
				testSetter( c, m, pType );
			}

			Class<?>[] xType  = m.getExceptionTypes();
			Type[] gxType = m.getGenericExceptionTypes();
			for (int i = 0; i < xType.length; i++) {
		    	//out.format(fmt,"ExceptionType", xType[i]);
		    	//out.format(fmt,"GenericExceptionType", gxType[i]);
			}
	    }

        // production code should handle these exceptions more gracefully
		} catch (ClassNotFoundException x) {
	    	x.printStackTrace();
		}
    }
}
