package iestr.gag.examen.view

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import iestr.gag.examen.R

class PreferenciasFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}