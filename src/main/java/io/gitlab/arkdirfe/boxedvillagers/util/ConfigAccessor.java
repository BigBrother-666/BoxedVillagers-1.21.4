/*
 * Copyright (C) 2012
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.gitlab.arkdirfe.boxedvillagers.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigAccessor
{
    
    private final String fileName;
    private final JavaPlugin plugin;
    
    private final File configFile;
    private FileConfiguration fileConfiguration;
    
    public ConfigAccessor(JavaPlugin plugin, String fileName)
    {
        if(plugin == null)
        {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        this.plugin = plugin;
        this.fileName = fileName;
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }
    
    public void reloadConfig()
    {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        
        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource(fileName);
        if(defConfigStream != null)
        {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            fileConfiguration.setDefaults(defConfig);
        }
    }
    
    public FileConfiguration getConfig()
    {
        if(fileConfiguration == null)
        {
            this.reloadConfig();
        }
        return fileConfiguration;
    }
    
    public void saveConfig()
    {
        if(fileConfiguration != null && configFile != null)
        {
            try
            {
                getConfig().save(configFile);
            }
            catch(IOException ex)
            {
                plugin.getLogger().log(Level.SEVERE, Strings.LOG_CUSTOM_CONFIG_LOAD_ERROR + configFile, ex);
            }
        }
    }
    
    public void saveDefaultConfig()
    {
        if(!configFile.exists())
        {
            this.plugin.saveResource(fileName, false);
        }
    }
    
}
