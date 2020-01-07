/*
 * Copyright 2018 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.mk5.gdx.fireapp.html.database;

import pl.mk5.gdx.fireapp.database.validators.ArgumentsValidator;

class QueryPush extends GwtDatabaseQuery<String> {

    QueryPush(Database databaseDistribution, String databasePath) {
        super(databaseDistribution, databasePath);
    }

    @Override
    protected void runJS() {
        databaseDistribution.inReference(push(databasePath));
    }

    @Override
    protected ArgumentsValidator createArgumentsValidator() {
        return null;
    }

    public static native String push(String reference) /*-{
        return $wnd.firebase.database().ref(reference).push().path["Q"].join("/");
    }-*/;
}
