from http.server import BaseHTTPRequestHandler,HTTPServer
from os import curdir, sep
import cgi
import sys
from pprint import pprint
import urllib.parse
import codecs
import random
import os
import json
from socketserver import ThreadingMixIn
import threading
import sqlite3 as sql
import json
import operator
from collections import OrderedDict
import numpy as np
import face_recognition as fr
from PIL import Image
from random import random

conn = sql.connect('actors.sqlite')
cur = conn.cursor()
cur.execute('''SELECT * FROM actors''')
response = cur.fetchall()

actors_encodngs = []
for i in range(1,13):
    actors_encodngs.append(np.load('encodes/1_{}_1000encdngs.npy'.format(i)))



#This class will handles any incoming request form clients

class myHandler(BaseHTTPRequestHandler):
    
    """
    Custom functions to process incoming and outgoing signals
    
    """
    def gen_file_name(self):
        fnamelst = [0,1,2,3,4,5,6,7,8,9,0,'a','b','b','d','e','f','g','h','i','j','k','l','m','n','q','r','s','z','t']
        fname = ''
        for i in fnamelst:
            fname += str(fnamelst[int(random() * len(fnamelst))])
        fname += '.png'
        return fname
    
    def rec_face(self,path,find_closer = False):
        global actors_encodngs
        global response
        unknown_picture = fr.load_image_file(path)
        
        #delete the temp file
        
        
        unknown_face_encoding = fr.face_encodings(unknown_picture)
        if len(unknown_face_encoding) > 1:
            unknown_face_encoding = unknown_face_encoding[0]
        try:
            indices = []
            responses = []
            row = 0
            for actenk in actors_encodngs:
                r = fr.compare_faces(actenk, unknown_face_encoding)
                try:
                    inx = r.index(True)
                    indices.append(inx)
                except:
                    indices.append(-1)



            for inx in indices:
                responses.append(response[inx][1])

            #find the responses frequency
            myset = set(indices)
            b = list(myset)
            cnt = [indices.count(i) for i in b]
            final_result = dict(zip(b, cnt))
            print(final_result)
            max_id = max(final_result, key=final_result.get)
            print(max_id)

            if find_closer:
                if max_id == -1 and final_result[max_id] == 12:
                    print('Unknown Actor!!!')
                    return {'name' : 'Unknown Actor!!!','url' : 'Unknown Actor!!!'}
                elif max_id == -1:
                    final_result.pop(max_id)
                    max_id = max(final_result, key=final_result.get)
            else:
                if max_id == -1:
                    print('Unknown Actor!!!')
                    return {'name' : 'Unknown Actor!!!','url' : 'Unknown Actor!!!'}

            print(response[max_id][1])
            web_url = "https://www.imdb.com/name/{}".format(response[max_id][0])
            return {'name' : response[max_id][1],'url' : web_url}
        except Exception as e:
            print(e)
            return {'name' : 'Unknown Actor!!!','url' : 'Unknown Actor!!!'}

    """
    End of Custom
    
    """
    
    #Handler for the GET requests
    def do_GET(self):
        print('do GET')
        return

    #Handler for the POST requests
    def do_POST(self):
        pprint (vars(self))
        length = int(self.headers['Content-Length'])
        print(self.headers)
     
        
        
        form = cgi.FieldStorage(
            fp=self.rfile,
            headers=self.headers,
            environ={'REQUEST_METHOD':'POST'}
        )
        
       
        image = form['fileUpload'].value
        fname = self.gen_file_name();
        imgpath = 'unk_face/' + fname
        with open(imgpath,'wb') as handler:
            handler.write(image)
            
        
        
        response = self.rec_face(imgpath)
        response = json.dumps(response)
        
        # Send back response after the data is processed
        
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Content-type','text/html')
        
        self.end_headers()
        
        # Send the html message
        self.wfile.write(response.encode())
        

        return
    
    def do_OPTIONS(self):
        self.send_response(200, "ok")
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, OPTIONS')
        self.send_header("Access-Control-Allow-Headers", "X-Requested-With")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()
        
        return

class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    """Handle requests in a separate thread."""
    

PORT_NUMBER = 23000

try:
    #Create a web server and define the handler to manage the incoming request
    
    server = ThreadedHTTPServer(('', PORT_NUMBER), myHandler)
    print('Started httpserver on port ' , PORT_NUMBER)
    
    #Wait forever for incoming htto requests
    server.serve_forever()

except KeyboardInterrupt:
    print('^C received, shutting down the web server')
    server.socket.close()