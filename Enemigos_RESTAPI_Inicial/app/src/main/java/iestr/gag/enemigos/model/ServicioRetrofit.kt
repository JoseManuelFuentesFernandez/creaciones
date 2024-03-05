package iestr.gag.enemigos.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServicioRetrofit {
    @GET("orco/{id}")
    suspend fun eligeUno(@Path("id") id:String):Orco
    @GET("orco")
    suspend fun eligeTodos():List<Orco>
    @POST("orco")
    suspend fun insertaUno(@Body o:Orco):Orco
    @DELETE("orco/{id}")
    suspend fun borraUno(@Path("id") id:String):Orco
    @PUT("orco/{id}")
    suspend fun modificaUno(@Path("id") id:String, @Body o:Orco):Orco
}

object ClienteRetrofit{
    private val retrofit= Retrofit.Builder()
        .baseUrl("https://65e7691953d564627a8ec3cf.mockapi.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val servicio=retrofit.create(ServicioRetrofit::class.java)
}