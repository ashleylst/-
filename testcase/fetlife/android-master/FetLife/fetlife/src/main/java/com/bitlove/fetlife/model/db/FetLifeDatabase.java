/*
 * Copyright (C) 2015 The Android Open Source Project
 *
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
 *
 */

package com.bitlove.fetlife.model.db;

import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

@Database(name = FetLifeDatabase.NAME, version = FetLifeDatabase.VERSION)
public class FetLifeDatabase {

    public static final String NAME = "fetlife";

    //Simple increase the version number in case of new tables
    public static final int VERSION = 31;

    //Add new Migration classes in case of table structure change
    @Migration(version = 26, database = FetLifeDatabase.class)
    public static class Migration26 extends AlterTableMigration<Conversation> {

        public Migration26(Class<Conversation> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "draftMessage");
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 27, database = FetLifeDatabase.class)
    public static class Migration27 extends BaseMigration {
        @Override
        public void migrate(DatabaseWrapper database) {
        }
    }

    //Add new Migration classes in case of table structure change
    @Migration(version = 28, database = FetLifeDatabase.class)
    public static class Migration28 extends AlterTableMigration<FriendRequest> {

        public Migration28(Class<FriendRequest> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "targetMemberId");
        }
    }


}
