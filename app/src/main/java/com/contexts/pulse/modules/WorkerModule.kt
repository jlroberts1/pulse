/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.modules

import androidx.work.WorkerParameters
import com.contexts.pulse.domain.repository.PendingUploadRepository
import com.contexts.pulse.domain.repository.PostRepository
import com.contexts.pulse.worker.CreatePostWorker
import com.contexts.pulse.worker.UploadBlobWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule =
    module {
        worker {
            UploadBlobWorker(
                androidContext(),
                get<WorkerParameters>(),
            )
        }
        worker {
            CreatePostWorker(
                androidContext(),
                get<WorkerParameters>(),
                get<PostRepository>(),
                get<PendingUploadRepository>(),
            )
        }
    }
