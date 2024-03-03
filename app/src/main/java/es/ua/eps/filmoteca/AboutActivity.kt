package es.ua.eps.filmoteca

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initUI(){
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        with(binding){
            setContentView(root)
            goWeb.setOnClickListener{
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"))
                if(webIntent.resolveActivity(packageManager) != null){
                    startActivity(webIntent)
                }
            }
            goSupport.setOnClickListener{
                val supportIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:1195562121ende@gmail.com"))
                if(supportIntent.resolveActivity(packageManager) != null){
                    startActivity(supportIntent)
                }
            }
            goBack.setOnClickListener {
                finish()
            }

            if(account != null){
                userId.text = getString(R.string.user_id_format, account.id)
                userName.text = getString(R.string.user_name_format, account.displayName)
                userEmail.text = getString(R.string.user_email_format, account.email)
                Glide.with(applicationContext).load(account.photoUrl).into(userImage)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        //special id for the home button
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this,
                Intent(this, FilmListFragment::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}