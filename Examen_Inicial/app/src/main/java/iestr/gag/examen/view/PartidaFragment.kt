package iestr.gag.examen.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import iestr.gag.examen.R
import iestr.gag.examen.databinding.FragmentPartidaBinding
import iestr.gag.examen.vm.PartidaViewModel


class PartidaFragment : Fragment() {
    lateinit var enlace:FragmentPartidaBinding
    val modelo by viewModels<PartidaViewModel>()
    val adaptador:BichoAdapter by lazy{
        BichoAdapter(this,modelo)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        enlace= FragmentPartidaBinding.inflate(inflater,container,false)
        return enlace.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observador de barra superior
        modelo.textoInformativo.observe(viewLifecycleOwner) { mensaje ->
            enlace.textoInformativo.text = mensaje
        }

        //Ajusto el prota
        enlace.armaProta.setImageResource(when(modelo.arma.ordinal){
            0 -> R.drawable.cuchillos
            1 -> R.drawable.espadaescudo
            2 -> R.drawable.hacha
            else -> R.drawable.maza
        })
        enlace.botonReponer.setImageResource(R.drawable.icono_reponer)
        enlace.botonMele.setImageResource(R.drawable.icono_mele)
        //Preparo la lista
        enlace.lista.adapter=adaptador

        //Ajusto el rótulo de arriba y la visibilidad de los botones del prota según el turno
        modelo.turnoHeroe.observe(viewLifecycleOwner){
            if(!it){//En el turno de los enemigos, lanzo ATAQUE de ENEMIGO
                val pos=(Math.random()*modelo.listaBichos.size).toInt()
                enlace.lista.smoothScrollToPosition(pos)//<-hago que la lista se mueva para que el atacante sea visible
                modelo.bichoAtacaProta(pos)
            }
        }
        //Ajusto los puntos
        modelo.puntos.observe(viewLifecycleOwner){
            enlace.puntosProta.text=it.toString()
        }
        //Ajusto la energía
        modelo.energia.observe(viewLifecycleOwner){
            enlace.fuerzaProta.progress=it
            if(it<=0) findNavController().navigate(PartidaFragmentDirections.actionPartidaFragmentToFinFragment(modelo.puntos.value!!))
        }
        /*//Ajusto la lista
        modelo.cambio.observe(viewLifecycleOwner){
            adaptador.lista=modelo.listaBichos
        }*/

        /////////////////  ACCIONES DE LOS BOTONES  ////////////////////////////
        //No  haría falta chequear que es el turno del héroe porque están ocultos cuando no lo es,
        //pero ... por asegurar
        enlace.botonReponer.setOnClickListener{
            if(modelo.turnoHeroe.value!!){
                modelo.repara()
            }
        }
        enlace.botonMele.setOnClickListener{
            if(modelo.turnoHeroe.value!!){
                modelo.mele()
            }
        }
    }

}