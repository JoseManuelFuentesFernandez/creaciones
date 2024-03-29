package iestr.gag.enemigos.viewmodel

import android.annotation.SuppressLint
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

    @SuppressLint("StaticFieldLeak") //Necesario para que no leakee memoria por lo visto
    lateinit var context:Context

    init{
        recarga()
    }

    fun recarga(){
        viewModelScope.launch {
            try {
                _lista.clear()
                _lista.addAll(ClienteRetrofit.servicio.eligeTodos())
                _cambio.value=_cambio.value!!+1

                Toast.makeText(context,"Cargando orcos...",Toast.LENGTH_SHORT).show()
                if(lista.isEmpty()){
                    Toast.makeText(context,"No se han encontrado orcos",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Orcos cargados correctamente",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context,"Error en la carga de datos", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun insertaOrco(){
        viewModelScope.launch {
            try {
                // Como devuelve el Orco creado, lo meto en la lista con el id ya asignado por la restapi
                // Method	URL	    Payload	Response
                // POST	    /users	User	User
                _lista.add(ClienteRetrofit.servicio.insertaUno(Orco()))
                _cambio.value = _cambio.value!! + 1
            }catch (e: Exception) {
                Toast.makeText(context,"Error al insertar", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun modificaOrco(posicion:Int){
        viewModelScope.launch {
            try {
                val modificado = _lista[posicion]
                modificado.energia=max(0,modificado.energia-10)
                ClienteRetrofit.servicio.modificaUno(modificado.id,modificado)
                _cambio.value=_cambio.value!!+1
            }catch (e: Exception) {
                Toast.makeText(context,"Error al modificar", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun eliminaOrco(posicion:Int){
        viewModelScope.launch {
            try {
                val eliminado = _lista[posicion]
                _lista.removeAt(posicion)
                ClienteRetrofit.servicio.borraUno(eliminado.id)
                _cambio.value=_cambio.value!!+1
            }catch (e: Exception) {
                Toast.makeText(context,"Error al eliminar", Toast.LENGTH_LONG).show()
            }
        }
    }
}