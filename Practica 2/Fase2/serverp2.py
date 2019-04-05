import os
import sys
import urlparse
import MySQLdb
import datetime

from BaseHTTPServer import BaseHTTPRequestHandler,HTTPServer

PORT_NUMBER = 8003

class myHandler(BaseHTTPRequestHandler):

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','text/plain')
        self.end_headers()

        parsed_path = urlparse.urlparse(self.path)
        db = MySQLdb.connect(host="127.0.0.1", user="roott",passwd="1234", db="DB_arqui")
        cursor = db.cursor()

        if parsed_path.path == "/datos":
            arreglo_valores = parsed_path.query.split('&')
            cursor.execute("insert into resultadoDos (peso, obstaculos, tiempo, distancia) values ("+arreglo_valores[0]+","+arreglo_valores[1]+","+arreglo_valores[2]+","+arreglo_valores[3]+");")
            self.wfile.write("ok");
            db.commit()

        if parsed_path.path == "/valores":
            mensaje = ""
            cursor.execute("select * from resultadoDos;")
            resultado = cursor.fetchall()
            if len(resultado) > 0:
                for linea in resultado:
                    peso = int(linea[1])
                    obstaculo = int(linea[2])
                    tiempo = int(linea[3])
                    distancia = int(linea[4])
                    mensaje = mensaje +"peso: "+str(peso)+"\tobstaculos: "+ str(obstaculo) +"\ttiempo: "+str(tiempo)+"\tdistancia: "+str(distancia)+ "\n"
            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()


        return

try:
	#Create a web server and define the handler to manage the
	#incoming request
	server = HTTPServer(('', PORT_NUMBER), myHandler)
	print 'Started httpserver on port ' , PORT_NUMBER

	#Wait forever for incoming htto requests
	server.serve_forever()

except KeyboardInterrupt:
	print '^C received, shutting down the web server'
	server.socket.close()

'''
primero
	instalo el paquete -> sudo apt install python-mysqldb
	esto es para conectar python con la base de datos
#Handler for the GET requests
    #http://127.0.0.1:8006/resultado?valor1&valor2&valor3
'''
