package iestr.gag.enemigos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import iestr.gag.enemigos.model.Orco
import java.lang.Integer.max

class ListaViewModel: ViewModel() {
    private val _lista=mutableListOf<Orco>()
    val lista:List<Orco>
        get()=_lista
    private var _cambio=MutableLiveData(0)
    val cambio:LiveData<Int>
        get() = _cambio

    init{
        //_lista.addAll(OrcosProvider.generaLista(3))
        _cambio.value=_cambio.value!!+1
    }

    fun recarga(){
        _lista.clear()
        //_lista.addAll(OrcosProvider.generaLista(3))
        _cambio.value=_cambio.value!!+1
    }

    fun insertaOrco(){
       _lista.add(Orco())
        _cambio.value=_cambio.value!!+1
    }

    fun modificaOrco(posicion:Int){
        _lista[posicion].energia=max(0,lista[posicion].energia-10)
        _cambio.value=_cambio.value!!+1
    }

    fun eliminaOrco(posicion:Int){
        _lista.removeAt(posicion)
        _cambio.value=_cambio.value!!+1
    }
}