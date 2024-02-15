import mysql.connector

mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    password="admin"
)

cursor = mydb.cursor()


# CREACION DB
cursor.execute("DROP DATABASE IF EXISTS RESERVAS")
cursor.execute("CREATE DATABASE RESERVAS")
cursor.execute("USE RESERVAS")

# CREACION Tablas
cursor.execute("CREATE TABLE Precios ("
               "id INT AUTO_INCREMENT,"
               "pHoras DOUBLE,"
               "pDias DOUBLE,"
               "pMeses DOUBLE,"
               "PRIMARY KEY (id))")

cursor.execute("CREATE TABLE Salas ("
               "id INT AUTO_INCREMENT, "
               "nombre VARCHAR(255), "
               "capacidad INT,"
               "idPrecio INT,"
               "PRIMARY KEY (id),"
               "FOREIGN KEY (idPrecio) REFERENCES Precios(id))")

cursor.execute("CREATE TABLE Reservas ("
               "id INT AUTO_INCREMENT,"
               "fechaInicio DATE,"
               "fechaFin DATE,"               
               "horaInicio VARCHAR(255),"
               "horaFin VARCHAR(255),"
               "idSala INT,"
               "PRIMARY KEY (id),"
               "FOREIGN KEY (idSala) REFERENCES Salas(id))")

# INSERCION DATOS

