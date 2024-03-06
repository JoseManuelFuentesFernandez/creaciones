package iestr.gag.enemigos.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import iestr.gag.enemigos.model.ClienteRetrofit
import iestr.gag.enemigos.model.Orco
import iestr.gag.enemigos.model.ServicioRetrofit
import iestr.gag.enemigos.view.ListaFragment
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.Integer.max

class ListaViewModel: ViewModel() {
    private val _lista=mutableListOf<Orco>()
    val lista:List<Orco>
        get()=_lista
    private var _cambio=MutableLiveData(0)
    val cambio:LiveData<Int>
        get() = _cambio

    init{
        recarga()
    }

    fun recarga(){
        viewModelScope.launch {
            _lista.clear()
            _lista.addAll(ClienteRetrofit.servicio.eligeTodos())
            _cambio.value=_cambio.value!!+1
        }
    }

    fun insertaOrco(){
        viewModelScope.launch {
            //Como devuelve el Orco creado, lo meto en la lista con el id asignado en la restapi
            _lista.add(ClienteRetrofit.servicio.insertaUno(Orco()))
            _cambio.value=_cambio.value!!+1
        }
    }

    fun modificaOrco(posicion:Int){
        viewModelScope.launch {
            val modificado = _lista[posicion]
            modificado.energia=max(0,modificado.energia-10)
            ClienteRetrofit.servicio.modificaUno(modificado.id,modificado)
            _cambio.value=_cambio.value!!+1
        }
    }

    fun eliminaOrco(posicion:Int){
        viewModelScope.launch {
            val eliminado = _lista[posicion]
            _lista.removeAt(posicion)
            ClienteRetrofit.servicio.borraUno(eliminado.id)
            _cambio.value=_cambio.value!!+1
        }
    }
}