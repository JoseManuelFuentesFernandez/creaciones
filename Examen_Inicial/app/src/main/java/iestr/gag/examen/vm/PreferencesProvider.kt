package iestr.gag.examen.vm

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import iestr.gag.examen.model.Arma

class PreferencesProvider(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("root_preferences", Context.MODE_PRIVATE)

    fun getNombreHeroe(defaultValue: String = "Heroe"): String {
        return sharedPreferences.getString("nombre", defaultValue) ?: defaultValue
    }

    fun getArmaHeroe(defaultValue: Arma = Arma.CUCHILLOS): Arma {
        val armaOrdinal = sharedPreferences.getString("arma", "0")?.toIntOrNull() ?: 0
        return Arma.values().getOrNull(armaOrdinal) ?: defaultValue
    }

    fun estaModoFacil(defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean("facil", defaultValue)
    }

    //TODO TIPOS
}
