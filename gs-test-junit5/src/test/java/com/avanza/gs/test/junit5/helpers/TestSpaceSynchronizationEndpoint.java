/*
 * Copyright 2017 Avanza Bank AB
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
 */
package com.avanza.gs.test.junit5.helpers;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.OperationsBatchData;
import com.gigaspaces.sync.SpaceSynchronizationEndpoint;

public class TestSpaceSynchronizationEndpoint extends SpaceSynchronizationEndpoint {

	private final List<DataSyncOperation> dataSyncOperations = new ArrayList<>();

	@Override
	public void onOperationsBatchSynchronization(OperationsBatchData batchData) {
		dataSyncOperations.addAll(List.of(batchData.getBatchDataItems()));
	}

	public List<DataSyncOperation> getDataSyncOperations() {
		return unmodifiableList(dataSyncOperations);
	}
}
