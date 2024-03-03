package es.ua.eps.filmoteca

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import android.Manifest
import android.content.Context
import android.os.Build
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import es.ua.eps.filmoteca.databinding.ActivityFilmSignBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.tasks.OnCompleteListener


@Suppress("DEPRECATION")
class FilmSignActivity : AppCompatActivity() {


    companion object {
        private const val RC_SIGN_IN = 9001
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 123
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityFilmSignBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding = ActivityFilmSignBinding.inflate(layoutInflater)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null){
            val intent = Intent(this@FilmSignActivity, MainActivity::class.java)
            startActivity(intent)
        }

        with(binding) {
            setContentView(root)
            signButton.setOnClickListener { signIn() }
        }

        //Request the notification permission for FCM
        requestNotificationPermission()
        notification()
    }

    private fun notification(){
        Log.d("TAG", "Entra")
        //Identify the user token for specific message
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                Log.d("TAG", "FCM registration Token: $token")
            })
        //Subscribe for Topics messages from FCM
        FirebaseMessaging.getInstance().subscribeToTopic("best")
    }


    //---------------------------------
    private fun signIn() {
        val isGooglePlayServicesAvailable = checkGooglePlayServices(applicationContext)
        if (isGooglePlayServicesAvailable) {
            /* Initial configuration */
            val gso: GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                handleSignInResult(task)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                Log.d("GPS", "SingInResult Failed")
            }
        } catch (e: ApiException) {
            Log.e("TAG", e.toString())
        }
    }


    /* Check google disponibility*/
    private fun checkGooglePlayServices(context: Context?): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(context!!)
        if (result != ConnectionResult.SUCCESS) {
            Log.d("GPS", "Google Play Services not available")
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST)!!.show()
            } else {
                Log.d("GPS", "This device is not supported")
            }
            return false
        }
        return true
    }

    //---------------------------------
    private fun requestNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if(!hasPermission){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}