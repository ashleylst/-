package com.bitlove.fetlife.model.hack;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.sql.migration.Migration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.ModelViewAdapter;
import com.raizlabs.android.dbflow.structure.QueryModelAdapter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class HackDatabaseDefinition extends DatabaseDefinition {

    private final DatabaseDefinition base;
    private final String databaseName;

    public HackDatabaseDefinition(DatabaseDefinition databaseDefinition, String databaseName) throws Exception {
        base = databaseDefinition;
        this.databaseName = databaseName;

        Field baseField = DatabaseDefinition.class.getDeclaredField("migrationMap");
        baseField.setAccessible(true);
        Map<Integer, List<Migration>> hackMigrationMap = (Map<Integer, List<Migration>>) baseField.get(this);
        hackMigrationMap.putAll((Map<Integer, List<Migration>>) baseField.get(base));

        baseField = DatabaseDefinition.class.getDeclaredField("models");
        baseField.setAccessible(true);
        List<Class<?>> hackModels = (List<Class<?>>) baseField.get(this);
        hackModels.addAll((List<Class<?>>) baseField.get(base));

        baseField = DatabaseDefinition.class.getDeclaredField("modelAdapters");
        baseField.setAccessible(true);
        Map<Class<?>, ModelAdapter> hackModelAdapters = (Map<Class<?>, ModelAdapter>) baseField.get(this);
        hackModelAdapters.putAll((Map<Class<?>, ModelAdapter>) baseField.get(base));

        baseField = DatabaseDefinition.class.getDeclaredField("modelTableNames");
        baseField.setAccessible(true);
        Map<String, Class<?>> hackModelTableNames = (Map<String, Class<?>>) baseField.get(this);
        hackModelTableNames.putAll((Map<String, Class<?>>) baseField.get(base));

        baseField = DatabaseDefinition.class.getDeclaredField("modelViews");
        baseField.setAccessible(true);
        List<Class<?>> hackModelViews = (List<Class<?>>) baseField.get(this);
        hackModelViews.addAll((List<Class<?>>) baseField.get(base));

        baseField = DatabaseDefinition.class.getDeclaredField("modelViewAdapterMap");
        baseField.setAccessible(true);
        Map<Class<?>, ModelViewAdapter> hackModelViewAdapterMap = (Map<Class<?>, ModelViewAdapter>) baseField.get(this);
        hackModelViewAdapterMap.putAll((Map<Class<?>, ModelViewAdapter>) baseField.get(base));

        baseField = DatabaseDefinition.class.getDeclaredField("queryModelAdapterMap");
        baseField.setAccessible(true);
        Map<Class<?>, QueryModelAdapter> hackQueryModelAdapter = (Map<Class<?>, QueryModelAdapter>) baseField.get(this);
        hackQueryModelAdapter.putAll((Map<Class<?>, QueryModelAdapter>) baseField.get(base));
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public boolean isInMemory() {
        return base.isInMemory();
    }

    @Override
    public int getDatabaseVersion() {
        return base.getDatabaseVersion();
    }

    @Override
    public boolean areConsistencyChecksEnabled() {
        return base.areConsistencyChecksEnabled();
    }

    @Override
    public boolean isForeignKeysSupported() {
        return base.isForeignKeysSupported();
    }

    @Override
    public boolean backupEnabled() {
        return false;
    }

    @Override
    public Class<?> getAssociatedDatabaseClassFile() {
        return FetLifeDatabase.class;
    }
}
