package fr.MaxWgamer.custom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Throwables;

public class KnockBackConfig {
	
    private static File CONFIG_FILE;
    /*========================================================================*/
    public static YamlConfiguration config;
    /*========================================================================*/

    public static void init(File configFile)
    {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try
        {
            config.load ( CONFIG_FILE );
        } catch ( IOException ex )
        {
        } catch ( InvalidConfigurationException ex )
        {
            Bukkit.getLogger().log( Level.SEVERE, "Could not load knockback.yml, please correct your syntax errors", ex );
            throw Throwables.propagate( ex );
        }
        config.options().copyDefaults( true );
        
        readConfig( KnockBackConfig.class, null );
    }
    
    static void readConfig(Class<?> clazz, Object instance)
    {
        for ( Method method : clazz.getDeclaredMethods() )
        {
            if ( Modifier.isPrivate( method.getModifiers() ) )
            {
                if ( method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE )
                {
                    try
                    {
                        method.setAccessible( true );
                        method.invoke( instance );
                    } catch ( InvocationTargetException ex )
                    {
                        throw Throwables.propagate( ex.getCause() );
                    } catch ( Exception ex )
                    {
                        Bukkit.getLogger().log( Level.SEVERE, "Error invoking " + method, ex );
                    }
                }
            }
        }

        try
        {
            config.save( CONFIG_FILE );
        } catch ( IOException ex )
        {
            Bukkit.getLogger().log( Level.SEVERE, "Could not save " + CONFIG_FILE, ex );
        }
    }

    private static double getDouble(String path, double def)
    {
        config.addDefault( path, def );
        return config.getDouble( path, config.getDouble( path ) );
    }

    public static double knockbackFriction;
    public static double knockbackHorizontal;
    public static double knockbackVertical;
    public static double knockbackVerticalLimit;
    public static double knockbackExtraHorizontal;
    public static double knockbackExtraVertical;
    private static void knockBackModifiers()
    {
    	knockbackFriction = getDouble( "knockback-friction", 2.0D );
    	knockbackHorizontal = getDouble( "knockback-horizontal", 0.425D );
    	knockbackVertical = getDouble( "knockback-vertical", 0.35D );
    	knockbackVerticalLimit = getDouble( "knockback-vertical-limit", 0.4D );
    	knockbackExtraHorizontal = getDouble( "knockback-extra-horizontal", 0.485D );
    	knockbackExtraVertical = getDouble( "knockback-extra-vertical", 0.085D );
    }

}
