package iestr.gag.enemigos.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iestr.gag.enemigos.R
import iestr.gag.enemigos.viewmodel.ListaViewModel
import iestr.gag.enemigos.databinding.FragmentListaBinding


class ListaFragment : Fragment() {
    private var _enlace:FragmentListaBinding?=null
    private val enlace:FragmentListaBinding
        get()=_enlace!!

    val vm:ListaViewModel by viewModels()
    val adaptador:OrcosAdapter by lazy{
        OrcosAdapter(this,vm)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.context = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _enlace= FragmentListaBinding.inflate(inflater,container,false)
        return enlace.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(enlace.listado){
            layoutManager= LinearLayoutManager(activity)
            adapter=adaptador
        }

        //Manejar los gestos sobre el recyclerview
        val manejadorGestos=ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                vm.eliminaOrco(viewHolder.absoluteAdapterPosition)
            }
        })
        manejadorGestos.attachToRecyclerView(enlace.listado)

        //Añadir el menú
        activity?.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_superior,menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.recargar -> vm.recarga()
                    R.id.insertar -> vm.insertaOrco()
                }
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _enlace=null
    }
}