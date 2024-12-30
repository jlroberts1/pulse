/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import com.atproto.repo.StrongRef
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid

data class TimelineReference(
    val uri: AtUri,
    val cid: Cid,
)

fun StrongRef.toReference(): TimelineReference {
    return TimelineReference(
        uri = uri,
        cid = cid,
    )
}
