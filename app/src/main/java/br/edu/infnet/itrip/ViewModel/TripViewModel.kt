package br.edu.infnet.itrip.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.edu.infnet.itrip.Model.Trip

class TripViewModel : ViewModel() {
    val tripVM = MutableLiveData<Trip?>()
}