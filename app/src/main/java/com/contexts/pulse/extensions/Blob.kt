/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.extensions

import sh.christian.ozone.api.model.Blob

fun Blob?.getRemoteLink(): String? {
    return when (this) {
        is Blob.LegacyBlob -> this.cid
        is Blob.StandardBlob -> this.ref.link.cid
        null -> null
    }
}
