package iestr.gag.examen.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import iestr.gag.examen.model.Arma
import iestr.gag.examen.model.Bicho
import iestr.gag.examen.model.BichosProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

class PartidaViewModel(application: Application) : AndroidViewModel(application) {
    //TextView barra superior
    private val _textoInformativo = MutableLiveData<String>()
    val textoInformativo: LiveData<String>
        get() = _textoInformativo

    //Lista de mensajes para que no se solapen
    private val _mensajes = MutableLiveData<List<String>>()
    val mensajes: LiveData<List<String>>
        get() = _mensajes

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
    
    /*init {
        recuperaLista()
    }*/

    /*private fun recuperaLista(){
        for(i in 1..4){
            nuevoBicho()
        }
    }*/

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
            //mostrarMensajeBarraSuperior("esquivado")
            viewModelScope.launch {
                _textoInformativo.value = "\"esquivado\""
                delay(2000)
                _textoInformativo.value = "Turno del jugador"
            }
        //Si no me esquiva, le haré daño: más o menos, dependiendo de si para el golpe o no
        }else{
            //La fuerza de mi golpe efectiva se ve afectada por la energía que tengo
            var fuerzaGolpe=(arma.ataque*_energia.value!!).toInt()
            //Si me para, le hago un 10% del daño que le haría si le doy de lleno
            if(Math.random()<bicho.paradaReal()){
                //mostrarMensajeBarraSuperior("parado")
                viewModelScope.launch {
                    _textoInformativo.value = "parado!!"
                    delay(2000)
                    _textoInformativo.value = "Turno del jugador"
                }
                fuerzaGolpe=(fuerzaGolpe/10f).toInt()
            }else {
                //mostrarMensajeBarraSuperior("le das de lleno")
                viewModelScope.launch {
                    _textoInformativo.value = "le das de lleno"
                    delay(2000)
                    _textoInformativo.value = "Turno del jugador"
                }
            }
            //La energía que quito al enemigo no puede ser más de la que tiene
            val efecto=Math.min(bicho.energia,fuerzaGolpe)
            bicho.energia-=efecto
            if(bicho.energia<=0) {//Si lo mato, gano 10 (sin pasar de 100) de energía y genero otro bicho
                _listaBichos.removeAt(pos)
                _energia.value = Math.min(_energia.value!! + 10, 100)
                nuevoBicho()
            }
            hayQueInformar=true
            //Sumo tantos puntos como energía le quito
            _puntos.value=_puntos.value!!+efecto
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
            //mostrarMensajeBarraSuperior("ESQUIVAO!!")
            viewModelScope.launch {
                _textoInformativo.value = "ESQUIVAO!!"
                delay(2000)
                _textoInformativo.value = "Turno del jugador"
            }
        //Si no lo esquivo, me hará más o menos daño, dependiendo de si paro o no
        }else {
            var impactoReal=bicho.fuerzaReal()
            val paradaReal= arma.parada*_energia.value!!/100
            //Si lo paro, me hace un 10% del daño que me haría si me da de lleno
            if(Math.random()<paradaReal){

                //mostrarMensajeBarraSuperior("PARAO!!")
                viewModelScope.launch {
                    _textoInformativo.value = "PARAO!!"
                    delay(2000)
                    _textoInformativo.value = "Turno del jugador"
                }
                impactoReal=(impactoReal/10f).toInt()
            }else{
                mostrarMensajeBarraSuperior("T'AN DAO!!")
                viewModelScope.launch {
                    _textoInformativo.value = "T'AN DAO!!"
                    delay(2000)
                    _textoInformativo.value = "Turno del jugador"
                }
            }
            _energia.value=Math.max(0,_energia.value!!-impactoReal)
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
                _puntos.value=_puntos.value!!+efecto
            }
        }
        for(b in listaBajas){
            _listaBichos.remove(b)
            _energia.value = min(_energia.value!! + 10, 100)
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


    //Mostrar mensajes en la barra superior
    fun mostrarMensajeBarraSuperior(mensaje: String) {

    }

}