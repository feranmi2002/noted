package com.faithdeveloper.noted

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.faithdeveloper.noted.ui.fragments.OnboardingFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        setContentView(R.layout.activity_main)
        checkIfUserExists(auth.currentUser)
    }

    private fun checkIfUserExists(currentUser: FirebaseUser?) =
        if (currentUser != null && currentUser.isEmailVerified) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            navController = navHostFragment.navController
            navController.navigate(OnboardingFragmentDirections.actionOnboardingFragmentToNotesFragment())
        } else {
//         do nothing
        }

    override fun onResume() {
        super.onResume()
    }
}