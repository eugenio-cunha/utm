package br.com.b256.feature.utm

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UtmViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {}
