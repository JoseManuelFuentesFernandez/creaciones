package iestr.gag.examen.view

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import iestr.gag.examen.R
import iestr.gag.examen.vm.PartidaViewModel

class PreferenciasFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val buttonPreference = findPreference<Preference>("button_preference")
        buttonPreference?.let {
            it.setOnPreferenceClickListener {
                findNavController().navigate(PreferenciasFragmentDirections.actionPreferenciasFragmentToPartidaFragment())
                true
            }
        }
    }
}