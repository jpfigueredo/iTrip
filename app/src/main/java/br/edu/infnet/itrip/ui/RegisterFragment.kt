package br.edu.infnet.itrip.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.edu.infnet.itrip.Model.User
import br.edu.infnet.itrip.R
import br.edu.infnet.luis_barbosa_dr4_at.viewModel.UserViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private var firestoreDB: FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        firestoreDB = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
                act -> userViewModel = ViewModelProviders.of(act)
            .get(UserViewModel::class.java)
        }

        setUpListeners()
    }

    private fun setUpListeners() {
        val emailInput = et_email_register.text
        val passwordInput = et_password_register.text

        btn_sign_up.setOnClickListener {
            if (checkPassword()) {
                if (emailInput!!.isNotEmpty() && passwordInput!!.isNotEmpty()) {
                    doRegister()
                } else {
                    Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show()
            }
        }

        layout_have_account.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_signInFragment, null)
        }
    }

    private fun checkPassword(): Boolean {
        val passwordInput = et_password_register.text.toString()
        val confirmInput = et_confirm_password_register.text.toString()
        var flag = true

        if (passwordInput != confirmInput) {
            flag = false
        }
        if(passwordInput.isEmpty() || confirmInput.isEmpty()){
            flag = false
        }
        return flag
    }

    private fun doRegister() {
        val emailInput = et_email_register.text
        val passwordInput = et_password_register.text

        auth.createUserWithEmailAndPassword(emailInput.toString(), passwordInput.toString())
            .addOnSuccessListener {
                val name = et_name_register.text.toString()
                val user = auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            userViewModel.email.value = auth.currentUser!!.email.toString()
                            addUserFirebase(user.uid, user.displayName!!, user.email!!)
                            Toast.makeText(context, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                        }
                    }
                findNavController().navigate(R.id.action_registerFragment_to_signInFragment, null)
            }
            .addOnFailureListener {
                when (it) {
                    is FirebaseAuthWeakPasswordException -> Toast.makeText(context, "  Password length error", Toast.LENGTH_SHORT).show()
                    is FirebaseAuthUserCollisionException -> Toast.makeText(context, "E-mail already registered", Toast.LENGTH_SHORT).show()
                    is FirebaseNetworkException -> Toast.makeText(context, " Internet error", Toast.LENGTH_SHORT).show()
                    else ->  Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserFirebase(id: String, name: String, email: String){
        val user = User(id, name, email)

        firestoreDB!!.collection("Users").document(id)
            .set(user.toMap())
            .addOnSuccessListener {
                Toast.makeText(context, "User has been added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "User could not be added!", Toast.LENGTH_SHORT).show()
            }

    }
}