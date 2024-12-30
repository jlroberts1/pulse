/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid

data class Reference(
    val uri: AtUri,
    val cid: Cid,
)
