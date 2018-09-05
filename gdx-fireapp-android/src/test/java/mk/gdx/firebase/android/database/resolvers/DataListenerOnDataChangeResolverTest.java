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

package mk.gdx.firebase.android.database.resolvers;

import com.badlogic.gdx.utils.GdxNativesLoader;
import com.google.firebase.database.DataSnapshot;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.List;

import mk.gdx.firebase.android.AndroidContextTest;
import mk.gdx.firebase.database.pojos.OrderByClause;
import mk.gdx.firebase.listeners.DataChangeListener;

@PrepareForTest({GdxNativesLoader.class, DataSnapshotOrderByResolver.class})
public class DataListenerOnDataChangeResolverTest extends AndroidContextTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        PowerMockito.mockStatic(DataSnapshotOrderByResolver.class);
    }

    @Test
    public void resolve() {
        // Given
        Mockito.when(DataSnapshotOrderByResolver.shouldResolveOrderBy(
                Mockito.any(OrderByClause.class), Mockito.any(Class.class), Mockito.any(DataSnapshot.class)))
                .thenReturn(true);
        List list = Mockito.mock(List.class);
        Mockito.when(DataSnapshotOrderByResolver.resolve(Mockito.any(DataSnapshot.class))).thenReturn(list);
        DataChangeListener dataListener = Mockito.mock(DataChangeListener.class);

        // When
        DataListenerOnDataChangeResolver.resolve(String.class, Mockito.mock(OrderByClause.class), Mockito.mock(DataSnapshot.class), dataListener);

        // Then
        Mockito.verify(dataListener, VerificationModeFactory.times(1)).onChange(Mockito.refEq(list));
    }

    @Test
    public void resolve2() {
        // Given
        Mockito.when(DataSnapshotOrderByResolver.shouldResolveOrderBy(
                Mockito.any(OrderByClause.class), Mockito.any(Class.class), Mockito.any(DataSnapshot.class)))
                .thenReturn(false);
        DataChangeListener dataListener = Mockito.mock(DataChangeListener.class);
        DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot.getValue()).thenReturn("snapshot_value");

        // When
        DataListenerOnDataChangeResolver.resolve(String.class, Mockito.mock(OrderByClause.class), dataSnapshot, dataListener);

        // Then
        Mockito.verify(dataListener, VerificationModeFactory.times(1)).onChange(Mockito.eq("snapshot_value"));
    }
}