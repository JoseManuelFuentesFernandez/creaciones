import mysql.connector


# CONEXION BASE DE DATOS
mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    password="admin"
)

cursor = mydb.cursor()

