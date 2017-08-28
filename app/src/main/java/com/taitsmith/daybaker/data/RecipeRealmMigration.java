package com.taitsmith.daybaker.data;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by tait on 8/28/17. */

public class RecipeRealmMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        if (oldVersion == 0) {
            RealmSchema schema = realm.getSchema();

            if (oldVersion == 0) {
                RealmObjectSchema realmObjectSchema = schema.get("Recipe");
                realmObjectSchema.addField("imageUrl", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.set("imageUrl", false);
                            }
                        });

                oldVersion++;
            }
        }
    }
}
