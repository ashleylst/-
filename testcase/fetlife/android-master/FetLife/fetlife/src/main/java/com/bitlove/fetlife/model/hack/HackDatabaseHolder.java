package com.bitlove.fetlife.model.hack;

import com.bitlove.fetlife.FetLifeApplication;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.lang.reflect.Field;
import java.util.Map;

public class HackDatabaseHolder extends DatabaseHolder {

    private final DatabaseHolder base;
    private final String databaseName;

    public HackDatabaseHolder() throws Exception {
        this.base = (DatabaseHolder) Class.forName("com.raizlabs.android.dbflow.config.GeneratedDatabaseHolder").newInstance();
        this.databaseName = FetLifeApplication.getInstance().getUserSessionManager().getUserDbName();

        Field baseField = DatabaseHolder.class.getDeclaredField("databaseDefinitionMap");
        baseField.setAccessible(true);
        databaseDefinitionMap.clear();
        databaseDefinitionMap.putAll((Map<Class<?>, DatabaseDefinition>) baseField.get(base));

        baseField = DatabaseHolder.class.getDeclaredField("databaseNameMap");
        baseField.setAccessible(true);
        databaseNameMap.clear();
        databaseNameMap.putAll((Map<? extends String, ? extends DatabaseDefinition>) baseField.get(base));

        baseField = DatabaseHolder.class.getDeclaredField("databaseClassLookupMap");
        baseField.setAccessible(true);
        databaseClassLookupMap.clear();
        databaseClassLookupMap.putAll((Map<? extends Class<?>, ? extends DatabaseDefinition>) baseField.get(base));

        baseField = DatabaseHolder.class.getDeclaredField("typeConverters");
        baseField.setAccessible(true);
        typeConverters.clear();
        typeConverters.putAll((Map<? extends Class<?>, ? extends TypeConverter>) baseField.get(base));

        for (Map.Entry<Class<?>, DatabaseDefinition> databaseDefinitionEntry : databaseDefinitionMap.entrySet()) {
            Class<?> key = databaseDefinitionEntry.getKey();
            DatabaseDefinition value = databaseDefinitionEntry.getValue();
            if (value == null) {
                continue;
            }
            databaseDefinitionMap.put(key,new HackDatabaseDefinition(value,databaseName));
        }
    }
}
