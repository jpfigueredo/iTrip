package br.edu.infnet.luis_barbosa_dr4_at.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.DateFormat
import java.util.*

class UserViewModel : ViewModel() {
    val name = MutableLiveData<String>().apply { value = "" }
    val email = MutableLiveData<String>().apply { value = "" }
    val currentDate = MutableLiveData<String>().apply { value = getDate() }


    private fun getDate(): String {
        val date = Calendar.getInstance().time
        return DateFormat.getDateInstance(DateFormat.LONG).format(date)
    }

}