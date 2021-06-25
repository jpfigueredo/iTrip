package br.edu.infnet.itrip.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.edu.infnet.itrip.R
import br.edu.infnet.luis_barbosa_dr4_at.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_my_trips.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { act ->
            userViewModel = ViewModelProviders.of(act)
                .get(UserViewModel::class.java)
        }

        fillUserData()
        setupListeners()
    }

    private fun fillUserData() {
        userViewModel.name.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                et_name_profile.setText(it.toString())
            }
        })
        userViewModel.email.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                et_email_profile.setText(it.toString())
            }
        })
    }

    private fun setupListeners() {
        change_password_layout.setOnClickListener {
            val email = et_email_profile.text.toString()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(context, getString(R.string.enter_your_email), Toast.LENGTH_LONG).show()
            }
        }
        btn_save_changes.setOnClickListener {
            val email = et_name_profile.text.toString()
            val nome = et_name_profile.text.toString()
            if (email.isNotEmpty() && nome.isNotEmpty()){
                updateNameInFirebase()
                updateEmailInFirebase()
            } else {
                Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, getString(R.string.check_your_email), Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateNameInFirebase() {
        val profileUpdates = userProfileChangeRequest {
            displayName = et_name_profile.text.toString()
        }
        val user = auth.currentUser
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userViewModel.name.value = auth.currentUser!!.displayName.toString()
                    Toast.makeText(context, getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateEmailInFirebase() {
        val user = auth.currentUser
        val email = et_email_profile.text.toString()
        user!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userViewModel.email.value = auth.currentUser!!.email.toString()
                    Toast.makeText(context, getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater!!.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_signOut -> {
                auth.signOut()
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment, null)
            }
        }
        return false
    }

}