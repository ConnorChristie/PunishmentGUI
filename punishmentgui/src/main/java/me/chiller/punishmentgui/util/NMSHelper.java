package me.chiller.punishmentgui.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	/**
	 * Creates a method builder instance for a static method
	 * 
	 * @param clazz The class's name
	 * @return The created method builder
	 * @throws Exception
	 */
	public static MethodBuilder buildStaticMethod(String clazz) throws Exception
	{
		return buildStaticMethod(getClass(clazz));
	}
	
	/**
	 * Creates a method builder instance for a static method
	 * 
	 * @param clazz The class's name
	 * @return The created method builder
	 */
	public static MethodBuilder buildStaticMethod(Class<?> clazz)
	{
		return new MethodBuilder(clazz);
	}
	
	/**
	 * Creates a method builder instance for a method
	 * 
	 * @param instance The object the method is going to be called on
	 * @return The created method builder
	 */
	public static MethodBuilder buildMethod(Object instance)
	{
		return new MethodBuilder(instance);
	}
	
	public static class MethodBuilder
	{
		private List<VersionMethod> versionMethods = new ArrayList<VersionMethod>();
		
		private Class<?> clazz;
		private Object instance = null;
		
		protected MethodBuilder(Class<?> clazz)
		{
			this.clazz = clazz;
		}
		
		protected MethodBuilder(Object instance)
		{
			this.instance = instance;
			this.clazz = instance != null ? instance.getClass() : null;
		}
		
		/**
		 * Adds a method to the method builder for the specified version
		 * Your plugin will call this method if the bukkit version matches
		 * 
		 * @param version Bukkit version for the method to be called on
		 * @param method Method to be called
		 * @param argTypes Optional, argument types for the method
		 * @return The current method builder
		 */
		public MethodBuilder addVersionMethod(String version, String method, Class<?>... argTypes)
		{
			versionMethods.add(new VersionMethod(version, method, argTypes));
			
			return this;
		}
		
		/**
		 * Adds a method to the method builder for the any version
		 * Your plugin will call this method if you did not add a version method for the bukkit version
		 * 
		 * @param method Method to be called
		 * @param argTypes Optional, argument types for the method
		 * @return The current method builder
		 */
		public MethodBuilder addUniversalMethod(String method, Class<?>... argTypes)
		{
			versionMethods.add(new VersionMethod("*", method, argTypes));
			
			return this;
		}
		
		/**
		 * Adds the specified arguments for the specified version
		 * If you use this, you do not have to specify arguments in MethodBuilder.execute();
		 * 
		 * @param version Bukkit version of the method for the arguments to be added to
		 * @param args The arguments for the method
		 * @return The current method builder
		 */
		public MethodBuilder addArguments(String version, Object... args)
		{
			VersionMethod versionMethod = getMethodForVersion(version);
			
			versionMethod.addArgs(args);
			
			return this;
		}
		
		/**
		 * Executes the method for the current version of Bukkit running
		 * Arguments are optional, not needed if MethodBuilder.addArguments() was called
		 * 
		 * @param args Optional, the arguments for the method
		 * @return Called method's return, null if method is of void type
		 * @throws NoSuchMethodException
		 * @throws IllegalAccessException
		 * @throws IllegalArgumentException
		 * @throws InvocationTargetException
		 */
		public Object execute(Object... args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
		{
			VersionMethod method = getMethodForVersion(version);
			
			if (method == null)
			{
				throw new NoSuchMethodException("The developer of this plugin does not support your version: " + version + ", contact the developer to update the plugin.");
			}
			
			Object[] usingArgs = args.length == 0 ? method.getArgs() : args;
			Class<?>[] argTypes = method.hasArgTypes() ? method.getArgTypes() : getArgTypes(usingArgs);
			
			Method m = getMethod(clazz, method.getMethod(), argTypes);
			
			if (m != null)
			{
				return m.invoke(instance, usingArgs);
			}
			
			throw new NoSuchMethodException("Method " + method + "(" + argTypesToString(argTypes) + ") not found in any imported classes, are you sure you have the right arg types?");
		}
		
		private VersionMethod getMethodForVersion(String version)
		{
			for (VersionMethod vMethod : versionMethods)
			{
				if (vMethod.isVersion(version))
				{
					return vMethod;
				}
			}
			
			for (VersionMethod vMethod : versionMethods)
			{
				if (vMethod.isUniversal())
				{
					return vMethod;
				}
			}
			
			return null;
		}
	}
	
	public static class VersionMethod
	{
		private String version = "*";
		private String method;
		
		private Class<?>[] argTypes = new Class<?>[0];
		private Object[] args = new Object[0];
		
		public VersionMethod(String version, String method)
		{
			this.version = version;
			
			this.method = method;
		}
		
		public VersionMethod(String version, String method, Class<?>... argTypes)
		{
			this.version = version;
			
			this.method = method;
			this.argTypes = argTypes;
		}
		
		public VersionMethod(String method)
		{
			this.method = method;
		}
		
		public VersionMethod(String method, Class<?>... argTypes)
		{
			this.method = method;
			this.argTypes = argTypes;
		}
		
		public boolean isVersion(String version)
		{
			if (version.replace("_", ".").toLowerCase().contains(this.version.toLowerCase()))
			{
				return true;
			} else if (version.toLowerCase().contains(this.version.toLowerCase()))
			{
				return true;
			}
			
			return false;
		}
		
		public boolean isUniversal()
		{
			return version.equals("*");
		}
		
		public String getMethod()
		{
			return method;
		}
		
		public boolean hasArgTypes()
		{
			return argTypes.length != 0;
		}
		
		public Class<?>[] getArgTypes()
		{
			return argTypes;
		}
		
		public Object[] getArgs()
		{
			return args;
		}
		
		protected void addArgs(Object... args)
		{
			this.args = args;
		}
	}
}