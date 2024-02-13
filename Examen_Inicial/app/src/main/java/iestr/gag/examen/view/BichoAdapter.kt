package iestr.gag.examen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import iestr.gag.examen.R
import iestr.gag.examen.model.Bicho
import iestr.gag.examen.vm.PartidaViewModel

class BichoAdapter(padre: Fragment, val vm:PartidaViewModel):RecyclerView.Adapter<BichoAdapter.Holder>() {
    private var lista=listOf<Bicho>()
    init{
        vm.cambio.observe(padre.viewLifecycleOwner){
            lista=vm.listaBichos
            notifyDataSetChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val vista=LayoutInflater.from(parent.context).inflate(R.layout.linea,parent,false)
        return Holder(vista)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bicho=lista[position]
        holder.rellena(bicho)
    }
    override fun getItemCount(): Int=lista.size

    ////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val caraBicho=itemView.findViewById<ImageView>(R.id.cara_bicho)
        private val calaveraBicho=itemView.findViewById<ImageView>(R.id.calavera_bicho)
        private val rasgosBicho=itemView.findViewById<ImageView>(R.id.rasgos_bicho)
        private val nombreBicho=itemView.findViewById<TextView>(R.id.nombre_bicho)
        private val armaBicho=itemView.findViewById<ImageView>(R.id.arma_bicho)

        init{
            itemView.setOnClickListener{
                vm.ProtaAtacaBicho(adapterPosition)
            }
        }

        fun rellena(bicho: Bicho){
            when(bicho.tipo){
                0 ->{
                    caraBicho.setImageResource(R.drawable.vampiro_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo_cuernos)
                    rasgosBicho.setImageResource(R.drawable.vampiro_normal)
                }
                1 ->{
                    caraBicho.setImageResource(R.drawable.demonio_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo_cuernos)
                    rasgosBicho.setImageResource(R.drawable.demonio_normal)
                }
                2 ->{
                    caraBicho.setImageResource(R.drawable.orco_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo)
                    rasgosBicho.setImageResource(R.drawable.orco_normal)
                }
                else ->{
                    caraBicho.setImageResource(R.drawable.troll_cara)
                    calaveraBicho.setImageResource(R.drawable.craneo)
                    rasgosBicho.setImageResource(R.drawable.troll_normal)
                }
            }
            caraBicho.alpha= kotlin.math.min(
                1f,
                (bicho.energia / 100.0f) * 1.1f
            )//le añado un poco para que no se note hasta bajar de 90
            rasgosBicho.alpha=caraBicho.alpha
            calaveraBicho.alpha=(1-caraBicho.alpha)*.75f//dos tercios para que no aparezca muy rápido
            nombreBicho.text=bicho.nombre+bicho.energia.toString()
            armaBicho.setImageResource(when(bicho.arma){
                0-> R.drawable.cuchillos_orco
                1 -> R.drawable.espadaescudo_orco
                2 -> R.drawable.hacha_orco
                else -> R.drawable.maza_orco
            })
        }
    }
}