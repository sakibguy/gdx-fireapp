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

package mk.gdx.firebase.html.database;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import mk.gdx.firebase.database.validators.ArgumentsValidator;
import mk.gdx.firebase.database.validators.ReadValueValidator;
import mk.gdx.firebase.html.firebase.ScriptRunner;
import mk.gdx.firebase.promises.ConverterPromise;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@PrepareForTest({ScriptRunner.class, DatabaseReference.class})
public class QueryReadValueTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(DatabaseReference.class);
        PowerMockito.mockStatic(ScriptRunner.class);
        PowerMockito.when(ScriptRunner.class, "firebaseScript", Mockito.any(Runnable.class)).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable) invocation.getArgument(0)).run();
                return null;
            }
        });
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void runJS() {
        // Given
        Database database = spy(Database.class);
        database.inReference("/test");
        QueryReadValue query = new QueryReadValue(database);
        ConverterPromise promise = mock(ConverterPromise.class);

        // When
        query.with(promise).withArgs(String.class).execute();

        // Then
        Assert.fail("Native method should be run");
    }

    @Test
    public void createArgumentsValidator() {
        // Given
        QueryReadValue query = new QueryReadValue(mock(Database.class));

        // When
        ArgumentsValidator argumentsValidator = query.createArgumentsValidator();

        // Then
        Assert.assertTrue(argumentsValidator instanceof ReadValueValidator);
    }
}