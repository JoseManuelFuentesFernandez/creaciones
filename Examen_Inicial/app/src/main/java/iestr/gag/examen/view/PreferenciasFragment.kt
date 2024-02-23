package iestr.gag.examen.view

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import iestr.gag.examen.R

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