package com.faridchavez.ayudacampus.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faridchavez.ayudacampus.AyudaApp


class AyudaViewModelFactory(private val crear: () -> ViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = crear() as T
}


@Composable
inline fun <reified VM : ViewModel> ayudaViewModel(
    clave: String? = null,
    noinline crear: (AyudaApp) -> VM
): VM {
    val app = LocalContext.current.applicationContext as AyudaApp
    return viewModel(key = clave, factory = AyudaViewModelFactory { crear(app) })
}