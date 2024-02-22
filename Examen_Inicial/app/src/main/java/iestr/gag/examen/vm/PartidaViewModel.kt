package iestr.gag.examen.vm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import iestr.gag.examen.R
import iestr.gag.examen.model.Arma
import iestr.gag.examen.model.Bicho
import iestr.gag.examen.model.BichosProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

class PartidaViewModel(application: Application) : AndroidViewModel(application) {
    //Preferencias
    // TODO FIX sharedPreferences NULL
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("root_preferences", Context.MODE_PRIVATE)

    //TextView barra superior
    private val _textoInformativo = MutableLiveData<String>()
    val textoInformativo: LiveData<String>
        get() = _textoInformativo

    private val _mensajesBarraSuperior = MutableLiveData<List<String>>(emptyList())
    val mensajesBarraSuperior: LiveData<List<String>>
        get() = _mensajesBarraSuperior

    //Información del héroe
    private var _energia=MutableLiveData<Int>(100)
    val energia:LiveData<Int>
        get()=_energia
    private val _puntos=MutableLiveData<Int>(0)
    val puntos:LiveData<Int>
        get()=_puntos
    //Flag para cambiar turno entre elprota y los enemigos
    private var _turnoHeroe=MutableLiveData<Boolean>(true)
    val turnoHeroe:LiveData<Boolean>
        get()=_turnoHeroe
    //el héroe tendrá una de entre cuatro armas (ver enum class Arma)
    val arma=Arma.values()[(0..3).random()]

    //Datos de los enemigos
    private var _listaBichos=BichosProvider.getLista()
    val listaBichos:List<Bicho>
        get()=_listaBichos
    //Para ver si ha habido algún cambio en los bichos
    private var _cambio=MutableLiveData<Int>(1)
    val cambio:LiveData<Int>
        get()=_cambio

    //Para saber cuándo genero un enemigo extra:
    private var ataques=0
    
    init {
        applyPreferences()
    }

    private fun applyPreferences() {
        val nombreHeroe = sharedPreferences.getString("nombre", "Heroe") ?: "Heroe"

        val armaOrdinal = sharedPreferences.getString("arma", "0")?.toIntOrNull() ?: 0
        val arma = Arma.values().getOrNull(armaOrdinal) ?: Arma.CUCHILLOS

        val esFacil = sharedPreferences.getBoolean("facil", false)
        if (esFacil) {
            recuperaLista(2)
        }else{
            recuperaLista(4)
        }

        val permitirVampiros = sharedPreferences.getBoolean(R.string.vampiros_preference.toString(), true)
        val permitirDemonios = sharedPreferences.getBoolean(R.string.demonios_preferences.toString(), true)
        val permitirOrcos = sharedPreferences.getBoolean(R.string.orcos_preferences.toString(), true)
        val permitirTrolls = sharedPreferences.getBoolean(R.string.trolls_preferences.toString(), true)
    }

    private fun recuperaLista(nEnemigos:Int){
        _listaBichos.clear()
        for(i in 1..nEnemigos){
            nuevoBicho()
        }
    }

    private fun nuevoBicho(){
        _listaBichos.add(BichosProvider.getUno())
    }

    public fun ProtaAtacaBicho(pos:Int){
        if(!_turnoHeroe.value!!)
            return
        val bicho=_listaBichos[pos]
        var hayQueInformar=false//¿Hay que informar de cambios en la lista tras el ataque?
        //Si me esquiva, no le hago nada
        if(Math.random()<bicho.esquiveReal()){
            mostrarMensajeBarraSuperior("esquivado")
        //Si no me esquiva, le haré daño: más o menos, dependiendo de si para el golpe o no
        }else{
            //La fuerza de mi golpe efectiva se ve afectada por la energía que tengo
            var fuerzaGolpe=(arma.ataque*_energia.value!!).toInt()
            //Si me para, le hago un 10% del daño que le haría si le doy de lleno
            if(Math.random()<bicho.paradaReal()){
                mostrarMensajeBarraSuperior("parado")
                fuerzaGolpe=(fuerzaGolpe/10f).toInt()
            }else {
                mostrarMensajeBarraSuperior("le das de lleno")
            }
            //La energía que quito al enemigo no puede ser más de la que tiene
            val efecto=Math.min(bicho.energia,fuerzaGolpe)
            bicho.energia-=efecto
            if(bicho.energia<=0) {//Si lo mato, gano 10 (sin pasar de 100) de energía y genero otro bicho
                _listaBichos.removeAt(pos)
                actualizacionEnergiaHeroe(Math.min(_energia.value!! + 10, 100))
                nuevoBicho()
            }
            hayQueInformar=true
            //Sumo tantos puntos como energía le quito
            actualizacionPuntosHeroe(_puntos.value!!+efecto)
        }
        finalizaAtaque(hayQueInformar)
    }

