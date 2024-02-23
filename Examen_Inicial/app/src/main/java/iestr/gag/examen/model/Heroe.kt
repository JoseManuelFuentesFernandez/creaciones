package iestr.gag.examen.model

//@Entity(tableName = "heroe")
data class Heroe(
    //@PrimaryKey(autoGenerate = true) val id: Int = 0,
    val energia: Int,
    val puntos: Int
)
