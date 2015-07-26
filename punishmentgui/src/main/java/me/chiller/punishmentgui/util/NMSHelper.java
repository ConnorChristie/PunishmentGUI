package me.chiller.punishmentgui.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

public class NMSHelper
{
	private static String version = "";
	private static Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	
	static
	{
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
	/**
	 * Imports the specified class, if found, then returns a Java class object for the specified class name.
	 * 
	 * @param name The fully qualified class name including the package name
	 * @return The class, if found, otherwise throws exception
	 * @throws ClassNotFoundException
	 */
	public static Class<?> importClass(String name) throws ClassNotFoundException
	{
		Class<?> realClass = getClassFromName(name);
		
		classes.put(name, realClass);
		
		return realClass;
	}
	
	/**
	 * Returns a Java class object for the specified class name.
	 * 
	 * @param name The class name, either fully qualified or just the simple name
	 * @return The class, if found, otherwise throws exception
	 * @throws Exception
	 */
	public static Class<?> getClass(String name) throws Exception
	{
		for (String clazz : classes.keySet())
		{
			if (clazz.toLowerCase().contains(name.toLowerCase()))
			{
				return classes.get(clazz);
			}
		}
		
		throw new Exception("Class " + name + " not found, try including the package name, for instance: net.minecraft.server._version_." + name);
	}
	
	/**
	 * Calls the specified method on the instance provided
	 * 
	 * @param method The method to be called
	 * @param instance The object the method is being called on
	 * @param args The arguments for the method
	 * @return Called method's return, null if method is of void type
	 * @throws Exception 
	 */
	public static Object callMethod(String method, Object instance, Object... args) throws Exception
	{
		return callMethod(method, instance, getArgTypes(args), args);
	}
	
	/**
	 * Calls the specified method on the instance provided, with argument types specified
	 * 
	 * @param method The method to be called
	 * @param instance The object the method is being called on
	 * @param argTypes The argument types of the method
	 * @param args The arguments for the method
	 * @return Called method's return, null if method is of void type
	 * @throws Exception
	 */
	public static Object callMethod(String method, Object instance, Class<?>[] argTypes, Object... args) throws Exception
	{
		if (instance != null)
		{
			return callMethod(instance.getClass(), method, instance, argTypes, args);
		}
		
		throw new Exception("Provided instance is null, use callStaticMethod() is instance is null");
	}
	
	private static Object callMethod(Class<?> clazz, String method, Object instance, Class<?>[] argTypes, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method m = getMethod(clazz, method, argTypes);
		
		if (m != null)
		{
			return m.invoke(instance, args);
		}
		
		throw new NoSuchMethodException("Method " + method + "(" + argTypesToString(argTypes) + ") not found in any imported classes, are you sure you have the right arg types?");
	}
	
	/**
	 * Calls the specified static method on the class provided
	 * 
	 * @param clazz The class name, either fully qualified or just the simple name
	 * @param method The method name
	 * @param args The arguments for the method
	 * @return Called method's return, null if method is of void type
	 * @throws Exception
	 */
	public static Object callStaticMethod(String clazz, String method, Object... args) throws Exception
	{
		return callStaticMethod(getClass(clazz), method, args);
	}
	
	/**
	 * Calls the specified static method on the class provided
	 * 
	 * @param clazz The class object
	 * @param method The method name
	 * @param args The arguments for the method
	 * @return Called method's return, null if method is of void type
	 * @throws Exception
	 */
	public static Object callStaticMethod(Class<?> clazz, String method, Object... args) throws Exception
	{
		return callMethod(clazz, method, null, getArgTypes(args), args);
	}
	
	/**
	 * Creates a new instance of the specified class name
	 * 
	 * @param clazz The class name, either fully qualified or just the simple name
	 * @return The new instance of the class
	 * @throws Exception
	 */
	public static Object newInstance(String clazz) throws Exception
	{
		Class<?> realClazz = getClass(clazz);

		if (realClazz != null)
		{
			return realClazz.newInstance();
		}
		
		return null;
	}
	
	/**
	 * Creates a new instance of the specified class
	 * 
	 * @param clazz The class object
	 * @return The new instance of the class
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws Exception
	 */
	public static Object newInstance(Class<?> clazz, Object... args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException
	{
		return newInstance(clazz, getArgTypes(args), args);
	}
	
	/**
	 * Creates a new instance of the specified class, with argument types specified
	 * 
	 * @param clazz The class object
	 * @param argTypes The argument types of the class's constructor
	 * @return The new instance of the class
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws Exception
	 */
	public static Object newInstance(Class<?> clazz, Class<?>[] argTypes, Object... args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException
	{
		Constructor<?> constructor = clazz.getConstructor(getArgTypes(args));
		
		return constructor.newInstance(args);
	}
	
	private static Class<?> getClassFromName(String clazz) throws ClassNotFoundException
	{
		return Class.forName(clazz.replace("_version_", version));
	}
	
	private static Method getMethod(Class<?> clazz, String method, Class<?>[] argTypes)
	{
		try
		{
			return clazz.getMethod(method, argTypes);
		} catch (Exception e)
		{
			return null;
		}
	}
	
	private static Class<?>[] getArgTypes(Object... args)
	{
		if (args != null)
		{
			Class<?>[] argTypes = new Class<?>[args.length];
			
			for (int i = 0; i < args.length; i++)
			{
				argTypes[i] = args[i] != null ? args[i].getClass() : null;
			}
			
			return argTypes;
		}
		
		return null;
	}
	
	private static String argTypesToString(Class<?>[] argTypes)
	{
		String str = "";
		
		for (int i = 0; i < argTypes.length; i++)
		{
			str += (i != 0 ? ", " : "") + argTypes[i].getSimpleName();
		}
		
		return str;
	}
}