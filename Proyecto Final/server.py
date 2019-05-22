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
        db = MySQLdb.connect(host="127.0.0.1", user="root",passwd="1234", db="DB_arqui")
        cursor = db.cursor()

        if parsed_path.path == "/resultados":
            arreglo_valores = parsed_path.query.split('&')
            cursor.execute("insert into resultado (fecha, hora_inicio, hora_final, segundos) values (\""+arreglo_valores[0]+"\",\""+arreglo_valores[1]+"\",\""+arreglo_valores[2]+"\","+arreglo_valores[3]+");")
            self.wfile.write("ok");
            db.commit()

        if parsed_path.path == "/hola":
            self.wfile.write("hola");

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
    instalo el paquete -> sudo apt -y install python-mysqldb
    esto es para conectar python con la base de datos
#Handler for the GET requests
    #http://127.0.0.1:8006/resultado?valor1&valor2&valor3
'''