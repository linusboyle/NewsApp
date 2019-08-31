/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.injection.component;

import com.example.newsclientapp.injection.module.FavoriteModule;
import com.example.newsclientapp.ui.fragment.FavoriteFragment;

import dagger.Component;

@Component(modules = FavoriteModule.class)
public interface FavoriteComponent {
	void inject (FavoriteFragment fragment);
}
