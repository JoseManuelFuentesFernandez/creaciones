package iestr.gag.enemigos.model

object OrcosProvider {
    fun generaLista(cantidad:Int=100)=MutableList(cantidad){Orco()}
}