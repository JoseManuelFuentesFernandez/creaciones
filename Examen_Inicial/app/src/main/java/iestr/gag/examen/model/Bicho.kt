package iestr.gag.examen.model

import java.lang.StringBuilder
import kotlin.random.Random

//@Entity(tableName = "bicho")
data class Bicho(
    //@PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo:Int=(0..3).random(),
    val nombre:String= nombreAleatorio(),
    val arma:Int=tipo,//Por defecto, a cada bicho le doy un tipo de arma
    var energia:Int=100,
    val heroeId: Int // ID del héroe al que pertenece este bicho
){
    fun esquiveReal()= Arma.values()[arma].esquive*energia/100//Es un porcentaje (entre 0 y 1)
    fun paradaReal()= Arma.values()[arma].parada*energia/100//Es un porcentaje (entre 0 y 1)
    fun fuerzaReal()=(Arma.values()[arma].ataque*energia).toInt()//Es un número(entre 0 y 100)

    companion object{
        const val MAX_ENERGIA=100
        private const val VOCALES = "aeiou"
        private const val CONSONANTES = "bcdfghjklmnpqrstvwxyz"

        private fun nombreAleatorio():String{//Entre 4 y 6 letras
            val nombre: StringBuilder = StringBuilder("")
            //Dos letras siempre
            if (Math.random() < 0.5) { //por añadir un poco de variedad a la estructura
                nombre.append(vocalAleatoria())
                nombre.append(consonanteAleatoria())
            } else {
                nombre.append(consonanteAleatoria())
                nombre.append(vocalAleatoria())
            }
            if(Random.nextInt(2)==1) nombre.append(vocalAleatoria())//quizás esta también
            if(Random.nextInt(2)==1)nombre.append(consonanteAleatoria())//quizás esta también
            //Otras dos letras siempre
            if (Math.random() < 0.5) { //por añadir un poco de variedad a la estructura
                nombre.append(vocalAleatoria())
                nombre.append(consonanteAleatoria())
            } else {
                nombre.append(consonanteAleatoria())
                nombre.append(vocalAleatoria())
            }
            return nombre.toString()
        }

        private fun vocalAleatoria(): String {
            val pos = (Math.random() * VOCALES.length).toInt()
            return VOCALES.substring(pos, pos + 1)
        }

        private fun consonanteAleatoria(): String {
            val pos = (Math.random() * CONSONANTES.length).toInt()
            return CONSONANTES.substring(pos, pos + 1)
        }

    }

}
