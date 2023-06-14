package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Identity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityRegister1Binding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.GsonBuilder

class Register1Activity : AppCompatActivity(), View.OnClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private lateinit var functions: FirebaseFunctions
    private lateinit var binding:ActivityRegister1Binding
    private var countAddress : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance()
        db = FirebaseFirestore.getInstance()

        supportActionBar?.hide()

        SecurityPreferences(this).storeString(Constants.KEY_SHARED.FT_PERFIL, "")

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.second))
        binding.imageArrowNext.setColorFilter(ContextCompat.getColor(this, R.color.second))

        binding.btnAvancarRegister.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)
        binding.addAddressButton.setOnClickListener(this)
    }

    private fun userRegister() {
        if (binding.editNomeRegister.text.toString().trim().isEmpty()) {
            binding.editNomeRegister.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if (binding.editEmailRegister.text.toString().trim().isEmpty()) {
            binding.editEmailRegister.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if (binding.editSenhaRegister.text.toString().trim().isEmpty()) {
            binding.editSenhaRegister.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if (binding.editSenhaRegister.text.toString().trim().length < 8) {
            binding.editSenhaRegister.error = Constants.PHRASE.MIN_LENGHT
            return
        }
        if (binding.editTelefoneRegister.text.toString().trim().isEmpty()) {
            binding.editTelefoneRegister.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if(countAddress == 0){
            Toast.makeText(this, Constants.PHRASE.NO_ADDRESS, Toast.LENGTH_SHORT).show()
            return
        }

        SecurityPreferences(this).storeString(Constants.KEY_SHARED.EMAIL_REGISTER, binding.editEmailRegister.text.toString().trim().lowercase())
        SecurityPreferences(this).storeString(Constants.KEY_SHARED.PASSWORD_REGISTER, binding.editSenhaRegister.text.toString().trim().lowercase())
        SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHONE_NUMBER_REGISTER, binding.editTelefoneRegister.text.toString().trim().lowercase())
        SecurityPreferences(this).storeString(Constants.KEY_SHARED.NAME_REGISTER, binding.editNomeRegister.text.toString().trim().lowercase())

        startActivity(Intent(this, PhotoRegisterActivity::class.java))
    }

    private fun verifyAddress():Boolean{
        if(binding.addressName.text.toString().trim().isEmpty()){
            binding.addressName.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressStreet.text.toString().trim().isEmpty()){
            binding.addressStreet.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressNumber.text.toString().trim().isEmpty()){
            binding.addressNumber.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressBairro.text.toString().trim().isEmpty()){
            binding.addressBairro.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressCep.text.toString().trim().isEmpty()){
            binding.addressCep.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressCep.text.toString().trim().length != 8){
            binding.addressCep.error = Constants.PHRASE.MIN_LENGHT
            return false
        }
        if(binding.addressCidade.text.toString().trim().isEmpty()){
            binding.addressCidade.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressEstado.text.toString().trim().isEmpty()){
            binding.addressEstado.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        return true
    }

    private fun addAddress(){
        if(!verifyAddress()) return
        val address: String = binding.addressName.text.toString().trim().lowercase() + "," +
                binding.addressStreet.text.toString().trim().lowercase() + "," +
                binding.addressNumber.text.toString().trim().lowercase() + "," +
                binding.addressBairro.text.toString().trim().lowercase() + "," +
                binding.addressCep.text.toString().trim().lowercase() + "," +
                binding.addressCidade.text.toString().trim().lowercase() + "," +
                binding.addressEstado.text.toString().trim().lowercase()

        when(countAddress) {
            0 -> SecurityPreferences(this).storeString(Constants.KEY_SHARED.ADDRESS_1_REGISTER, address)
            1 -> SecurityPreferences(this).storeString(Constants.KEY_SHARED.ADDRESS_2_REGISTER, address)
            2 -> SecurityPreferences(this).storeString(Constants.KEY_SHARED.ADDRESS_3_REGISTER, address)
        }

        val additionalAddressesContainer = findViewById<LinearLayout>(R.id.additional_addresses_container)
        val newAddressTextView = TextView(this)

        newAddressTextView.layoutParams = LinearLayout.LayoutParams(
            0,
            100,
            1f
        )
        newAddressTextView.hint = getString(R.string.endereço)
        newAddressTextView.minHeight = resources.getDimensionPixelSize(R.dimen.text_field_size)
        newAddressTextView.text = binding.addressName.text.toString().trim()
        newAddressTextView.textSize = 24f
        newAddressTextView.setPadding(10, 8, 10, 8)
        newAddressTextView.setTextColor(ContextCompat.getColorStateList(this, R.color.purple_500))
        newAddressTextView.id = View.generateViewId()

        additionalAddressesContainer.addView(newAddressTextView)
        countAddress += 1

        binding.addressName.setText("")
        binding.addressStreet.setText("")
        binding.addressNumber.setText("")
        binding.addressBairro.setText("")
        binding.addressCep.setText("")
        binding.addressCidade.setText("")
        binding.addressEstado.setText("")
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_avancar_register -> userRegister()
            R.id.add_address_button -> {
                if(countAddress < 3) addAddress()
                else Snackbar.make(binding.root, Constants.PHRASE.LIMIT_ADDRESS, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.rgb(229,0,37))
                    .show()
            }
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
        }
    }
}