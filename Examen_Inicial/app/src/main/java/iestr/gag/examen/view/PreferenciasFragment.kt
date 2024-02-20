package iestr.gag.examen.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import iestr.gag.examen.R
import iestr.gag.examen.databinding.ButtonPreferencesBinding
import iestr.gag.examen.databinding.FragmentInicioBinding

class PreferenciasFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val buttonPreference = findPreference<Preference>("button_preference")
        buttonPreference?.setOnPreferenceClickListener {
            findNavController().navigate(PreferenciasFragmentDirections.actionPreferenciasFragmentToPartidaFragment())
            true
        }
    }
}