    private fun cambiaTurno(){
        _turnoHeroe.value=!(_turnoHeroe.value!!)
    }

    private fun informaCambio(){
        _cambio.value=_cambio.value!!*(-1)
    }

    public fun bichoAtacaProta(pos:Int){
        val bicho=_listaBichos[pos]
        mostrarMensajeBarraSuperior("${bicho.nombre} atacando")
        //Si esquivo el golpe, no me hace nada
        val esquiveReal= arma.esquive*_energia.value!!/100
        if(Math.random()<esquiveReal){
            mostrarMensajeBarraSuperior("ESQUIVAO!!")
        //Si no lo esquivo, me hará más o menos daño, dependiendo de si paro o no
        }else {
            var impactoReal=bicho.fuerzaReal()
            val paradaReal= arma.parada*_energia.value!!/100
            //Si lo paro, me hace un 10% del daño que me haría si me da de lleno
            if(Math.random()<paradaReal){
                mostrarMensajeBarraSuperior("PARAO!!")
                impactoReal=(impactoReal/10f).toInt()
            }else{
                mostrarMensajeBarraSuperior("T'AN DAO!!")
            }
            actualizacionEnergiaHeroe(Math.max(0,_energia.value!!-impactoReal))
        }
        cambiaTurno()
    }

    public fun repara(){
        _energia.value=Math.min(100,_energia.value!!+50)
        cambiaTurno()
    }

    public fun mele(){//Reparto mi ataque entre todos los enemigos
        //Repartimos la fuerza entre todos
        var fuerzaGolpe=(arma.ataque*_energia.value!!/listaBichos.size).toInt()
        var hayQueInformar=false//¿Hay que informar de cambios en la lista tras el ataque?
        val listaBajas= mutableListOf<Bicho>()
        for(b in listaBichos){
            if(Math.random()>b.esquiveReal()){
                if(Math.random()<b.paradaReal()){
                    fuerzaGolpe=(fuerzaGolpe/10f).toInt()
                }
                val efecto= min(b.energia,fuerzaGolpe)
                b.energia-=efecto
                if(b.energia<=0) {
                    listaBajas.add(b)
                }
                hayQueInformar=true
                actualizacionPuntosHeroe(_puntos.value!!+efecto)
            }
        }
        for(b in listaBajas){
            _listaBichos.remove(b)
            actualizacionEnergiaHeroe(min(_energia.value!! + 10, 100))
            nuevoBicho()
        }
        finalizaAtaque(hayQueInformar)
    }

    fun finalizaAtaque(hqi:Boolean){
        var hayQueInformar=hqi
        //Cuando has acumulado un cierto número de ataques, aparece un enemigo adicional
        ataques++
        if(ataques>=3){
            ataques=0
            nuevoBicho()
            hayQueInformar=true
        }
        //Si hace falta (debido a cambios en el listado) informamos de los cambios a reflejar en el adapter
        if(hayQueInformar) informaCambio()
        cambiaTurno()
    }

    //Animación energía héroe
    fun actualizacionEnergiaHeroe(nuevaEnergia: Int) {
        viewModelScope.launch {
            val puntosActuales = energia.value ?: 0
            val paso = if (nuevaEnergia > puntosActuales) 1 else -1
            var actual = puntosActuales

            while (actual != nuevaEnergia) {
                actual += paso
                _energia.value = actual
                delay(30)
            }
        }
    }

    //Animación puntos héroe
    fun actualizacionPuntosHeroe(nuevosPuntos: Int) {
        viewModelScope.launch {
            val puntosActuales = puntos.value ?: 0
            val paso = if (nuevosPuntos > puntosActuales) 1 else -1
            var actual = puntosActuales

            while (actual != nuevosPuntos) {
                actual += paso
                _puntos.value = actual
                delay(50)
            }
        }
    }

    //Animación mensajes barra superior
    fun mostrarMensajeBarraSuperior(texto: String) {
        // Agrega el nuevo mensaje a la lista de mensajes
        val mensajesActuales = _mensajesBarraSuperior.value ?: emptyList()
        val nuevosMensajes = mensajesActuales.toMutableList()
        nuevosMensajes.add(texto)
        _mensajesBarraSuperior.value = nuevosMensajes

        // Solo inicia el cambio de texto si no hay uno en curso
        if (mensajesActuales.isEmpty()) {
            animarMensajesBarraSuperior()
        }
    }

    private fun animarMensajesBarraSuperior() {
        viewModelScope.launch {
            delay(2)
            // Muestra los mensajes uno por uno
            _mensajesBarraSuperior.value?.forEach { mensaje ->
                _textoInformativo.value = mensaje
                delay(2000)
            }

            // Vacía la lista de mensajes después de mostrarlos todos
            _mensajesBarraSuperior.value = emptyList()
            _textoInformativo.value = "Turno del jugador"
        }
    }
}