/*
 * Copyright 2014 Toxic Bakery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.commlib.weight.banner.transformer

import android.view.View

class RotateUpTransformer : ABaseTransformer() {
    override fun onTransform(view: View, position: Float) {
        val width = view.width.toFloat()
        val rotation = ROT_MOD * position
        view.pivotX = width * 0.5f
        view.pivotY = 0f
        view.translationX = 0f
        view.rotation = rotation
    }

    protected override val isPagingEnabled: Boolean
         get() = true

    companion object {
        private const val ROT_MOD = -15f
    }
}