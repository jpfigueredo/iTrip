package br.edu.infnet.itrip.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.edu.infnet.itrip.R
import br.edu.infnet.luis_barbosa_dr4_at.viewModel.UserViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private lateinit var callbackManager: CallbackManager
    private var mUser: FirebaseUser? = null
    private val TAG = "SignInFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
                act -> userViewModel = ViewModelProviders.of(act)
            .get(UserViewModel::class.java)
        }

        setUpListeners()
        fillUserData()
        setUpWidgets()
    }

    private fun setUpListeners() {

        login_facebook_button.setReadPermissions("email", "public_profile")
        login_facebook_button.fragment = this
        signInFacebook(login_facebook_button)

        btn_sign_in.setOnClickListener {
            val email = et_email_login.text.toString()
            val password = et_password_login.text.toString()
            //findNavController().navigate(R.id.action_signInFragment_to_myTripsFragment, null)

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword()
            } else {
                Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
        layout_dont_have_account.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_registerFragment, null)
        }

        tv_forgot_password.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment, null)
        }
    }

    private fun fillUserData() {
        userViewModel.email.observe(viewLifecycleOwner, Observer {
            if(it != null){
                et_email_login.setText(it.toString())
            }
        })
    }

    private fun signInWithEmailAndPassword() {
        val email = et_email_login.text.toString()
        val password = et_password_login.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { taskSnapshot ->
                    updateUI()
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.authentication_failure), Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun signInFacebook(_login_facebook_button: LoginButton) {
        _login_facebook_button.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                handleFacebookAccessToken(loginResult!!.accessToken)
                Log.d(TAG, "facebook:onSuccess:$loginResult")
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")

            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)

            }
        })
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                if (it != null) {
                    mUser = auth.currentUser
                    updateUI()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.authentication_failure), Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                updateUI()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun updateUI(){
        if (auth.currentUser != null) {
            userViewModel.email.value = auth.currentUser!!.email.toString()
            userViewModel.name.value = auth.currentUser!!.displayName.toString()
            findNavController().navigate(R.id.action_signInFragment_to_myTripsFragment, null)
        }
    }

    private fun setUpWidgets() {
        activity?.toolbar_layout!!.visibility = View.GONE
    }

}
