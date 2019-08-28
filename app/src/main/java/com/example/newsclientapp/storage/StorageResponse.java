/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.storage;

import java.util.List;

public class StorageResponse {
	private List<StorageEntity> storageEntities;

	public StorageResponse (List<StorageEntity> storageEntities) {
		this.storageEntities = storageEntities;
	}

	public List<StorageEntity> getStorageEntities () {
		return storageEntities;
	}
}
