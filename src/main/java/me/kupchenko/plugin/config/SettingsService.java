package me.kupchenko.plugin.config;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;
import me.kupchenko.plugin.exception.ConfigSavingException;
import me.kupchenko.plugin.model.PluginConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.lang.reflect.Field;

@Slf4j
@Named("settingsManager")
public class SettingsService {

    private final PluginSettings pluginSettings;

    @Autowired
    public SettingsService(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    /**
     * Provides mechanism for saving given PluginConfig using reflection.
     *
     * @param pluginConfig - parsed user request
     * @throws ConfigSavingException occurs if IllegalAccessException is thrown
     */
    public void updateConfig(PluginConfig pluginConfig) throws ConfigSavingException {
        try {
            Field[] declaredFields = PluginConfig.class.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object value = declaredField.get(pluginConfig);
                String fieldName = declaredField.getName();
                setProperty(fieldName, value);
            }
        } catch (IllegalAccessException e) {
            log.error("Error while saving plugin config", e);
            throw new ConfigSavingException("Error while saving plugin config");
        }
    }

    /**
     * Provides mechanism for converting application settings into PluginConfig using reflection.
     *
     * @return PluginConfig - saved settings
     * @throws ConfigSavingException occurs if IllegalAccessException is thrown
     */
    public PluginConfig getConfig() throws ConfigSavingException {
        try {
            PluginConfig pluginConfig = new PluginConfig();
            Field[] declaredFields = PluginConfig.class.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                String fieldName = declaredField.getName();
                Object property = getProperty(fieldName);
                declaredField.set(pluginConfig, property);
            }
            return pluginConfig;
        } catch (IllegalAccessException e) {
            log.error("Error while saving plugin config", e);
            throw new ConfigSavingException("Error while saving plugin config");
        }

    }

    private void setProperty(String propertyName, Object propertyValue) {
        pluginSettings.put(propertyName, propertyValue);
    }

    private Object getProperty(String propertyName) {
        return pluginSettings.get(propertyName);
    }
}
