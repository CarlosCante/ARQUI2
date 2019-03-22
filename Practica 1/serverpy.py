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
        now = datetime.datetime.now()
        formatted_date = now.strftime('%Y-%m-%d %H:%M:%S')
        db = MySQLdb.connect(host="127.0.0.1", user="roott",passwd="1234", db="DB_arqui")
        cursor = db.cursor()

        if parsed_path.path == "/resultado":
            arreglo_valores = parsed_path.query.split('&')
            cursor.execute("insert into resultado (fecha_h, indice_uv, co, co2) values (\""+formatted_date+"\","+arreglo_valores[0]+","+arreglo_valores[1]+","+arreglo_valores[2]+");")
            self.wfile.write("ok");
            db.commit()

        if parsed_path.path == "/fechas":
            mensaje = ""
            cursor.execute("select date(fecha_h) as fecha from resultado group by fecha;")
            resultado = cursor.fetchall()
            limite = len(resultado)
            if limite > 0:
                contador = 0
                for linea in resultado:
                    contador += 1
                    mensaje = mensaje +str(linea[0])
                    if (contador != limite):
                        mensaje= mensaje + ";"
            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()

        if parsed_path.path == "/horas":
            mensaje = ""
            cursor.execute("select hour(time(fecha_h)) as hora from resultado where fecha_h > \""+parsed_path.query+" 00:00:00\" and fecha_h < \""+parsed_path.query+" 23:59:59\" group by hora;")
            resultado = cursor.fetchall()
            limite = len(resultado)
            if limite > 0:
                contador = 0
                for linea in resultado:
                    contador += 1
                    mensaje = mensaje +str(linea[0])
                    if (contador != limite):
                        mensaje= mensaje + ";"
            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()

        if parsed_path.path == "/s_fecha":
            mensaje = ""
            cursor.execute("select indice_uv, co, co2 from resultado where fecha_h > \""+parsed_path.query+" 00:00:00\" and fecha_h < \""+parsed_path.query+" 23:59:59\";")
            resultado = cursor.fetchall()
            limite = len(resultado)
            if limite > 0:
                contador = 0
                for linea in resultado:
                    contador += 1
                    sensor_uv = int(linea[0])
                    sensor_co = int(linea[1])
                    sensor_co2 = int(linea[2])
                    mensaje = mensaje + str(sensor_co2)+","+str(sensor_co)+","+str(sensor_uv)
                    if (contador != limite):
                        mensaje= mensaje + ";"
            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()

        if parsed_path.path == "/s_hora":
            mensaje = ""
            arreglo = parsed_path.query.split('&')
            cursor.execute("select indice_uv, co, co2 from resultado where fecha_h > \""+arreglo[0]+" "+arreglo[1]+":00:00\" and fecha_h < \""+arreglo[0]+" "+arreglo[1]+":59:59\";")
            resultado = cursor.fetchall()
            limite = len(resultado)
            if limite > 0:
                contador = 0
                for linea in resultado:
                    contador += 1
                    sensor_uv = int(linea[0])
                    sensor_co = int(linea[1])
                    sensor_co2 = int(linea[2])
                    mensaje = mensaje + str(sensor_co2)+","+str(sensor_co)+","+str(sensor_uv)
                    if (contador != limite):
                        mensaje= mensaje + ";"
            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()

        if parsed_path.path == "/datos":
            mensaje = ""
            cursor.execute("select * from resultado;")
            resultado = cursor.fetchall()
            if len(resultado) > 0:
                for linea in resultado:
                    datetime_object = datetime.datetime.strptime(str(linea[1]), '%Y-%m-%d %H:%M:%S')
                    sensor_uv = int(linea[2])
                    sensor_co = int(linea[3])
                    sensor_co2 = int(linea[4])
                    mensaje = mensaje +"["+str(datetime_object)+"]\tUV: "+ str(sensor_uv) +"   -   CO: "+str(sensor_co)+"   -   CO2: "+str(sensor_co2)+ "\n"
            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()

        if parsed_path.path == "/ubicaciones":
            mensaje = ""
            cursor.execute("select ubicacion from resultado group by ubicacion;")
            resultado = cursor.fetchall()
            limite = len(resultado)
            if limite > 0:
                contador = 0
                for linea in resultado:
                    contador +=1
                    mensaje = mensaje +str(linea[0])
                    if (contador != limite):
                        mensaje= mensaje + ";"
            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()

        if parsed_path.path == "/sensores":
            mensaje = ""
            cursor.execute("select indice_uv, co, co2 from resultado where ubicacion=\""+parsed_path.query+"\";")
            resultado = cursor.fetchall()
            limite = len(resultado)
            if limite > 0:
                contador = 0
                for linea in resultado:
                    contador +=1
                    sensor_uv = int(linea[0])
                    sensor_co = int(linea[1])
                    sensor_co2 = int(linea[2])
                    mensaje = mensaje +str(sensor_co2)+","+str(sensor_co)+","+ str(sensor_uv)
                    if (contador != limite):
                        mensaje= mensaje + ";"

            else:
                mensaje ="Datos no disponible"
            self.wfile.write(mensaje)
            db.commit()

        if parsed_path.path == "/json":
            mensaje = ""
            cursor.execute("select * from resultado;")
            resultado = cursor.fetchall()
            limite =len(resultado)
            if limite > 0:
                mensaje = mensaje +"{\n"
                contador = 0
                for linea in resultado:
                    contador +=1;
                    datetime_object = datetime.datetime.strptime(str(linea[1]), '%Y-%m-%d %H:%M:%S')
                    sensor_uv = int(linea[2])
                    sensor_co = int(linea[3])
                    sensor_co2 = int(linea[4])
                    ubicacion_p = str(linea[5])

                    mensaje = mensaje +"\t{\n"
                    mensaje = mensaje +"\t\t\"fecha\": \""    +str(datetime_object)+"\",\n"
                    mensaje = mensaje +"\t\t\"sensor_uv\": "  +str(sensor_uv)+",\n"
                    mensaje = mensaje +"\t\t\"sensor_co\": "  +str(sensor_co)+",\n"
                    mensaje = mensaje +"\t\t\"sensor_co2\": " +str(sensor_co2)+",\n"
                    mensaje = mensaje +"\t\t\"ubicacion\": \""+ ubicacion_p +"\"\n"
                    if (contador == limite):
                        mensaje= mensaje + "\t}\n"
                    else:
                        mensaje= mensaje + "\t},\n"

                mensaje= mensaje + "}"
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
