package iestr.gag.enemigos.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import iestr.gag.enemigos.R
import iestr.gag.enemigos.model.Orco
import iestr.gag.enemigos.viewmodel.ListaViewModel

class OrcosAdapter(padre: Fragment, private val vm:ListaViewModel): RecyclerView.Adapter<OrcosAdapter.Holder>() {
    private var lista=vm.lista

    init{
        vm.cambio.observe(padre.viewLifecycleOwner){
            if(it>0){
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val vista=LayoutInflater.from(parent.context).inflate(R.layout.linea,parent,false)
        return Holder(vista)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val orco= lista[position]
        holder.rellena(orco)
    }

    override fun getItemCount(): Int =lista.size

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    inner class Holder(itemView: View) :RecyclerView.ViewHolder(itemView){
        private val nombre=itemView.findViewById<TextView>(R.id.nombre)
        private val energia=itemView.findViewById<TextView>(R.id.energia)
        init{
            itemView.setOnClickListener{
                vm.modificaOrco(absoluteAdapterPosition)
            }
        }
        fun rellena(orco: Orco){
            nombre.text=orco.nombre
            energia.text=orco.energia.toString()
        }
    }
}